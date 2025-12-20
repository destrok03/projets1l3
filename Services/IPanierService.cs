using BrasilBurger.Models.DTO;

namespace BrasilBurger.Services
{
    public interface IPanierService
    {
        PanierDto GetPanier();
        void AjouterProduit(int produitId, int quantite = 1);
        void ModifierQuantite(int produitId, int quantite);
        void SupprimerProduit(int produitId);
        void ViderPanier();
        void SetFraisLivraison(decimal frais);
        int GetNombreItems();
    }
}
