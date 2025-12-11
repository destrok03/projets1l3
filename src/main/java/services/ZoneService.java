package services;

import java.util.ArrayList;
import java.util.Optional;
import entity.Zone;

public interface ZoneService {
    boolean save(Zone zone);
    ArrayList<Zone> getAll();
    Optional<Zone> getById(int id);
    Optional<Zone> getByNom(String nom);
    boolean update(Zone zone);
    boolean delete(int id);
    ArrayList<Zone> getAllActive();
    boolean toggleActive(int id);
}
