package dio.budgeting.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Strong typed identifier for a {@link Transaction}.
 * Avoids passing raw UUID/String values around and mixing them up with other ids.
 */
public record TransactionId(UUID value) {

    public TransactionId {
        Objects.requireNonNull(value, "TransactionId value must not be null");
    }

    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId of(String value) {
        return new TransactionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
