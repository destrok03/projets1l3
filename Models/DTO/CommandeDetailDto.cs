using BrasilBurger.Models;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Models.DTO
{
    public class CommandeDetailDto
    {
        public int Id { get; set; }
        public DateTime DateCommande { get; set; }
        public StatutCommande Statut { get; set; }
        public TypeLivraison TypeLivraison { get; set; }
        public decimal MontantTotal { get; set; }
        public decimal FraisLivraison { get; set; }
        public string? Notes { get; set; }
        public string? ClientNom { get; set; }
        public string? ClientTelephone { get; set; }
        public string? ClientAdresse { get; set; }
        public string? ZoneNom { get; set; }
        public string? QuartierNom { get; set; }
        public string? LivreurNom { get; set; }
        public string? LivreurTelephone { get; set; }
        public bool EstPayee { get; set; }
        public PaiementDto? Paiement { get; set; }
        
        public List<CommandeLigneDto> Lignes { get; set; } = new List<CommandeLigneDto>();

        public static CommandeDetailDto FromEntity(Commande c)
        {
            return new CommandeDetailDto
            {
                Id = c.Id,
                DateCommande = c.DateCommande,
                Statut = c.Statut,
                TypeLivraison = c.TypeLivraison,
                MontantTotal = c.MontantTotal,
                FraisLivraison = c.FraisLivraison,
                Notes = c.Notes,
                ClientNom = c.Client?.NomComplet,
                ClientTelephone = c.Client?.Telephone,
                ClientAdresse = c.Client?.Adresse,
                ZoneNom = c.Zone?.Nom,
                QuartierNom = c.Quartier?.Nom,
                LivreurNom = c.Livreur?.NomComplet,
                LivreurTelephone = c.Livreur?.Telephone,
                EstPayee = c.EstPayee,
                Paiement = c.Paiement != null ? PaiementDto.FromEntity(c.Paiement) : null,
                Lignes = c.Lignes?.Select(l => CommandeLigneDto.FromEntity(l)).ToList() ?? new List<CommandeLigneDto>()
            };
        }
    }

    public class CommandeLigneDto
    {
        public int Id { get; set; }
        public string ProduitNom { get; set; } = string.Empty;
        public string? ProduitImage { get; set; }
        public int Quantite { get; set; }
        public decimal PrixUnitaire { get; set; }
        public decimal SousTotal { get; set; }

        public static CommandeLigneDto FromEntity(CommandeLigne l)
        {
            return new CommandeLigneDto
            {
                Id = l.Id,
                ProduitNom = l.Produit?.Nom ?? "",
                ProduitImage = l.Produit?.Image,
                Quantite = l.Quantite,
                PrixUnitaire = l.PrixUnitaire,
                SousTotal = l.SousTotal
            };
        }
    }

    public class PaiementDto
    {
        public int Id { get; set; }
        public DateTime DatePaiement { get; set; }
        public decimal Montant { get; set; }
        public ModePaiement ModePaiement { get; set; }
        public string? ReferenceTransaction { get; set; }
        public string Statut { get; set; } = string.Empty;

        public static PaiementDto FromEntity(Paiement p)
        {
            return new PaiementDto
            {
                Id = p.Id,
                DatePaiement = p.DatePaiement,
                Montant = p.Montant,
                ModePaiement = p.ModePaiement,
                ReferenceTransaction = p.ReferenceTransaction,
                Statut = p.Statut
            };
        }
    }
}
