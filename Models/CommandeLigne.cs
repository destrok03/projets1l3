using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Models
{
    [Table("commande_lignes")]
    public class CommandeLigne
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("commande_id")]
        public int CommandeId { get; set; }

        [Required]
        [Column("produit_id")]
        public int ProduitId { get; set; }

        [Column("quantite")]
        public int Quantite { get; set; } = 1;

        [Column("prix_unitaire")]
        public decimal PrixUnitaire { get; set; }

        [Column("sous_total")]
        public decimal SousTotal { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }

        [ForeignKey("ProduitId")]
        public virtual Produit? Produit { get; set; }

        // Méthode pour calculer le sous-total
        public void CalculerSousTotal()
        {
            SousTotal = PrixUnitaire * Quantite;
        }
    }
}
