using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using System.Text.Json;


namespace BrasilBurger.Controllers
{
    [Route("commande")]
    public class CommandeController : Controller
    {
        private readonly ICommandeService _commandeService;
        private readonly IPanierService _panierService;
        private readonly IPaiementService _paiementService;
        private readonly IZoneService _zoneService;
        private readonly IClientService _clientService;
        private readonly IConfiguration _configuration;
        private readonly PayDunyaService _payDunyaService;

        private int LimitParPage => _configuration.GetValue<int>("LimitParPage", 10);

        public CommandeController(
            ICommandeService commandeService,
            IPanierService panierService,
            IPaiementService paiementService,
            IZoneService zoneService,
            IClientService clientService,
            IConfiguration configuration)
        {
            _commandeService = commandeService;
            _panierService = panierService;
            _paiementService = paiementService;
            _zoneService = zoneService;
            _clientService = clientService;
            _configuration = configuration;
        }

        // Vérifier si le client est connecté
        private int? GetClientId()
        {
            return HttpContext.Session.GetInt32("ClientId");
        }

        [HttpGet("")]
        [HttpGet("mes-commandes")]
        public IActionResult MesCommandes(int page = 1, StatutCommande? statut = null)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth", new { returnUrl = "/commande/mes-commandes" });
            }

            var commandes = _commandeService.GetByClientWithDetails(clientId.Value);

            // Filtrer par statut si spécifié
            if (statut.HasValue)
            {
                commandes = commandes.Where(c => c.Statut == statut.Value).ToList();
            }

            var totalCount = commandes.Count;
            var items = commandes
                .Skip((page - 1) * LimitParPage)
                .Take(LimitParPage)
                .ToList();

            var vm = new CommandeListViewModel
            {
                Commandes = CommandeListDto.FromEntities(items),
                PageEncours = page,
                NbrePage = (int)Math.Ceiling(totalCount / (double)LimitParPage),
                StatutFilter = statut
            };

            return View(vm);
        }

        [HttpGet("detail/{id}")]
        public IActionResult Detail(int id)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/commande/detail/{id}" });
            }

            var commande = _commandeService.GetByIdWithDetails(id);
            if (commande == null || commande.ClientId != clientId.Value)
            {
                return NotFound();
            }

            var vm = new CommandeDetailViewModel
            {
                Commande = CommandeDetailDto.FromEntity(commande)
            };

            return View(vm);
        }

        [HttpGet("passer")]
public IActionResult Passer()
{
    var clientId = GetClientId();

    // Récupérer le panier
    var panier = _panierService.GetPanier();
    if (panier.NombreItems == 0)
    {
        TempData["error"] = "Votre panier est vide";
        return RedirectToAction("Index", "Panier");
    }

    // Préparer ViewModel
    var vm = new PasserCommandeViewModel
    {
        Panier = panier,
        Zones = _zoneService.GetAll(),
        Quartiers = _zoneService.GetAllQuartiers(),
        CommandeForm = new CommandeFormDto()
    };

    // Restaurer TempData si présent
    if (TempData["CommandeForm"] != null)
    {
        var tempCommandeForm = JsonSerializer.Deserialize<CommandeFormDto>((string)TempData["CommandeForm"]);
        if (tempCommandeForm != null)
        {
            vm.CommandeForm = tempCommandeForm;
        }
        TempData.Keep("CommandeForm");
    }

    // Calculer les frais déjà sélectionnés
    if (vm.CommandeForm.TypeLivraison == TypeLivraison.LIVRAISON && vm.CommandeForm.QuartierId.HasValue)
    {
        var quartier = vm.Quartiers.FirstOrDefault(q => q.Id == vm.CommandeForm.QuartierId.Value);
        if (quartier != null)
        {
            _panierService.SetFraisLivraison(quartier.Zone?.PrixLivraison ?? 0);
            vm.Panier = _panierService.GetPanier(); // recharger panier avec frais
        }
    }

    PrepareSelectLists(vm);
    return View(vm);
}

        [HttpPost("passer")]
        public IActionResult Passer(PasserCommandeViewModel vm)
        {
            var clientId = GetClientId();

            // Si utilisateur non connecté
            if (clientId == null)
            {
                TempData["CommandeForm"] = JsonSerializer.Serialize(vm.CommandeForm);
                TempData["Panier"] = JsonSerializer.Serialize(_panierService.GetPanier());
                return RedirectToAction("Connexion", "Auth", new { returnUrl = "/commande/passer" });
            }

            var panier = _panierService.GetPanier();
            if (panier.NombreItems == 0)
            {
                TempData["error"] = "Votre panier est vide";
                return RedirectToAction("Index", "Panier");
            }

            // Vérifier si livraison et quartier nécessaire
            if (vm.CommandeForm.TypeLivraison == TypeLivraison.LIVRAISON && !vm.CommandeForm.QuartierId.HasValue)
            {
                ModelState.AddModelError("CommandeForm.QuartierId", "Veuillez sélectionner un quartier pour la livraison");
            }

            if (!ModelState.IsValid)
            {
                vm.Panier = panier;
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }

            // Ajouter les frais de livraison au panier
            if (vm.CommandeForm.TypeLivraison == TypeLivraison.LIVRAISON && vm.CommandeForm.QuartierId.HasValue)
            {
                var quartier = _zoneService.GetAllQuartiers()
                    .FirstOrDefault(q => q.Id == vm.CommandeForm.QuartierId.Value);
                decimal fraisLivraison = quartier?.Zone?.PrixLivraison ?? 0;
                _panierService.SetFraisLivraison(fraisLivraison);
            }
            else
            {
                _panierService.SetFraisLivraison(0);
            }

            try
            {
                // Créer la commande
                var creerCommandeDto = new CreerCommandeDto
                {
                    TypeLivraison = vm.CommandeForm.TypeLivraison,
                    QuartierId = vm.CommandeForm.QuartierId,
                    Adresse = vm.CommandeForm.Adresse,
                    Notes = vm.CommandeForm.Notes
                };
                var commande = _commandeService.CreerCommande(clientId.Value, creerCommandeDto, panier.Items);

                // Rediriger vers paiement
                return RedirectToAction("Payer", new { id = commande.Id });
            }
            catch (Exception ex)
            {
                ModelState.AddModelError("", "Une erreur est survenue lors de la création de la commande");
                vm.Panier = panier;
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }
        }
        [HttpGet("payer/{id}")]
        public IActionResult Payer(int id)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/commande/payer/{id}" });
            }

            var commande = _commandeService.GetByIdWithDetails(id);
            if (commande == null || commande.ClientId != clientId.Value)
            {
                return NotFound();
            }

            // Vérifier si déjà payée
            if (commande.EstPayee)
            {
                TempData["info"] = "Cette commande est déjà payée";
                return RedirectToAction("Detail", new { id });
            }

            var client = _clientService.GetById(clientId.Value);

            var vm = new PaiementViewModel
            {
                Commande = commande,
                PaiementForm = new PaiementFormDto
                {
                    CommandeId = id,
                    NumeroTelephone = client?.Telephone ?? ""
                }
            };

            return View(vm);
        }

        /*[HttpPost("payer")]
        public IActionResult Payer(PaiementViewModel vm)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth");
            }

            var commande = _commandeService.GetByIdWithDetails(vm.PaiementForm.CommandeId);
            if (commande == null || commande.ClientId != clientId.Value)
            {
                return NotFound();
            }

            if (!ModelState.IsValid)
            {
                vm.Commande = commande;
                return View(vm);
            }

            try
            {
                var paiement = _paiementService.EffectuerPaiement(
                    vm.PaiementForm.CommandeId,
                    vm.PaiementForm.ModePaiement,
                    vm.PaiementForm.NumeroTelephone
                );

                // Vider le panier après paiement réussi
                _panierService.ViderPanier();

                TempData["success"] = "Paiement effectué avec succès ! Votre commande est en cours de préparation.";
                return RedirectToAction("Confirmation", new { id = commande.Id });
            }
            catch (Exception ex)
            {
                ModelState.AddModelError("", ex.Message);
                vm.Commande = commande;
                return View(vm);
            }
        }*/
        [HttpPost("payer")]
        public IActionResult Payer(PaiementViewModel vm)
        {
            ViewBag.Message = "Votre commande est en attente de confirmation. Veuillez vérifier votre téléphone pour valider le paiement.";
            return View("PaiementAttente");
        }
        [HttpGet("confirmation/{id}")]
        public IActionResult Confirmation(int id)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth");
            }

            var commande = _commandeService.GetByIdWithDetails(id);
            if (commande == null || commande.ClientId != clientId.Value)
            {
                return NotFound();
            }

            var vm = new CommandeDetailViewModel
            {
                Commande = CommandeDetailDto.FromEntity(commande)
            };

            return View(vm);
        }

        [HttpPost("annuler/{id}")]
        public IActionResult Annuler(int id)
        {
            var clientId = GetClientId();
            if (clientId == null)
            {
                return RedirectToAction("Connexion", "Auth");
            }

            var commande = _commandeService.GetById(id);
            if (commande == null || commande.ClientId != clientId.Value)
            {
                return NotFound();
            }

            // Seules les commandes en attente peuvent être annulées par le client
            if (commande.Statut != StatutCommande.EN_ATTENTE)
            {
                TempData["error"] = "Cette commande ne peut plus être annulée";
                return RedirectToAction("Detail", new { id });
            }

            _commandeService.AnnulerCommande(id);
            TempData["success"] = "Commande annulée";

            return RedirectToAction("MesCommandes");
        }

        // API pour calculer les frais de livraison (AJAX)
        [HttpGet("frais-livraison/{quartierId}")]
        public IActionResult GetFraisLivraison(int quartierId)
        {
            var frais = _commandeService.GetFraisLivraison(quartierId);
            return Json(new { frais });
        }

        // Pour "autre quartier" (saisie manuelle)
        [HttpGet("frais-livraison-autre")]
        public IActionResult GetFraisLivraisonAutre(string nomQuartier)
        {
            // Ici, on peut faire une recherche dans la base pour voir si le quartier existe déjà
            // ou appliquer un tarif par défaut (exemple : 0 ou 1000 FCFA)
            var quartier = _zoneService.GetAllQuartiers().FirstOrDefault(q => q.Nom.ToLower() == nomQuartier.ToLower());
            decimal frais = quartier?.Zone?.PrixLivraison ?? 0; // ou un tarif par défaut si non trouvé
            return Json(new { frais });
        }

        private void PrepareSelectLists(PasserCommandeViewModel vm)
        {
            ViewBag.Quartiers = new SelectList(vm.Quartiers, "Id", "Nom", vm.CommandeForm.QuartierId);
            ViewBag.TypesLivraison = new SelectList(new[]
            {
                new { Value = TypeLivraison.SUR_PLACE, Text = "Sur place" },
                new { Value = TypeLivraison.A_EMPORTER, Text = "À emporter" },
                new { Value = TypeLivraison.LIVRAISON, Text = "Livraison" }
            }, "Value", "Text", vm.CommandeForm.TypeLivraison);
        }
    }
}