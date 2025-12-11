package repository.list;

import java.util.ArrayList;
import java.util.Optional;

import entity.Livreur;
import entity.Zone;
import repository.LivreurRepository;

public class LivreurRepositoryListImpl implements LivreurRepository {
    private static int compteur = 0;
    private ArrayList<Livreur> livreurs = new ArrayList<>();

    @Override
    public Livreur insert(Livreur livreur) {
        compteur++;
        livreur.setId(compteur);
        livreurs.add(livreur);
        return livreur;
    }

    @Override
    public ArrayList<Livreur> selectAll() {
        return new ArrayList<>(livreurs);
    }

    @Override
    public Optional<Livreur> selectById(int id) {
        for (Livreur l : livreurs) {
            if (l.getId() == id) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Livreur> selectByTelephone(String telephone) {
        for (Livreur l : livreurs) {
            if (l.getTelephone().equals(telephone)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    @Override
    public Livreur update(Livreur livreur) {
        for (int i = 0; i < livreurs.size(); i++) {
            if (livreurs.get(i).getId() == livreur.getId()) {
                livreurs.set(i, livreur);
                return livreur;
            }
        }
        return livreur;
    }

    @Override
    public boolean delete(int id) {
        return livreurs.removeIf(l -> l.getId() == id);
    }

    @Override
    public ArrayList<Livreur> selectDisponibles() {
        ArrayList<Livreur> disponibles = new ArrayList<>();
        for (Livreur l : livreurs) {
            if (l.isDisponible()) {
                disponibles.add(l);
            }
        }
        return disponibles;
    }

    @Override
    public boolean affecterZone(int livreurId, int zoneId) {
        Optional<Livreur> opt = selectById(livreurId);
        if (opt.isPresent()) {
            Zone zone = new Zone();
            zone.setId(zoneId);
            opt.get().addZone(zone);
            return true;
        }
        return false;
    }

    @Override
    public boolean retirerZone(int livreurId, int zoneId) {
        Optional<Livreur> opt = selectById(livreurId);
        if (opt.isPresent()) {
            Zone zone = new Zone();
            zone.setId(zoneId);
            opt.get().removeZone(zone);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Livreur> selectByZoneId(int zoneId) {
        ArrayList<Livreur> result = new ArrayList<>();
        for (Livreur l : livreurs) {
            for (Zone z : l.getZones()) {
                if (z.getId() == zoneId) {
                    result.add(l);
                    break;
                }
            }
        }
        return result;
    }
}
