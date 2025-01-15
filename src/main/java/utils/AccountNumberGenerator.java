package utils;

import java.util.UUID;

public class AccountNumberGenerator {

    public static long generateAccountNumber(UUID id) {
        var mostSigBits = id.getMostSignificantBits();
        var leastSigBits = id.getLeastSignificantBits();
        var firstPart = Math.abs(mostSigBits) % 1_000_000_000_000L;
        var secondPart = Math.abs(leastSigBits) % 1_000_000_000_000L;
        var accountNumber = firstPart + secondPart;

        return accountNumber % 1_000_000_000_000L;
    }
}
