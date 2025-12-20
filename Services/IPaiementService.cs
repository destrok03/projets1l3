using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Services
{
    public interface IPaiementService
    {
        Paiement? GetByCommande(int commandeId);
        Paiement EffectuerPaiement(int commandeId, ModePaiement modePaiement, string numeroTelephone);
        bool CommandeEstPayee(int commandeId);
    }
}
