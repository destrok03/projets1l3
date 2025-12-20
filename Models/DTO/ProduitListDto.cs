using BrasilBurger.Models;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class ProduitListDto
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public decimal Prix { get; set; }
        public string? Image { get; set; }
        public TypeProduit TypeProduit { get; set; }
        public string? Description { get; set; }
        public string? Ingredients { get; set; }
        public TypeComplement? TypeComplement { get; set; }
        public bool Disponible { get; set; }

        // Pour les menus
        public string? BurgerNom { get; set; }
        public string? BoissonNom { get; set; }
        public string? FritesNom { get; set; }

        public static ProduitListDto FromEntity(Produit p)
        {
            return new ProduitListDto
            {
                Id = p.Id,
                Nom = p.Nom,
                Prix = p.TypeProduit == TypeProduit.MENU ? p.PrixCalcule : p.Prix,
                Image = p.Image,
                TypeProduit = p.TypeProduit,
                Description = p.Description,
                Ingredients = p.Ingredients,
                TypeComplement = p.TypeComplement,
                Disponible = p.Disponible,
                BurgerNom = p.Burger?.Nom,
                BoissonNom = p.Boisson?.Nom,
                FritesNom = p.Frites?.Nom
            };
        }

        public static List<ProduitListDto> FromEntities(IEnumerable<Produit> produits)
        {
            return produits.Select(FromEntity).ToList();
        }
    }
}
