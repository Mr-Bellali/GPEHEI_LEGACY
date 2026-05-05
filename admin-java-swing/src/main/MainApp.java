package main;

import config.DatabaseConnection;
import model.Admin;
import model.AdminRole;
import model.AdminStatus;
import utils.Logger;
import view.auth.LoginFrame;
import java.sql.Connection;

public class MainApp {
    public static void main(String[] args) {

        Admin A1 = new Admin("achraf","elabouye",
                "email.com","hashed_password",
                AdminRole.SUPER,"256365",
                AdminStatus.ACTIVE
        );

        Logger.error("Failed to fetch student by ID");

        Connection conn = DatabaseConnection.getInstance().getConnection();

        System.out.println(A1);
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
    }
}