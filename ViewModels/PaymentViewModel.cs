using BrasilBurger.Models.Enums;
using System.ComponentModel.DataAnnotations;

namespace BrasilBurger.ViewModels;

public class PaymentViewModel
{
    public int CommandeId { get; set; }

    [Required]
    public ModePaiement ModePaiement { get; set; }

    [Required]
    [Display(Name = "Téléphone")]
    public string NumeroTelephone { get; set; } = string.Empty;

    // envoyé par le client, vérifié côté serveur
    public decimal Montant { get; set; }
}
