package view;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import core.factory.EntityName;
import core.factory.ServiceFactory;
import entity.*;
import services.CloudinaryService;
import services.ProduitService;
import services.impl.CloudinaryServiceImpl;

public class ProduitView {
    private Scanner scanner;
    private ProduitService produitService;

    public ProduitView(Scanner scanner) {
        this.scanner = scanner;
        this.produitService = (ProduitService) ServiceFactory.getInstance(EntityName.PRODUIT);
    }

    public void afficherMenu() {
        int choix;
        do {
            System.out.println("\n========== GESTION DES PRODUITS ==========");
            System.out.println("1- Gestion des Burgers");
            System.out.println("2- Gestion des Complements");
            System.out.println("3- Gestion des Menus");
            System.out.println("4- Lister tous les produits");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    menuBurgers();
                    break;
                case 2:
                    menuComplements();
                    break;
                case 3:
                    menuMenus();
                    break;
                case 4:
                    listerTousProduits();
                    break;
                case 0:
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        } while (choix != 0);
    }

    private void menuBurgers() {
        int choix;
        do {
            System.out.println("\n--- BURGERS ---");
            System.out.println("1- Ajouter un burger");
            System.out.println("2- Lister les burgers");
            System.out.println("3- Modifier un burger");
            System.out.println("4- Archiver un burger");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterBurger();
                    break;
                case 2:
                    listerBurgers();
                    break;
                case 3:
                    modifierBurger();
                    break;
                case 4:
                    archiverProduit();
                    break;
            }
        } while (choix != 0);
    }

    private void ajouterBurger() {
        System.out.println("\n--- Ajouter un burger ---");
        
        String nom;
        do {
            System.out.print("Nom: ");
            nom = scanner.nextLine();
            if (nom.trim().isEmpty()) {
                System.out.println("Le nom ne peut pas etre vide!");
                nom = null;
            } else if (nom.trim().length() < 2 || nom.trim().length() > 150) {
                System.out.println("Le nom doit contenir entre 2 et 150 caracteres!");
                nom = null;
            }
        } while (nom == null);
        
        double prix;
        do {
            System.out.print("Prix (FCFA): ");
            prix = scanner.nextDouble();
            scanner.nextLine();
            if (!core.ValidationUtil.isValidPrix(prix)) {
                System.out.println(core.ValidationUtil.getPrixErrorMessage());
            }
        } while (!core.ValidationUtil.isValidPrix(prix));
        
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Ingredients: ");
        String ingredients = scanner.nextLine();
        
        String imageUrl = null;
        System.out.print("Chemin complet de l'image (ex: C:/images/burger.jpg): ");
        String cheminImage = scanner.nextLine();
        
        File imageFile = new File(cheminImage);
        if (imageFile.exists() && imageFile.isFile()) {
            CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
            imageUrl = cloudinaryService.uploadImage(imageFile, "burgers");
            
            if (imageUrl == null) {
                System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                imageUrl = imageFile.getName();
            }
        } else {
            System.out.println("Fichier introuvable: " + cheminImage);
            imageUrl = "default_burger.jpg";
        }

        Burger burger = new Burger();
        burger.setNom(nom.trim());
        burger.setPrix(prix);
        burger.setDescription(description);
        burger.setIngredients(ingredients);
        burger.setImage(imageUrl);

        if (produitService.save(burger)) {
            System.out.println("Burger ajoute avec succes!");
            if (imageUrl != null && imageUrl.startsWith("https://")) {
                System.out.println("   Image disponible sur: " + imageUrl);
            }
        } else {
            System.out.println("Erreur lors de l'ajout!");
        }
    }

    private void listerBurgers() {
        System.out.println("\n--- Liste des burgers ---");
        ArrayList<Produit> burgers = produitService.getBurgers();
        if (burgers.isEmpty()) {
            System.out.println("Aucun burger enregistre.");
        } else {
            for (Produit p : burgers) {
                Burger b = (Burger) p;
                System.out.println(b.getId() + " | " + b.getNom() + " | " + b.getPrix() + " FCFA");
                System.out.println("    " + b.getDescription());
            }
        }
    }

    private void modifierBurger() {
        listerBurgers();
        System.out.print("\nID du burger a modifier: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Optional<Produit> opt = produitService.getById(id);
        if (opt.isPresent() && opt.get() instanceof Burger) {
            Burger burger = (Burger) opt.get();

            System.out.print("Nouveau nom (" + burger.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) burger.setNom(nom);

            System.out.print("Nouveau prix (" + burger.getPrix() + "): ");
            String prixStr = scanner.nextLine();
            if (!prixStr.isEmpty()) burger.setPrix(Double.parseDouble(prixStr));

            System.out.print("Nouvelle description: ");
            String desc = scanner.nextLine();
            if (!desc.isEmpty()) burger.setDescription(desc);

            System.out.print("Nouveaux ingredients: ");
            String ing = scanner.nextLine();
            if (!ing.isEmpty()) burger.setIngredients(ing);

            produitService.update(burger);
            System.out.println("Burger modifie avec succes!");
        } else {
            System.out.println("Burger non trouve!");
        }
    }

    private void menuComplements() {
        int choix;
        do {
            System.out.println("\n--- COMPLEMENTS ---");
            System.out.println("1- Ajouter une boisson");
            System.out.println("2- Ajouter des frites");
            System.out.println("3- Ajouter une sauce");
            System.out.println("4- Ajouter un dessert");
            System.out.println("5- Lister les complements");
            System.out.println("6- Archiver un complement");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    ajouterComplement(TypeComplement.BOISSON);
                    break;
                case 2:
                    ajouterComplement(TypeComplement.FRITES);
                    break;
                case 3:
                    ajouterComplement(TypeComplement.SAUCE);
                    break;
                case 4:
                    ajouterComplement(TypeComplement.DESSERT);
                    break;
                case 5:
                    listerComplements();
                    break;
                case 6:
                    archiverProduit();
                    break;
            }
        } while (choix != 0);
    }

    private void ajouterComplement(TypeComplement type) {
        System.out.println("\n--- Ajouter " + type + " ---");
        
        String nom;
        do {
            System.out.print("Nom: ");
            nom = scanner.nextLine();
            if (nom.trim().isEmpty()) {
                System.out.println("Le nom ne peut pas etre vide!");
                nom = null;
            } else if (nom.trim().length() < 2 || nom.trim().length() > 150) {
                System.out.println("Le nom doit contenir entre 2 et 150 caracteres!");
                nom = null;
            } else if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                System.out.println("Le nom doit contenir uniquement des lettres!");
                nom = null;
            }
        } while (nom == null);
        
        double prix;
        do {
            System.out.print("Prix (FCFA): ");
            prix = scanner.nextDouble();
            scanner.nextLine();
            if (!core.ValidationUtil.isValidPrix(prix)) {
                System.out.println(core.ValidationUtil.getPrixErrorMessage());
            }
        } while (!core.ValidationUtil.isValidPrix(prix));
        
        String imageUrl = null;
        System.out.print("Chemin complet de l'image (ex: C:/images/" + type.toString().toLowerCase() + ".jpg): ");
        String cheminImage = scanner.nextLine();
        
        File imageFile = new File(cheminImage);
        if (imageFile.exists() && imageFile.isFile()) {
            CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
            String folder = type.toString().toLowerCase() + "s";
            imageUrl = cloudinaryService.uploadImage(imageFile, folder);
            
            if (imageUrl == null) {
                System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                imageUrl = imageFile.getName();
            }
        } else {
            System.out.println("Fichier introuvable: " + cheminImage);
            imageUrl = "default_" + type.toString().toLowerCase() + ".jpg";
        }

        Complement complement = new Complement();
        complement.setNom(nom.trim());
        complement.setPrix(prix);
        complement.setTypeComplement(type);
        complement.setImage(imageUrl);

        if (produitService.save(complement)) {
            System.out.println(type + " ajoute avec succes!");
            if (imageUrl != null && imageUrl.startsWith("https://")) {
                System.out.println("   Image disponible sur: " + imageUrl);
            }
        } else {
            System.out.println("Erreur lors de l'ajout!");
        }
    }

    private void listerComplements() {
        System.out.println("\n--- BOISSONS ---");
        for (Produit p : produitService.getBoissons()) {
            System.out.println("  " + p);
        }
        System.out.println("\n--- FRITES ---");
        for (Produit p : produitService.getFrites()) {
            System.out.println("  " + p);
        }
        System.out.println("\n--- SAUCES ---");
        for (Produit p : produitService.getSauces()) {
            System.out.println("  " + p);
        }
        System.out.println("\n--- DESSERTS ---");
        for (Produit p : produitService.getDesserts()) {
            System.out.println("  " + p);
        }
    }

    private void menuMenus() {
        int choix;
        do {
            System.out.println("\n--- MENUS ---");
            System.out.println("1- Creer un menu");
            System.out.println("2- Lister les menus");
            System.out.println("3- Archiver un menu");
            System.out.println("0- Retour");
            System.out.print("Votre choix: ");
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    creerMenu();
                    break;
                case 2:
                    listerMenus();
                    break;
                case 3:
                    archiverProduit();
                    break;
            }
        } while (choix != 0);
    }

    private void creerMenu() {
        System.out.println("\n--- Creer un menu ---");
        
        String nom;
        do {
            System.out.print("Nom du menu: ");
            nom = scanner.nextLine();
            if (nom.trim().isEmpty()) {
                System.out.println("Le nom ne peut pas etre vide!");
                nom = null;
            } else if (nom.trim().length() < 2 || nom.trim().length() > 150) {
                System.out.println("Le nom doit contenir entre 2 et 150 caracteres!");
                nom = null;
            } else if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                System.out.println("Le nom doit contenir uniquement des lettres!");
                nom = null;
            }
        } while (nom == null);

        System.out.println("\n--- Choisir le burger ---");
        ArrayList<Produit> burgers = produitService.getBurgers();
        for (Produit p : burgers) {
            System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
        }
        System.out.print("ID du burger: ");
        int burgerId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n--- Choisir la boisson ---");
        ArrayList<Produit> boissons = produitService.getBoissons();
        for (Produit p : boissons) {
            System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
        }
        System.out.print("ID de la boisson: ");
        int boissonId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n--- Choisir les frites ---");
        ArrayList<Produit> frites = produitService.getFrites();
        for (Produit p : frites) {
            System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
        }
        System.out.print("ID des frites: ");
        int fritesId = scanner.nextInt();
        scanner.nextLine();

        Optional<Produit> burgerOpt = produitService.getById(burgerId);
        Optional<Produit> boissonOpt = produitService.getById(boissonId);
        Optional<Produit> fritesOpt = produitService.getById(fritesId);

        if (burgerOpt.isPresent() && boissonOpt.isPresent() && fritesOpt.isPresent()) {
            String imageUrl = null;
            System.out.print("Chemin complet de l'image (ex: C:/images/menu.jpg): ");
            String cheminImage = scanner.nextLine();
            
            File imageFile = new File(cheminImage);
            if (imageFile.exists() && imageFile.isFile()) {
                CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                imageUrl = cloudinaryService.uploadImage(imageFile, "menus");
                
                if (imageUrl == null) {
                    System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                    imageUrl = imageFile.getName();
                }
            } else {
                System.out.println("Fichier introuvable: " + cheminImage);
                imageUrl = "default_menu.jpg";
            }
            
            Menu menu = new Menu();
            menu.setNom(nom.trim());
            menu.setBurger((Burger) burgerOpt.get());
            menu.setBoisson((Complement) boissonOpt.get());
            menu.setFrites((Complement) fritesOpt.get());
            menu.setImage(imageUrl);
            menu.calculerPrix();

            if (produitService.save(menu)) {
                System.out.println("Menu cree avec succes! Prix: " + menu.getPrix() + " FCFA");
                if (imageUrl != null && imageUrl.startsWith("https://")) {
                    System.out.println("   Image disponible sur: " + imageUrl);
                }
            } else {
                System.out.println("Erreur lors de la creation!");
            }
        } else {
            System.out.println("Produits non trouves!");
        }
    }

    private void listerMenus() {
        System.out.println("\n--- Liste des menus ---");
        ArrayList<Produit> menus = produitService.getMenus();
        if (menus.isEmpty()) {
            System.out.println("Aucun menu enregistre.");
        } else {
            for (Produit p : menus) {
                Menu m = (Menu) p;
                System.out.println(m);
            }
        }
    }

    private void listerTousProduits() {
        System.out.println("\n--- Tous les produits ---");
        ArrayList<Produit> produits = produitService.getAll();
        if (produits.isEmpty()) {
            System.out.println("Aucun produit enregistre.");
        } else {
            for (Produit p : produits) {
                System.out.println(p);
            }
        }
    }

    private void archiverProduit() {
        listerTousProduits();
        System.out.print("\nID du produit a archiver: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Confirmer l'archivage? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (produitService.archive(id)) {
                System.out.println("Produit archive!");
            } else {
                System.out.println("Erreur lors de l'archivage!");
            }
        }
    }
}
