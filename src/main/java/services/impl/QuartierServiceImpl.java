package services.impl;

import java.util.ArrayList;
import java.util.Optional;

import entity.Quartier;
import repository.QuartierRepository;
import services.QuartierService;

public class QuartierServiceImpl implements QuartierService {

    private QuartierRepository quartierRepository;

    public QuartierServiceImpl(QuartierRepository quartierRepository) {
        this.quartierRepository = quartierRepository;
    }

    @Override
    public boolean save(Quartier quartier) {
        if (quartier.getZone() == null) {
            return false;
        }
        quartierRepository.insert(quartier);
        return true;
    }

    @Override
    public ArrayList<Quartier> getAll() {
        return quartierRepository.selectAll();
    }

    @Override
    public Optional<Quartier> getById(int id) {
        return quartierRepository.selectById(id);
    }

    @Override
    public ArrayList<Quartier> getByZoneId(int zoneId) {
        return quartierRepository.selectByZoneId(zoneId);
    }

    @Override
    public boolean update(Quartier quartier) {
        quartierRepository.update(quartier);
        return true;
    }

    @Override
    public boolean delete(int id) {
        return quartierRepository.delete(id);
    }
}
