using BrasilBurger.Models;

namespace BrasilBurger.Models.DTO
{
    public class ClientDto
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Prenom { get; set; } = string.Empty;
        public string Telephone { get; set; } = string.Empty;
        public string? Email { get; set; }
        public string? Adresse { get; set; }
        public string? QuartierNom { get; set; }
        public string? ZoneNom { get; set; }

        public string NomComplet => $"{Prenom} {Nom}";

        public static ClientDto FromEntity(Client c)
        {
            return new ClientDto
            {
                Id = c.Id,
                Nom = c.Nom,
                Prenom = c.Prenom,
                Telephone = c.Telephone,
                Email = c.Email,
                Adresse = c.Adresse,
                QuartierNom = c.Quartier?.Nom,
                ZoneNom = c.Quartier?.Zone?.Nom
            };
        }
    }
}
