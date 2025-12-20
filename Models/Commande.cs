using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models
{
    [Table("commandes")]
    public class Commande
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("client_id")]
        public int ClientId { get; set; }

        [Column("date_commande")]
        public DateTime DateCommande { get; set; } = DateTime.UtcNow;

        [Column("statut")]
        public StatutCommande Statut { get; set; } = StatutCommande.EN_ATTENTE;

        [Required]
        [Column("type_livraison")]
        public TypeLivraison TypeLivraison { get; set; }

        [Column("quartier_id")]
        public int? QuartierId { get; set; }

        [Column("zone_id")]
        public int? ZoneId { get; set; }

        [Column("livreur_id")]
        public int? LivreurId { get; set; }

        [Column("montant_total")]
        public decimal MontantTotal { get; set; } = 0.00m;

        [Column("frais_livraison")]
        public decimal FraisLivraison { get; set; } = 0.00m;

        [Column("notes")]
        public string? Notes { get; set; }

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        [Column("updated_at")]
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation
        [ForeignKey("ClientId")]
        public virtual Client? Client { get; set; }

        [ForeignKey("QuartierId")]
        public virtual Quartier? Quartier { get; set; }

        [ForeignKey("ZoneId")]
        public virtual Zone? Zone { get; set; }

        [ForeignKey("LivreurId")]
        public virtual Livreur? Livreur { get; set; }

        public virtual ICollection<CommandeLigne> Lignes { get; set; } = new List<CommandeLigne>();
        public virtual Paiement? Paiement { get; set; }

        // Méthode pour calculer le total
        public decimal CalculerTotal()
        {
            decimal total = 0;
            foreach (var ligne in Lignes)
            {
                total += ligne.SousTotal;
            }
            return total + FraisLivraison;
        }

        // Propriété pour vérifier si la commande est payée
        [NotMapped]
        public bool EstPayee => Paiement != null && Paiement.Statut == "VALIDE";
    }
}
