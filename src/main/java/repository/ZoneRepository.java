package repository;

import java.util.ArrayList;
import java.util.Optional;
import entity.Zone;

public interface ZoneRepository {
    Zone insert(Zone zone);
    ArrayList<Zone> selectAll();
    Optional<Zone> selectById(int id);
    Optional<Zone> selectByNom(String nom);
    Zone update(Zone zone);
    boolean delete(int id);
    ArrayList<Zone> selectAllActive();
}
