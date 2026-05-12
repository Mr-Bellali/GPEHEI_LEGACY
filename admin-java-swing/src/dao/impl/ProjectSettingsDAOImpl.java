package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.ProjectSettingsDAO;
import exception.DatabaseException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ProjectSettingsDAOImpl implements ProjectSettingsDAO {

    @Override
    public void updateCapacity(int filiereId, String promotion, int min, int max) throws DatabaseException {
        String sql = "INSERT INTO projectSettings (filiere_id, promotion, min_capacity, max_capacity) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE min_capacity = ?, max_capacity = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            stmt.setString(2, promotion);
            stmt.setInt(3, min);
            stmt.setInt(4, max);
            stmt.setInt(5, min);
            stmt.setInt(6, max);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating capacity settings", e);
        }
    }

    @Override
    public void updateDeadline(int filiereId, String promotion, java.time.LocalDateTime deadline) throws DatabaseException {
        String sql = "INSERT INTO projectSettings (filiere_id, promotion, deadline) " +
                "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE deadline = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            stmt.setString(2, promotion);
            stmt.setTimestamp(3, Timestamp.valueOf(deadline));
            stmt.setTimestamp(4, Timestamp.valueOf(deadline));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating deadline settings", e);
        }
    }

    @Override
    public Map<String, Object> getSettings(int filiereId, String promotion) throws DatabaseException {
        String sql = "SELECT * FROM projectSettings WHERE filiere_id = ? AND promotion = ?";
        Map<String, Object> settings = new HashMap<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            stmt.setString(2, promotion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    settings.put("min_capacity", rs.getInt("min_capacity"));
                    settings.put("max_capacity", rs.getInt("max_capacity"));
                    settings.put("deadline", rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime() : null);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching project settings", e);
        }
        return settings;
    }
}
