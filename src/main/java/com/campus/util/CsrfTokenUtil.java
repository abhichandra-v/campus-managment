package com.campus.util;

import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/** Issues and validates a per-session CSRF token for state-changing form submissions. */
public final class CsrfTokenUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfTokenUtil() {
    }

    /** Returns the session's existing CSRF token, generating and storing one if absent. */
    public static String getOrCreateToken(HttpSession session) {
        String token = (String) session.getAttribute(Attributes.CSRF_TOKEN);
        if (token == null) {
            token = generateToken();
            session.setAttribute(Attributes.CSRF_TOKEN, token);
        }
        return token;
    }

    /** True if the submitted token is present and matches the session's token exactly. */
    public static boolean isValid(HttpSession session, String submittedToken) {
        if (session == null || submittedToken == null) {
            return false;
        }
        String expected = (String) session.getAttribute(Attributes.CSRF_TOKEN);
        return expected != null && expected.equals(submittedToken);
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
