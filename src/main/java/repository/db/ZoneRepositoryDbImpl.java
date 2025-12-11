package repository.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import core.DatabaseConnection;
import entity.Zone;
import repository.ZoneRepository;

public class ZoneRepositoryDbImpl implements ZoneRepository {

    @Override
    public Zone insert(Zone zone) {
        String sql = "INSERT INTO zones (nom, prix_livraison) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, zone.getNom());
            stmt.setDouble(2, zone.getPrixLivraison());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                zone.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur insertion zone: " + e.getMessage());
        }
        return zone;
    }

    @Override
    public ArrayList<Zone> selectAll() {
        ArrayList<Zone> zones = new ArrayList<>();
        String sql = "SELECT * FROM zones ORDER BY nom";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                zones.add(mapResultSetToZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection zones: " + e.getMessage());
        }
        return zones;
    }

    @Override
    public Optional<Zone> selectById(int id) {
        String sql = "SELECT * FROM zones WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection zone par id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Zone> selectByNom(String nom) {
        String sql = "SELECT * FROM zones WHERE nom = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToZone(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection zone par nom: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Zone update(Zone zone) {
        String sql = "UPDATE zones SET nom = ?, prix_livraison = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, zone.getNom());
            stmt.setDouble(2, zone.getPrixLivraison());
            stmt.setInt(3, zone.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise a jour zone: " + e.getMessage());
        }
        return zone;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM zones WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression zone: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Zone> selectAllActive() {
        return selectAll();
    }

    private Zone mapResultSetToZone(ResultSet rs) throws SQLException {
        Zone zone = new Zone();
        zone.setId(rs.getInt("id"));
        zone.setNom(rs.getString("nom"));
        zone.setPrixLivraison(rs.getDouble("prix_livraison"));
        zone.setActive(true);
        return zone;
    }
}
