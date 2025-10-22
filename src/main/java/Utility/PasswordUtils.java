package Utility;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * Utility class for password operations
 *
 * - Provides BCrypt hashing and verification (verifyPassword).
 * - Provides random password generation and a simple strength checker.
 *
 * Note:
 * - Do NOT trim or modify the plain password before hashing/verification (users may have leading/trailing spaces).
 * - This class is not instantiable.
 *
 * @author hungk
 */
public final class PasswordUtils {

    private PasswordUtils() {
        // utility
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BCRYPT_COST = 12;

    /**
     * Hash a plain password using BCrypt.
     *
     * @param plainPassword the plain password (must not be null or empty)
     * @return the BCrypt hash
     * @throws IllegalArgumentException if plainPassword is null or empty
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verify a plain password against a BCrypt hashed password.
     *
     * @param plainPassword  the plain password to verify
     * @param hashedPassword the stored BCrypt hash
     * @return true if match; false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception ex) {
            // If the hash is malformed or BCrypt fails, treat as non-match
            return false;
        }
    }

    /**
     * Backward-compatible alias (older code may use checkPassword).
     *
     * @param plainPassword  the plain password
     * @param hashedPassword the hashed password
     * @return true if match; false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return verifyPassword(plainPassword, hashedPassword);
    }

    /**
     * Generate a random password composed of [A-Za-z0-9].
     * Note: If you require special characters, extend CHARACTERS.
     *
     * @param length length of the password (minimum 6)
     * @return generated password
     * @throws IllegalArgumentException if length < 6
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            throw new IllegalArgumentException("Password length must be at least 6 characters");
        }

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    /**
     * Basic password strength check:
     * - at least 8 chars
     * - contains upper and lower case letters
     * - contains a digit
     * - contains a special character from the common set
     *
     * @param password password to check
     * @return true if meets all rules, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        return hasUppercase && hasLowercase && hasNumber && hasSpecialChar;
    }
}