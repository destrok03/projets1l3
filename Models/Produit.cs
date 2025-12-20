using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models
{
    [Table("produits")]
    public class Produit
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required(ErrorMessage = "Le nom du produit est obligatoire")]
        [StringLength(150)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Column("prix")]
        public decimal Prix { get; set; } = 0.00m;

        [StringLength(500)]
        [Column("image")]
        public string? Image { get; set; }

        [Required]
        [Column("type_produit")]
        public TypeProduit TypeProduit { get; set; }

        [Column("description")]
        public string? Description { get; set; }

        [Column("ingredients")]
        public string? Ingredients { get; set; }

        [Column("type_complement")]
        public TypeComplement? TypeComplement { get; set; }

        // Pour les menus : références aux composants
        [Column("burger_id")]
        public int? BurgerId { get; set; }

        [Column("boisson_id")]
        public int? BoissonId { get; set; }

        [Column("frites_id")]
        public int? FritesId { get; set; }

        [Column("archived")]
        public bool Archived { get; set; } = false;

        [Column("disponible")]
        public bool Disponible { get; set; } = true;

        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        [Column("updated_at")]
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        // Navigation pour les menus
        [ForeignKey("BurgerId")]
        public virtual Produit? Burger { get; set; }

        [ForeignKey("BoissonId")]
        public virtual Produit? Boisson { get; set; }

        [ForeignKey("FritesId")]
        public virtual Produit? Frites { get; set; }

        // Méthode pour calculer le prix d'un menu
        [NotMapped]
        public decimal PrixCalcule
        {
            get
            {
                if (TypeProduit == TypeProduit.MENU)
                {
                    decimal total = 0;
                    if (Burger != null) total += Burger.Prix;
                    if (Boisson != null) total += Boisson.Prix;
                    if (Frites != null) total += Frites.Prix;
                    return total;
                }
                return Prix;
            }
        }
    }
}
