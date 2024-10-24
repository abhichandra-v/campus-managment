package com.campus.util;

import org.mindrot.jbcrypt.BCrypt;

/** Wraps BCrypt for password hashing and verification. */
public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    /** Hashes a plaintext password with a freshly generated BCrypt salt. */
    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    /** Returns true if the plaintext password matches the given BCrypt hash. */
    public static boolean verify(String plainTextPassword, String hash) {
        if (plainTextPassword == null || hash == null) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hash);
    }
}
