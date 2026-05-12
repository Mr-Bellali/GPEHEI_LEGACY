package dao.impl;

import config.DatabaseConnection;
import dao.interfaces.AdminDAO;
import model.Admin;
import model.AdminRole;
import model.AdminStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public Admin findByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
