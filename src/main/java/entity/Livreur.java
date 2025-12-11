package entity;

import java.util.ArrayList;

public class Livreur {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private boolean disponible;

    private ArrayList<Zone> zones = new ArrayList<>();

    public Livreur() {
    }

    public Livreur(int id, String nom, String prenom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.disponible = true;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void setZones(ArrayList<Zone> zones) {
        this.zones = zones;
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public void removeZone(Zone zone) {
        this.zones.removeIf(z -> z.getId() == zone.getId());
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public String getZonesNoms() {
        if (zones.isEmpty()) return "Aucune zone";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < zones.size(); i++) {
            sb.append(zones.get(i).getNom());
            if (i < zones.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return id + " | " + getNomComplet() + " | " + telephone + " | " +
               (disponible ? "Disponible" : "Indisponible") + " | Zones: " + getZonesNoms();
    }
}
