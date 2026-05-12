package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.ModuleDAO;
import exception.DatabaseException;
import model.ModuleStatus;
import model.ModuleType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAOImpl implements ModuleDAO {

    @Override
    public int insert(model.Module module) throws DatabaseException {
        String sql = "INSERT INTO module (module_name, type_module, parent_module_id, filiere_id, module_status, semester) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, module.getName());
            stmt.setString(2, module.getType().name().toLowerCase());
            if (module.getParentModuleId() != null) stmt.setInt(3, module.getParentModuleId());
            else stmt.setNull(3, Types.INTEGER);
            stmt.setInt(4, module.getFiliereId());
            stmt.setString(5, module.getStatus() != null ? module.getStatus().name().toLowerCase() : "active");
            if (module.getSemester() != null) stmt.setInt(6, module.getSemester());
            else stmt.setNull(6, Types.INTEGER);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting module", e);
        }
        return -1;
    }

    @Override
    public model.Module findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM module WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding module", e);
        }
        return null;
    }

    @Override
    public List<model.Module> findAll() throws DatabaseException {
        String sql = "SELECT * FROM module ORDER BY id DESC";
        return executeQuery(sql);
    }

    @Override
    public List<model.Module> findByFiliere(int filiereId) throws DatabaseException {
        String sql = "SELECT * FROM module WHERE filiere_id = ? ORDER BY semester, module_name";
        List<model.Module> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding modules by filiere", e);
        }
        return list;
    }

    @Override
    public List<model.Module> findByParent(int parentId) throws DatabaseException {
        String sql = "SELECT * FROM module WHERE parent_module_id = ? ORDER BY module_name";
        List<model.Module> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding elements by parent module", e);
        }
        return list;
    }

    @Override
    public boolean update(model.Module module) throws DatabaseException {
        String sql = "UPDATE module SET module_name = ?, type_module = ?, parent_module_id = ?, " +
                "filiere_id = ?, module_status = ?, semester = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, module.getName());
            stmt.setString(2, module.getType().name().toLowerCase());
            if (module.getParentModuleId() != null) stmt.setInt(3, module.getParentModuleId());
            else stmt.setNull(3, Types.INTEGER);
            stmt.setInt(4, module.getFiliereId());
            stmt.setString(5, module.getStatus().name().toLowerCase());
            if (module.getSemester() != null) stmt.setInt(6, module.getSemester());
            else stmt.setNull(6, Types.INTEGER);
            stmt.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setInt(8, module.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating module", e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM module WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting module", e);
        }
    }

    private List<model.Module> executeQuery(String sql) throws DatabaseException {
        List<model.Module> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Error executing module query", e);
        }
        return list;
    }

    private model.Module mapResultSet(ResultSet rs) throws SQLException {
        model.Module m = new model.Module();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("module_name"));
        m.setType(ModuleType.valueOf(rs.getString("type_module").toUpperCase()));
        int parentId = rs.getInt("parent_module_id");
        if (!rs.wasNull()) m.setParentModuleId(parentId);
        m.setFiliereId(rs.getInt("filiere_id"));
        m.setStatus(ModuleStatus.valueOf(rs.getString("module_status").toUpperCase()));
        int sem = rs.getInt("semester");
        if (!rs.wasNull()) m.setSemester(sem);
        m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        m.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return m;
    }
}
