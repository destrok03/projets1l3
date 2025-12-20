using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Models
{
    [Table("livreurs")]
    public class Livreur
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required(ErrorMessage = "Le nom du livreur est obligatoire")]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le prénom du livreur est obligatoire")]
        [StringLength(100)]
        [Column("prenom")]
        public string Prenom { get; set; } = string.Empty;

        [Required(ErrorMessage = "Le téléphone est obligatoire")]
        [StringLength(20)]
        [Column("telephone")]
        public string Telephone { get; set; } = string.Empty;

        [Column("disponible")]
        public bool Disponible { get; set; } = true;

        [Column("actif")]
        public bool Actif { get; set; } = true;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        public virtual ICollection<LivreurZone> LivreurZones { get; set; } = new List<LivreurZone>();
        public virtual ICollection<Commande> Commandes { get; set; } = new List<Commande>();

        // Propriété calculée
        [NotMapped]
        public string NomComplet => $"{Prenom} {Nom}";
    }
}
