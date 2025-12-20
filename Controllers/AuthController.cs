using BrasilBurger.Models.DTO;
using BrasilBurger.Services;
using BrasilBurger.ViewModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;

namespace BrasilBurger.Controllers
{
    [Route("auth")]
    public class AuthController : Controller
    {
        private readonly IClientService _clientService;
        private readonly IZoneService _zoneService;

        public AuthController(IClientService clientService, IZoneService zoneService)
        {
            _clientService = clientService;
            _zoneService = zoneService;
        }

        [HttpGet("connexion")]
        public IActionResult Connexion(string? returnUrl)
        {
            // Si déjà connecté, rediriger
            if (HttpContext.Session.GetInt32("ClientId") != null)
            {
                return RedirectToAction("Profil");
            }

            var vm = new ConnexionViewModel
            {
                ReturnUrl = returnUrl
            };
            return View(vm);
        }

        [HttpPost("connexion")]
        public IActionResult Connexion(ConnexionViewModel vm)
        {
            if (!ModelState.IsValid)
            {
                return View(vm);
            }

            var client = _clientService.Authentifier(vm.Form.Identifiant, vm.Form.MotDePasse);
            if (client == null)
            {
                ModelState.AddModelError("", "Email/Téléphone ou mot de passe incorrect");
                return View(vm);
            }

            // Stocker les informations dans la session
            HttpContext.Session.SetInt32("ClientId", client.Id);
            HttpContext.Session.SetString("ClientNom", client.NomComplet);
            HttpContext.Session.SetString("ClientTelephone", client.Telephone);

            TempData["success"] = $"Bienvenue {client.Prenom} !";

            // Rediriger vers la page précédente ou l'accueil
            if (!string.IsNullOrEmpty(vm.ReturnUrl) && Url.IsLocalUrl(vm.ReturnUrl))
            {
                return Redirect(vm.ReturnUrl);
            }

            return RedirectToAction("Index", "Home");
        }

        [HttpGet("inscription")]
        public IActionResult Inscription()
        {
            var vm = new InscriptionViewModel
            {
                Zones = _zoneService.GetAll(),
                Quartiers = _zoneService.GetAllQuartiers()
            };

            PrepareSelectLists(vm);
            return View(vm);
        }

        [HttpPost("inscription")]
        public IActionResult Inscription(InscriptionViewModel vm)
        {
            if (!ModelState.IsValid)
            {
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }

            // Vérifier si le téléphone existe déjà
            if (_clientService.TelephoneExiste(vm.Form.Telephone))
            {
                ModelState.AddModelError("Form.Telephone", "Ce numéro de téléphone est déjà utilisé");
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }

            // Vérifier si l'email existe déjà
            if (!string.IsNullOrEmpty(vm.Form.Email) && _clientService.EmailExiste(vm.Form.Email))
            {
                ModelState.AddModelError("Form.Email", "Cet email est déjà utilisé");
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }

            try
            {
                var client = _clientService.Inscrire(vm.Form);

                // Connecter automatiquement après inscription
                HttpContext.Session.SetInt32("ClientId", client.Id);
                HttpContext.Session.SetString("ClientNom", client.NomComplet);
                HttpContext.Session.SetString("ClientTelephone", client.Telephone);

                TempData["success"] = "Inscription réussie ! Bienvenue chez Brasil Burger.";
                return RedirectToAction("Index", "Home");
            }
            catch (Exception ex)
            {
                ModelState.AddModelError("", "Une erreur est survenue lors de l'inscription");
                vm.Zones = _zoneService.GetAll();
                vm.Quartiers = _zoneService.GetAllQuartiers();
                PrepareSelectLists(vm);
                return View(vm);
            }
        }

        [HttpGet("deconnexion")]
        [HttpPost("deconnexion")]
        public IActionResult Deconnexion()
        {
            HttpContext.Session.Clear();
            TempData["success"] = "Vous êtes déconnecté";
            return RedirectToAction("Index", "Home");
        }

        [HttpGet("profil")]
        public IActionResult Profil()
        {
            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Connexion", new { returnUrl = "/auth/profil" });
            }

            var client = _clientService.GetById(clientId.Value);
            if (client == null)
            {
                HttpContext.Session.Clear();
                return RedirectToAction("Connexion");
            }

            var vm = new ProfilViewModel
            {
                Client = ClientDto.FromEntity(client)
            };

            return View(vm);
        }

        private void PrepareSelectLists(InscriptionViewModel vm)
        {
            ViewBag.Quartiers = new SelectList(vm.Quartiers, "Id", "Nom", vm.Form.QuartierId);
        }

        // API pour récupérer les quartiers par zone (AJAX)
        [HttpGet("quartiers/{zoneId}")]
        public IActionResult GetQuartiersByZone(int zoneId)
        {
            var quartiers = _zoneService.GetQuartiersByZone(zoneId);
            return Json(quartiers.Select(q => new { q.Id, q.Nom }));
        }
    }
}
