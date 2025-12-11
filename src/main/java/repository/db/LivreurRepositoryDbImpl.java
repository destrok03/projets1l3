package repository.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import core.DatabaseConnection;
import entity.Livreur;
import entity.Zone;
import repository.LivreurRepository;

public class LivreurRepositoryDbImpl implements LivreurRepository {

    @Override
    public Livreur insert(Livreur livreur) {
        String sql = "INSERT INTO livreurs (nom, prenom, telephone, disponible) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livreur.getNom());
            stmt.setString(2, livreur.getPrenom());
            stmt.setString(3, livreur.getTelephone());
            stmt.setBoolean(4, livreur.isDisponible());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                livreur.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur insertion livreur: " + e.getMessage());
        }
        return livreur;
    }

    @Override
    public ArrayList<Livreur> selectAll() {
        ArrayList<Livreur> livreurs = new ArrayList<>();
        String sql = "SELECT * FROM livreurs ORDER BY nom, prenom";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Livreur livreur = mapResultSetToLivreur(rs);
                chargerZones(livreur);
                livreurs.add(livreur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection livreurs: " + e.getMessage());
        }
        return livreurs;
    }

    @Override
    public Optional<Livreur> selectById(int id) {
        String sql = "SELECT * FROM livreurs WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Livreur livreur = mapResultSetToLivreur(rs);
                chargerZones(livreur);
                return Optional.of(livreur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection livreur par id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Livreur> selectByTelephone(String telephone) {
        String sql = "SELECT * FROM livreurs WHERE telephone = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, telephone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Livreur livreur = mapResultSetToLivreur(rs);
                chargerZones(livreur);
                return Optional.of(livreur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection livreur par telephone: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Livreur update(Livreur livreur) {
        String sql = "UPDATE livreurs SET nom = ?, prenom = ?, telephone = ?, disponible = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, livreur.getNom());
            stmt.setString(2, livreur.getPrenom());
            stmt.setString(3, livreur.getTelephone());
            stmt.setBoolean(4, livreur.isDisponible());
            stmt.setInt(5, livreur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise a jour livreur: " + e.getMessage());
        }
        return livreur;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM livreurs WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression livreur: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Livreur> selectDisponibles() {
        ArrayList<Livreur> livreurs = new ArrayList<>();
        String sql = "SELECT * FROM livreurs WHERE disponible = true ORDER BY nom, prenom";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Livreur livreur = mapResultSetToLivreur(rs);
                chargerZones(livreur);
                livreurs.add(livreur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection livreurs disponibles: " + e.getMessage());
        }
        return livreurs;
    }

    @Override
    public boolean affecterZone(int livreurId, int zoneId) {
        String sql = "INSERT INTO livreur_zones (livreur_id, zone_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livreurId);
            stmt.setInt(2, zoneId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur affectation zone: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean retirerZone(int livreurId, int zoneId) {
        String sql = "DELETE FROM livreur_zones WHERE livreur_id = ? AND zone_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livreurId);
            stmt.setInt(2, zoneId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur retrait zone: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Livreur> selectByZoneId(int zoneId) {
        ArrayList<Livreur> livreurs = new ArrayList<>();
        String sql = "SELECT l.* FROM livreurs l " +
                     "JOIN livreur_zones lz ON l.id = lz.livreur_id " +
                     "WHERE lz.zone_id = ? ORDER BY l.nom, l.prenom";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, zoneId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Livreur livreur = mapResultSetToLivreur(rs);
                chargerZones(livreur);
                livreurs.add(livreur);
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection livreurs par zone: " + e.getMessage());
        }
        return livreurs;
    }

    private void chargerZones(Livreur livreur) {
        String sql = "SELECT z.* FROM zones z " +
                     "JOIN livreur_zones lz ON z.id = lz.zone_id " +
                     "WHERE lz.livreur_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, livreur.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Zone zone = new Zone();
                zone.setId(rs.getInt("id"));
                zone.setNom(rs.getString("nom"));
                zone.setPrixLivraison(rs.getDouble("prix_livraison"));
                zone.setActive(true);
                livreur.addZone(zone);
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement zones livreur: " + e.getMessage());
        }
    }

    private Livreur mapResultSetToLivreur(ResultSet rs) throws SQLException {
        Livreur livreur = new Livreur();
        livreur.setId(rs.getInt("id"));
        livreur.setNom(rs.getString("nom"));
        livreur.setPrenom(rs.getString("prenom"));
        livreur.setTelephone(rs.getString("telephone"));
        livreur.setDisponible(rs.getBoolean("disponible"));
        return livreur;
    }
}
