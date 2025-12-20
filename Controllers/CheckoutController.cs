using BrasilBurger.Models.Enums;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using Microsoft.AspNetCore.Mvc;

namespace BrasilBurger.Controllers;

/// <summary>
/// Checkout inspiré e-commerce (type Jumia).
/// - Catalogue/Panier accessibles sans connexion.
/// - Checkout + paiement + historique nécessitent une connexion.
/// </summary>
[Route("checkout")]
public class CheckoutController : Controller
{
    private readonly IPanierService _panierService;
    private readonly ICommandeService _commandeService;
    private readonly IPaiementService _paiementService;
    private readonly IZoneService _zoneService;

    public CheckoutController(
        IPanierService panierService,
        ICommandeService commandeService,
        IPaiementService paiementService,
        IZoneService zoneService)
    {
        _panierService = panierService;
        _commandeService = commandeService;
        _paiementService = paiementService;
        _zoneService = zoneService;
    }

    private int? ClientId => HttpContext.Session.GetInt32("ClientId");

    private IActionResult RedirectToLogin()
        => RedirectToAction("Connexion", "Auth", new { returnUrl = "/checkout" });

    // GET /checkout
    [HttpGet("")]
    public IActionResult Index()
    {
        if (ClientId == null)
            return RedirectToLogin();

        var panier = _panierService.GetPanier();
        if (panier == null || panier.NombreItems == 0)
        {
            TempData["error"] = "Votre panier est vide.";
            return RedirectToAction("Index", "Panier");
        }

        var quartiers = _zoneService.GetAllQuartiers();
        ViewBag.Quartiers = quartiers;

        var vm = new CheckoutViewModel
        {
            TypeLivraison = TypeLivraison.SUR_PLACE,
            SousTotal = panier.SousTotal,
            FraisLivraison = 0,
            Total = panier.SousTotal
        };

        return View(vm);
    }

    // POST /checkout  -> crée la commande et redirige vers paiement
    [HttpPost("")]
    [ValidateAntiForgeryToken]
    public IActionResult Index(CheckoutViewModel vm)
    {
        if (ClientId == null)
            return RedirectToLogin();

        var panier = _panierService.GetPanier();
        if (panier == null || panier.NombreItems == 0)
        {
            TempData["error"] = "Votre panier est vide.";
            return RedirectToAction("Index", "Panier");
        }

        // Validation livraison
        if (vm.TypeLivraison == TypeLivraison.LIVRAISON)
        {
            if (!vm.QuartierId.HasValue)
                ModelState.AddModelError(nameof(vm.QuartierId), "Le quartier est obligatoire pour la livraison.");
            if (string.IsNullOrWhiteSpace(vm.AdresseLivraison))
                ModelState.AddModelError(nameof(vm.AdresseLivraison), "L'adresse est obligatoire pour la livraison.");
        }

        if (!ModelState.IsValid)
        {
            ViewBag.Quartiers = _zoneService.GetAllQuartiers();
            vm.SousTotal = panier.SousTotal;
            vm.FraisLivraison = _commandeService.GetFraisLivraison(vm.QuartierId);
            if (vm.TypeLivraison != TypeLivraison.LIVRAISON)
                vm.FraisLivraison = 0;
            vm.Total = vm.SousTotal + vm.FraisLivraison;
            return View(vm);
        }

        // Calcul frais (source de vérité serveur)
        var frais = vm.TypeLivraison == TypeLivraison.LIVRAISON
            ? _commandeService.GetFraisLivraison(vm.QuartierId)
            : 0;

        // Créer commande
        var dto = new BrasilBurger.Models.DTO.CreerCommandeDto
        {
            TypeLivraison = vm.TypeLivraison,
            QuartierId = vm.TypeLivraison == TypeLivraison.LIVRAISON ? vm.QuartierId : null,
            Notes = BuildNotes(vm)
        };

        var commande = _commandeService.CreerCommande(ClientId.Value, dto, panier.Items);

        // Sécurité: s'assurer que le total inclut les frais calculés
        if (commande.FraisLivraison != frais)
        {
            // recalage minimal (ne doit pas arriver si le service est cohérent)
            commande.FraisLivraison = frais;
            commande.MontantTotal = panier.SousTotal + frais;
        }

        _panierService.ViderPanier();
        return RedirectToAction(nameof(Payment), new { id = commande.Id });
    }

    // GET /checkout/payment/{id}
    [HttpGet("payment/{id:int}")]
    public IActionResult Payment(int id)
    {
        if (ClientId == null)
            return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/checkout/payment/{id}" });

        var cmd = _commandeService.GetByIdWithDetails(id);
        if (cmd == null || cmd.ClientId != ClientId.Value)
            return NotFound();

        if (cmd.Paiement != null)
        {
            TempData["info"] = "Cette commande est déjà payée.";
            return RedirectToAction(nameof(Confirmation), new { id });
        }

        var vm = new PaymentViewModel
        {
            CommandeId = id,
            Montant = cmd.MontantTotal
        };
        ViewBag.Commande = cmd;
        return View(vm);
    }

    // POST /checkout/payment/{id}
    [HttpPost("payment/{id:int}")]
    [ValidateAntiForgeryToken]
    public IActionResult Payment(int id, PaymentViewModel vm)
    {
        if (ClientId == null)
            return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/checkout/payment/{id}" });

        if (id != vm.CommandeId)
            return BadRequest("CommandeId invalide.");

        var cmd = _commandeService.GetByIdWithDetails(id);
        if (cmd == null || cmd.ClientId != ClientId.Value)
            return NotFound();

        if (cmd.Paiement != null)
        {
            TempData["info"] = "Cette commande est déjà payée.";
            return RedirectToAction(nameof(Confirmation), new { id });
        }

        // Mode paiement autorisé
        if (vm.ModePaiement != ModePaiement.WAVE && vm.ModePaiement != ModePaiement.ORANGE_MONEY)
            ModelState.AddModelError(nameof(vm.ModePaiement), "Choisissez Wave ou Orange Money.");

        if (string.IsNullOrWhiteSpace(vm.NumeroTelephone))
            ModelState.AddModelError(nameof(vm.NumeroTelephone), "Numéro de téléphone obligatoire.");

        // Vérification montant (anti-triche)
        if (vm.Montant != cmd.MontantTotal)
            ModelState.AddModelError(nameof(vm.Montant), "Montant invalide.");

        if (!ModelState.IsValid)
        {
            ViewBag.Commande = cmd;
            vm.Montant = cmd.MontantTotal;
            return View(vm);
        }

        // Paiement
        _paiementService.EffectuerPaiement(id, vm.ModePaiement, vm.NumeroTelephone);

        TempData["success"] = "Paiement réussi.";
        return RedirectToAction(nameof(Confirmation), new { id });
    }

    // GET /checkout/confirmation/{id}
    [HttpGet("confirmation/{id:int}")]
    public IActionResult Confirmation(int id)
    {
        if (ClientId == null)
            return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/checkout/confirmation/{id}" });

        var cmd = _commandeService.GetByIdWithDetails(id);
        if (cmd == null || cmd.ClientId != ClientId.Value)
            return NotFound();

        return View(cmd);
    }

    // Notes de commande : on stocke l'adresse dans Notes (sans changer la DB)
    private static string BuildNotes(CheckoutViewModel vm)
    {
        var parts = new List<string>();
        if (!string.IsNullOrWhiteSpace(vm.AdresseLivraison))
            parts.Add($"Adresse: {vm.AdresseLivraison.Trim()}");
        if (!string.IsNullOrWhiteSpace(vm.Notes))
            parts.Add(vm.Notes.Trim());
        return string.Join(" | ", parts);
    }
}
