package com.campus.testsupport;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

/**
 * Spins up a fresh in-memory H2 database, in MySQL compatibility mode, loaded
 * from the project's real db/migrations/001_schema.sql - so integration tests
 * exercise the same schema (constraints, ENUMs, ON DUPLICATE KEY UPDATE
 * upserts) that ships to production, without requiring a MySQL server.
 */
public final class TestDatabase {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private static final Path SCHEMA_FILE = Path.of("db/migrations/001_schema.sql");

    private TestDatabase() {
    }

    public static JdbcDataSource createDataSource() {
        String dbName = "campus_test_" + COUNTER.incrementAndGet();
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + dbName + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try {
            String sql = Files.readString(SCHEMA_FILE);
            // H2 doesn't support multi-database CREATE DATABASE/USE; the rest of the
            // script (tables, constraints, indexes) is plain, portable DDL.
            sql = Pattern.compile("(?is)CREATE DATABASE.*?;").matcher(sql).replaceFirst("");
            sql = Pattern.compile("(?im)^USE .*?;\\s*$").matcher(sql).replaceAll("");
            try (Connection conn = dataSource.getConnection()) {
                RunScript.execute(conn, new StringReader(sql));
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Failed to initialize H2 test database from " + SCHEMA_FILE, e);
        }
        return dataSource;
    }
}
