using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;

namespace BrasilBurger.Services
{
    public interface IProduitService
    {
        List<Produit> GetAll();
        List<Produit> GetAllDisponibles();
        Produit? GetById(int id);
        List<Produit> GetByType(TypeProduit type);
        List<Produit> GetBurgers();
        List<Produit> GetMenus();
        List<Produit> GetComplements();
        List<Produit> GetComplementsByType(TypeComplement type);
        (List<Produit> Items, int TotalCount) Search(ProduitSearchFormDto filter, int page, int pageSize);
    }
}
