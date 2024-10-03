package com.campus.dao;

import com.campus.model.Attendance;
import com.campus.model.AttendanceStatus;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcAttendanceDao implements AttendanceDao {

    private final DataSource dataSource;

    public JdbcAttendanceDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Attendance> findByEnrollmentAndDate(long enrollmentId, LocalDate date) {
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ? AND attendance_date = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollmentId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to find attendance for enrollment " + enrollmentId + " on " + date, e);
        }
    }

    @Override
    public List<Attendance> findByEnrollmentId(long enrollmentId) {
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ? ORDER BY attendance_date DESC";
        List<Attendance> records = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRow(rs));
                }
            }
            return records;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list attendance for enrollment " + enrollmentId, e);
        }
    }

    @Override
    public List<Attendance> findByStudentId(long studentId) {
        String sql = "SELECT a.* FROM attendance a "
                + "JOIN enrollments e ON e.id = a.enrollment_id "
                + "WHERE e.student_id = ? ORDER BY a.attendance_date DESC";
        List<Attendance> records = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRow(rs));
                }
            }
            return records;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list attendance for student " + studentId, e);
        }
    }

    @Override
    public void upsert(long enrollmentId, LocalDate date, AttendanceStatus status) {
        String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE status = VALUES(status)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollmentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, status.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(
                    "Failed to record attendance for enrollment " + enrollmentId + " on " + date, e);
        }
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getLong("id"));
        a.setEnrollmentId(rs.getLong("enrollment_id"));
        a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        a.setStatus(AttendanceStatus.valueOf(rs.getString("status")));
        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) {
            a.setRecordedAt(recordedAt.toLocalDateTime());
        }
        return a;
    }
}
