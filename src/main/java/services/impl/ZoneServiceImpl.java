package services.impl;

import java.util.ArrayList;
import java.util.Optional;

import entity.Zone;
import repository.ZoneRepository;
import services.ZoneService;

public class ZoneServiceImpl implements ZoneService {

    private ZoneRepository zoneRepository;

    public ZoneServiceImpl(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @Override
    public boolean save(Zone zone) {

        Optional<Zone> existing = zoneRepository.selectByNom(zone.getNom());
        if (existing.isPresent()) {
            return false;
        }
        zoneRepository.insert(zone);
        return true;
    }

    @Override
    public ArrayList<Zone> getAll() {
        return zoneRepository.selectAll();
    }

    @Override
    public Optional<Zone> getById(int id) {
        return zoneRepository.selectById(id);
    }

    @Override
    public Optional<Zone> getByNom(String nom) {
        return zoneRepository.selectByNom(nom);
    }

    @Override
    public boolean update(Zone zone) {
        zoneRepository.update(zone);
        return true;
    }

    @Override
    public boolean delete(int id) {
        return zoneRepository.delete(id);
    }

    @Override
    public ArrayList<Zone> getAllActive() {
        return zoneRepository.selectAllActive();
    }

    @Override
    public boolean toggleActive(int id) {
        Optional<Zone> opt = zoneRepository.selectById(id);
        if (opt.isPresent()) {
            Zone zone = opt.get();
            zone.setActive(!zone.isActive());
            zoneRepository.update(zone);
            return true;
        }
        return false;
    }
}
