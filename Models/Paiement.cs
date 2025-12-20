using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models
{
    [Table("paiements")]
    public class Paiement
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("commande_id")]
        public int CommandeId { get; set; }

        [Column("date_paiement")]
        public DateTime DatePaiement { get; set; } = DateTime.UtcNow;

        [Column("montant")]
        public decimal Montant { get; set; }

        [Required]
        [Column("mode_paiement")]
        public ModePaiement ModePaiement { get; set; }

        [StringLength(100)]
        [Column("reference_transaction")]
        public string? ReferenceTransaction { get; set; }

        [StringLength(50)]
        [Column("statut")]
        public string Statut { get; set; } = "VALIDE";

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }
    }
}
