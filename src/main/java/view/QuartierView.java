package view;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import core.factory.EntityName;
import core.factory.ServiceFactory;
import entity.Quartier;
import entity.Zone;
import services.QuartierService;
import services.ZoneService;

public class QuartierView {
    private Scanner scanner;
    private QuartierService quartierService;
    private ZoneService zoneService;

    public QuartierView(Scanner scanner) {
        this.scanner = scanner;
        this.quartierService = (QuartierService) ServiceFactory.getInstance(EntityName.QUARTIER);
        this.zoneService = (ZoneService) ServiceFactory.getInstance(EntityName.ZONE);
    }

    public void afficherMenu() {
        int choix;
        do {
            System.out.println("\n========== GESTION DES QUARTIERS ==========");
            System.out.println("1- Ajouter un quartier");
            System.out.println("2- Lister tous les quartiers");
            System.out.println("3- Lister par zone");
            System.out.println("4- Modifier un quartier");
            System.out.println("5- Supprimer un quartier");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterQuartier();
                    break;
                case 2:
                    listerQuartiers();
                    break;
                case 3:
                    listerParZone();
                    break;
                case 4:
                    modifierQuartier();
                    break;
                case 5:
                    supprimerQuartier();
                    break;
                case 0:
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        } while (choix != 0);
    }

    private void afficherZones() {
        System.out.println("\n--- Zones disponibles ---");
        ArrayList<Zone> zones = zoneService.getAll();
        for (Zone z : zones) {
            System.out.println(z.getId() + "- " + z.getNom());
        }
    }

    private void ajouterQuartier() {
        System.out.println("\n--- Ajouter un quartier ---");
        
        String nom;
        do {
            System.out.print("Nom du quartier: ");
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

        afficherZones();
        System.out.print("ID de la zone: ");
        int zoneId = scanner.nextInt();
        scanner.nextLine();

        Optional<Zone> zoneOpt = zoneService.getById(zoneId);
        if (zoneOpt.isPresent()) {
            Quartier quartier = new Quartier();
            quartier.setNom(nom.trim());
            quartier.setZone(zoneOpt.get());

            if (quartierService.save(quartier)) {
                System.out.println("Quartier ajoute avec succes!");
            } else {
                System.out.println("Erreur lors de l'ajout!");
            }
        } else {
            System.out.println("Zone non trouvee!");
        }
    }

    private void listerQuartiers() {
        System.out.println("\n--- Liste des quartiers ---");
        ArrayList<Quartier> quartiers = quartierService.getAll();
        if (quartiers.isEmpty()) {
            System.out.println("Aucun quartier enregistre.");
        } else {
            System.out.println("ID | Nom | Zone");
            System.out.println("-".repeat(50));
            for (Quartier q : quartiers) {
                System.out.println(q);
            }
        }
    }

    private void listerParZone() {
        afficherZones();
        System.out.print("\nID de la zone: ");
        int zoneId = scanner.nextInt();
        scanner.nextLine();

        Optional<Zone> zoneOpt = zoneService.getById(zoneId);
        if (zoneOpt.isPresent()) {
            System.out.println("\n--- Quartiers de " + zoneOpt.get().getNom() + " ---");
            ArrayList<Quartier> quartiers = quartierService.getByZoneId(zoneId);
            if (quartiers.isEmpty()) {
                System.out.println("Aucun quartier dans cette zone.");
            } else {
                for (Quartier q : quartiers) {
                    System.out.println("  " + q.getId() + "- " + q.getNom());
                }
            }
        } else {
            System.out.println("Zone non trouvee!");
        }
    }

    private void modifierQuartier() {
        listerQuartiers();
        System.out.print("\nID du quartier a modifier: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Optional<Quartier> opt = quartierService.getById(id);
        if (opt.isPresent()) {
            Quartier quartier = opt.get();
            System.out.print("Nouveau nom (" + quartier.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) quartier.setNom(nom);

            afficherZones();
            System.out.print("Nouvelle zone (actuelle: " + quartier.getZone().getNom() + "): ");
            String zoneStr = scanner.nextLine();
            if (!zoneStr.isEmpty()) {
                Optional<Zone> zoneOpt = zoneService.getById(Integer.parseInt(zoneStr));
                zoneOpt.ifPresent(quartier::setZone);
            }

            quartierService.update(quartier);
            System.out.println("Quartier modifie avec succes!");
        } else {
            System.out.println("Quartier non trouve!");
        }
    }

    private void supprimerQuartier() {
        listerQuartiers();
        System.out.print("\nID du quartier a supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Confirmer la suppression? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (quartierService.delete(id)) {
                System.out.println("Quartier supprime!");
            } else {
                System.out.println("Erreur lors de la suppression!");
            }
        }
    }
}
