package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.GroupeDAO;
import exception.DatabaseException;
import model.Groupe;
import model.GroupeStatus;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupeDAOImpl implements GroupeDAO {

    @Override
    public int insert(Groupe groupe) throws DatabaseException {
        String sql = "INSERT INTO groupe (group_name, promotion, filiere_id, groupe_status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, groupe.getName());
            // Using promotion as a string (e.g., '2024')
            stmt.setString(2, "2026"); // Default current year or dynamic
            stmt.setInt(3, groupe.getFiliereId());
            stmt.setString(4, groupe.getStatus() != null ? groupe.getStatus().name().toLowerCase() : "active");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting group", e);
        }
        return -1;
    }

    @Override
    public Groupe findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM groupe WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding group", e);
        }
        return null;
    }

    @Override
    public List<Groupe> findAll() throws DatabaseException {
        String sql = "SELECT * FROM groupe ORDER BY id DESC";
        List<Groupe> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Error finding groups", e);
        }
        return list;
    }

    @Override
    public List<Groupe> findByFiliere(int filiereId) throws DatabaseException {
        String sql = "SELECT * FROM groupe WHERE filiere_id = ? ORDER BY group_name";
        List<Groupe> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, filiereId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding groups by filiere", e);
        }
        return list;
    }

    @Override
    public boolean update(Groupe groupe) throws DatabaseException {
        String sql = "UPDATE groupe SET group_name = ?, filiere_id = ?, groupe_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, groupe.getName());
            stmt.setInt(2, groupe.getFiliereId());
            stmt.setString(3, groupe.getStatus().name().toLowerCase());
            stmt.setInt(4, groupe.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating group", e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM groupe WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting group", e);
        }
    }

    private Groupe mapResultSet(ResultSet rs) throws SQLException {
        Groupe g = new Groupe();
        g.setId(rs.getInt("id"));
        g.setName(rs.getString("group_name"));
        g.setFiliereId(rs.getInt("filiere_id"));
        String statusStr = rs.getString("groupe_status");
        if (statusStr != null) {
            try {
                g.setStatus(GroupeStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                g.setStatus(GroupeStatus.ACTIVE);
            }
        }
        return g;
    }
}
