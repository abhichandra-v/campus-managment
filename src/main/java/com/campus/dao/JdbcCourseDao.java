package com.campus.dao;

import com.campus.model.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class JdbcCourseDao implements CourseDao {

    private static final String BASE_SELECT =
            "SELECT c.id, c.code, c.title, c.credits, c.faculty_id, c.capacity, c.semester, c.created_at, "
                    + "u.full_name AS faculty_name, "
                    + "(SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id AND e.status = 'ENROLLED') "
                    + "AS enrolled_count "
                    + "FROM courses c "
                    + "LEFT JOIN faculty f ON f.user_id = c.faculty_id "
                    + "LEFT JOIN users u ON u.id = f.user_id";

    private final DataSource dataSource;

    public JdbcCourseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Course> findById(long id) {
        String sql = BASE_SELECT + " WHERE c.id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find course by id " + id, e);
        }
    }

    @Override
    public List<Course> findAll() {
        String sql = BASE_SELECT + " ORDER BY c.semester DESC, c.code";
        return query(sql, ps -> { });
    }

    @Override
    public List<Course> findBySemester(String semester) {
        String sql = BASE_SELECT + " WHERE c.semester = ? ORDER BY c.code";
        return query(sql, ps -> ps.setString(1, semester));
    }

    @Override
    public List<Course> findByFacultyId(long facultyId) {
        String sql = BASE_SELECT + " WHERE c.faculty_id = ? ORDER BY c.semester DESC, c.code";
        return query(sql, ps -> ps.setLong(1, facultyId));
    }

    @Override
    public List<Course> findAvailableForStudent(long studentId, String semester) {
        String sql = BASE_SELECT
                + " WHERE c.semester = ? "
                + "AND NOT EXISTS (SELECT 1 FROM enrollments e WHERE e.course_id = c.id "
                + "AND e.student_id = ? AND e.status IN ('ENROLLED','COMPLETED')) "
                + "ORDER BY c.code";
        return query(sql, ps -> {
            ps.setString(1, semester);
            ps.setLong(2, studentId);
        });
    }

    @Override
    public long insert(Course course) {
        String sql = "INSERT INTO courses (code, title, credits, faculty_id, capacity, semester) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            if (course.getFacultyId() != null) {
                ps.setLong(4, course.getFacultyId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            ps.setInt(5, course.getCapacity());
            ps.setString(6, course.getSemester());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            throw new DataAccessException("Insert into courses did not return a generated key", null);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert course " + course.getCode(), e);
        }
    }

    @Override
    public void update(Course course) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ?, capacity = ?, semester = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, course.getCapacity());
            ps.setString(5, course.getSemester());
            ps.setLong(6, course.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update course " + course.getId(), e);
        }
    }

    @Override
    public void assignFaculty(long courseId, Long facultyId) {
        String sql = "UPDATE courses SET faculty_id = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (facultyId != null) {
                ps.setLong(1, facultyId);
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }
            ps.setLong(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to assign faculty to course " + courseId, e);
        }
    }

    @Override
    public int countActiveEnrollments(long courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ? AND status = 'ENROLLED'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to count enrollments for course " + courseId, e);
        }
    }

    @Override
    public boolean existsByCodeAndSemester(String code, String semester) {
        String sql = "SELECT 1 FROM courses WHERE code = ? AND semester = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, semester);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existence of course " + code + "/" + semester, e);
        }
    }

    private List<Course> query(String sql, SqlSetter setter) {
        List<Course> courses = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            }
            return courses;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to query courses", e);
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getLong("id"));
        course.setCode(rs.getString("code"));
        course.setTitle(rs.getString("title"));
        course.setCredits(rs.getInt("credits"));
        long facultyId = rs.getLong("faculty_id");
        course.setFacultyId(rs.wasNull() ? null : facultyId);
        course.setFacultyName(rs.getString("faculty_name"));
        course.setCapacity(rs.getInt("capacity"));
        course.setSemester(rs.getString("semester"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }
        course.setEnrolledCount(rs.getInt("enrolled_count"));
        return course;
    }

    @FunctionalInterface
    private interface SqlSetter {
        void set(PreparedStatement ps) throws SQLException;
    }
}
