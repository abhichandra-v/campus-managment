package com.campus.service;

import com.campus.dao.FacultyDao;
import com.campus.dao.StudentDao;
import com.campus.dao.UserDao;
import com.campus.model.Role;
import com.campus.model.User;
import com.campus.util.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 * Business rules for admin-managed accounts. Creating a student or faculty
 * account writes to both {@code users} and the role-specific table, so both
 * statements run in one JDBC transaction: either both succeed or neither does.
 */
public class UserManagementService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final DataSource dataSource;
    private final UserDao userDao;
    private final StudentDao studentDao;
    private final FacultyDao facultyDao;

    public UserManagementService(DataSource dataSource, UserDao userDao, StudentDao studentDao,
            FacultyDao facultyDao) {
        this.dataSource = dataSource;
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.facultyDao = facultyDao;
    }

    public User createStudent(String username, String password, String email, String fullName,
            String studentNumber, int enrollmentYear, String major, LocalDate dateOfBirth) {
        validateNewAccount(username, password, email, fullName);
        if (studentNumber == null || studentNumber.isBlank()) {
            throw new ValidationException("Student number is required.");
        }
        if (studentDao.existsByStudentNumber(studentNumber.trim())) {
            throw new ValidationException("Student number '" + studentNumber + "' is already in use.");
        }

        User user = newUser(username, password, email, fullName, Role.STUDENT);
        long userId = runInTransaction(conn -> {
            long id = userDao.insert(conn, user);
            studentDao.insert(conn, id, studentNumber.trim(), enrollmentYear, major, dateOfBirth);
            return id;
        });
        return userDao.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User vanished immediately after creation"));
    }

    public User createFaculty(String username, String password, String email, String fullName,
            String department, String title, String officeLocation) {
        validateNewAccount(username, password, email, fullName);
        if (department == null || department.isBlank()) {
            throw new ValidationException("Department is required.");
        }

        User user = newUser(username, password, email, fullName, Role.FACULTY);
        long userId = runInTransaction(conn -> {
            long id = userDao.insert(conn, user);
            facultyDao.insert(conn, id, department.trim(), title, officeLocation);
            return id;
        });
        return userDao.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User vanished immediately after creation"));
    }

    public void setActive(long userId, boolean active) {
        userDao.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        userDao.setActive(userId, active);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public List<User> findByRole(Role role) {
        return userDao.findByRole(role);
    }

    private User newUser(String username, String password, String email, String fullName, Role role) {
        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);
        user.setEmail(email.trim());
        user.setFullName(fullName.trim());
        user.setActive(true);
        return user;
    }

    private void validateNewAccount(String username, String password, String email, String fullName) {
        if (username == null || username.trim().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters.");
        }
        if (userDao.existsByUsername(username.trim())) {
            throw new ValidationException("Username '" + username + "' is already taken.");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Full name is required.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("A valid email address is required.");
        }
        if (userDao.existsByEmail(email.trim())) {
            throw new ValidationException("That email address is already in use.");
        }
    }

    private long runInTransaction(TransactionalWork work) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long result = work.run(conn);
                conn.commit();
                return result;
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new com.campus.dao.DataAccessException("Transaction failed", e);
        }
    }

    @FunctionalInterface
    private interface TransactionalWork {
        long run(Connection conn) throws SQLException;
    }
}
