package view;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import core.factory.EntityName;
import core.factory.ServiceFactory;
import entity.Livreur;
import entity.Zone;
import services.LivreurService;
import services.ZoneService;

public class LivreurView {
    private Scanner scanner;
    private LivreurService livreurService;
    private ZoneService zoneService;

    public LivreurView(Scanner scanner) {
        this.scanner = scanner;
        this.livreurService = (LivreurService) ServiceFactory.getInstance(EntityName.LIVREUR);
        this.zoneService = (ZoneService) ServiceFactory.getInstance(EntityName.ZONE);
    }

    public void afficherMenu() {
        int choix;
        do {
            System.out.println("\n========== GESTION DES LIVREURS ==========");
            System.out.println("1- Ajouter un livreur");
            System.out.println("2- Lister tous les livreurs");
            System.out.println("3- Lister les livreurs disponibles");
            System.out.println("4- Modifier un livreur");
            System.out.println("5- Changer disponibilite");
            System.out.println("6- Affecter une zone");
            System.out.println("7- Retirer une zone");
            System.out.println("8- Supprimer un livreur");
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
                    ajouterLivreur();
                    break;
                case 2:
                    listerLivreurs();
                    break;
                case 3:
                    listerDisponibles();
                    break;
                case 4:
                    modifierLivreur();
                    break;
                case 5:
                    changerDisponibilite();
                    break;
                case 6:
                    affecterZone();
                    break;
                case 7:
                    retirerZone();
                    break;
                case 8:
                    supprimerLivreur();
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
            System.out.println(z.getId() + "- " + z.getNom() + " (" + z.getPrixLivraison() + " FCFA)");
        }
    }

    private void ajouterLivreur() {
        System.out.println("\n--- Ajouter un livreur ---");
        
        String telephone;
        do {
            System.out.print("Telephone: ");
            telephone = scanner.nextLine();
            if (!core.ValidationUtil.isValidTelephone(telephone)) {
                System.out.println(core.ValidationUtil.getTelephoneErrorMessage());
            } else {
                Optional<Livreur> existing = livreurService.getByTelephone(telephone);
                if (existing.isPresent()) {
                    System.out.println("Ce numero de telephone existe deja!");
                    telephone = null;
                }
            }
        } while (telephone == null || !core.ValidationUtil.isValidTelephone(telephone));

        String nom;
        do {
            System.out.print("Nom: ");
            nom = scanner.nextLine();
            if (!core.ValidationUtil.isValidNom(nom)) {
                System.out.println(core.ValidationUtil.getNomErrorMessage());
                nom = null;
            }
        } while (nom == null);

        String prenom;
        do {
            System.out.print("Prenom: ");
            prenom = scanner.nextLine();
            if (!core.ValidationUtil.isValidPrenom(prenom)) {
                System.out.println(core.ValidationUtil.getPrenomErrorMessage());
                prenom = null;
            }
        } while (prenom == null);

        Livreur livreur = new Livreur();
        livreur.setNom(nom);
        livreur.setPrenom(prenom);
        livreur.setTelephone(telephone);
        livreur.setDisponible(true);

        if (livreurService.save(livreur)) {
            System.out.println("Livreur ajoute avec succes!");
        } else {
            System.out.println("Erreur lors de l'ajout!");
        }
    }

    private void listerLivreurs() {
        System.out.println("\n--- Liste des livreurs ---");
        ArrayList<Livreur> livreurs = livreurService.getAll();
        if (livreurs.isEmpty()) {
            System.out.println("Aucun livreur enregistre.");
        } else {
            for (Livreur l : livreurs) {
                System.out.println(l);
            }
        }
    }

    private void listerDisponibles() {
        System.out.println("\n--- Livreurs disponibles ---");
        ArrayList<Livreur> livreurs = livreurService.getDisponibles();
        if (livreurs.isEmpty()) {
            System.out.println("Aucun livreur disponible.");
        } else {
            for (Livreur l : livreurs) {
                System.out.println(l);
            }
        }
    }

    private void modifierLivreur() {
        listerLivreurs();
        System.out.print("\nID du livreur a modifier: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Optional<Livreur> opt = livreurService.getById(id);
        if (opt.isPresent()) {
            Livreur livreur = opt.get();
            
            System.out.print("Nouveau nom (" + livreur.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) {
                if (core.ValidationUtil.isValidNom(nom)) {
                    livreur.setNom(nom);
                } else {
                    System.out.println(core.ValidationUtil.getNomErrorMessage());
                }
            }

            System.out.print("Nouveau prenom (" + livreur.getPrenom() + "): ");
            String prenom = scanner.nextLine();
            if (!prenom.isEmpty()) {
                if (core.ValidationUtil.isValidPrenom(prenom)) {
                    livreur.setPrenom(prenom);
                } else {
                    System.out.println(core.ValidationUtil.getPrenomErrorMessage());
                }
            }

            System.out.print("Nouveau telephone (" + livreur.getTelephone() + "): ");
            String tel = scanner.nextLine();
            if (!tel.isEmpty()) {
                if (core.ValidationUtil.isValidTelephone(tel)) {
                    livreur.setTelephone(tel);
                } else {
                    System.out.println(core.ValidationUtil.getTelephoneErrorMessage());
                }
            }

            livreurService.update(livreur);
            System.out.println("Livreur modifie avec succes!");
        } else {
            System.out.println("Livreur non trouve!");
        }
    }

    private void changerDisponibilite() {
        listerLivreurs();
        System.out.print("\nID du livreur: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (livreurService.toggleDisponible(id)) {
            System.out.println("Disponibilite modifiee!");
        } else {
            System.out.println("Livreur non trouve!");
        }
    }

    private void affecterZone() {
        listerLivreurs();
        System.out.print("\nID du livreur: ");
        int livreurId = scanner.nextInt();
        scanner.nextLine();

        Optional<Livreur> livreurOpt = livreurService.getById(livreurId);
        if (livreurOpt.isPresent()) {
            afficherZones();
            System.out.print("ID de la zone a affecter: ");
            int zoneId = scanner.nextInt();
            scanner.nextLine();

            if (livreurService.affecterZone(livreurId, zoneId)) {
                System.out.println("Zone affectee avec succes!");
            } else {
                System.out.println("Erreur lors de l'affectation!");
            }
        } else {
            System.out.println("Livreur non trouve!");
        }
    }

    private void retirerZone() {
        listerLivreurs();
        System.out.print("\nID du livreur: ");
        int livreurId = scanner.nextInt();
        scanner.nextLine();

        Optional<Livreur> livreurOpt = livreurService.getById(livreurId);
        if (livreurOpt.isPresent()) {
            Livreur livreur = livreurOpt.get();
            System.out.println("Zones actuelles: " + livreur.getZonesNoms());

            afficherZones();
            System.out.print("ID de la zone a retirer: ");
            int zoneId = scanner.nextInt();
            scanner.nextLine();

            if (livreurService.retirerZone(livreurId, zoneId)) {
                System.out.println("Zone retiree avec succes!");
            } else {
                System.out.println("Erreur lors du retrait!");
            }
        } else {
            System.out.println("Livreur non trouve!");
        }
    }

    private void supprimerLivreur() {
        listerLivreurs();
        System.out.print("\nID du livreur a supprimer: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Confirmer la suppression? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (livreurService.delete(id)) {
                System.out.println("Livreur supprime!");
            } else {
                System.out.println("Erreur lors de la suppression!");
            }
        }
    }
}
