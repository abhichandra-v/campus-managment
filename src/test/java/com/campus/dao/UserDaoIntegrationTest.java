package com.campus.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.campus.model.Role;
import com.campus.model.User;
import com.campus.testsupport.TestDatabase;
import com.campus.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises JdbcUserDao against a real relational engine (H2 in MySQL
 * compatibility mode, loaded from the actual db/migrations/001_schema.sql)
 * rather than mocks, so the parameterized SQL and the schema's constraints
 * are both verified together.
 */
class UserDaoIntegrationTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new JdbcUserDao(TestDatabase.createDataSource());
    }

    private User newUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hash("Str0ngPassword!"));
        user.setRole(Role.STUDENT);
        user.setEmail(email);
        user.setFullName("Test User");
        user.setActive(true);
        return user;
    }

    @Test
    void insertThenFindByIdReturnsTheSameUser() {
        long id = userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        User found = userDao.findById(id).orElseThrow();
        assertEquals("jdoe", found.getUsername());
        assertEquals("jdoe@example.edu", found.getEmail());
        assertEquals(Role.STUDENT, found.getRole());
        assertTrue(found.isActive());
    }

    @Test
    void findByUsernameIsCaseSensitiveAndReturnsEmptyWhenNotFound() {
        userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        assertTrue(userDao.findByUsername("jdoe").isPresent());
        assertTrue(userDao.findByUsername("nosuchuser").isEmpty());
    }

    @Test
    void existsByUsernameAndExistsByEmailReflectInsertedRows() {
        userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        assertTrue(userDao.existsByUsername("jdoe"));
        assertTrue(userDao.existsByEmail("jdoe@example.edu"));
        assertFalse(userDao.existsByUsername("someoneelse"));
    }

    @Test
    void insertRejectsADuplicateUsernameViaTheSchemasUniqueConstraint() {
        userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        assertThrows(DataAccessException.class,
                () -> userDao.insert(newUser("jdoe", "different@example.edu")));
    }

    @Test
    void insertRejectsADuplicateEmailViaTheSchemasUniqueConstraint() {
        userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        assertThrows(DataAccessException.class,
                () -> userDao.insert(newUser("someoneelse", "jdoe@example.edu")));
    }

    @Test
    void updatePasswordHashPersists() {
        long id = userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        String newHash = PasswordUtil.hash("ANewPassword!");
        userDao.updatePasswordHash(id, newHash);

        assertEquals(newHash, userDao.findById(id).orElseThrow().getPasswordHash());
    }

    @Test
    void setActiveTogglesTheFlag() {
        long id = userDao.insert(newUser("jdoe", "jdoe@example.edu"));

        userDao.setActive(id, false);

        assertFalse(userDao.findById(id).orElseThrow().isActive());
    }
}
