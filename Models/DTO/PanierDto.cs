using System.ComponentModel.DataAnnotations;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class PanierDto
    {
        public List<PanierItemDto> Items { get; set; } = new List<PanierItemDto>();
        public decimal SousTotal => Items.Sum(i => i.SousTotal);
        public decimal FraisLivraison { get; set; } = 0;
        public decimal Total => SousTotal + FraisLivraison;
        public int NombreItems => Items.Sum(i => i.Quantite);
    }

    public class PanierItemDto
    {
        public int ProduitId { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string? Image { get; set; }
        public decimal Prix { get; set; }
        public int Quantite { get; set; } = 1;
        public decimal SousTotal => Prix * Quantite;
    }

    public class AjouterPanierDto
    {
        [Required]
        public int ProduitId { get; set; }
        public int Quantite { get; set; } = 1;
        public List<int>? ComplementIds { get; set; }
    }

    public class CreerCommandeDto
    {
        [Required(ErrorMessage = "Le type de service est obligatoire")]
        public TypeLivraison TypeLivraison { get; set; }

        public int? QuartierId { get; set; }
        public string? Adresse { get; set; }
        public string? Notes { get; set; }
    }

    public class PaiementFormDto
    {
        [Required]
        public int CommandeId { get; set; }

        [Required(ErrorMessage = "Le mode de paiement est obligatoire")]
        public ModePaiement ModePaiement { get; set; }

        [Required(ErrorMessage = "Le numéro de téléphone est obligatoire")]
        [RegularExpression(@"^(77|78|76|70)\d{7}$",
            ErrorMessage = "Le numéro de téléphone n'est pas valide")]
        public string NumeroTelephone { get; set; } = string.Empty;
    }
}
