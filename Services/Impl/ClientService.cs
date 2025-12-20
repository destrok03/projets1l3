using BrasilBurger.Data;
using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Services.Impl
{
    public class ClientService : IClientService
    {
        private readonly BrasilBurgerDbContext _context;

        public ClientService(BrasilBurgerDbContext context)
        {
            _context = context;
        }

        public Client? GetById(int id)
        {
            return _context.Clients
                .Include(c => c.Quartier)
                .ThenInclude(q => q!.Zone)
                .FirstOrDefault(c => c.Id == id && c.Actif);
        }

        public Client? GetByTelephone(string telephone)
        {
            return _context.Clients
                .Include(c => c.Quartier)
                .ThenInclude(q => q!.Zone)
                .FirstOrDefault(c => c.Telephone == telephone && c.Actif);
        }

        public Client? GetByEmail(string email)
        {
            return _context.Clients
                .Include(c => c.Quartier)
                .ThenInclude(q => q!.Zone)
                .FirstOrDefault(c => c.Email == email && c.Actif);
        }

        public Client? Authentifier(string identifiant, string motDePasse)
        {
            // Essayer d'abord par email, puis par téléphone
            var client = GetByEmail(identifiant) ?? GetByTelephone(identifiant);
            if (client == null) return null;

            // Vérifier le mot de passe avec BCrypt
            if (!BCrypt.Net.BCrypt.Verify(motDePasse, client.MotDePasse))
            {
                return null;
            }

            return client;
        }

        public Client? AuthentifierParEmail(string email, string motDePasse)
        {
            var client = GetByEmail(email);
            if (client == null) return null;

            if (!BCrypt.Net.BCrypt.Verify(motDePasse, client.MotDePasse))
            {
                return null;
            }

            return client;
        }

        public Client? AuthentifierParTelephone(string telephone, string motDePasse)
        {
            var client = GetByTelephone(telephone);
            if (client == null) return null;

            if (!BCrypt.Net.BCrypt.Verify(motDePasse, client.MotDePasse))
            {
                return null;
            }

            return client;
        }

        public Client Inscrire(InscriptionDto dto)
        {
            var client = new Client
            {
                Nom = dto.Nom,
                Prenom = dto.Prenom,
                Telephone = dto.Telephone,
                Email = dto.Email,
                MotDePasse = BCrypt.Net.BCrypt.HashPassword(dto.MotDePasse),
                Adresse = dto.Adresse,
                QuartierId = dto.QuartierId,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            };

            _context.Clients.Add(client);
            _context.SaveChanges();

            return client;
        }

        public Client? Update(Client client)
        {
            var existing = _context.Clients.Find(client.Id);
            if (existing == null) return null;

            existing.Nom = client.Nom;
            existing.Prenom = client.Prenom;
            existing.Email = client.Email;
            existing.Adresse = client.Adresse;
            existing.QuartierId = client.QuartierId;
            existing.UpdatedAt = DateTime.UtcNow;

            _context.SaveChanges();
            return existing;
        }

        public bool TelephoneExiste(string telephone)
        {
            return _context.Clients.Any(c => c.Telephone == telephone);
        }

        public bool EmailExiste(string email)
        {
            return _context.Clients.Any(c => c.Email == email);
        }
    }
}
