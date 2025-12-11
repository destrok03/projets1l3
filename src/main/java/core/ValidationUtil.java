package core;

public class ValidationUtil {
    
    public static boolean isValidTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        
        telephone = telephone.trim();
        
        if (telephone.length() != 9) {
            return false;
        }
        
        if (!telephone.matches("\\d{9}")) {
            return false;
        }
        
        String prefix = telephone.substring(0, 2);
        return prefix.equals("70") || prefix.equals("76") || 
               prefix.equals("77") || prefix.equals("78");
    }
    
    public static boolean isValidNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }
        
        nom = nom.trim();
        
        if (nom.length() < 2 || nom.length() > 100) {
            return false;
        }
        
        return nom.matches("[a-zA-ZÀ-ÿ\\s'-]+");
    }
    
    public static boolean isValidPrenom(String prenom) {
        return isValidNom(prenom);
    }
    
    public static boolean isValidPrix(double prix) {
        return prix > 0 && prix <= 1000000;
    }
    
    public static String getTelephoneErrorMessage() {
        return "Telephone invalide! Format: 9 chiffres commencant par 70, 76, 77 ou 78";
    }
    
    public static String getNomErrorMessage() {
        return "Nom invalide! Uniquement des lettres (2-100 caracteres)";
    }
    
    public static String getPrenomErrorMessage() {
        return "Prenom invalide! Uniquement des lettres (2-100 caracteres)";
    }
    
    public static String getPrixErrorMessage() {
        return "Prix invalide! Doit etre superieur a 0 et inferieur a 1 000 000 FCFA";
    }
}
