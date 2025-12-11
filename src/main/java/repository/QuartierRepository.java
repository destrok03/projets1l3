package repository;

import java.util.ArrayList;
import java.util.Optional;
import entity.Quartier;

public interface QuartierRepository {
    Quartier insert(Quartier quartier);
    ArrayList<Quartier> selectAll();
    Optional<Quartier> selectById(int id);
    ArrayList<Quartier> selectByZoneId(int zoneId);
    Quartier update(Quartier quartier);
    boolean delete(int id);
}
