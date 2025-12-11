package repository.list;

import java.util.ArrayList;
import java.util.Optional;

import entity.*;
import repository.ProduitRepository;

public class ProduitRepositoryListImpl implements ProduitRepository {
    private static int compteur = 0;
    private ArrayList<Produit> produits = new ArrayList<>();

    @Override
    public Produit insert(Produit produit) {
        compteur++;
        produit.setId(compteur);
        produits.add(produit);
        return produit;
    }

    @Override
    public ArrayList<Produit> selectAll() {
        ArrayList<Produit> result = new ArrayList<>();
        for (Produit p : produits) {
            if (!p.isArchived()) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public Optional<Produit> selectById(int id) {
        for (Produit p : produits) {
            if (p.getId() == id) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public Produit update(Produit produit) {
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).getId() == produit.getId()) {
                produits.set(i, produit);
                return produit;
            }
        }
        return produit;
    }

    @Override
    public boolean archive(int id) {
        Optional<Produit> opt = selectById(id);
        if (opt.isPresent()) {
            opt.get().setArchived(true);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Produit> selectByType(TypeProduit type) {
        ArrayList<Produit> result = new ArrayList<>();
        for (Produit p : produits) {
            if (!p.isArchived() && p.getTypeProduit() == type) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Produit> selectByTypeComplement(TypeComplement typeComplement) {
        ArrayList<Produit> result = new ArrayList<>();
        for (Produit p : produits) {
            if (!p.isArchived() && p instanceof Complement) {
                Complement c = (Complement) p;
                if (c.getTypeComplement() == typeComplement) {
                    result.add(p);
                }
            }
        }
        return result;
    }

    @Override
    public ArrayList<Produit> selectDisponibles() {
        return selectAll();
    }
}
