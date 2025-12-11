package core;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        loadProperties();
    }

    private DatabaseConnection() {
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Fichier application.properties non trouvé!");
                // Valeurs par défaut
                URL = "jdbc:postgresql://localhost:5432/brasil_burger";
                USER = "postgres";
                PASSWORD = "amina2003@";
                DRIVER = "org.postgresql.Driver";
                return;
            }
            props.load(input);
            URL = props.getProperty("db.url", "jdbc:postgresql://localhost:5432/brasil_burger");
            USER = props.getProperty("db.username", "postgres");
            PASSWORD = props.getProperty("db.password", "amina2003@");
            DRIVER = props.getProperty("db.driver", "org.postgresql.Driver");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la configuration: " + e.getMessage());
            // Valeurs par défaut en cas d'erreur
            URL = "jdbc:postgresql://localhost:5432/brasil_burger";
            USER = "postgres";
            PASSWORD = "amina2003@";
            DRIVER = "org.postgresql.Driver";
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver PostgreSQL non trouve: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
