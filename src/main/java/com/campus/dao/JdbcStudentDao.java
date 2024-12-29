package com.campus.dao;

import com.campus.model.Student;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcStudentDao implements StudentDao {

    private static final String BASE_SELECT =
            "SELECT s.user_id, s.student_number, s.enrollment_year, s.major, s.date_of_birth, "
                    + "u.username, u.email, u.full_name, u.is_active "
                    + "FROM students s JOIN users u ON u.id = s.user_id";

    private final DataSource dataSource;

    public JdbcStudentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Student> findByUserId(long userId) {
        String sql = BASE_SELECT + " WHERE s.user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find student by user id " + userId, e);
        }
    }

    @Override
    public Optional<Student> findByStudentNumber(String studentNumber) {
        String sql = BASE_SELECT + " WHERE s.student_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find student by number " + studentNumber, e);
        }
    }

    @Override
    public List<Student> findAll() {
        String sql = BASE_SELECT + " ORDER BY s.user_id";
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
            return students;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list students", e);
        }
    }

    @Override
    public void insert(long userId, String studentNumber, int enrollmentYear, String major, LocalDate dateOfBirth) {
        try (Connection conn = dataSource.getConnection()) {
            insert(conn, userId, studentNumber, enrollmentYear, major, dateOfBirth);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert student for user " + userId, e);
        }
    }

    @Override
    public void insert(Connection conn, long userId, String studentNumber, int enrollmentYear, String major,
            LocalDate dateOfBirth) {
        String sql = "INSERT INTO students (user_id, student_number, enrollment_year, major, date_of_birth) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, studentNumber);
            ps.setInt(3, enrollmentYear);
            ps.setString(4, major);
            ps.setDate(5, dateOfBirth != null ? Date.valueOf(dateOfBirth) : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert student for user " + userId, e);
        }
    }

    @Override
    public void updateProfile(long userId, String major, LocalDate dateOfBirth) {
        String sql = "UPDATE students SET major = ?, date_of_birth = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, major);
            ps.setDate(2, dateOfBirth != null ? Date.valueOf(dateOfBirth) : null);
            ps.setLong(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update profile for student " + userId, e);
        }
    }

    @Override
    public boolean existsByStudentNumber(String studentNumber) {
        String sql = "SELECT 1 FROM students WHERE student_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existence of student number " + studentNumber, e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setUserId(rs.getLong("user_id"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setEnrollmentYear(rs.getInt("enrollment_year"));
        student.setMajor(rs.getString("major"));
        Date dob = rs.getDate("date_of_birth");
        student.setDateOfBirth(dob != null ? dob.toLocalDate() : null);
        student.setUsername(rs.getString("username"));
        student.setEmail(rs.getString("email"));
        student.setFullName(rs.getString("full_name"));
        student.setActive(rs.getBoolean("is_active"));
        return student;
    }
}
