package repository.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import core.DatabaseConnection;
import entity.Quartier;
import entity.Zone;
import repository.QuartierRepository;

public class QuartierRepositoryDbImpl implements QuartierRepository {

    @Override
    public Quartier insert(Quartier quartier) {
        String sql = "INSERT INTO quartiers (nom, zone_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quartier.getNom());
            stmt.setInt(2, quartier.getZone().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                quartier.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur insertion quartier: " + e.getMessage());
        }
        return quartier;
    }

    @Override
    public ArrayList<Quartier> selectAll() {
        ArrayList<Quartier> quartiers = new ArrayList<>();
        String sql = "SELECT q.*, z.nom as zone_nom, z.prix_livraison " +
                     "FROM quartiers q JOIN zones z ON q.zone_id = z.id ORDER BY q.nom";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quartiers.add(mapResultSetToQuartier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection quartiers: " + e.getMessage());
        }
        return quartiers;
    }

    @Override
    public Optional<Quartier> selectById(int id) {
        String sql = "SELECT q.*, z.nom as zone_nom, z.prix_livraison " +
                     "FROM quartiers q JOIN zones z ON q.zone_id = z.id WHERE q.id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToQuartier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection quartier par id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Quartier> selectByZoneId(int zoneId) {
        ArrayList<Quartier> quartiers = new ArrayList<>();
        String sql = "SELECT q.*, z.nom as zone_nom, z.prix_livraison " +
                     "FROM quartiers q JOIN zones z ON q.zone_id = z.id WHERE q.zone_id = ? ORDER BY q.nom";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, zoneId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quartiers.add(mapResultSetToQuartier(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection quartiers par zone: " + e.getMessage());
        }
        return quartiers;
    }

    @Override
    public Quartier update(Quartier quartier) {
        String sql = "UPDATE quartiers SET nom = ?, zone_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quartier.getNom());
            stmt.setInt(2, quartier.getZone().getId());
            stmt.setInt(3, quartier.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise a jour quartier: " + e.getMessage());
        }
        return quartier;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM quartiers WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur suppression quartier: " + e.getMessage());
        }
        return false;
    }

    private Quartier mapResultSetToQuartier(ResultSet rs) throws SQLException {
        Quartier quartier = new Quartier();
        quartier.setId(rs.getInt("id"));
        quartier.setNom(rs.getString("nom"));

        Zone zone = new Zone();
        zone.setId(rs.getInt("zone_id"));
        zone.setNom(rs.getString("zone_nom"));
        zone.setPrixLivraison(rs.getDouble("prix_livraison"));
        zone.setActive(true);
        quartier.setZone(zone);

        return quartier;
    }
}
