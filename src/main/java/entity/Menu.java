package entity;

public class Menu extends Produit {
    private Burger burger;
    private Complement boisson;
    private Complement frites;

    public Menu() {
        super();
        this.typeProduit = TypeProduit.MENU;
    }

    public Menu(int id, String nom, Burger burger, Complement boisson, Complement frites) {
        super(id, nom, 0, TypeProduit.MENU);
        this.burger = burger;
        this.boisson = boisson;
        this.frites = frites;
        calculerPrix();
    }

    public Burger getBurger() {
        return burger;
    }

    public void setBurger(Burger burger) {
        this.burger = burger;
        calculerPrix();
    }

    public Complement getBoisson() {
        return boisson;
    }

    public void setBoisson(Complement boisson) {
        this.boisson = boisson;
        calculerPrix();
    }

    public Complement getFrites() {
        return frites;
    }

    public void setFrites(Complement frites) {
        this.frites = frites;
        calculerPrix();
    }

    public void calculerPrix() {
        this.prix = 0;
        if (burger != null) this.prix += burger.getPrix();
        if (boisson != null) this.prix += boisson.getPrix();
        if (frites != null) this.prix += frites.getPrix();
    }

    @Override
    public String toString() {
        String burgerNom = (burger != null) ? burger.getNom() : "N/A";
        String boissonNom = (boisson != null) ? boisson.getNom() : "N/A";
        String fritesNom = (frites != null) ? frites.getNom() : "N/A";
        return id + " | " + nom + " | " + prix + " FCFA | " + burgerNom + " + " + boissonNom + " + " + fritesNom;
    }
}
