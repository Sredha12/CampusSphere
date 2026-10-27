package com.campussphere.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cryptographic utility for hashing and verifying passwords using PBKDF2WithHmacSHA256.
 * <p>
 * Storage format: {@code pbkdf2_sha256$<iterations>$<saltBase64>$<hashBase64>}
 * <p>
 * Specifications:
 * <ul>
 *   <li>Algorithm: PBKDF2WithHmacSHA256</li>
 *   <li>Iterations: 600,000</li>
 *   <li>Salt: 16 cryptographically secure random bytes</li>
 *   <li>Key length: 256 bits (32 bytes)</li>
 * </ul>
 */
public final class PasswordUtil {

    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2_sha256";
    private static final int ITERATIONS = 600000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_LENGTH_BITS = 256;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * A pre-computed dummy valid hash used when verifying non-existent accounts.
     * This ensures the CPU cost is identical whether an email exists or not,
     * preventing account enumeration timing attacks.
     */
    private static final String DUMMY_HASH;

    static {
        // Pre-compute once at class loading so dummy verify has a valid structure.
        byte[] dummySalt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(dummySalt);
        byte[] dummyDerived = pbkdf2("dummy_timing_password".toCharArray(), dummySalt, ITERATIONS, KEY_LENGTH_BITS);
        DUMMY_HASH = PREFIX + "$" + ITERATIONS + "$" +
                Base64.getEncoder().encodeToString(dummySalt) + "$" +
                Base64.getEncoder().encodeToString(dummyDerived);
    }

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes a password string with a fresh 16-byte random salt and 600,000 iterations.
     *
     * @param password the plaintext password to hash
     * @return formatted hash string: {@code pbkdf2_sha256$<iterations>$<saltBase64>$<hashBase64>}
     */
    public static String hash(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return hash(password.toCharArray());
    }

    /**
     * Hashes a password character array with a fresh 16-byte random salt and 600,000 iterations.
     *
     * @param passwordChars the plaintext password characters
     * @return formatted hash string
     */
    public static String hash(char[] passwordChars) {
        if (passwordChars == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        byte[] hash = pbkdf2(passwordChars, salt, ITERATIONS, KEY_LENGTH_BITS);

        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return PREFIX + "$" + ITERATIONS + "$" + saltBase64 + "$" + hashBase64;
    }

    /**
     * Verifies a candidate password against a stored hash string.
     * <p>
     * Parses defensively: any malformed, missing, or unknown-format value
     * (including {@code !NO_LOGIN_DEMO_ACCOUNT!}, null, empty) returns {@code false}
     * and never throws an exception.
     * Comparison is performed in constant time using {@link MessageDigest#isEqual}.
     *
     * @param password   the candidate password string
     * @param storedHash the stored hash string from database
     * @return true if password matches the stored hash, false otherwise
     */
    public static boolean verify(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }
        return verify(password.toCharArray(), storedHash);
    }

    /**
     * Verifies a candidate password character array against a stored hash string.
     *
     * @param passwordChars candidate password characters
     * @param storedHash    stored hash string from database
     * @return true if matches, false otherwise
     */
    public static boolean verify(char[] passwordChars, String storedHash) {
        if (passwordChars == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        // Expected format: pbkdf2_sha256$<iterations>$<saltBase64>$<hashBase64>
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        if (!PREFIX.equals(parts[0])) {
            return false;
        }

        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
            if (iterations <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;
        try {
            salt = Base64.getDecoder().decode(parts[2]);
            expectedHash = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (salt.length == 0 || expectedHash.length == 0) {
            return false;
        }

        byte[] computedHash = pbkdf2(passwordChars, salt, iterations, expectedHash.length * 8);

        // Constant-time byte array comparison
        return MessageDigest.isEqual(expectedHash, computedHash);
    }

    /**
     * Executes a dummy verification with the given password against a validly formatted
     * dummy hash. Used when an email address is not found in the database to equalize
     * response times and defend against user enumeration attacks.
     *
     * @param password the candidate password entered by the user
     */
    public static void verifyDummy(String password) {
        char[] chars = (password != null) ? password.toCharArray() : new char[0];
        verify(chars, DUMMY_HASH);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.log(Level.SEVERE, "Failed to compute PBKDF2 hash", e);
            throw new IllegalStateException("Cryptographic algorithm unavailable", e);
        }
    }
}
