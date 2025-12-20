using BrasilBurger.Models;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class CommandeListDto
    {
        public int Id { get; set; }
        public DateTime DateCommande { get; set; }
        public StatutCommande Statut { get; set; }
        public TypeLivraison TypeLivraison { get; set; }
        public decimal MontantTotal { get; set; }
        public decimal FraisLivraison { get; set; }
        public string? ClientNom { get; set; }
        public string? ZoneNom { get; set; }
        public string? QuartierNom { get; set; }
        public string? LivreurNom { get; set; }
        public bool EstPayee { get; set; }
        public int NombreProduits { get; set; }

        public static CommandeListDto FromEntity(Commande c)
        {
            return new CommandeListDto
            {
                Id = c.Id,
                DateCommande = c.DateCommande,
                Statut = c.Statut,
                TypeLivraison = c.TypeLivraison,
                MontantTotal = c.MontantTotal,
                FraisLivraison = c.FraisLivraison,
                ClientNom = c.Client?.NomComplet,
                ZoneNom = c.Zone?.Nom,
                QuartierNom = c.Quartier?.Nom,
                LivreurNom = c.Livreur?.NomComplet,
                EstPayee = c.EstPayee,
                NombreProduits = c.Lignes?.Sum(l => l.Quantite) ?? 0
            };
        }

        public static List<CommandeListDto> FromEntities(IEnumerable<Commande> commandes)
        {
            return commandes.Select(FromEntity).ToList();
        }
    }
}
