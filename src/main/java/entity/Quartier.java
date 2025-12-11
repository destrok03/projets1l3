package entity;

public class Quartier {
    private int id;
    private String nom;

    private Zone zone;

    public Quartier() {
    }

    public Quartier(int id, String nom, Zone zone) {
        this.id = id;
        this.nom = nom;
        this.zone = zone;
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

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        String zoneNom = (zone != null) ? zone.getNom() : "Non assigne";
        return id + " | " + nom + " | Zone: " + zoneNom;
    }
}
