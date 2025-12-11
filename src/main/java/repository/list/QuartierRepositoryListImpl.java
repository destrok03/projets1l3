package repository.list;

import java.util.ArrayList;
import java.util.Optional;

import entity.Quartier;
import repository.QuartierRepository;

public class QuartierRepositoryListImpl implements QuartierRepository {
    private static int compteur = 0;
    private ArrayList<Quartier> quartiers = new ArrayList<>();

    @Override
    public Quartier insert(Quartier quartier) {
        compteur++;
        quartier.setId(compteur);
        quartiers.add(quartier);
        return quartier;
    }

    @Override
    public ArrayList<Quartier> selectAll() {
        return new ArrayList<>(quartiers);
    }

    @Override
    public Optional<Quartier> selectById(int id) {
        for (Quartier q : quartiers) {
            if (q.getId() == id) {
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Quartier> selectByZoneId(int zoneId) {
        ArrayList<Quartier> result = new ArrayList<>();
        for (Quartier q : quartiers) {
            if (q.getZone() != null && q.getZone().getId() == zoneId) {
                result.add(q);
            }
        }
        return result;
    }

    @Override
    public Quartier update(Quartier quartier) {
        for (int i = 0; i < quartiers.size(); i++) {
            if (quartiers.get(i).getId() == quartier.getId()) {
                quartiers.set(i, quartier);
                return quartier;
            }
        }
        return quartier;
    }

    @Override
    public boolean delete(int id) {
        return quartiers.removeIf(q -> q.getId() == id);
    }
}
