package entity;

public class Produit {
    protected int id;
    protected String nom;
    protected double prix;
    protected String image;
    protected TypeProduit typeProduit;
    protected boolean archived;

    public Produit() {
        this.archived = false;
    }

    public Produit(int id, String nom, double prix, TypeProduit typeProduit) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.typeProduit = typeProduit;
        this.archived = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public TypeProduit getTypeProduit() {
        return typeProduit;
    }

    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public String toString() {
        return id + " | " + nom + " | " + prix + " FCFA | " + typeProduit;
    }
}
