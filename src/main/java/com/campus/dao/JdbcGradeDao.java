package com.campus.dao;

import com.campus.model.Grade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcGradeDao implements GradeDao {

    private final DataSource dataSource;

    public JdbcGradeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String JOIN_SELECT =
            "SELECT g.*, c.code AS course_code, c.title AS course_title "
                    + "FROM grades g "
                    + "JOIN enrollments e ON e.id = g.enrollment_id "
                    + "JOIN courses c ON c.id = e.course_id";

    @Override
    public Optional<Grade> findByEnrollmentId(long enrollmentId) {
        String sql = JOIN_SELECT + " WHERE g.enrollment_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find grade for enrollment " + enrollmentId, e);
        }
    }

    @Override
    public List<Grade> findByStudentId(long studentId) {
        String sql = JOIN_SELECT + " WHERE e.student_id = ? ORDER BY g.updated_at DESC";
        List<Grade> grades = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapRow(rs));
                }
            }
            return grades;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list grades for student " + studentId, e);
        }
    }

    @Override
    public void upsert(long enrollmentId, String grade) {
        String sql = "INSERT INTO grades (enrollment_id, grade, graded_at) VALUES (?, ?, CURRENT_TIMESTAMP) "
                + "ON DUPLICATE KEY UPDATE grade = VALUES(grade), graded_at = CURRENT_TIMESTAMP";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollmentId);
            ps.setString(2, grade);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to record grade for enrollment " + enrollmentId, e);
        }
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getLong("id"));
        g.setEnrollmentId(rs.getLong("enrollment_id"));
        g.setGrade(rs.getString("grade"));
        Timestamp gradedAt = rs.getTimestamp("graded_at");
        if (gradedAt != null) {
            g.setGradedAt(gradedAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            g.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        g.setCourseCode(rs.getString("course_code"));
        g.setCourseTitle(rs.getString("course_title"));
        return g;
    }
}
