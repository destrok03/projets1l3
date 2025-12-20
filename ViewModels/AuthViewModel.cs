using BrasilBurger.Models;
using BrasilBurger.Models.DTO;

namespace BrasilBurger.ViewModels
{
    public class InscriptionViewModel
    {
        public InscriptionDto Form { get; set; } = new InscriptionDto();
        public List<Zone> Zones { get; set; } = new List<Zone>();
        public List<Quartier> Quartiers { get; set; } = new List<Quartier>();
    }

    public class ConnexionViewModel
    {
        public ConnexionDto Form { get; set; } = new ConnexionDto();
        public string? ReturnUrl { get; set; }
    }

    public class ProfilViewModel
    {
        public ClientDto Client { get; set; } = null!;
        public List<CommandeListDto> DernieresCommandes { get; set; } = new List<CommandeListDto>();
    }
}
