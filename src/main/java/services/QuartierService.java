package services;

import java.util.ArrayList;
import java.util.Optional;
import entity.Quartier;

public interface QuartierService {
    boolean save(Quartier quartier);
    ArrayList<Quartier> getAll();
    Optional<Quartier> getById(int id);
    ArrayList<Quartier> getByZoneId(int zoneId);
    boolean update(Quartier quartier);
    boolean delete(int id);
}
