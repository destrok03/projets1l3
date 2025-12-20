using BrasilBurger.Models.DTO;

namespace BrasilBurger.ViewModels
{
    public class PanierViewModel
    {
        public PanierDto Panier { get; set; } = new PanierDto();
        public bool PeutCommander => Panier.NombreItems > 0;
    }
}
