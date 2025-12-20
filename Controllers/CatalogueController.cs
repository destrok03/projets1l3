using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using Microsoft.AspNetCore.Mvc;

namespace BrasilBurger.Controllers
{
    [Route("catalogue")]
    public class CatalogueController : Controller
    {
        private readonly IProduitService _produitService;
        private readonly IConfiguration _configuration;

        private int LimitParPage => _configuration.GetValue<int>("LimitParPage", 12);

        public CatalogueController(IProduitService produitService, IConfiguration configuration)
        {
            _produitService = produitService;
            _configuration = configuration;
        }

        [HttpGet("ajouter/{id}")]
        public IActionResult Ajouter(int id, int quantite = 1, List<int>? FritesIds = null, List<int>? BoissonIds = null)
        {
            var produit = _produitService.GetById(id);
            if (produit == null)
            {
                return NotFound();
            }

            var boissons = _produitService.GetComplementsByType(TypeComplement.BOISSON);
            var frites = _produitService.GetComplementsByType(TypeComplement.FRITES);

            var produitDto = ProduitListDto.FromEntity(produit) ?? new ProduitListDto();
            decimal total = produit.Prix;
            var menuComposants = new List<ProduitListDto>();
            if (produit.TypeProduit == TypeProduit.MENU)
            {
                if (produit.Burger != null)
                    menuComposants.Add(ProduitListDto.FromEntity(produit.Burger));
                if (produit.Frites != null)
                    menuComposants.Add(ProduitListDto.FromEntity(produit.Frites));
                if (produit.Boisson != null)
                    menuComposants.Add(ProduitListDto.FromEntity(produit.Boisson));
            }
            if (FritesIds != null && frites != null)
            {
                foreach (var fritesId in FritesIds)
                {
                    var fritesProduit = frites.FirstOrDefault(f => f.Id == fritesId);
                    if (fritesProduit != null) total += fritesProduit.Prix;
                }
            }
            if (BoissonIds != null && boissons != null)
            {
                foreach (var boissonId in BoissonIds)
                {
                    var boissonProduit = boissons.FirstOrDefault(b => b.Id == boissonId);
                    if (boissonProduit != null) total += boissonProduit.Prix;
                }
            }
            total *= quantite;

            var vm = new ViewModels.ProduitDetailViewModel
            {
                Produit = produitDto,
                Boissons = ProduitListDto.FromEntities(boissons != null ? boissons : new List<BrasilBurger.Models.Produit>()),
                Frites = ProduitListDto.FromEntities(frites != null ? frites : new List<BrasilBurger.Models.Produit>()),
                Quantite = quantite,
                Total = total,
                MenuComposants = menuComposants
            };

            return View(vm);
        }

        [HttpPost("ajouter-au-panier")]
        public IActionResult AjouterAuPanier(int ProduitId, int Quantite, List<int>? FritesIds, List<int>? BoissonIds)
        {
            // Ajout du produit principal et des compléments
            var panierService = HttpContext.RequestServices.GetService(typeof(IPanierService)) as IPanierService;
            if (panierService != null)
            {
                panierService.AjouterProduit(ProduitId, Quantite);
                if (FritesIds != null)
                {
                    foreach (var fritesId in FritesIds)
                    {
                        panierService.AjouterProduit(fritesId, 1);
                    }
                }
                if (BoissonIds != null)
                {
                    foreach (var boissonId in BoissonIds)
                    {
                        panierService.AjouterProduit(boissonId, 1);
                    }
                }
            }

            TempData["success"] = "Produit ajouté au panier !";
            return RedirectToAction("Index", "Panier");
        }

        [HttpGet("")]
        [HttpGet("index")]
        public IActionResult Index([FromQuery] ProduitSearchFormDto? search, int page = 1)
        {
            search ??= new ProduitSearchFormDto();

            // Filtrer uniquement burgers et menus
            if (!search.TypeProduit.HasValue)
            {
                // On veut les burgers et menus
                // Dans ce cas, le service ne supporte qu'un seul type, donc on laisse null pour récupérer tous et filtrer dans la requête SQL
            }

            var (items, totalCount) = _produitService.Search(search, page, LimitParPage);

            // On ne fait plus de Where ici ! sinon la pagination est fausse
            var vm = new CatalogueViewModel
            {
                Produits = ProduitListDto.FromEntities(items),
                Search = search,
                PageEncours = page,
                NbrePage = (int)Math.Ceiling(totalCount / (double)LimitParPage)
            };

            return View(vm);
        }

        [HttpGet("burgers")]
        public IActionResult Burgers(int page = 1)
        {
            var search = new ProduitSearchFormDto { TypeProduit = TypeProduit.BURGER };
            var (items, totalCount) = _produitService.Search(search, page, LimitParPage);

            var vm = new CatalogueViewModel
            {
                Produits = ProduitListDto.FromEntities(items),
                Search = search,
                TypeFilter = TypeProduit.BURGER,
                PageEncours = page,
                NbrePage = (int)Math.Ceiling(totalCount / (double)LimitParPage)
            };

            ViewData["Title"] = "Nos Burgers";
            return View("Index", vm);
        }

        [HttpGet("menus")]
        public IActionResult Menus(int page = 1)
        {
            var search = new ProduitSearchFormDto { TypeProduit = TypeProduit.MENU };
            var (items, totalCount) = _produitService.Search(search, page, LimitParPage);

            var vm = new CatalogueViewModel
            {
                Produits = ProduitListDto.FromEntities(items),
                Search = search,
                TypeFilter = TypeProduit.MENU,
                PageEncours = page,
                NbrePage = (int)Math.Ceiling(totalCount / (double)LimitParPage)
            };

            ViewData["Title"] = "Nos Menus";
            return View("Index", vm);
        }

        [HttpGet("complements")]
        public IActionResult Complements([FromQuery] TypeComplement? type, int page = 1)
        {
            var search = new ProduitSearchFormDto
            {
                TypeProduit = TypeProduit.COMPLEMENT,
                TypeComplement = type
            };
            var (items, totalCount) = _produitService.Search(search, page, LimitParPage);

            var vm = new CatalogueViewModel
            {
                Produits = ProduitListDto.FromEntities(items),
                Search = search,
                TypeFilter = TypeProduit.COMPLEMENT,
                PageEncours = page,
                NbrePage = (int)Math.Ceiling(totalCount / (double)LimitParPage)
            };

            ViewData["Title"] = "Nos Compléments";
            return View("Index", vm);
        }

        [HttpGet("detail/{id}")]
        public IActionResult Detail(int id)
        {
            var produit = _produitService.GetById(id);
            if (produit == null)
            {
                return NotFound();
            }

            // Récupérer les compléments pour les suggestions
            var boissons = _produitService.GetComplementsByType(TypeComplement.BOISSON);
            var frites = _produitService.GetComplementsByType(TypeComplement.FRITES);

            // Produits similaires
            var similaires = _produitService.GetByType(produit.TypeProduit)
                .Where(p => p.Id != id)
                .Take(4)
                .ToList();

            var vm = new ProduitDetailViewModel
            {
                Produit = ProduitListDto.FromEntity(produit),
                Boissons = ProduitListDto.FromEntities(boissons),
                Frites = ProduitListDto.FromEntities(frites),
                ProduitsSimilaires = ProduitListDto.FromEntities(similaires)
            };

            return View(vm);
        }
    }
}
