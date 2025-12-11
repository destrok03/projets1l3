package services;

import java.util.ArrayList;
import java.util.Optional;
import entity.Livreur;

public interface LivreurService {
    boolean save(Livreur livreur);
    ArrayList<Livreur> getAll();
    Optional<Livreur> getById(int id);
    Optional<Livreur> getByTelephone(String telephone);
    boolean update(Livreur livreur);
    boolean delete(int id);
    ArrayList<Livreur> getDisponibles();
    boolean affecterZone(int livreurId, int zoneId);
    boolean retirerZone(int livreurId, int zoneId);
    boolean toggleDisponible(int id);
    ArrayList<Livreur> getByZoneId(int zoneId);
}
