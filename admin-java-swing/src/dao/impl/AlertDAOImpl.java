package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.AlertDAO;
import exception.DatabaseException;
import model.Alert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDAOImpl implements AlertDAO {

    @Override
    public List<Alert> findAll() throws DatabaseException {
        String sql = "SELECT * FROM alerts ORDER BY created_at DESC";
        return executeQuery(sql);
    }

    @Override
    public List<Alert> findAllUnread() throws DatabaseException {
        String sql = "SELECT * FROM alerts WHERE is_read = FALSE ORDER BY created_at DESC";
        return executeQuery(sql);
    }

    private List<Alert> executeQuery(String sql) throws DatabaseException {
        List<Alert> alerts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Alert a = new Alert();
                a.setId(rs.getInt("id"));
                a.setMessage(rs.getString("message"));
                a.setType(rs.getString("type"));
                a.setRead(rs.getBoolean("is_read"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                alerts.add(a);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching alerts: " + e.getMessage(), e);
        }
        return alerts;
    }

    @Override
    public void markAsRead(int id) throws DatabaseException {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error marking alert as read: " + e.getMessage(), e);
        }
    }

    @Override
    public void markAllAsRead() throws DatabaseException {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE is_read = FALSE";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error marking all alerts as read: " + e.getMessage(), e);
        }
    }
}
