using BrasilBurger.Models;
using BrasilBurger.Models.DTO;

namespace BrasilBurger.Services
{
    public interface IClientService
    {
        Client? GetById(int id);
        Client? GetByTelephone(string telephone);
        Client? GetByEmail(string email);
        Client? Authentifier(string identifiant, string motDePasse);
        Client? AuthentifierParEmail(string email, string motDePasse);
        Client? AuthentifierParTelephone(string telephone, string motDePasse);
        Client Inscrire(InscriptionDto dto);
        Client? Update(Client client);
        bool TelephoneExiste(string telephone);
        bool EmailExiste(string email);
    }
}
