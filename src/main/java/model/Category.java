package model;

import java.util.UUID;

public class Category {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private double limit;

    public Category(UUID id, UUID userId, String name, double limit) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.limit = limit;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getRemaining(double spentAmount) {
        return limit - spentAmount;
    }
}
