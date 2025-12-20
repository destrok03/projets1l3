using BrasilBurger.Models;

namespace BrasilBurger.Services
{
    public interface IZoneService
    {
        List<Zone> GetAll();
        Zone? GetById(int id);
        List<Quartier> GetQuartiersByZone(int zoneId);
        List<Quartier> GetAllQuartiers();
        Quartier? GetQuartierById(int id);
    }
}
