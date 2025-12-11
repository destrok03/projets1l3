package services.impl;

import java.util.ArrayList;
import java.util.Optional;

import entity.Livreur;
import repository.LivreurRepository;
import services.LivreurService;

public class LivreurServiceImpl implements LivreurService {

    private LivreurRepository livreurRepository;

    public LivreurServiceImpl(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    @Override
    public boolean save(Livreur livreur) {

        Optional<Livreur> existing = livreurRepository.selectByTelephone(livreur.getTelephone());
        if (existing.isPresent()) {
            return false;
        }
        livreurRepository.insert(livreur);
        return true;
    }

    @Override
    public ArrayList<Livreur> getAll() {
        return livreurRepository.selectAll();
    }

    @Override
    public Optional<Livreur> getById(int id) {
        return livreurRepository.selectById(id);
    }

    @Override
    public Optional<Livreur> getByTelephone(String telephone) {
        return livreurRepository.selectByTelephone(telephone);
    }

    @Override
    public boolean update(Livreur livreur) {
        livreurRepository.update(livreur);
        return true;
    }

    @Override
    public boolean delete(int id) {
        return livreurRepository.delete(id);
    }

    @Override
    public ArrayList<Livreur> getDisponibles() {
        return livreurRepository.selectDisponibles();
    }

    @Override
    public boolean affecterZone(int livreurId, int zoneId) {
        return livreurRepository.affecterZone(livreurId, zoneId);
    }

    @Override
    public boolean retirerZone(int livreurId, int zoneId) {
        return livreurRepository.retirerZone(livreurId, zoneId);
    }

    @Override
    public boolean toggleDisponible(int id) {
        Optional<Livreur> opt = livreurRepository.selectById(id);
        if (opt.isPresent()) {
            Livreur livreur = opt.get();
            livreur.setDisponible(!livreur.isDisponible());
            livreurRepository.update(livreur);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Livreur> getByZoneId(int zoneId) {
        return livreurRepository.selectByZoneId(zoneId);
    }
}
