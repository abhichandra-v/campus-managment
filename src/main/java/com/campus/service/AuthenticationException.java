package com.campus.service;

/** Thrown for a failed login attempt: unknown username, wrong password, or deactivated account. */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
