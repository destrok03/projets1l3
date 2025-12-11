package repository.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import core.DatabaseConnection;
import entity.*;
import repository.ProduitRepository;

public class ProduitRepositoryDbImpl implements ProduitRepository {

    public ProduitRepositoryDbImpl() {
    }

    @Override
    public Produit insert(Produit produit) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "INSERT INTO produits (nom, prix, image, type_produit, description, ingredients, " +
                     "type_complement, burger_id, boisson_id, frites_id, archived) " +
                     "VALUES (?, ?, ?, ?::type_produit_enum, ?, ?, ?::type_complement_enum, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setString(3, produit.getImage());
            stmt.setString(4, produit.getTypeProduit().name());

            if (produit instanceof Burger) {
                Burger burger = (Burger) produit;
                stmt.setString(5, burger.getDescription());
                stmt.setString(6, burger.getIngredients());
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
            } else if (produit instanceof Complement) {
                Complement complement = (Complement) produit;
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setString(7, complement.getTypeComplement().name());
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
            } else if (produit instanceof Menu) {
                Menu menu = (Menu) produit;
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setInt(8, menu.getBurger() != null ? menu.getBurger().getId() : 0);
                stmt.setInt(9, menu.getBoisson() != null ? menu.getBoisson().getId() : 0);
                stmt.setInt(10, menu.getFrites() != null ? menu.getFrites().getId() : 0);
            } else {
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
                stmt.setNull(10, Types.INTEGER);
            }

            stmt.setBoolean(11, produit.isArchived());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                produit.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erreur insertion produit: " + e.getMessage());
        }
        return produit;
    }

    @Override
    public ArrayList<Produit> selectAll() {
        Connection connection = DatabaseConnection.getConnection();
        ArrayList<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE archived = false ORDER BY type_produit, nom";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection produits: " + e.getMessage());
        }
        return produits;
    }

    @Override
    public Optional<Produit> selectById(int id) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM produits WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection produit par id: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Produit update(Produit produit) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "UPDATE produits SET nom = ?, prix = ?, image = ?, description = ?, " +
                     "ingredients = ?, type_complement = ?::type_complement_enum, burger_id = ?, boisson_id = ?, frites_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setDouble(2, produit.getPrix());
            stmt.setString(3, produit.getImage());

            if (produit instanceof Burger) {
                Burger burger = (Burger) produit;
                stmt.setString(4, burger.getDescription());
                stmt.setString(5, burger.getIngredients());
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.INTEGER);
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
            } else if (produit instanceof Complement) {
                Complement complement = (Complement) produit;
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setString(6, complement.getTypeComplement().name());
                stmt.setNull(7, Types.INTEGER);
                stmt.setNull(8, Types.INTEGER);
                stmt.setNull(9, Types.INTEGER);
            } else if (produit instanceof Menu) {
                Menu menu = (Menu) produit;
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
                stmt.setInt(7, menu.getBurger() != null ? menu.getBurger().getId() : 0);
                stmt.setInt(8, menu.getBoisson() != null ? menu.getBoisson().getId() : 0);
                stmt.setInt(9, menu.getFrites() != null ? menu.getFrites().getId() : 0);
            }

            stmt.setInt(10, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur mise a jour produit: " + e.getMessage());
        }
        return produit;
    }

    @Override
    public boolean archive(int id) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "UPDATE produits SET archived = true WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur archivage produit: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Produit> selectByType(TypeProduit type) {
        Connection connection = DatabaseConnection.getConnection();
        ArrayList<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE type_produit = ?::type_produit_enum AND archived = false ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection produits par type: " + e.getMessage());
        }
        return produits;
    }

    @Override
    public ArrayList<Produit> selectByTypeComplement(TypeComplement typeComplement) {
        Connection connection = DatabaseConnection.getConnection();
        ArrayList<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produits WHERE type_produit = 'COMPLEMENT'::type_produit_enum AND type_complement = ?::type_complement_enum AND archived = false ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeComplement.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur selection complements par type: " + e.getMessage());
        }
        return produits;
    }

    @Override
    public ArrayList<Produit> selectDisponibles() {
        return selectAll();
    }

    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        String typeProduit = rs.getString("type_produit");
        Produit produit;

        switch (TypeProduit.valueOf(typeProduit)) {
            case BURGER:
                Burger burger = new Burger();
                burger.setDescription(rs.getString("description"));
                burger.setIngredients(rs.getString("ingredients"));
                produit = burger;
                break;
            case COMPLEMENT:
                Complement complement = new Complement();
                String typeComp = rs.getString("type_complement");
                if (typeComp != null) {
                    complement.setTypeComplement(TypeComplement.valueOf(typeComp));
                }
                produit = complement;
                break;
            case MENU:
                Menu menu = new Menu();
                int burgerId = rs.getInt("burger_id");
                int boissonId = rs.getInt("boisson_id");
                int fritesId = rs.getInt("frites_id");

                if (burgerId > 0) {
                    Optional<Produit> burgerOpt = selectById(burgerId);
                    burgerOpt.ifPresent(p -> menu.setBurger((Burger) p));
                }
                if (boissonId > 0) {
                    Optional<Produit> boissonOpt = selectById(boissonId);
                    boissonOpt.ifPresent(p -> menu.setBoisson((Complement) p));
                }
                if (fritesId > 0) {
                    Optional<Produit> fritesOpt = selectById(fritesId);
                    fritesOpt.ifPresent(p -> menu.setFrites((Complement) p));
                }
                produit = menu;
                break;
            default:
                produit = new Produit();
        }

        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setImage(rs.getString("image"));
        produit.setTypeProduit(TypeProduit.valueOf(typeProduit));
        produit.setArchived(rs.getBoolean("archived"));

        return produit;
    }
}
