using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Services
{
    public interface ICommandeService
    {
        Commande? GetById(int id);
        Commande? GetByIdWithDetails(int id);
        List<Commande> GetByClient(int clientId);
        List<Commande> GetByClientWithDetails(int clientId);
        Commande CreerCommande(int clientId, CreerCommandeDto dto, List<PanierItemDto> items);
        Commande? AjouterLigne(int commandeId, int produitId, int quantite);
        bool SupprimerLigne(int ligneId);
        Commande? ChangerStatut(int commandeId, StatutCommande statut);
        Commande? AnnulerCommande(int commandeId);
        decimal CalculerTotal(int commandeId);
        decimal GetFraisLivraison(int? quartierId);
    }
}
