package model;

import java.util.UUID;

public record Operation(UUID id, UUID accountId, UUID categoryId, String type, double amount, String date) {
}
