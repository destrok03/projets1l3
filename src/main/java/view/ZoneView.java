package view;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import core.factory.EntityName;
import core.factory.ServiceFactory;
import entity.Zone;
import entity.Quartier;
import services.ZoneService;
import services.QuartierService;

public class ZoneView {
    private Scanner scanner;
    private ZoneService zoneService;
    private QuartierService quartierService;

    public ZoneView(Scanner scanner) {
        this.scanner = scanner;
        this.zoneService = (ZoneService) ServiceFactory.getInstance(EntityName.ZONE);
        this.quartierService = (QuartierService) ServiceFactory.getInstance(EntityName.QUARTIER);
    }

    public void afficherMenu() {
        int choix;
        do {
            System.out.println("\n========== GESTION DES ZONES ==========");
            System.out.println("1- Ajouter une zone");
            System.out.println("2- Lister toutes les zones");
            System.out.println("3- Modifier une zone");
            System.out.println("4- Activer/Desactiver une zone");
            System.out.println("5- Voir les quartiers d'une zone");
            System.out.println("6- Supprimer une zone");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            
            try {
                choix = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Entree invalide!");
                scanner = new java.util.Scanner(System.in);
                choix = -1;
                continue;
            }

            switch (choix) {
                case 1:
                    ajouterZone();
                    break;
                case 2:
                    listerZones();
                    break;
                case 3:
                    modifierZone();
                    break;
                case 4:
                    toggleZone();
                    break;
                case 5:
                    voirQuartiers();
                    break;
                case 6:
                    supprimerZone();
                    break;
                case 0:
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        } while (choix != 0);
    }

    private void ajouterZone() {
        System.out.println("\n--- Ajouter une zone ---");
        
        String nom;
        do {
            System.out.print("Nom de la zone: ");
            nom = scanner.nextLine();
            if (nom.trim().isEmpty()) {
                System.out.println("Le nom ne peut pas etre vide!");
                nom = null;
            } else if (nom.trim().length() < 2 || nom.trim().length() > 100) {
                System.out.println("Le nom doit contenir entre 2 et 100 caracteres!");
                nom = null;
            } else if (!nom.trim().matches("[a-zA-ZÀ-ÿ0-9\\s'-]+")) {
                System.out.println("Le nom contient des caracteres non autorises!");
                nom = null;
            }
        } while (nom == null);
        
        double prix;
        do {
            System.out.print("Prix de livraison (FCFA): ");
            prix = scanner.nextDouble();
            scanner.nextLine();
            if (!core.ValidationUtil.isValidPrix(prix)) {
                System.out.println(core.ValidationUtil.getPrixErrorMessage());
            }
        } while (!core.ValidationUtil.isValidPrix(prix));

        Zone zone = new Zone();
        zone.setNom(nom.trim());
        zone.setPrixLivraison(prix);
        zone.setActive(true);

        if (zoneService.save(zone)) {
            System.out.println("Zone ajoutee avec succes!");
        } else {
            System.out.println("Erreur: Cette zone existe deja!");
        }
    }

    private void listerZones() {
        System.out.println("\n--- Liste des zones ---");
        ArrayList<Zone> zones = zoneService.getAll();
        if (zones.isEmpty()) {
            System.out.println("Aucune zone enregistree.");
        } else {
            System.out.println("ID | Nom | Prix Livraison | Statut");
            System.out.println("-".repeat(50));
            for (Zone z : zones) {
                System.out.println(z);
            }
        }
    }

    private void modifierZone() {
        listerZones();
        System.out.print("\nID de la zone a modifier: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Optional<Zone> opt = zoneService.getById(id);
        if (opt.isPresent()) {
            Zone zone = opt.get();
            
            System.out.print("Nouveau nom (" + zone.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) {
                if (nom.trim().length() >= 2 && nom.trim().length() <= 100 && nom.trim().matches("[a-zA-ZÀ-ÿ0-9\\s'-]+")) {
                    zone.setNom(nom.trim());
                } else {
                    System.out.println("Nom invalide! Doit contenir entre 2 et 100 caracteres (lettres, chiffres, espaces).");
                }
            }

            System.out.print("Nouveau prix (" + zone.getPrixLivraison() + "): ");
            String prixStr = scanner.nextLine();
            if (!prixStr.isEmpty()) {
                try {
                    double prix = Double.parseDouble(prixStr);
                    if (core.ValidationUtil.isValidPrix(prix)) {
                        zone.setPrixLivraison(prix);
                    } else {
                        System.out.println(core.ValidationUtil.getPrixErrorMessage());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Prix invalide!");
                }
            }

            zoneService.update(zone);
            System.out.println("Zone modifiee avec succes!");
        } else {
            System.out.println("Zone non trouvee!");
        }
    }

    private void toggleZone() {
        listerZones();
        System.out.print("\nID de la zone a activer/desactiver: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (zoneService.toggleActive(id)) {
            System.out.println("Statut de la zone modifie!");
        } else {
            System.out.println("Zone non trouvee!");
        }
    }

    private void voirQuartiers() {
        listerZones();
        System.out.print("\nID de la zone: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Optional<Zone> opt = zoneService.getById(id);
        if (opt.isPresent()) {
            Zone zone = opt.get();
            System.out.println("\n--- Quartiers de " + zone.getNom() + " ---");
            ArrayList<Quartier> quartiers = quartierService.getByZoneId(id);
            if (quartiers.isEmpty()) {
                System.out.println("Aucun quartier dans cette zone.");
            } else {
                for (Quartier q : quartiers) {
                    System.out.println("  - " + q.getNom());
                }
            }
        } else {
            System.out.println("Zone non trouvee!");
        }
    }

    private void supprimerZone() {
        listerZones();
        System.out.print("\nID de la zone a supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Confirmer la suppression? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (zoneService.delete(id)) {
                System.out.println("Zone supprimee!");
            } else {
                System.out.println("Erreur lors de la suppression!");
            }
        }
    }
}
