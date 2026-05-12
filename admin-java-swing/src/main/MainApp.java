package main;

import config.DatabaseConnection;
import controller.LoginController;
import view.auth.LoginFrame;
import view.master.MasterFrame;
import javax.swing.*;
import java.sql.Connection;

public class MainApp {

    private static LoginFrame loginFrame;
    private static MasterFrame masterFrame;

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Initialize login
            loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }

    // Method to navigate from login to master page (with dashboard)
    public static void navigateToMasterPage() {
        SwingUtilities.invokeLater(() -> {
            if (loginFrame != null) {
                loginFrame.dispose();
            }

            // Create master frame (this contains header, sidebar, footer, and all panels)
            masterFrame = new MasterFrame();
            masterFrame.setVisible(true);

            // Dashboard is automatically shown by MasterController
        });
    }

    // Method to navigate back to login
    public static void navigateToLogin() {
        SwingUtilities.invokeLater(() -> {
            if (masterFrame != null) {
                masterFrame.dispose();
                masterFrame = null;
            }

            loginFrame = new LoginFrame();
            new LoginController(loginFrame);
            loginFrame.setVisible(true);
        });
    }

    // Getter for master frame (if other classes need access)
    public static MasterFrame getMasterFrame() {
        return masterFrame;
    }
}