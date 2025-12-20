using BrasilBurger.Models;
using BrasilBurger.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Data
{
    public class BrasilBurgerDbContext : DbContext
    {
        public BrasilBurgerDbContext(DbContextOptions<BrasilBurgerDbContext> options)
            : base(options)
        {
        }

        public DbSet<Zone> Zones { get; set; } = null!;
        public DbSet<Quartier> Quartiers { get; set; } = null!;
        public DbSet<Livreur> Livreurs { get; set; } = null!;
        public DbSet<LivreurZone> LivreurZones { get; set; } = null!;
        public DbSet<Produit> Produits { get; set; } = null!;
        public DbSet<Client> Clients { get; set; } = null!;
        public DbSet<Commande> Commandes { get; set; } = null!;
        public DbSet<CommandeLigne> CommandeLignes { get; set; } = null!;
        public DbSet<Paiement> Paiements { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Les enums sont maintenant mappés via NpgsqlDataSourceBuilder dans Program.cs
            // Donc on supprime les conversions string et on utilise les enums PostgreSQL natifs
            modelBuilder.HasPostgresEnum<TypeProduit>("type_produit_enum");
            modelBuilder.HasPostgresEnum<TypeComplement>("type_complement_enum");
            modelBuilder.HasPostgresEnum<StatutCommande>("statut_commande_enum");
            modelBuilder.HasPostgresEnum<TypeLivraison>("type_livraison_enum");
            modelBuilder.HasPostgresEnum<ModePaiement>("mode_paiement_enum");

            // === Zone ===
            modelBuilder.Entity<Zone>(entity =>
            {
                entity.HasKey(z => z.Id);
                entity.HasIndex(z => z.Nom).IsUnique();
            });

            // === Quartier ===
            modelBuilder.Entity<Quartier>(entity =>
            {
                entity.HasKey(q => q.Id);
                entity.HasOne(q => q.Zone)
                      .WithMany(z => z.Quartiers)
                      .HasForeignKey(q => q.ZoneId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // === Livreur ===
            modelBuilder.Entity<Livreur>(entity =>
            {
                entity.HasKey(l => l.Id);
                entity.HasIndex(l => l.Telephone).IsUnique();
            });

            // === LivreurZone ===
            modelBuilder.Entity<LivreurZone>(entity =>
            {
                entity.HasKey(lz => new { lz.LivreurId, lz.ZoneId });
                
                entity.HasOne(lz => lz.Livreur)
                      .WithMany(l => l.LivreurZones)
                      .HasForeignKey(lz => lz.LivreurId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(lz => lz.Zone)
                      .WithMany(z => z.LivreurZones)
                      .HasForeignKey(lz => lz.ZoneId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // === Produit ===
            modelBuilder.Entity<Produit>(entity =>
            {
                entity.HasKey(p => p.Id);
                
                entity.HasOne(p => p.Burger)
                      .WithMany()
                      .HasForeignKey(p => p.BurgerId)
                      .OnDelete(DeleteBehavior.SetNull);

                entity.HasOne(p => p.Boisson)
                      .WithMany()
                      .HasForeignKey(p => p.BoissonId)
                      .OnDelete(DeleteBehavior.SetNull);

                entity.HasOne(p => p.Frites)
                      .WithMany()
                      .HasForeignKey(p => p.FritesId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // === Client ===
            modelBuilder.Entity<Client>(entity =>
            {
                entity.HasKey(c => c.Id);
                entity.HasIndex(c => c.Telephone).IsUnique();
                entity.HasIndex(c => c.Email).IsUnique();

                entity.HasOne(c => c.Quartier)
                      .WithMany()
                      .HasForeignKey(c => c.QuartierId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // === Commande ===
            modelBuilder.Entity<Commande>(entity =>
            {
                entity.HasKey(c => c.Id);

                entity.HasOne(c => c.Client)
                      .WithMany(cl => cl.Commandes)
                      .HasForeignKey(c => c.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(c => c.Quartier)
                      .WithMany()
                      .HasForeignKey(c => c.QuartierId)
                      .OnDelete(DeleteBehavior.SetNull);

                entity.HasOne(c => c.Zone)
                      .WithMany()
                      .HasForeignKey(c => c.ZoneId)
                      .OnDelete(DeleteBehavior.SetNull);

                entity.HasOne(c => c.Livreur)
                      .WithMany(l => l.Commandes)
                      .HasForeignKey(c => c.LivreurId)
                      .OnDelete(DeleteBehavior.SetNull);
            });

            // === CommandeLigne ===
            modelBuilder.Entity<CommandeLigne>(entity =>
            {
                entity.HasKey(cl => cl.Id);

                entity.HasOne(cl => cl.Commande)
                      .WithMany(c => c.Lignes)
                      .HasForeignKey(cl => cl.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(cl => cl.Produit)
                      .WithMany()
                      .HasForeignKey(cl => cl.ProduitId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // === Paiement ===
            modelBuilder.Entity<Paiement>(entity =>
            {
                entity.HasKey(p => p.Id);
                entity.HasIndex(p => p.CommandeId).IsUnique();
                entity.HasIndex(p => p.ReferenceTransaction).IsUnique();

                entity.HasOne(p => p.Commande)
                      .WithOne(c => c.Paiement)
                      .HasForeignKey<Paiement>(p => p.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
            });
        }
    }
}
