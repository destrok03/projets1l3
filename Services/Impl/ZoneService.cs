using BrasilBurger.Data;
using BrasilBurger.Models;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Services.Impl
{
    public class ZoneService : IZoneService
    {
        private readonly BrasilBurgerDbContext _context;

        public ZoneService(BrasilBurgerDbContext context)
        {
            _context = context;
        }

        public List<Zone> GetAll()
        {
            return _context.Zones
                .Include(z => z.Quartiers)
                .OrderBy(z => z.Nom)
                .ToList();
        }

        public Zone? GetById(int id)
        {
            return _context.Zones
                .Include(z => z.Quartiers)
                .FirstOrDefault(z => z.Id == id);
        }

        public List<Quartier> GetQuartiersByZone(int zoneId)
        {
            return _context.Quartiers
                .Where(q => q.ZoneId == zoneId)
                .OrderBy(q => q.Nom)
                .ToList();
        }

        public List<Quartier> GetAllQuartiers()
        {
            return _context.Quartiers
                .Include(q => q.Zone)
                .OrderBy(q => q.Zone!.Nom)
                .ThenBy(q => q.Nom)
                .ToList();
        }

        public Quartier? GetQuartierById(int id)
        {
            return _context.Quartiers
                .Include(q => q.Zone)
                .FirstOrDefault(q => q.Id == id);
        }
    }
}
