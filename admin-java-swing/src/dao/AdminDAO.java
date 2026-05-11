package dao;

import config.DatabaseConnection;

import java.sql.Connection;

public class AdminDAO {
    Connection conn = DatabaseConnection.getInstance().getConnection();

}
