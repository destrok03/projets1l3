package entity;

import java.util.ArrayList;

public class Zone {
    private int id;
    private String nom;
    private double prixLivraison;
    private boolean active;

    private ArrayList<Quartier> quartiers = new ArrayList<>();
    private ArrayList<Livreur> livreurs = new ArrayList<>();

    public Zone() {
    }

    public Zone(int id, String nom, double prixLivraison) {
        this.id = id;
        this.nom = nom;
        this.prixLivraison = prixLivraison;
        this.active = true;
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

    public double getPrixLivraison() {
        return prixLivraison;
    }

    public void setPrixLivraison(double prixLivraison) {
        this.prixLivraison = prixLivraison;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<Quartier> getQuartiers() {
        return quartiers;
    }

    public void setQuartiers(ArrayList<Quartier> quartiers) {
        this.quartiers = quartiers;
    }

    public void addQuartier(Quartier quartier) {
        this.quartiers.add(quartier);
    }

    public ArrayList<Livreur> getLivreurs() {
        return livreurs;
    }

    public void setLivreurs(ArrayList<Livreur> livreurs) {
        this.livreurs = livreurs;
    }

    public void addLivreur(Livreur livreur) {
        this.livreurs.add(livreur);
    }

    @Override
    public String toString() {
        return id + " | " + nom + " | " + prixLivraison + " FCFA | " + (active ? "Active" : "Inactive");
    }
}
