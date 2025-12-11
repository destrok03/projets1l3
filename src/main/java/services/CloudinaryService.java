package services;

import java.io.File;

/**
 * Service pour gérer l'upload d'images vers Cloudinary
 */
public interface CloudinaryService {
    
    /**
     * Upload une image vers Cloudinary
     * @param imageFile Le fichier image à uploader
     * @param folder Le dossier de destination sur Cloudinary (ex: "burgers", "menus")
     * @return L'URL sécurisée de l'image uploadée, ou null en cas d'erreur
     */
    String uploadImage(File imageFile, String folder);
    
    /**
     * Upload une image vers Cloudinary avec un nom spécifique
     * @param imageFile Le fichier image à uploader
     * @param folder Le dossier de destination
     * @param publicId Le nom public de l'image (sans extension)
     * @return L'URL sécurisée de l'image uploadée, ou null en cas d'erreur
     */
    String uploadImage(File imageFile, String folder, String publicId);
    
    /**
     * Supprime une image de Cloudinary
     * @param publicId L'ID public de l'image (format: folder/nom_image)
     * @return true si la suppression a réussi, false sinon
     */
    boolean deleteImage(String publicId);
    
    /**
     * Extrait l'ID public d'une URL Cloudinary
     * @param imageUrl L'URL complète de l'image
     * @return L'ID public de l'image
     */
    String extractPublicId(String imageUrl);
}
