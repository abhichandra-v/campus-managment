package com.campus.service;

/**
 * Thrown when an authenticated user attempts an action outside their
 * ownership scope - e.g. a faculty member grading a course they are not
 * assigned to, or a student dropping another student's enrollment. Distinct
 * from role-based authorization (handled by the servlet filter): this is a
 * data-ownership check that can only happen once the specific record is loaded.
 */
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
