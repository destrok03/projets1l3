using System.ComponentModel.DataAnnotations;

namespace BrasilBurger.Models.DTO
{
    public class InscriptionDto
    {
        [Required(ErrorMessage = "Le nom est obligatoire")]
        [StringLength(100)]
        public string Nom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le prénom est obligatoire")]
        [StringLength(100)]
        public string Prenom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le téléphone est obligatoire")]
        [RegularExpression(@"^(77|78|76|70)\d{7}$",
            ErrorMessage = "Le numéro de téléphone doit commencer par 77, 78, 76 ou 70 et contenir 9 chiffres")]
        public string Telephone { get; set; } = string.Empty;

        [EmailAddress(ErrorMessage = "L'email n'est pas valide")]
        public string? Email { get; set; }

        [Required(ErrorMessage = "Le mot de passe est obligatoire")]
        [StringLength(100, MinimumLength = 6, ErrorMessage = "Le mot de passe doit contenir au moins 6 caractères")]
        public string MotDePasse { get; set; } = string.Empty;

        [Required(ErrorMessage = "La confirmation du mot de passe est obligatoire")]
        [Compare("MotDePasse", ErrorMessage = "Les mots de passe ne correspondent pas")]
        public string ConfirmMotDePasse { get; set; } = string.Empty;

        public string? Adresse { get; set; }
        public int? QuartierId { get; set; }
    }

    public class ConnexionDto
    {
        [Required(ErrorMessage = "L'identifiant est obligatoire")]
        public string Identifiant { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le mot de passe est obligatoire")]
        public string MotDePasse { get; set; } = string.Empty;
    }
}
