package view.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    private JButton loginButton;
    private JButton clearButton;

    public LoginFrame() {
        initializeUI();
    }

    private void initializeUI() {

        setTitle("GPEHEI - Login");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        clearButton = new JButton("Clear");

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Email
        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons
        gbc.gridy++;
        gbc.gridx = 1;
        mainPanel.add(loginButton, gbc);


        add(mainPanel);
    }


    // ========= GETTERS =========

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }


    // ========= LISTENERS =========

    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }


    // ========= UI HELPERS =========



    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}