package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.DashboardDAO;
import exception.DatabaseException;
import java.sql.*;

public class DashboardDAOImpl implements DashboardDAO {

    @Override
    public int getTotalStudents() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM student";  // Changed from students to student

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting students: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getActiveProjects() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM project WHERE status = 'Active'";  // Changed from projects to project

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting projects: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getPendingProjects() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM project WHERE status = 'Pending'";  // Changed from projects to project

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting pending projects: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int getUnreadAlerts() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM alerts WHERE is_read = false";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error counting alerts: " + e.getMessage(), e);
        }
        return 0;
    }
}