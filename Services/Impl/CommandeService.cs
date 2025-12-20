using BrasilBurger.Data;
using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Services.Impl
{
    public class CommandeService : ICommandeService
    {
        private readonly BrasilBurgerDbContext _context;
        private readonly IProduitService _produitService;

        public CommandeService(BrasilBurgerDbContext context, IProduitService produitService)
        {
            _context = context;
            _produitService = produitService;
        }

        public Commande? GetById(int id)
        {
            return _context.Commandes
                .FirstOrDefault(c => c.Id == id);
        }

        public Commande? GetByIdWithDetails(int id)
        {
            return _context.Commandes
                .Include(c => c.Client)
                .Include(c => c.Zone)
                .Include(c => c.Quartier)
                .Include(c => c.Livreur)
                .Include(c => c.Lignes)
                    .ThenInclude(l => l.Produit)
                .Include(c => c.Paiement)
                .FirstOrDefault(c => c.Id == id);
        }

        public List<Commande> GetByClient(int clientId)
        {
            return _context.Commandes
                .Where(c => c.ClientId == clientId)
                .OrderByDescending(c => c.DateCommande)
                .ToList();
        }

        public List<Commande> GetByClientWithDetails(int clientId)
        {
            return _context.Commandes
                .Include(c => c.Zone)
                .Include(c => c.Quartier)
                .Include(c => c.Livreur)
                .Include(c => c.Lignes)
                    .ThenInclude(l => l.Produit)
                .Include(c => c.Paiement)
                .Where(c => c.ClientId == clientId)
                .OrderByDescending(c => c.DateCommande)
                .ToList();
        }

        public Commande CreerCommande(int clientId, CreerCommandeDto dto, List<PanierItemDto> items)
        {
            // Calculer les frais de livraison
            decimal fraisLivraison = 0;
            int? zoneId = null;

            if (dto.TypeLivraison == TypeLivraison.LIVRAISON && dto.QuartierId.HasValue)
            {
                var quartier = _context.Quartiers
                    .Include(q => q.Zone)
                    .FirstOrDefault(q => q.Id == dto.QuartierId);
                
                if (quartier?.Zone != null)
                {
                    fraisLivraison = quartier.Zone.PrixLivraison;
                    zoneId = quartier.ZoneId;
                }
            }

            // Créer la commande
            var commande = new Commande
            {
                ClientId = clientId,
                TypeLivraison = dto.TypeLivraison,
                QuartierId = dto.QuartierId,
                ZoneId = zoneId,
                Notes = dto.Notes,
                FraisLivraison = fraisLivraison,
                Statut = StatutCommande.EN_ATTENTE,
                DateCommande = DateTime.UtcNow,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            };

            _context.Commandes.Add(commande);
            _context.SaveChanges();

            // Ajouter les lignes de commande
            decimal montantTotal = 0;
            foreach (var item in items)
            {
                var ligne = new CommandeLigne
                {
                    CommandeId = commande.Id,
                    ProduitId = item.ProduitId,
                    Quantite = item.Quantite,
                    PrixUnitaire = item.Prix,
                    SousTotal = item.Prix * item.Quantite,
                    CreatedAt = DateTime.UtcNow
                };
                _context.CommandeLignes.Add(ligne);
                montantTotal += ligne.SousTotal;
            }

            // Mettre à jour le montant total
            commande.MontantTotal = montantTotal + fraisLivraison;
            _context.SaveChanges();

            return commande;
        }

        public Commande? AjouterLigne(int commandeId, int produitId, int quantite)
        {
            var commande = GetByIdWithDetails(commandeId);
            if (commande == null) return null;

            var produit = _produitService.GetById(produitId);
            if (produit == null) return null;

            // Vérifier si le produit existe déjà dans la commande
            var ligneExistante = commande.Lignes.FirstOrDefault(l => l.ProduitId == produitId);
            if (ligneExistante != null)
            {
                ligneExistante.Quantite += quantite;
                ligneExistante.CalculerSousTotal();
            }
            else
            {
                var ligne = new CommandeLigne
                {
                    CommandeId = commandeId,
                    ProduitId = produitId,
                    Quantite = quantite,
                    PrixUnitaire = produit.Prix,
                    CreatedAt = DateTime.UtcNow
                };
                ligne.CalculerSousTotal();
                _context.CommandeLignes.Add(ligne);
            }

            // Recalculer le total
            commande.MontantTotal = commande.CalculerTotal();
            commande.UpdatedAt = DateTime.UtcNow;
            _context.SaveChanges();

            return commande;
        }

        public bool SupprimerLigne(int ligneId)
        {
            var ligne = _context.CommandeLignes
                .Include(l => l.Commande)
                .FirstOrDefault(l => l.Id == ligneId);
            
            if (ligne == null) return false;

            var commande = ligne.Commande;
            _context.CommandeLignes.Remove(ligne);
            _context.SaveChanges();

            // Recalculer le total
            if (commande != null)
            {
                var lignes = _context.CommandeLignes
                    .Where(l => l.CommandeId == commande.Id)
                    .ToList();
                commande.MontantTotal = lignes.Sum(l => l.SousTotal) + commande.FraisLivraison;
                commande.UpdatedAt = DateTime.UtcNow;
                _context.SaveChanges();
            }

            return true;
        }

        public Commande? ChangerStatut(int commandeId, StatutCommande statut)
        {
            var commande = GetById(commandeId);
            if (commande == null) return null;

            commande.Statut = statut;
            commande.UpdatedAt = DateTime.UtcNow;
            _context.SaveChanges();

            return commande;
        }

        public Commande? AnnulerCommande(int commandeId)
        {
            return ChangerStatut(commandeId, StatutCommande.ANNULEE);
        }

        public decimal CalculerTotal(int commandeId)
        {
            var commande = GetByIdWithDetails(commandeId);
            if (commande == null) return 0;

            return commande.CalculerTotal();
        }

        public decimal GetFraisLivraison(int? quartierId)
        {
            if (!quartierId.HasValue) return 0;

            var quartier = _context.Quartiers
                .Include(q => q.Zone)
                .FirstOrDefault(q => q.Id == quartierId);

            return quartier?.Zone?.PrixLivraison ?? 0;
        }
    }
}
