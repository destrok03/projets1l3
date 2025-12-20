using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.ViewModels
{
    public class CommandeListViewModel
    {
        public List<CommandeListDto> Commandes { get; set; } = new List<CommandeListDto>();
        public int PageEncours { get; set; } = 1;
        public int NbrePage { get; set; }
        public StatutCommande? StatutFilter { get; set; }
    }

    public class CommandeDetailViewModel
    {
        public CommandeDetailDto Commande { get; set; } = null!;
    }

    public class PasserCommandeViewModel
    {
        public PanierDto Panier { get; set; } = new PanierDto();
        public CreerCommandeDto CommandeForm { get; set; } = new CreerCommandeDto();
        public List<Zone> Zones { get; set; } = new List<Zone>();
        public List<Quartier> Quartiers { get; set; } = new List<Quartier>();
    }

    public class PaiementViewModel
    {
        public Commande Commande { get; set; } = null!;
        public PaiementFormDto PaiementForm { get; set; } = new PaiementFormDto();
    }
}
