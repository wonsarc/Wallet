package storage;

import model.Category;
import utils.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryStorage implements StorageInterface<Category> {
    private static final Logger logger = Logger.getLogger(CategoryStorage.class.getName());
    private static final String DATABASE_URL = "jdbc:sqlite:wallet.db";
    private final String tableName;

    public CategoryStorage() {
        this.tableName = "categories";
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var stmt = connection.createStatement()) {
            var sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, userId TEXT, name TEXT, [limit] REAL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating table", e);
        }
    }

    @Override
    public void save(Category category) {
        var sql = "INSERT INTO " + tableName + " (id, userId, name, [limit]) VALUES (?, ?, ?, ?)";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getId().toString());
            pstmt.setString(2, category.getUserId().toString());
            pstmt.setString(3, category.getName());
            pstmt.setDouble(4, category.getLimit());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving category", e);
        }
    }

    @Override
    public Category load(UUID id) {
        var sql = "SELECT id, userId, name, [limit] FROM " + tableName + " WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    var categoryId = UUID.fromString(rs.getString("id"));
                    var userId = UUID.fromString(rs.getString("userId"));
                    var name = rs.getString("name");
                    var limit = rs.getDouble("limit");
                    return new Category(categoryId, userId, name, limit);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading category", e);
        }
        return null;
    }

    public Category loadByUserIdAndName(UUID userId, String name) {
        var sql = "SELECT id, userId, name, [limit] FROM " + tableName + " WHERE userId = ? AND name = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId.toString());
            pstmt.setString(2, name);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    var categoryId = UUID.fromString(rs.getString("id"));
                    var categoryName = rs.getString("name");
                    var limit = rs.getDouble("limit");
                    return new Category(categoryId, userId, categoryName, limit);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading category by userId and name", e);
        }
        return null;
    }

    @Override
    public List<Category> loadAll() {
        return loadAllByUserId(null);
    }

    public List<Category> loadAllByUserId(UUID userId) {
        var categories = new ArrayList<Category>();
        var sql = "SELECT id, userId, name, [limit] FROM " + tableName;
        if (userId != null) {
            sql += " WHERE userId = ?";
        }
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            if (userId != null) {
                pstmt.setString(1, userId.toString());
            }
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    var categoryId = UUID.fromString(rs.getString("id"));
                    var userIdFromDb = UUID.fromString(rs.getString("userId"));
                    var name = rs.getString("name");
                    var limit = rs.getDouble("limit");
                    categories.add(new Category(categoryId, userIdFromDb, name, limit));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading categories by userId", e);
        }
        return categories;
    }

    @Override
    public void update(Category category) {
        var sql = "UPDATE " + tableName + " SET name = ?, [limit] = ? WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setDouble(2, category.getLimit());
            pstmt.setString(3, category.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating category", e);
        }
    }

    @Override
    public void delete(UUID id) {
        var sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting category", e);
        }
    }
}
