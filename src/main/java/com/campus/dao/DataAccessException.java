package com.campus.dao;

/** Unchecked wrapper around {@link java.sql.SQLException} so DAO interfaces stay free of checked exceptions. */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
