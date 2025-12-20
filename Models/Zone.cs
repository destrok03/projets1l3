using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Models
{
    [Table("zones")]
    public class Zone
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required(ErrorMessage = "Le nom de la zone est obligatoire")]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Column("prix_livraison")]
        public decimal PrixLivraison { get; set; } = 0.00m;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        public virtual ICollection<Quartier> Quartiers { get; set; } = new List<Quartier>();
        public virtual ICollection<LivreurZone> LivreurZones { get; set; } = new List<LivreurZone>();
    }
}
