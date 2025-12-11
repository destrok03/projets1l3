package repository;

import java.util.ArrayList;
import java.util.Optional;
import entity.Produit;
import entity.TypeProduit;
import entity.TypeComplement;

public interface ProduitRepository {
    Produit insert(Produit produit);
    ArrayList<Produit> selectAll();
    Optional<Produit> selectById(int id);
    Produit update(Produit produit);
    boolean archive(int id);
    ArrayList<Produit> selectByType(TypeProduit type);
    ArrayList<Produit> selectByTypeComplement(TypeComplement typeComplement);
    ArrayList<Produit> selectDisponibles();
}
