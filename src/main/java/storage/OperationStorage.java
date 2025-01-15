package storage;

import model.Operation;
import utils.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OperationStorage implements StorageInterface<Operation> {
    private static final Logger logger = Logger.getLogger(OperationStorage.class.getName());
    private static final String DATABASE_URL = "jdbc:sqlite:wallet.db";
    private final String tableName;

    public OperationStorage() {
        this.tableName = "operations";
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var stmt = connection.createStatement()) {
            var sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, accountId TEXT, categoryId TEXT, type TEXT, amount REAL, date TEXT)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating table", e);
        }
    }

    @Override
    public void save(Operation operation) {
        var sql = "INSERT INTO " + tableName + " (id, accountId, categoryId, type, amount, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, operation.id().toString());
            pstmt.setString(2, operation.accountId().toString());
            pstmt.setString(3, operation.categoryId() != null ? operation.categoryId().toString() : null);
            pstmt.setString(4, operation.type());
            pstmt.setDouble(5, operation.amount());
            pstmt.setString(6, operation.date());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving operation", e);
        }
    }

    @Override
    public List<Operation> loadAll() {
        return loadAllByAccountId(null);
    }

    public List<Operation> loadAllByAccountId(UUID accountId) {
        var operations = new ArrayList<Operation>();
        var sql = "SELECT id, accountId, categoryId, type, amount, date FROM " + tableName;
        if (accountId != null) {
            sql += " WHERE accountId = ?";
        }
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            if (accountId != null) {
                pstmt.setString(1, accountId.toString());
            }
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    var operationId = UUID.fromString(rs.getString("id"));
                    var categoryId = rs.getString("categoryId") != null ? UUID.fromString(rs.getString("categoryId")) : null;
                    var type = rs.getString("type");
                    var amount = rs.getDouble("amount");
                    var date = rs.getString("date");
                    operations.add(new Operation(operationId, accountId, categoryId, type, amount, date));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading operations by accountId", e);
        }
        return operations;
    }

    @Override
    public Operation load(UUID id) {
        var sql = "SELECT id, accountId, categoryId, type, amount, date FROM " + tableName + " WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    var operationId = UUID.fromString(rs.getString("id"));
                    var accountId = UUID.fromString(rs.getString("accountId"));
                    var categoryId = rs.getString("categoryId") != null ? UUID.fromString(rs.getString("categoryId")) : null;
                    var type = rs.getString("type");
                    var amount = rs.getDouble("amount");
                    var date = rs.getString("date");
                    return new Operation(operationId, accountId, categoryId, type, amount, date);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading operation", e);
        }
        return null;
    }

    @Override
    public void update(Operation operation) {
        var sql = "UPDATE " + tableName + " SET type = ?, amount = ?, categoryId = ?, date = ? WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, operation.type());
            pstmt.setDouble(2, operation.amount());
            pstmt.setString(3, operation.categoryId() != null ? operation.categoryId().toString() : null);
            pstmt.setString(4, operation.date());
            pstmt.setString(5, operation.id().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating operation", e);
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
            logger.log(Level.SEVERE, "Error deleting operation", e);
        }
    }
}
