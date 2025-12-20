using BrasilBurger.Services;
using Microsoft.AspNetCore.Mvc;

namespace BrasilBurger.Controllers;

[Route("mon-compte/commandes")]
public class OrdersController : Controller
{
    private readonly ICommandeService _commandeService;

    public OrdersController(ICommandeService commandeService)
    {
        _commandeService = commandeService;
    }

    private int? ClientId => HttpContext.Session.GetInt32("ClientId");

    [HttpGet("")]
    public IActionResult Index(int page = 1)
    {
        if (ClientId == null)
            return RedirectToAction("Connexion", "Auth", new { returnUrl = "/mon-compte/commandes" });

        var commandes = _commandeService.GetByClientWithDetails(ClientId.Value);

        // Filtre statut
        string statutStr = Request.Query["statut"];
        ViewBag.StatutFilter = statutStr;
        if (!string.IsNullOrEmpty(statutStr) && Enum.TryParse<BrasilBurger.Models.Enums.StatutCommande>(statutStr, out var statut))
        {
            commandes = commandes.Where(c => c.Statut == statut).ToList();
        }

        // Filtre date
        string dateStr = Request.Query["date"];
        ViewBag.DateFilter = dateStr;
        if (!string.IsNullOrEmpty(dateStr) && DateTime.TryParse(dateStr, out var dateParsed))
        {
            commandes = commandes.Where(c => c.DateCommande.Date == dateParsed.Date).ToList();
        }

        // Pagination
        int limitParPage = 10;
        int totalCount = commandes.Count;
        var items = commandes.Skip((page - 1) * limitParPage).Take(limitParPage).ToList();

        ViewBag.PageEncours = page;
        ViewBag.NbrePage = (int)Math.Ceiling(totalCount / (double)limitParPage);

        return View(items);
    }

    [HttpGet("{id:int}")]
    public IActionResult Details(int id)
    {
        if (ClientId == null)
            return RedirectToAction("Connexion", "Auth", new { returnUrl = $"/mon-compte/commandes/{id}" });

        var cmd = _commandeService.GetByIdWithDetails(id);
        if (cmd == null || cmd.ClientId != ClientId.Value)
            return NotFound();

        return View(cmd);
    }
}
