package entity;

public class Burger extends Produit {
    private String description;
    private String ingredients;

    public Burger() {
        super();
        this.typeProduit = TypeProduit.BURGER;
    }

    public Burger(int id, String nom, double prix, String description, String ingredients) {
        super(id, nom, prix, TypeProduit.BURGER);
        this.description = description;
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return id + " | " + nom + " | " + prix + " FCFA | " + description;
    }
}
