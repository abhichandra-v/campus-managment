package com.campus.dao;

import com.campus.model.Role;
import com.campus.model.User;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    List<User> findByRole(Role role);

    long insert(User user);

    /** Same as {@link #insert(User)} but participates in a caller-managed transaction. */
    long insert(Connection conn, User user);

    void updateProfile(long userId, String email, String fullName);

    void updatePasswordHash(long userId, String passwordHash);

    void setActive(long userId, boolean active);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
