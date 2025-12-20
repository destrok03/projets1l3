using BrasilBurger.Data;
using BrasilBurger.Models;
using BrasilBurger.Models.DTO;
using BrasilBurger.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Services.Impl
{
    public class ProduitService : IProduitService
    {
        private readonly BrasilBurgerDbContext _context;

        public ProduitService(BrasilBurgerDbContext context)
        {
            _context = context;
        }

        public List<Produit> GetAll()
        {
            return _context.Produits
                .Include(p => p.Burger)
                .Include(p => p.Boisson)
                .Include(p => p.Frites)
                .Where(p => !p.Archived)
                .OrderBy(p => p.TypeProduit)
                .ThenBy(p => p.Nom)
                .ToList();
        }

        public List<Produit> GetAllDisponibles()
        {
            return _context.Produits
                .Include(p => p.Burger)
                .Include(p => p.Boisson)
                .Include(p => p.Frites)
                .Where(p => !p.Archived && p.Disponible)
                .OrderBy(p => p.TypeProduit)
                .ThenBy(p => p.Nom)
                .ToList();
        }

        public Produit? GetById(int id)
        {
            return _context.Produits
                .Include(p => p.Burger)
                .Include(p => p.Boisson)
                .Include(p => p.Frites)
                .FirstOrDefault(p => p.Id == id && !p.Archived);
        }

        public List<Produit> GetByType(TypeProduit type)
        {
            return _context.Produits
                .Include(p => p.Burger)
                .Include(p => p.Boisson)
                .Include(p => p.Frites)
                .Where(p => !p.Archived && p.Disponible && p.TypeProduit == type)
                .OrderBy(p => p.Nom)
                .ToList();
        }

        public List<Produit> GetBurgers()
        {
            return GetByType(TypeProduit.BURGER);
        }

        public List<Produit> GetMenus()
        {
            return GetByType(TypeProduit.MENU);
        }

        public List<Produit> GetComplements()
        {
            return GetByType(TypeProduit.COMPLEMENT);
        }

        public List<Produit> GetComplementsByType(TypeComplement type)
        {
            return _context.Produits
                .Where(p => !p.Archived && p.Disponible &&
                       p.TypeProduit == TypeProduit.COMPLEMENT &&
                       p.TypeComplement == type)
                .OrderBy(p => p.Nom)
                .ToList();
        }

        public (List<Produit> Items, int TotalCount) Search(ProduitSearchFormDto filter, int page, int pageSize)
        {
            var query = _context.Produits
                .Include(p => p.Burger)
                .Include(p => p.Boisson)
                .Include(p => p.Frites)
                .Where(p => !p.Archived)
                .AsQueryable();

            // Par ceci pour gérer les menus et burgers sans casser la pagination
            if (filter.TypeProduit.HasValue)
            {
                query = query.Where(p => p.TypeProduit == filter.TypeProduit.Value);
            }
            else
            {
                // Par défaut, si aucun type spécifié, on veut burgers + menus
                query = query.Where(p => p.TypeProduit == TypeProduit.BURGER || p.TypeProduit == TypeProduit.MENU);
            }

            // Filtre par type de complément
            if (filter.TypeComplement.HasValue)
            {
                query = query.Where(p => p.TypeComplement == filter.TypeComplement.Value);
            }

            // Filtre par nom
            if (!string.IsNullOrWhiteSpace(filter.Nom))
            {
                query = query.Where(p => p.Nom.ToLower().Contains(filter.Nom.ToLower()));
            }

            // Filtre par prix min
            if (filter.PrixMin.HasValue)
            {
                query = query.Where(p => p.Prix >= filter.PrixMin.Value);
            }

            // Filtre par prix max
            if (filter.PrixMax.HasValue)
            {
                query = query.Where(p => p.Prix <= filter.PrixMax.Value);
            }

            // Filtre par disponibilité
            if (filter.Disponible.HasValue)
            {
                query = query.Where(p => p.Disponible == filter.Disponible.Value);
            }
            else
            {
                // Par défaut, afficher seulement les disponibles
                query = query.Where(p => p.Disponible);
            }

            var totalCount = query.Count();

            var items = query
                .OrderBy(p => p.TypeProduit)
                .ThenBy(p => p.Nom)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .ToList();

            return (items, totalCount);
        }
    }
}
