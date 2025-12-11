package services.impl;

import java.util.ArrayList;
import java.util.Optional;

import entity.*;
import repository.ProduitRepository;
import services.ProduitService;

public class ProduitServiceImpl implements ProduitService {

    private ProduitRepository produitRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Override
    public boolean save(Produit produit) {
        produitRepository.insert(produit);
        return true;
    }

    @Override
    public ArrayList<Produit> getAll() {
        return produitRepository.selectAll();
    }

    @Override
    public Optional<Produit> getById(int id) {
        return produitRepository.selectById(id);
    }

    @Override
    public boolean update(Produit produit) {
        produitRepository.update(produit);
        return true;
    }

    @Override
    public boolean archive(int id) {
        return produitRepository.archive(id);
    }

    @Override
    public ArrayList<Produit> getBurgers() {
        return produitRepository.selectByType(TypeProduit.BURGER);
    }

    @Override
    public ArrayList<Produit> getMenus() {
        return produitRepository.selectByType(TypeProduit.MENU);
    }

    @Override
    public ArrayList<Produit> getComplements() {
        return produitRepository.selectByType(TypeProduit.COMPLEMENT);
    }

    @Override
    public ArrayList<Produit> getBoissons() {
        return produitRepository.selectByTypeComplement(TypeComplement.BOISSON);
    }

    @Override
    public ArrayList<Produit> getFrites() {
        return produitRepository.selectByTypeComplement(TypeComplement.FRITES);
    }

    @Override
    public ArrayList<Produit> getSauces() {
        return produitRepository.selectByTypeComplement(TypeComplement.SAUCE);
    }

    @Override
    public ArrayList<Produit> getDesserts() {
        return produitRepository.selectByTypeComplement(TypeComplement.DESSERT);
    }
}
