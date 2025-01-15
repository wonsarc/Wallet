package model;

import java.util.UUID;

public class Account {
    private final UUID id;
    private final long accountNumber;
    private final UUID userId;
    private double balance;

    public Account(UUID id, UUID userId, long accountNumber, double balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}