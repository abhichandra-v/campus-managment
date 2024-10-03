package com.campus.dao;

import com.campus.model.Enrollment;
import com.campus.model.EnrollmentStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcEnrollmentDao implements EnrollmentDao {

    private static final String BASE_SELECT =
            "SELECT e.id, e.student_id, e.course_id, e.status, e.enrolled_at, e.dropped_at, "
                    + "su.full_name AS student_name, c.code AS course_code, c.title AS course_title "
                    + "FROM enrollments e "
                    + "JOIN students s ON s.user_id = e.student_id "
                    + "JOIN users su ON su.id = s.user_id "
                    + "JOIN courses c ON c.id = e.course_id";

    private final DataSource dataSource;

    public JdbcEnrollmentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Enrollment> findById(long id) {
        String sql = BASE_SELECT + " WHERE e.id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find enrollment by id " + id, e);
        }
    }

    @Override
    public Optional<Enrollment> findByStudentAndCourse(long studentId, long courseId) {
        String sql = BASE_SELECT + " WHERE e.student_id = ? AND e.course_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to find enrollment for student " + studentId + " / course " + courseId, e);
        }
    }

    @Override
    public List<Enrollment> findByStudentId(long studentId) {
        String sql = BASE_SELECT + " WHERE e.student_id = ? ORDER BY e.enrolled_at DESC";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRow(rs));
                }
            }
            return enrollments;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list enrollments for student " + studentId, e);
        }
    }

    @Override
    public List<Enrollment> findByCourseId(long courseId) {
        String sql = BASE_SELECT + " WHERE e.course_id = ? ORDER BY su.full_name";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapRow(rs));
                }
            }
            return enrollments;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list roster for course " + courseId, e);
        }
    }

    @Override
    public long insert(long studentId, long courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, 'ENROLLED')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new DataAccessException("Insert into enrollments did not return a generated key", null);
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to insert enrollment for student " + studentId + " / course " + courseId, e);
        }
    }

    @Override
    public void updateStatus(long enrollmentId, EnrollmentStatus status, LocalDateTime droppedAt) {
        String sql = "UPDATE enrollments SET status = ?, dropped_at = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setTimestamp(2, droppedAt != null ? Timestamp.valueOf(droppedAt) : null);
            ps.setLong(3, enrollmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update status for enrollment " + enrollmentId, e);
        }
    }

    @Override
    public int countActiveByCourseId(long courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND status = 'ENROLLED'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to count active enrollments for course " + courseId, e);
        }
    }

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getLong("id"));
        enrollment.setStudentId(rs.getLong("student_id"));
        enrollment.setCourseId(rs.getLong("course_id"));
        enrollment.setStatus(EnrollmentStatus.valueOf(rs.getString("status")));
        Timestamp enrolledAt = rs.getTimestamp("enrolled_at");
        if (enrolledAt != null) {
            enrollment.setEnrolledAt(enrolledAt.toLocalDateTime());
        }
        Timestamp droppedAt = rs.getTimestamp("dropped_at");
        if (droppedAt != null) {
            enrollment.setDroppedAt(droppedAt.toLocalDateTime());
        }
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setCourseTitle(rs.getString("course_title"));
        return enrollment;
    }
}
