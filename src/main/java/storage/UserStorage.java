package storage;

import model.User;
import utils.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserStorage implements StorageInterface<User> {
    private static final Logger logger = Logger.getLogger(UserStorage.class.getName());
    private final String tableName;
    private final DatabaseConnection databaseConnection;

    public UserStorage() {
        this.tableName = "users";
        this.databaseConnection = DatabaseConnection.getInstance("jdbc:sqlite:wallet.db");
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (var connection = databaseConnection.getConnection();
             var stmt = connection.createStatement()) {
            var sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, username TEXT, password TEXT)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating table: {0}", e.getMessage());
        }
    }

    @Override
    public void save(User user) {
        var sql = "INSERT INTO " + tableName + " (id, username, password) VALUES (?, ?, ?)";
        try (var connection = databaseConnection.getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.id().toString());
            pstmt.setString(2, user.username());
            pstmt.setString(3, user.password());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving user: {0}", e.getMessage());
        }
    }

    @Override
    public User load(UUID id) {
        return loadUser("SELECT id, username, password FROM " + tableName + " WHERE id = ?", id.toString());
    }

    public User loadByUsername(String username) {
        return loadUser("SELECT id, username, password FROM " + tableName + " WHERE username = ?", username);
    }

    private User loadUser(String sql, String parameter) {
        try (var connection = databaseConnection.getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, parameter);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    var userId = UUID.fromString(rs.getString("id"));
                    var username = rs.getString("username");
                    var password = rs.getString("password");
                    return new User(userId, username, password);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading user: {0}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> loadAll() {
        var users = new ArrayList<User>();
        var sql = "SELECT id, username, password FROM " + tableName;
        try (var connection = databaseConnection.getConnection();
             var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                var id = UUID.fromString(rs.getString("id"));
                var username = rs.getString("username");
                var password = rs.getString("password");
                users.add(new User(id, username, password));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading all users: {0}", e.getMessage());
        }
        return users;
    }

    @Override
    public void update(User user) {
        var sql = "UPDATE " + tableName + " SET password = ? WHERE id = ?";
        try (var connection = databaseConnection.getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.password());
            pstmt.setString(2, user.id().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user: {0}", e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        var sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (var connection = databaseConnection.getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting user: {0}", e.getMessage());
        }
    }
}
