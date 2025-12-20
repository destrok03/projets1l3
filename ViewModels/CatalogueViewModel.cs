using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.ViewModels
{
    public class CatalogueViewModel
    {
        public List<ProduitListDto> Produits { get; set; } = new List<ProduitListDto>();
        public ProduitSearchFormDto? Search { get; set; }
        public int PageEncours { get; set; } = 1;
        public int NbrePage { get; set; }
        public TypeProduit? TypeFilter { get; set; }

        // Produits regroupés
        public List<ProduitListDto> Burgers => Produits.Where(p => p.TypeProduit == TypeProduit.BURGER).ToList();
        public List<ProduitListDto> Menus => Produits.Where(p => p.TypeProduit == TypeProduit.MENU).ToList();
        public List<ProduitListDto> Complements => Produits.Where(p => p.TypeProduit == TypeProduit.COMPLEMENT).ToList();
    }

}
