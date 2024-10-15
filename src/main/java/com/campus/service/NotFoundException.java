package com.campus.service;

/** Thrown when a requested entity (course, enrollment, user, ...) does not exist. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
