package com.campus.dao;

import com.campus.model.Faculty;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface FacultyDao {

    Optional<Faculty> findByUserId(long userId);

    List<Faculty> findAll();

    void insert(long userId, String department, String title, String officeLocation);

    /** Same as {@link #insert} but participates in a caller-managed transaction. */
    void insert(Connection conn, long userId, String department, String title, String officeLocation);

    void updateProfile(long userId, String department, String title, String officeLocation);
}
