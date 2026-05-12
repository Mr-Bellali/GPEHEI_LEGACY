package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.LibraryDAO;
import exception.DatabaseException;
import model.Library;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDAOImpl implements LibraryDAO {

    @Override
    public List<Library> findAll() throws DatabaseException {
        List<Library> libraries = new ArrayList<>();
        String sql = "SELECT * FROM library ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Library l = new Library();
                l.setId(rs.getInt("id"));
                l.setName(rs.getString("library_name"));
                l.setStatus(rs.getString("library_status"));
                libraries.add(l);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching libraries: " + e.getMessage(), e);
        }
        return libraries;
    }

    @Override
    public void save(Library l) throws DatabaseException {
        String sql = "INSERT INTO library (library_name, library_status) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, l.getName());
            pstmt.setString(2, l.getStatus() != null ? l.getStatus() : "active");
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) l.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving library: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Library l) throws DatabaseException {
        String sql = "UPDATE library SET library_name = ?, library_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getName());
            pstmt.setString(2, l.getStatus());
            pstmt.setInt(3, l.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating library: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM library WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting library: " + e.getMessage(), e);
        }
    }
}
