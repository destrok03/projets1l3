using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class ProduitSearchFormDto
    {
        public TypeProduit? TypeProduit { get; set; }
        public TypeComplement? TypeComplement { get; set; }
        public string? Nom { get; set; }
        public decimal? PrixMin { get; set; }
        public decimal? PrixMax { get; set; }
        public bool? Disponible { get; set; }
    }
}
