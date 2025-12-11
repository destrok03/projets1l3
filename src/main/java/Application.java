import java.util.Scanner;

import view.ZoneView;
import view.QuartierView;
import view.LivreurView;
import view.ProduitView;

public class Application {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        afficherLogo();

        int choix;
        do {
            choix = menuPrincipal();
            switch (choix) {
                case 1:
                    new ProduitView(scanner).afficherMenu();
                    break;
                case 2:
                    new ZoneView(scanner).afficherMenu();
                    break;
                case 3:
                    new QuartierView(scanner).afficherMenu();
                    break;
                case 4:
                    new LivreurView(scanner).afficherMenu();
                    break;
                case 0:
                    System.out.println("\nMerci d'avoir utilise Brasil Burger!");
                    System.out.println("A bientot! ");
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        } while (choix != 0);

        scanner.close();
    }

    private static void afficherLogo() {
        System.out.println();
        System.out.println("*************************************************************");
        System.out.println("*                                                           *");
        System.out.println("*                    BRASIL BURGER                          *");
        System.out.println("*                                                           *");
        System.out.println("*              Application Console de Gestion               *");
        System.out.println("*                                                           *");
        System.out.println("*************************************************************");
        System.out.println();
    }

    private static int menuPrincipal() {
        System.out.println("\n========= MENU PRINCIPAL =========");
        System.out.println("1- Gestion des Produits");
        System.out.println("2- Gestion des Zones");
        System.out.println("3- Gestion des Quartiers");
        System.out.println("4- Gestion des Livreurs");
        System.out.println("0- Quitter");
        System.out.println("==================================");
        System.out.print("Votre choix: ");
        return scanner.nextInt();
    }
}
