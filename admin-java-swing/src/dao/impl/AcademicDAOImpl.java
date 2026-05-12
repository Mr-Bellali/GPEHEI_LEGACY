package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.AcademicDAO;
import exception.DatabaseException;
import model.Filiere;
import model.Groupe;
import model.Module;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcademicDAOImpl implements AcademicDAO {

    @Override
    public List<Filiere> findAllFilieres() throws DatabaseException {
        String sql = "SELECT * FROM filiere ORDER BY name_filier";
        List<Filiere> filieres = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Filiere f = new Filiere();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name_filier"));
                filieres.add(f);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching filieres: " + e.getMessage(), e);
        }
        return filieres;
    }

    @Override
    public List<model.Module> findModulesByFiliere(int filiereId) throws DatabaseException {
        String sql = "SELECT * FROM module WHERE filiere_id = ? AND module_status = 'active'";
        List<model.Module> modules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.Module m = new model.Module();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("module_name"));
                    modules.add(m);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching modules: " + e.getMessage(), e);
        }
        return modules;
    }

    @Override
    public List<Groupe> findGroupsByFiliere(int filiereId) throws DatabaseException {
        String sql = "SELECT * FROM groupe WHERE filiere_id = ? AND groupe_status = 'active'";
        List<Groupe> groups = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Groupe g = new Groupe();
                    g.setId(rs.getInt("id"));
                    g.setName(rs.getString("group_name"));
                    groups.add(g);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching groups: " + e.getMessage(), e);
        }
        return groups;
    }

    @Override
    public void assignTeacherToModules(int teacherId, List<Integer> moduleIds) throws DatabaseException {
        String sql = "INSERT IGNORE INTO teacher_module (teacher_id, module_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Integer moduleId : moduleIds) {
                stmt.setInt(1, teacherId);
                stmt.setInt(2, moduleId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error assigning modules: " + e.getMessage(), e);
        }
    }

    @Override
    public List<model.Module> findModulesByTeacher(int teacherId) throws DatabaseException {
        String sql = "SELECT m.* FROM module m JOIN teacher_module tm ON m.id = tm.module_id WHERE tm.teacher_id = ?";
        List<model.Module> modules = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.Module m = new model.Module();
                    m.setId(rs.getInt("id"));
                    m.setName(rs.getString("module_name"));
                    modules.add(m);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching teacher modules: " + e.getMessage(), e);
        }
        return modules;
    }

    @Override
    public void removeTeacherFromModules(int teacherId, List<Integer> moduleIds) throws DatabaseException {
        String sql = "DELETE FROM teacher_module WHERE teacher_id = ? AND module_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Integer moduleId : moduleIds) {
                stmt.setInt(1, teacherId);
                stmt.setInt(2, moduleId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error removing modules: " + e.getMessage(), e);
        }
    }

    @Override
    public void assignTeacherToProjects(int teacherId, List<Integer> projectIds) throws DatabaseException {
        String sql = "INSERT IGNORE INTO projectSupervisor (teacher_id, project_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Integer projectId : projectIds) {
                stmt.setInt(1, teacherId);
                stmt.setInt(2, projectId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error assigning projects: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeTeacherFromProjects(int teacherId, List<Integer> projectIds) throws DatabaseException {
        String sql = "DELETE FROM projectSupervisor WHERE teacher_id = ? AND project_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Integer projectId : projectIds) {
                stmt.setInt(1, teacherId);
                stmt.setInt(2, projectId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error removing projects: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTeacherSupervision(int teacherId, List<Integer> projectIds) throws DatabaseException {
        // Simple strategy: Clear all for this teacher and re-assign
        String clearSql = "DELETE FROM projectSupervisor WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement clearStmt = conn.prepareStatement(clearSql)) {
                clearStmt.setInt(1, teacherId);
                clearStmt.executeUpdate();
            }
            
            String assignSql = "INSERT INTO projectSupervisor (teacher_id, project_id) VALUES (?, ?)";
            try (PreparedStatement assignStmt = conn.prepareStatement(assignSql)) {
                for (Integer projectId : projectIds) {
                    assignStmt.setInt(1, teacherId);
                    assignStmt.setInt(2, projectId);
                    assignStmt.addBatch();
                }
                assignStmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating supervision: " + e.getMessage(), e);
        }
    }
}
