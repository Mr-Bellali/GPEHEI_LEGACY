package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.ProjectDAO;
import exception.DatabaseException;
import model.Project;
import model.ProjectStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAOImpl implements ProjectDAO {

    @Override
    public List<Project> findAll() throws DatabaseException {
        String sql = "SELECT * FROM project ORDER BY id DESC";
        return executeQuery(sql);
    }

    @Override
    public List<Project> findActive() throws DatabaseException {
        String sql = "SELECT * FROM project WHERE project_status = 'in_progress' ORDER BY id DESC";
        return executeQuery(sql);
    }

    @Override
    public void save(Project p) throws DatabaseException {
        // Updated to match the DB schema enum values
        String sql = "INSERT INTO project (project_title, project_description, project_status, project_type, privacy_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getTitle());
            pstmt.setString(2, p.getDescription());
            
            // Map enum to DB value. The model uses uppercase, DB uses snake_case lowercase
            String status = p.getStatus() != null ? p.getStatus().name().toLowerCase() : "in_progress";
            if (status.equals("proposed")) status = "in_progress";
            
            pstmt.setString(3, status);
            pstmt.setString(4, "projet_synthèse"); 
            pstmt.setString(5, "private");        
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error saving project: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Project p) throws DatabaseException {
        String sql = "UPDATE project SET project_title = ?, project_description = ?, project_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getTitle());
            pstmt.setString(2, p.getDescription());
            
            String status = p.getStatus() != null ? p.getStatus().name().toLowerCase() : "in_progress";
            if (status.equals("proposed")) status = "in_progress";
            
            pstmt.setString(3, status);
            pstmt.setInt(4, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating project: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) throws DatabaseException {
        String sql = "DELETE FROM project WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting project: " + e.getMessage(), e);
        }
    }

    private List<Project> executeQuery(String sql) throws DatabaseException {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Project p = new Project();
                p.setId(rs.getInt("id"));
                p.setTitle(rs.getString("project_title"));
                p.setDescription(rs.getString("project_description"));
                
                String statusStr = rs.getString("project_status");
                if (statusStr != null) {
                    try {
                        p.setStatus(ProjectStatus.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        p.setStatus(ProjectStatus.IN_PROGRESS);
                    }
                }
                projects.add(p);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching projects: " + e.getMessage(), e);
        }
        return projects;
    }
}
