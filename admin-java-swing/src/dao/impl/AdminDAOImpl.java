package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.AdminDAO;
import model.Admin;
import model.AdminRole;
import model.AdminStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public Admin findByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Admin findById(int id) {
        String sql = "SELECT * FROM admin WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin WHERE status_admin = 'active'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return admins;
    }

    @Override
    public boolean save(Admin admin) {
        String sql = "INSERT INTO admin (first_name, last_name, email, hashed_password, admin_role, phone, status_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getFirstName());
            ps.setString(2, admin.getLastName());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getHashedPassword());
            ps.setString(5, admin.getRole() == AdminRole.SUPER_ADMIN ? "super" : "regular");
            ps.setString(6, admin.getPhone());
            ps.setString(7, admin.getStatus() == AdminStatus.DISABLED ? "disabled" : "active");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Admin admin) {
        String sql = "UPDATE admin SET first_name = ?, last_name = ?, email = ?, admin_role = ?, phone = ?, status_admin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getFirstName());
            ps.setString(2, admin.getLastName());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getRole() == AdminRole.SUPER_ADMIN ? "super" : "regular");
            ps.setString(5, admin.getPhone());
            ps.setString(6, admin.getStatus() == AdminStatus.DISABLED ? "disabled" : "active");
            ps.setInt(7, admin.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // Soft delete
        String sql = "UPDATE admin SET status_admin = 'disabled' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Admin mapResultSetToAdmin(ResultSet rs) throws Exception {
        String roleStr = rs.getString("admin_role");
        AdminRole role = AdminRole.REGULAR;
        if ("super".equalsIgnoreCase(roleStr)) {
            role = AdminRole.SUPER_ADMIN;
        }

        String statusStr = rs.getString("status_admin");
        AdminStatus status = AdminStatus.ACTIVE;
        if ("disabled".equalsIgnoreCase(statusStr)) {
            status = AdminStatus.DISABLED;
        }

        return new Admin(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("hashed_password"),
                role,
                rs.getString("phone"),
                status
        );
    }
}
