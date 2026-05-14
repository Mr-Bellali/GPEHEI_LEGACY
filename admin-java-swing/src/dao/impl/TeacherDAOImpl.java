package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.TeacherDAO;
import exception.DatabaseException;
import model.Teacher;
import model.TeacherStatus;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public int insert(Teacher teacher) throws DatabaseException {
        String sql = "INSERT INTO teacher (teacher_number, first_name, last_name, email, hashed_password, " +
                "teacher_status, phone, cin, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, generateTeacherNumber());
            stmt.setString(2, teacher.getFirstName());
            stmt.setString(3, teacher.getLastName());
            stmt.setString(4, teacher.getEmail());
            stmt.setString(5, teacher.getPassword());
            stmt.setString(6, teacher.getStatus() != null ? teacher.getStatus().name().toLowerCase() : "active");
            stmt.setString(7, null); 
            stmt.setString(8, null); 
            stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting teacher: " + e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public Teacher findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM teacher WHERE id = ?";
        return executeSingleQuery(sql, id);
    }

    @Override
    public List<Teacher> findAll() throws DatabaseException {
        String sql = "SELECT * FROM teacher ORDER BY id DESC";
        return executeQueryList(sql);
    }

    @Override
    public List<Teacher> findAllActive() throws DatabaseException {
        String sql = "SELECT * FROM teacher WHERE teacher_status = 'active' ORDER BY id DESC";
        return executeQueryList(sql);
    }

    @Override
    public List<Teacher> findByStatus(String status) throws DatabaseException {
        if ("ALL".equalsIgnoreCase(status)) {
            return findAll();
        }
        String sql = "SELECT * FROM teacher WHERE teacher_status = ? ORDER BY id DESC";
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teachers.add(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding teachers by status: " + e.getMessage(), e);
        }
        return teachers;
    }

    @Override
    public List<Teacher> searchTeachers(String keyword) throws DatabaseException {
        String sql = "SELECT * FROM teacher WHERE " +
                "(first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR id LIKE ?) " +
                "ORDER BY id DESC";

        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teachers.add(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching teachers: " + e.getMessage(), e);
        }
        return teachers;
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM teacher";
        return executeCount(sql);
    }

    @Override
    public int getActiveCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM teacher WHERE teacher_status = 'active'";
        return executeCount(sql);
    }

    @Override
    public boolean existsByEmail(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM teacher WHERE email = ?";
        return executeExists(sql, email);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, int excludeId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM teacher WHERE email = ? AND id != ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Teacher teacher) throws DatabaseException {
        String sql = "UPDATE teacher SET first_name = ?, last_name = ?, email = ?, " +
                "hashed_password = ?, teacher_status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, teacher.getFirstName());
            stmt.setString(2, teacher.getLastName());
            stmt.setString(3, teacher.getEmail());
            stmt.setString(4, teacher.getPassword());
            stmt.setString(5, teacher.getStatus().name().toLowerCase());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(7, teacher.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating teacher: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deactivate(int id) throws DatabaseException {
        String sql = "UPDATE teacher SET teacher_status = 'disabled', updated_at = ? WHERE id = ?";
        return executeUpdateWithTimestamp(sql, id);
    }

    @Override
    public boolean reactivate(int id) throws DatabaseException {
        String sql = "UPDATE teacher SET teacher_status = 'active', updated_at = ? WHERE id = ?";
        return executeUpdateWithTimestamp(sql, id);
    }

    // ═══════════════ Helper Methods ═══════════════

    private int generateTeacherNumber() {
        return 2000 + (int)(Math.random() * 8000);
    }

    private boolean executeUpdateWithTimestamp(String sql, int id) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error executing update: " + e.getMessage(), e);
        }
    }

    private Teacher executeSingleQuery(String sql, int id) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacher(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query: " + e.getMessage(), e);
        }
        return null;
    }

    private List<Teacher> executeQueryList(String sql) throws DatabaseException {
        List<Teacher> teachers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query: " + e.getMessage(), e);
        }
        return teachers;
    }

    private int executeCount(String sql) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing count: " + e.getMessage(), e);
        }
        return 0;
    }

    private boolean executeExists(String sql, String email) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existence: " + e.getMessage(), e);
        }
        return false;
    }

    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setId(rs.getInt("id"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPassword(rs.getString("hashed_password"));
        
        String statusStr = rs.getString("teacher_status");
        if (statusStr != null) {
            try {
                teacher.setStatus(TeacherStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                teacher.setStatus(TeacherStatus.ACTIVE);
            }
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            teacher.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            teacher.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return teacher;
    }
}