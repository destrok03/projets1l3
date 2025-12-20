using BrasilBurger.Data;
using BrasilBurger.Models;
using BrasilBurger.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Services.Impl
{
    public class PaiementService : IPaiementService
    {
        private readonly BrasilBurgerDbContext _context;
        private readonly ICommandeService _commandeService;

        public PaiementService(BrasilBurgerDbContext context, ICommandeService commandeService)
        {
            _context = context;
            _commandeService = commandeService;
        }

        public Paiement? GetByCommande(int commandeId)
        {
            return _context.Paiements
                .FirstOrDefault(p => p.CommandeId == commandeId);
        }

        public Paiement EffectuerPaiement(int commandeId, ModePaiement modePaiement, string numeroTelephone)
        {
            var commande = _commandeService.GetByIdWithDetails(commandeId);
            if (commande == null)
            {
                throw new Exception("Commande non trouvée");
            }

            // Vérifier si la commande n'est pas déjà payée
            if (commande.Paiement != null)
            {
                throw new Exception("Cette commande est déjà payée");
            }

            // Simuler l'appel à l'API de paiement (Wave ou Orange Money)
            string referenceTransaction = GenererReferenceTransaction(modePaiement);

            // Créer le paiement
            var paiement = new Paiement
            {
                CommandeId = commandeId,
                Montant = commande.MontantTotal,
                ModePaiement = modePaiement,
                ReferenceTransaction = referenceTransaction,
                Statut = "VALIDE",
                DatePaiement = DateTime.UtcNow,
                CreatedAt = DateTime.UtcNow
            };

            _context.Paiements.Add(paiement);

            // Mettre à jour le statut de la commande
            commande.Statut = StatutCommande.VALIDEE;
            commande.UpdatedAt = DateTime.UtcNow;

            _context.SaveChanges();

            return paiement;
        }

        public bool CommandeEstPayee(int commandeId)
        {
            return _context.Paiements
                .Any(p => p.CommandeId == commandeId && p.Statut == "VALIDE");
        }

        private string GenererReferenceTransaction(ModePaiement mode)
        {
            string prefix = mode == ModePaiement.WAVE ? "WAV" : 
                           mode == ModePaiement.ORANGE_MONEY ? "OM" : "ESP";
            return $"{prefix}-{DateTime.UtcNow:yyyyMMddHHmmss}-{Guid.NewGuid().ToString().Substring(0, 8).ToUpper()}";
        }
    }
}
