package services;

import java.util.ArrayList;
import java.util.Optional;
import entity.*;

public interface ProduitService {
    boolean save(Produit produit);
    ArrayList<Produit> getAll();
    Optional<Produit> getById(int id);
    boolean update(Produit produit);
    boolean archive(int id);

    ArrayList<Produit> getBurgers();
    ArrayList<Produit> getMenus();
    ArrayList<Produit> getComplements();
    ArrayList<Produit> getBoissons();
    ArrayList<Produit> getFrites();
    ArrayList<Produit> getSauces();
    ArrayList<Produit> getDesserts();
}
