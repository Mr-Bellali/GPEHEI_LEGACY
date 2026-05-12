package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.FiliereDAO;
import exception.DatabaseException;
import model.Filiere;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FiliereDAOImpl implements FiliereDAO {

    @Override
    public int insert(Filiere filiere) throws DatabaseException {
        String sql = "INSERT INTO filiere (name_filier, short_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, filiere.getName());
            stmt.setString(2, filiere.getShortName());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error inserting filiere", e);
        }
        return -1;
    }

    @Override
    public Filiere findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM filiere WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error finding filiere", e);
        }
        return null;
    }

    @Override
    public List<Filiere> findAll() throws DatabaseException {
        String sql = "SELECT * FROM filiere ORDER BY name_filier";
        List<Filiere> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Error finding all filieres", e);
        }
        return list;
    }

    @Override
    public boolean update(Filiere filiere) throws DatabaseException {
        String sql = "UPDATE filiere SET name_filier = ?, short_name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, filiere.getName());
            stmt.setString(2, filiere.getShortName());
            stmt.setInt(3, filiere.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error updating filiere", e);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM filiere WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting filiere", e);
        }
    }

    private Filiere mapResultSet(ResultSet rs) throws SQLException {
        Filiere f = new Filiere();
        f.setId(rs.getInt("id"));
        f.setName(rs.getString("name_filier"));
        f.setShortName(rs.getString("short_name"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        f.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return f;
    }
}
