package repository;

import java.util.ArrayList;
import java.util.Optional;
import entity.Livreur;

public interface LivreurRepository {
    Livreur insert(Livreur livreur);
    ArrayList<Livreur> selectAll();
    Optional<Livreur> selectById(int id);
    Optional<Livreur> selectByTelephone(String telephone);
    Livreur update(Livreur livreur);
    boolean delete(int id);
    ArrayList<Livreur> selectDisponibles();
    boolean affecterZone(int livreurId, int zoneId);
    boolean retirerZone(int livreurId, int zoneId);
    ArrayList<Livreur> selectByZoneId(int zoneId);
}
