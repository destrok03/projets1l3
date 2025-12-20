using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Models
{
    [Table("livreur_zones")]
    public class LivreurZone
    {
        [Column("livreur_id")]
        public int LivreurId { get; set; }

        [Column("zone_id")]
        public int ZoneId { get; set; }

        // Navigation
        [ForeignKey("LivreurId")]
        public virtual Livreur? Livreur { get; set; }

        [ForeignKey("ZoneId")]
        public virtual Zone? Zone { get; set; }
    }
}
