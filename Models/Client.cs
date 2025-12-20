using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Models
{
    [Table("clients")]
    public class Client
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required(ErrorMessage = "Le nom est obligatoire")]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le prénom est obligatoire")]
        [StringLength(100)]
        [Column("prenom")]
        public string Prenom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le téléphone est obligatoire")]
        [StringLength(20)]
        [RegularExpression(@"^(77|78|76|70)\d{7}$", 
            ErrorMessage = "Le numéro de téléphone doit commencer par 77, 78, 76 ou 70 et contenir 9 chiffres")]
        [Column("telephone")]
        public string Telephone { get; set; } = string.Empty;

        [StringLength(150)]
        [EmailAddress(ErrorMessage = "L'email n'est pas valide")]
        [Column("email")]
        public string? Email { get; set; }

        [Required(ErrorMessage = "Le mot de passe est obligatoire")]
        [StringLength(255)]
        [Column("mot_de_passe")]
        public string MotDePasse { get; set; } = string.Empty;

        [Column("adresse")]
        public string? Adresse { get; set; }

        [Column("quartier_id")]
        public int? QuartierId { get; set; }

        [Column("actif")]
        public bool Actif { get; set; } = true;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        [Column("updated_at")]
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        [ForeignKey("QuartierId")]
        public virtual Quartier? Quartier { get; set; }

        public virtual ICollection<Commande> Commandes { get; set; } = new List<Commande>();

        // Propriétés calculées
        [NotMapped]
        public string NomComplet => $"{Prenom} {Nom}";
    }
}
