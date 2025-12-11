package repository.list;

import java.util.ArrayList;
import java.util.Optional;

import entity.Zone;
import repository.ZoneRepository;

public class ZoneRepositoryListImpl implements ZoneRepository {
    private static int compteur = 0;
    private ArrayList<Zone> zones = new ArrayList<>();

    @Override
    public Zone insert(Zone zone) {
        compteur++;
        zone.setId(compteur);
        zones.add(zone);
        return zone;
    }

    @Override
    public ArrayList<Zone> selectAll() {
        return new ArrayList<>(zones);
    }

    @Override
    public Optional<Zone> selectById(int id) {
        for (Zone z : zones) {
            if (z.getId() == id) {
                return Optional.of(z);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Zone> selectByNom(String nom) {
        for (Zone z : zones) {
            if (z.getNom().equalsIgnoreCase(nom)) {
                return Optional.of(z);
            }
        }
        return Optional.empty();
    }

    @Override
    public Zone update(Zone zone) {
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).getId() == zone.getId()) {
                zones.set(i, zone);
                return zone;
            }
        }
        return zone;
    }

    @Override
    public boolean delete(int id) {
        return zones.removeIf(z -> z.getId() == id);
    }

    @Override
    public ArrayList<Zone> selectAllActive() {
        ArrayList<Zone> actives = new ArrayList<>();
        for (Zone z : zones) {
            if (z.isActive()) {
                actives.add(z);
            }
        }
        return actives;
    }
}
