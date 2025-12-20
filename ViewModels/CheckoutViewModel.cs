using BrasilBurger.Models.Enums;
using System.ComponentModel.DataAnnotations;

namespace BrasilBurger.ViewModels;

public class CheckoutViewModel
{
    [Required]
    public TypeLivraison TypeLivraison { get; set; }

    public int? QuartierId { get; set; }

    [Display(Name = "Adresse de livraison")]
    public string? AdresseLivraison { get; set; }

    [Display(Name = "Notes")]
    public string? Notes { get; set; }

    // Récap
    public decimal SousTotal { get; set; }
    public decimal FraisLivraison { get; set; }
    public decimal Total { get; set; }
}
