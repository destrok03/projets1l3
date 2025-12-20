using BrasilBurger.Models.DTO;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using Microsoft.AspNetCore.Mvc;

namespace BrasilBurger.Controllers
{
    [Route("panier")]
    public class PanierController : Controller
    {
        private readonly IPanierService _panierService;
        private readonly IProduitService _produitService;
        private readonly IZoneService _zoneService;

        public PanierController(IPanierService panierService, IProduitService produitService, IZoneService zoneService)
        {
            _panierService = panierService;
            _produitService = produitService;
            _zoneService = zoneService;
        }

        [HttpGet("")]
        public IActionResult Index()
        {
            var vm = new PanierViewModel
            {
                Panier = _panierService.GetPanier()
            };
            ViewBag.Quartiers = _zoneService.GetAllQuartiers();
            return View(vm);
        }

        [HttpPost("ajouter")]
        public IActionResult Ajouter([FromForm] AjouterPanierDto dto)
        {
            _panierService.AjouterProduit(dto.ProduitId, dto.Quantite);

            // Ajouter les compléments si sélectionnés
            if (dto.ComplementIds != null)
            {
                foreach (var complementId in dto.ComplementIds)
                {
                    _panierService.AjouterProduit(complementId, 1);
                }
            }

            TempData["success"] = "Produit ajouté au panier !";
            
            // Si c'est une requête AJAX, retourner le nombre d'items
            if (Request.Headers["X-Requested-With"] == "XMLHttpRequest")
            {
                return Json(new { 
                    success = true, 
                    nombreItems = _panierService.GetNombreItems(),
                    message = "Produit ajouté au panier !"
                });
            }

            return RedirectToAction("Index");
        }

        [HttpPost("modifier")]
        public IActionResult Modifier(int produitId, int quantite)
        {
            _panierService.ModifierQuantite(produitId, quantite);

            if (Request.Headers["X-Requested-With"] == "XMLHttpRequest")
            {
                var panier = _panierService.GetPanier();
                return Json(new { 
                    success = true, 
                    nombreItems = panier.NombreItems,
                    sousTotal = panier.SousTotal,
                    total = panier.Total
                });
            }

            return RedirectToAction("Index");
        }

        [HttpPost("supprimer")]
        public IActionResult Supprimer(int produitId)
        {
            _panierService.SupprimerProduit(produitId);
            TempData["success"] = "Produit supprimé du panier";

            if (Request.Headers["X-Requested-With"] == "XMLHttpRequest")
            {
                var panier = _panierService.GetPanier();
                return Json(new { 
                    success = true, 
                    nombreItems = panier.NombreItems,
                    sousTotal = panier.SousTotal,
                    total = panier.Total
                });
            }

            return RedirectToAction("Index");
        }

        [HttpPost("vider")]
        public IActionResult Vider()
        {
            _panierService.ViderPanier();
            TempData["success"] = "Panier vidé";
            return RedirectToAction("Index");
        }

        [HttpGet("count")]
        public IActionResult Count()
        {
            return Json(new { count = _panierService.GetNombreItems() });
        }
    }
}
