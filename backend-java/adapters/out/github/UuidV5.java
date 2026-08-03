package imperator.adapters.out.github;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

final class UuidV5 {
    private UuidV5() {
    }

    static UUID from(UUID namespace, String name) {
        Objects.requireNonNull(namespace, "UUID namespace is required");
        Objects.requireNonNull(name, "UUID name is required");

        byte[] namespaceBytes = ByteBuffer.allocate(16)
                .putLong(namespace.getMostSignificantBits())
                .putLong(namespace.getLeastSignificantBits())
                .array();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-1 is required for UUIDv5", exception);
        }
        digest.update(namespaceBytes);
        byte[] hash = digest.digest(name.getBytes(StandardCharsets.UTF_8));
        hash[6] = (byte) ((hash[6] & 0x0f) | 0x50);
        hash[8] = (byte) ((hash[8] & 0x3f) | 0x80);
        ByteBuffer value = ByteBuffer.wrap(hash);
        return new UUID(value.getLong(), value.getLong());
    }
}
