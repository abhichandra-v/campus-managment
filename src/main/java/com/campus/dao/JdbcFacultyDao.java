package com.campus.dao;

import com.campus.model.Faculty;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcFacultyDao implements FacultyDao {

    private static final String BASE_SELECT =
            "SELECT f.user_id, f.department, f.title, f.office_location, "
                    + "u.username, u.email, u.full_name, u.is_active "
                    + "FROM faculty f JOIN users u ON u.id = f.user_id";

    private final DataSource dataSource;

    public JdbcFacultyDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Faculty> findByUserId(long userId) {
        String sql = BASE_SELECT + " WHERE f.user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find faculty by user id " + userId, e);
        }
    }

    @Override
    public List<Faculty> findAll() {
        String sql = BASE_SELECT + " ORDER BY f.user_id";
        List<Faculty> faculty = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                faculty.add(mapRow(rs));
            }
            return faculty;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list faculty", e);
        }
    }

    @Override
    public void insert(long userId, String department, String title, String officeLocation) {
        String sql = "INSERT INTO faculty (user_id, department, title, office_location) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, department);
            ps.setString(3, title);
            ps.setString(4, officeLocation);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert faculty for user " + userId, e);
        }
    }

    @Override
    public void updateProfile(long userId, String department, String title, String officeLocation) {
        String sql = "UPDATE faculty SET department = ?, title = ?, office_location = ? WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            ps.setString(2, title);
            ps.setString(3, officeLocation);
            ps.setLong(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update profile for faculty " + userId, e);
        }
    }

    private Faculty mapRow(ResultSet rs) throws SQLException {
        Faculty faculty = new Faculty();
        faculty.setUserId(rs.getLong("user_id"));
        faculty.setDepartment(rs.getString("department"));
        faculty.setTitle(rs.getString("title"));
        faculty.setOfficeLocation(rs.getString("office_location"));
        faculty.setUsername(rs.getString("username"));
        faculty.setEmail(rs.getString("email"));
        faculty.setFullName(rs.getString("full_name"));
        faculty.setActive(rs.getBoolean("is_active"));
        return faculty;
    }
}
