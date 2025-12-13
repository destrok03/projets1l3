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
            choix = -1;
            boolean saisieValide = false;
            
            while (!saisieValide) {
                try {
                    System.out.println("\n========== GESTION DES PRODUITS ==========");
                    System.out.println("1- Gestion des Burgers");
                    System.out.println("2- Gestion des Complements");
                    System.out.println("3- Gestion des Menus");
                    System.out.println("4- Lister tous les produits");
                    System.out.println("0- Retour");
                    System.out.print("Votre choix: ");
                    choix = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (choix >= 0 && choix <= 4) {
                        saisieValide = true;
                    } else {
                        System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre entre 0 et 4!");
                    }
                } catch (Exception e) {
                    System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre valide!");
                    scanner.nextLine();
                }
            }

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
            }
        } while (choix != 0);
    }

    private void menuBurgers() {
        int choix;
        do {
            choix = -1;
            boolean saisieValide = false;
            
            while (!saisieValide) {
                try {
                    System.out.println("\n--- BURGERS ---");
                    System.out.println("1- Ajouter un burger");
                    System.out.println("2- Lister les burgers");
                    System.out.println("3- Modifier un burger");
                    System.out.println("4- Archiver un burger");
                    System.out.println("0- Retour");
                    System.out.print("Votre choix: ");
                    choix = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (choix >= 0 && choix <= 4) {
                        saisieValide = true;
                    } else {
                        System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre entre 0 et 4!");
                    }
                } catch (Exception e) {
                    System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre valide!");
                    scanner.nextLine();
                }
            }

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
                    archiverBurger();
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
            } else if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                System.out.println("Le nom ne doit contenir que des lettres (pas de chiffres)!");
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
        boolean imageValide = false;
        while (!imageValide) {
            System.out.print("Chemin complet de l'image (ex: C:/images/burger.jpg): ");
            String cheminImage = scanner.nextLine();
            
            if (cheminImage.trim().isEmpty()) {
                System.out.println("⚠️  Le chemin de l'image est obligatoire!");
                continue;
            }
            
            File imageFile = new File(cheminImage);
            if (imageFile.exists() && imageFile.isFile()) {
                CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                imageUrl = cloudinaryService.uploadImage(imageFile, "burgers");
                
                if (imageUrl == null) {
                    System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                    imageUrl = imageFile.getName();
                }
                imageValide = true;
            } else {
                System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                System.out.println("Veuillez fournir un chemin valide.");
            }
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
        ArrayList<Produit> burgers = produitService.getBurgers();
        listerBurgers();
        
        if (burgers.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du burger a modifier: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : burgers) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        Optional<Produit> opt = produitService.getById(id);
        if (opt.isPresent() && opt.get() instanceof Burger) {
            Burger burger = (Burger) opt.get();

            System.out.print("Nouveau nom (" + burger.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) {
                if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                    System.out.println("⚠️  Le nom ne doit contenir que des lettres (pas de chiffres)! Modification du nom annulee.");
                } else {
                    burger.setNom(nom);
                }
            }

            System.out.print("Nouveau prix (" + burger.getPrix() + "): ");
            String prixStr = scanner.nextLine();
            if (!prixStr.isEmpty()) burger.setPrix(Double.parseDouble(prixStr));

            System.out.print("Nouvelle description: ");
            String desc = scanner.nextLine();
            if (!desc.isEmpty()) burger.setDescription(desc);

            System.out.print("Nouveaux ingredients: ");
            String ing = scanner.nextLine();
            if (!ing.isEmpty()) burger.setIngredients(ing);

            System.out.print("Modifier l'image? (o/n): ");
            String modifImage = scanner.nextLine();
            if (modifImage.equalsIgnoreCase("o")) {
                boolean imageValide = false;
                while (!imageValide) {
                    System.out.print("Chemin complet de l'image (ex: C:/images/burger.jpg): ");
                    String cheminImage = scanner.nextLine();
                    
                    File imageFile = new File(cheminImage);
                    if (imageFile.exists() && imageFile.isFile()) {
                        CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                        String imageUrl = cloudinaryService.uploadImage(imageFile, "burgers");
                        
                        if (imageUrl != null) {
                            burger.setImage(imageUrl);
                            System.out.println("Image mise a jour avec succes!");
                            System.out.println("   Image disponible sur: " + imageUrl);
                        } else {
                            System.out.println("Erreur d'upload. Image non modifiee.");
                        }
                        imageValide = true;
                    } else {
                        System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                        System.out.println("Veuillez fournir un chemin valide.");
                    }
                }
            }

            produitService.update(burger);
            System.out.println("Burger modifie avec succes!");
        } else {
            System.out.println("Burger non trouve!");
        }
    }

    private void menuComplements() {
        int choix;
        do {
            choix = -1;
            boolean saisieValide = false;
            
            while (!saisieValide) {
                try {
                    System.out.println("\n--- COMPLEMENTS ---");
                    System.out.println("1- Ajouter une boisson");
                    System.out.println("2- Ajouter des frites");
                    System.out.println("3- Lister les complements");
                    System.out.println("4- Modifier un complement");
                    System.out.println("5- Archiver un complement");
                    System.out.println("0- Retour");
                    System.out.print("Votre choix: ");
                    choix = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (choix >= 0 && choix <= 5) {
                        saisieValide = true;
                    } else {
                        System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre entre 0 et 5!");
                    }
                } catch (Exception e) {
                    System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre valide!");
                    scanner.nextLine();
                }
            }

            switch (choix) {
                case 1:
                    ajouterComplement(TypeComplement.BOISSON);
                    break;
                case 2:
                    ajouterComplement(TypeComplement.FRITES);
                    break;
                case 3:
                    listerComplements();
                    break;
                case 4:
                    modifierComplement();
                    break;
                case 5:
                    archiverComplement();
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
                System.out.println("Le nom ne doit contenir que des lettres (pas de chiffres)!");
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
        boolean imageValide = false;
        while (!imageValide) {
            System.out.print("Chemin complet de l'image (ex: C:/images/" + type.toString().toLowerCase() + ".jpg): ");
            String cheminImage = scanner.nextLine();
            
            if (cheminImage.trim().isEmpty()) {
                System.out.println("⚠️  Le chemin de l'image est obligatoire!");
                continue;
            }
            
            File imageFile = new File(cheminImage);
            if (imageFile.exists() && imageFile.isFile()) {
                CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                String folder = type.toString().toLowerCase() + "s";
                imageUrl = cloudinaryService.uploadImage(imageFile, folder);
                
                if (imageUrl == null) {
                    System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                    imageUrl = imageFile.getName();
                }
                imageValide = true;
            } else {
                System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                System.out.println("Veuillez fournir un chemin valide.");
            }
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
    }

    private void modifierComplement() {
        ArrayList<Produit> complements = produitService.getComplements();
        listerComplements();
        
        if (complements.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du complement a modifier: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : complements) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        Optional<Produit> opt = produitService.getById(id);
        if (opt.isPresent() && opt.get() instanceof Complement) {
            Complement complement = (Complement) opt.get();

            System.out.print("Nouveau nom (" + complement.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) {
                if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                    System.out.println("⚠️  Le nom ne doit contenir que des lettres (pas de chiffres)! Modification du nom annulee.");
                } else {
                    complement.setNom(nom);
                }
            }

            System.out.print("Nouveau prix (" + complement.getPrix() + "): ");
            String prixStr = scanner.nextLine();
            if (!prixStr.isEmpty()) complement.setPrix(Double.parseDouble(prixStr));

            System.out.print("Modifier l'image? (o/n): ");
            String modifImage = scanner.nextLine();
            if (modifImage.equalsIgnoreCase("o")) {
                boolean imageValide = false;
                while (!imageValide) {
                    System.out.print("Chemin complet de l'image: ");
                    String cheminImage = scanner.nextLine();
                    
                    File imageFile = new File(cheminImage);
                    if (imageFile.exists() && imageFile.isFile()) {
                        CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                        String folder = complement.getTypeComplement().toString().toLowerCase() + "s";
                        String imageUrl = cloudinaryService.uploadImage(imageFile, folder);
                        
                        if (imageUrl != null) {
                            complement.setImage(imageUrl);
                            System.out.println("Image mise a jour avec succes!");
                            System.out.println("   Image disponible sur: " + imageUrl);
                        } else {
                            System.out.println("Erreur d'upload. Image non modifiee.");
                        }
                        imageValide = true;
                    } else {
                        System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                        System.out.println("Veuillez fournir un chemin valide.");
                    }
                }
            }

            produitService.update(complement);
            System.out.println("Complement modifie avec succes!");
        } else {
            System.out.println("Complement non trouve!");
        }
    }

    private void menuMenus() {
        int choix;
        do {
            choix = -1;
            boolean saisieValide = false;
            
            while (!saisieValide) {
                try {
                    System.out.println("\n--- MENUS ---");
                    System.out.println("1- Creer un menu");
                    System.out.println("2- Lister les menus");
                    System.out.println("3- Modifier un menu");
                    System.out.println("4- Archiver un menu");
                    System.out.println("0- Retour");
                    System.out.print("Votre choix: ");
                    choix = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (choix >= 0 && choix <= 4) {
                        saisieValide = true;
                    } else {
                        System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre entre 0 et 4!");
                    }
                } catch (Exception e) {
                    System.out.println("\n⚠️  Erreur: Veuillez saisir un nombre valide!");
                    scanner.nextLine();
                }
            }

            switch (choix) {
                case 1:
                    creerMenu();
                    break;
                case 2:
                    listerMenus();
                    break;
                case 3:
                    modifierMenu();
                    break;
                case 4:
                    archiverMenu();
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
                System.out.println("Le nom ne doit contenir que des lettres (pas de chiffres)!");
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
            boolean imageValide = false;
            while (!imageValide) {
                System.out.print("Chemin complet de l'image (ex: C:/images/menu.jpg): ");
                String cheminImage = scanner.nextLine();
                
                if (cheminImage.trim().isEmpty()) {
                    System.out.println("⚠️  Le chemin de l'image est obligatoire!");
                    continue;
                }
                
                File imageFile = new File(cheminImage);
                if (imageFile.exists() && imageFile.isFile()) {
                    CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                    imageUrl = cloudinaryService.uploadImage(imageFile, "menus");
                    
                    if (imageUrl == null) {
                        System.out.println("Erreur d'upload. Utilisation du nom de fichier local.");
                        imageUrl = imageFile.getName();
                    }
                    imageValide = true;
                } else {
                    System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                    System.out.println("Veuillez fournir un chemin valide.");
                }
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

    private void modifierMenu() {
        ArrayList<Produit> menus = produitService.getMenus();
        listerMenus();
        
        if (menus.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du menu a modifier: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : menus) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        Optional<Produit> opt = produitService.getById(id);
        if (opt.isPresent() && opt.get() instanceof Menu) {
            Menu menu = (Menu) opt.get();

            System.out.print("Nouveau nom (" + menu.getNom() + "): ");
            String nom = scanner.nextLine();
            if (!nom.isEmpty()) {
                if (!nom.trim().matches("[a-zA-ZÀ-ÿ\\s'-]+")) {
                    System.out.println("⚠️  Le nom ne doit contenir que des lettres (pas de chiffres)! Modification du nom annulee.");
                } else {
                    menu.setNom(nom);
                }
            }

            System.out.print("Modifier le burger? (o/n): ");
            String modifBurger = scanner.nextLine();
            if (modifBurger.equalsIgnoreCase("o")) {
                System.out.println("\n--- Choisir le nouveau burger ---");
                ArrayList<Produit> burgers = produitService.getBurgers();
                for (Produit p : burgers) {
                    System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
                }
                System.out.print("ID du burger: ");
                int burgerId = scanner.nextInt();
                scanner.nextLine();
                Optional<Produit> burgerOpt = produitService.getById(burgerId);
                if (burgerOpt.isPresent()) {
                    menu.setBurger((Burger) burgerOpt.get());
                }
            }

            System.out.print("Modifier la boisson? (o/n): ");
            String modifBoisson = scanner.nextLine();
            if (modifBoisson.equalsIgnoreCase("o")) {
                System.out.println("\n--- Choisir la nouvelle boisson ---");
                ArrayList<Produit> boissons = produitService.getBoissons();
                for (Produit p : boissons) {
                    System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
                }
                System.out.print("ID de la boisson: ");
                int boissonId = scanner.nextInt();
                scanner.nextLine();
                Optional<Produit> boissonOpt = produitService.getById(boissonId);
                if (boissonOpt.isPresent()) {
                    menu.setBoisson((Complement) boissonOpt.get());
                }
            }

            System.out.print("Modifier les frites? (o/n): ");
            String modifFrites = scanner.nextLine();
            if (modifFrites.equalsIgnoreCase("o")) {
                System.out.println("\n--- Choisir les nouvelles frites ---");
                ArrayList<Produit> frites = produitService.getFrites();
                for (Produit p : frites) {
                    System.out.println(p.getId() + "- " + p.getNom() + " (" + p.getPrix() + " FCFA)");
                }
                System.out.print("ID des frites: ");
                int fritesId = scanner.nextInt();
                scanner.nextLine();
                Optional<Produit> fritesOpt = produitService.getById(fritesId);
                if (fritesOpt.isPresent()) {
                    menu.setFrites((Complement) fritesOpt.get());
                }
            }

            System.out.print("Modifier l'image? (o/n): ");
            String modifImage = scanner.nextLine();
            if (modifImage.equalsIgnoreCase("o")) {
                boolean imageValide = false;
                while (!imageValide) {
                    System.out.print("Chemin complet de l'image: ");
                    String cheminImage = scanner.nextLine();
                    
                    File imageFile = new File(cheminImage);
                    if (imageFile.exists() && imageFile.isFile()) {
                        CloudinaryService cloudinaryService = new CloudinaryServiceImpl();
                        String imageUrl = cloudinaryService.uploadImage(imageFile, "menus");
                        
                        if (imageUrl != null) {
                            menu.setImage(imageUrl);
                            System.out.println("Image mise a jour avec succes!");
                            System.out.println("   Image disponible sur: " + imageUrl);
                        } else {
                            System.out.println("Erreur d'upload. Image non modifiee.");
                        }
                        imageValide = true;
                    } else {
                        System.out.println("⚠️  Fichier introuvable: " + cheminImage);
                        System.out.println("Veuillez fournir un chemin valide.");
                    }
                }
            }

            menu.calculerPrix();
            produitService.update(menu);
            System.out.println("Menu modifie avec succes! Nouveau prix: " + menu.getPrix() + " FCFA");
        } else {
            System.out.println("Menu non trouve!");
        }
    }

    private void archiverBurger() {
        ArrayList<Produit> burgers = produitService.getBurgers();
        listerBurgers();
        
        if (burgers.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du burger a archiver: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : burgers) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        String confirm = "";
        while (!confirm.equalsIgnoreCase("o") && !confirm.equalsIgnoreCase("n")) {
            System.out.print("Confirmer l'archivage? (o/n): ");
            confirm = scanner.nextLine();
            if (!confirm.equalsIgnoreCase("o") && !confirm.equalsIgnoreCase("n")) {
                System.out.println("⚠️  Erreur: Veuillez saisir 'o' ou 'n'!");
            }
        }
        if (confirm.equalsIgnoreCase("o")) {
            if (produitService.archive(id)) {
                System.out.println("✓ Burger archive avec succes!");
            } else {
                System.out.println("⚠️  Erreur lors de l'archivage!");
            }
        } else {
            System.out.println("Archivage annule.");
        }
    }

    private void archiverComplement() {
        ArrayList<Produit> complements = produitService.getComplements();
        listerComplements();
        
        if (complements.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du complement a archiver: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : complements) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        System.out.print("Confirmer l'archivage? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (produitService.archive(id)) {
                System.out.println("✓ Complement archive avec succes!");
            } else {
                System.out.println("⚠️  Erreur lors de l'archivage!");
            }
        } else {
            System.out.println("Archivage annule.");
        }
    }

    private void archiverMenu() {
        ArrayList<Produit> menus = produitService.getMenus();
        listerMenus();
        
        if (menus.isEmpty()) {
            return;
        }
        
        int id = -1;
        boolean idValide = false;
        while (!idValide) {
            try {
                System.out.print("\nID du menu a archiver: ");
                String idStr = scanner.nextLine();
                id = Integer.parseInt(idStr);
                
                // Vérifier si l'ID existe dans la liste
                boolean existe = false;
                for (Produit p : menus) {
                    if (p.getId() == id) {
                        existe = true;
                        break;
                    }
                }
                
                if (existe) {
                    idValide = true;
                } else {
                    System.out.println("⚠️  Erreur: Cet ID n'existe pas dans la liste. Veuillez choisir un ID valide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Erreur: Veuillez saisir un nombre valide (pas de lettres)!");
            }
        }

        System.out.print("Confirmer l'archivage? (o/n): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("o")) {
            if (produitService.archive(id)) {
                System.out.println("✓ Menu archive avec succes!");
            } else {
                System.out.println("⚠️  Erreur lors de l'archivage!");
            }
        } else {
            System.out.println("Archivage annule.");
        }
    }
}
