package com.campus.service;

/**
 * Thrown when a request violates a business rule (duplicate enrollment, course
 * at capacity, invalid grade value, future-dated attendance, etc). Controllers
 * catch this and re-render the form with the message rather than a 500 page.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
