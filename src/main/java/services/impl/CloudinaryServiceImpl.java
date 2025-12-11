package services.impl;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import services.CloudinaryService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class CloudinaryServiceImpl implements CloudinaryService {
    private Cloudinary cloudinary;
    private boolean isConfigured = false;

    public CloudinaryServiceImpl() {
        initCloudinary();
    }

    private void initCloudinary() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                System.err.println("Fichier application.properties non trouve!");
                return;
            }
            
            props.load(input);
            
            String cloudName = props.getProperty("cloudinary.cloud_name");
            String apiKey = props.getProperty("cloudinary.api_key");
            String apiSecret = props.getProperty("cloudinary.api_secret");
            
            if (cloudName == null || cloudName.equals("your_cloud_name") ||
                apiKey == null || apiKey.equals("your_api_key") ||
                apiSecret == null || apiSecret.equals("your_api_secret")) {
                System.err.println("Cloudinary non configure!");
                System.err.println("Configurez vos credentials dans application.properties");
                System.err.println("   Obtenez-les sur: https://cloudinary.com/console");
                return;
            }
            
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
            ));
            
            isConfigured = true;
            System.out.println("Cloudinary initialise avec succes!");
            System.out.println("   Cloud Name: " + cloudName);
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la configuration Cloudinary: " + e.getMessage());
        }
    }

    @Override
    public String uploadImage(File imageFile, String folder) {
        return uploadImage(imageFile, folder, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String uploadImage(File imageFile, String folder, String publicId) {
        if (!isConfigured) {
            System.err.println("Cloudinary n'est pas configure!");
            return null;
        }
        
        if (imageFile == null || !imageFile.exists()) {
            System.err.println("Fichier image introuvable: " + 
                (imageFile != null ? imageFile.getAbsolutePath() : "null"));
            return null;
        }
        
        if (!imageFile.isFile()) {
            System.err.println("Le chemin specifie n'est pas un fichier: " + imageFile.getAbsolutePath());
            return null;
        }
        
        try {
            System.out.println("Upload de l'image en cours...");
            System.out.println("   Fichier: " + imageFile.getName());
            System.out.println("   Taille: " + (imageFile.length() / 1024) + " KB");
            
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "overwrite", true
            );
            
            if (publicId != null && !publicId.isEmpty()) {
                uploadParams.put("public_id", publicId);
            }
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile, uploadParams);
            
            String imageUrl = (String) uploadResult.get("secure_url");
            String public_id = (String) uploadResult.get("public_id");
            
            System.out.println("Image uploadee avec succes!");
            System.out.println("   URL: " + imageUrl);
            System.out.println("   Public ID: " + public_id);
            
            return imageUrl;
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'upload: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean deleteImage(String publicId) {
        if (!isConfigured) {
            System.err.println("Cloudinary n'est pas configure!");
            return false;
        }
        
        if (publicId == null || publicId.isEmpty()) {
            System.err.println("Public ID invalide");
            return false;
        }
        
        try {
            System.out.println("Suppression de l'image: " + publicId);
            
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");
            
            if ("ok".equals(resultStatus)) {
                System.out.println("Image supprimee avec succes!");
                return true;
            } else {
                System.err.println("Suppression echouee: " + resultStatus);
                return false;
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }
            
            String pathAfterUpload = parts[1];
            String[] pathParts = pathAfterUpload.split("/", 2);
            if (pathParts.length < 2) {
                return null;
            }
            
            String publicIdWithExtension = pathParts[1];
            int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return publicIdWithExtension.substring(0, lastDotIndex);
            }
            
            return publicIdWithExtension;
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction du public ID: " + e.getMessage());
            return null;
        }
    }

    public boolean isConfigured() {
        return isConfigured;
    }
}
