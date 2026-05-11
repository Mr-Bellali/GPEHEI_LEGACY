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

        String sql = """
                SELECT * 
                FROM admin
                WHERE email = ?
                """;

        try {

            Connection conn =
                    DatabaseConnection.getInstance().getConnection();

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Admin(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("hashed_password"),
                        AdminRole.valueOf(
                                rs.getString("admin_role").toUpperCase()
                        ),
                        rs.getString("phone"),
                        AdminStatus.valueOf(
                                rs.getString("status_admin").toUpperCase()
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}