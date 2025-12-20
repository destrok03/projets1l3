using BrasilBurger.Models;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using BrasilBurger.Models.DTO;
using Microsoft.AspNetCore.Mvc;
using System.Diagnostics;

namespace BrasilBurger.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly IProduitService _produitService;

        public HomeController(ILogger<HomeController> logger, IProduitService produitService)
        {
            _logger = logger;
            _produitService = produitService;
        }

        public IActionResult Index()
        {
            // Récupérer les produits pour la page d'accueil
            var burgers = _produitService.GetBurgers().Take(4).ToList();
            var menus = _produitService.GetMenus().Take(4).ToList();

            var vm = new CatalogueViewModel
            {
                Produits = ProduitListDto.FromEntities(burgers.Concat(menus))
            };

            return View(vm);
        }

        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
