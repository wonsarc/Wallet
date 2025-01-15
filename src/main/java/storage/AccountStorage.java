package storage;

import model.Account;
import utils.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountStorage implements StorageInterface<Account> {
    private static final Logger logger = Logger.getLogger(AccountStorage.class.getName());
    private static final String DATABASE_URL = "jdbc:sqlite:wallet.db";
    private final String tableName;

    public AccountStorage() {
        this.tableName = "accounts";
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var stmt = connection.createStatement()) {
            var sql = "CREATE TABLE IF NOT EXISTS " + tableName +
                    " (id TEXT PRIMARY KEY, userId TEXT, accountNumber INTEGER UNIQUE, balance REAL)";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating table", e);
        }
    }

    @Override
    public void save(Account account) {
        var sql = "INSERT INTO " + tableName + " (id, userId, accountNumber, balance) VALUES (?, ?, ?, ?)";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getId().toString());
            pstmt.setString(2, account.getUserId().toString());
            pstmt.setLong(3, account.getAccountNumber());
            pstmt.setDouble(4, account.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving account", e);
        }
    }

    @Override
    public Account load(UUID id) {
        var sql = "SELECT id, userId, accountNumber, balance FROM " + tableName + " WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("userId")),
                            rs.getLong("accountNumber"),
                            rs.getDouble("balance")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading account", e);
        }
        return null;
    }

    public Account loadByUserId(UUID userId) {
        var sql = "SELECT id, userId, accountNumber, balance FROM " + tableName + " WHERE userId = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId.toString());
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            UUID.fromString(rs.getString("id")),
                            userId,
                            rs.getLong("accountNumber"),
                            rs.getDouble("balance")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading account by userId", e);
        }
        return null;
    }

    public Account loadByAccountNumber(long accountNumber) {
        var sql = "SELECT id, userId, accountNumber, balance FROM " + tableName + " WHERE accountNumber = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, accountNumber);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            UUID.fromString(rs.getString("id")),
                            UUID.fromString(rs.getString("userId")),
                            rs.getLong("accountNumber"),
                            rs.getDouble("balance")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading account by accountNumber", e);
        }
        return null;
    }

    @Override
    public List<Account> loadAll() {
        var accounts = new ArrayList<Account>();
        var sql = "SELECT id, userId, accountNumber, balance FROM " + tableName;
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var stmt = connection.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(new Account(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("userId")),
                        rs.getLong("accountNumber"),
                        rs.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading all accounts", e);
        }
        return accounts;
    }

    @Override
    public void update(Account account) {
        var sql = "UPDATE " + tableName + " SET balance = ? WHERE id = ?";
        try (var connection = DatabaseConnection.getInstance(DATABASE_URL).getConnection();
             var pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, account.getBalance());
            pstmt.setString(2, account.getId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating account", e);
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
            logger.log(Level.SEVERE, "Error deleting account", e);
        }
    }
}