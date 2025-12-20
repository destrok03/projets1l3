using BrasilBurger.Models.DTO;
using System.Text.Json;

namespace BrasilBurger.Services.Impl
{
    public class PanierService : IPanierService
    {
        private readonly IHttpContextAccessor _httpContextAccessor;
        private readonly IProduitService _produitService;
        private const string PANIER_SESSION_KEY = "Panier";
        private const string PANIER_COUNT_KEY = "PanierCount";

        public PanierService(IHttpContextAccessor httpContextAccessor, IProduitService produitService)
        {
            _httpContextAccessor = httpContextAccessor;
            _produitService = produitService;
        }

        private ISession Session => _httpContextAccessor.HttpContext!.Session;

        public PanierDto GetPanier()
        {
            var panierJson = Session.GetString(PANIER_SESSION_KEY);
            if (string.IsNullOrEmpty(panierJson))
            {
                return new PanierDto();
            }
            return JsonSerializer.Deserialize<PanierDto>(panierJson) ?? new PanierDto();
        }

        private void SavePanier(PanierDto panier)
        {
            var panierJson = JsonSerializer.Serialize(panier);
            Session.SetString(PANIER_SESSION_KEY, panierJson);
            // Mettre à jour le compteur dans la session
            Session.SetString(PANIER_COUNT_KEY, panier.NombreItems.ToString());
        }

        public void AjouterProduit(int produitId, int quantite = 1)
        {
            var produit = _produitService.GetById(produitId);
            if (produit == null) return;

            var panier = GetPanier();
            var itemExistant = panier.Items.FirstOrDefault(i => i.ProduitId == produitId);

            if (itemExistant != null)
            {
                itemExistant.Quantite += quantite;
            }
            else
            {
                panier.Items.Add(new PanierItemDto
                {
                    ProduitId = produit.Id,
                    Nom = produit.Nom,
                    Image = produit.Image,
                    Prix = produit.TypeProduit == Models.Enums.TypeProduit.MENU ? produit.PrixCalcule : produit.Prix,
                    Quantite = quantite
                });
            }

            SavePanier(panier);
        }

        public void ModifierQuantite(int produitId, int quantite)
        {
            var panier = GetPanier();
            var item = panier.Items.FirstOrDefault(i => i.ProduitId == produitId);

            if (item != null)
            {
                if (quantite <= 0)
                {
                    panier.Items.Remove(item);
                }
                else
                {
                    item.Quantite = quantite;
                }
                SavePanier(panier);
            }
        }

        public void SupprimerProduit(int produitId)
        {
            var panier = GetPanier();
            var item = panier.Items.FirstOrDefault(i => i.ProduitId == produitId);

            if (item != null)
            {
                panier.Items.Remove(item);
                SavePanier(panier);
            }
        }

        public void ViderPanier()
        {
            Session.Remove(PANIER_SESSION_KEY);
            Session.SetString(PANIER_COUNT_KEY, "0");
        }

        public void SetFraisLivraison(decimal frais)
        {
            var panier = GetPanier();
            panier.FraisLivraison = frais;
            SavePanier(panier);
        }

        public int GetNombreItems()
        {
            return GetPanier().NombreItems;
        }
    }
}
