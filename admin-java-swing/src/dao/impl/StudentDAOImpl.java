package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.StudentDAO;
import exception.DatabaseException;
import model.Student;
import model.StudentStatus;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public int insert(Student student) throws DatabaseException {
        String sql = "INSERT INTO student (student_number, first_name, last_name, cin, cne, " +
                "email, hashed_password, phone, birth_date, StudentStatus, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, student.getStudentNumber() != 0 ? student.getStudentNumber() : generateStudentNumber());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getCin());
            stmt.setString(5, student.getCne());
            stmt.setString(6, student.getEmail());
            stmt.setString(7, student.getPassword());
            stmt.setString(8, student.getPhone());
            stmt.setDate(9, student.getBirthDate() != null ? Date.valueOf(student.getBirthDate().toLocalDate()) : Date.valueOf("2000-01-01"));
            stmt.setString(10, student.getStatus() != null ? student.getStatus().name() : "ACTIVE");
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting student: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return -1;
    }

    @Override
    public Student findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM student WHERE id = ?";
        return executeSingleQuery(sql, id);
    }

    @Override
    public List<Student> findAll() throws DatabaseException {
        String sql = "SELECT * FROM student ORDER BY id DESC";
        return executeQueryList(sql);
    }

    @Override
    public List<Student> findAllActive() throws DatabaseException {
        String sql = "SELECT * FROM student WHERE StudentStatus IN ('ACTIVE', 'SUSPENDED') ORDER BY id DESC";
        return executeQueryList(sql);
    }

    @Override
    public List<Student> searchStudents(String keyword) throws DatabaseException {
        String sql = "SELECT * FROM student WHERE " +
                "(first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR " +
                "id LIKE ? OR student_number LIKE ?) " +
                "ORDER BY id DESC";

        List<Student> students = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);

            rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error searching students: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return students;
    }

    @Override
    public int getTotalCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM student";
        return executeCount(sql);
    }

    @Override
    public int getActiveCount() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM student WHERE StudentStatus = 'ACTIVE'";
        return executeCount(sql);
    }

    @Override
    public boolean existsByEmail(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM student WHERE email = ?";
        return executeExists(sql, email);
    }

    @Override
    public boolean existsByEmailExcludingId(String email, int excludeId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM student WHERE email = ? AND id != ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setInt(2, excludeId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return false;
    }

    @Override
    public boolean update(Student student) throws DatabaseException {
        String sql = "UPDATE student SET first_name = ?, last_name = ?, email = ?, " +
                "hashed_password = ?, phone = ?, StudentStatus = ?, updated_at = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPassword());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getStatus().name());
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(8, student.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating student: " + e.getMessage(), e);
        } finally {
            closeQuietly(null, stmt);
        }
    }

    @Override
    public boolean deactivate(int id) throws DatabaseException {
        String sql = "UPDATE student SET StudentStatus = 'INACTIVE', updated_at = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deactivating student: " + e.getMessage(), e);
        } finally {
            closeQuietly(null, stmt);
        }
    }

    @Override
    public boolean reactivate(int id) throws DatabaseException {
        String sql = "UPDATE student SET StudentStatus = 'ACTIVE', updated_at = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error reactivating student: " + e.getMessage(), e);
        } finally {
            closeQuietly(null, stmt);
        }
    }

    @Override
    public int cleanUpInactive(int daysOld) throws DatabaseException {
        String sql = "DELETE FROM student WHERE StudentStatus = 'INACTIVE' " +
                "AND updated_at < DATE_SUB(NOW(), INTERVAL ? DAY)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, daysOld);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error cleaning up inactive students: " + e.getMessage(), e);
        } finally {
            closeQuietly(null, stmt);
        }
    }

    // ═══════════════ Helper Methods ═══════════════

    private int generateStudentNumber() {
        return 1000 + (int)(Math.random() * 9000);
    }

    private Student executeSingleQuery(String sql, int id) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return null;
    }

    private List<Student> executeQueryList(String sql) throws DatabaseException {
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return students;
    }

    private int executeCount(String sql) throws DatabaseException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error executing count: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return 0;
    }

    private boolean executeExists(String sql, String email) throws DatabaseException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking existence: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, stmt);
        }
        return false;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentNumber(rs.getInt("student_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setCin(rs.getString("cin"));
        student.setCne(rs.getString("cne"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("hashed_password"));
        student.setPhone(rs.getString("phone"));

        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            student.setBirthDate(birthDate.toLocalDate().atStartOfDay());
        }

        String statusStr = rs.getString("StudentStatus");
        if (statusStr != null) {
            try {
                student.setStatus(StudentStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                student.setStatus(StudentStatus.ACTIVE);
            }
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return student;
    }

    private void closeQuietly(ResultSet rs, Statement stmt) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
    }
}