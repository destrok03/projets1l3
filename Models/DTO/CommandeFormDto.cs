using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class CommandeFormDto
    {
        public TypeLivraison TypeLivraison { get; set; } = TypeLivraison.SUR_PLACE;
        public int? QuartierId { get; set; }
        public string Adresse { get; set; } = string.Empty;
        public string Notes { get; set; } = string.Empty;

        // Total avec frais de livraison
        public decimal MontantTotal { get; set; } = 0;
    }
}