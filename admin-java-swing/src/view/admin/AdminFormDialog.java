package view.admin;

import javax.swing.*;
import java.awt.*;
import model.Admin;
import model.AdminRole;
import model.AdminStatus;
import utils.Validator;
import utils.PasswordHasher;

public class AdminFormDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<AdminRole> roleCombo;
    private JComboBox<AdminStatus> statusCombo;

    private boolean confirmed = false;
    private Admin admin;

    public AdminFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public AdminFormDialog(JFrame parent, String title, Admin admin) {
        this.admin = admin;
        initComponents(); // Initialize components first
        populateFields();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(450, 450);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        int row = 0;

        // First Name
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("First Name *:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        mainPanel.add(firstNameField, gbc);
        row++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Last Name *:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        mainPanel.add(lastNameField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Email *:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Password *:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(AdminRole.values());
        mainPanel.add(roleCombo, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(AdminStatus.values());
        mainPanel.add(statusCombo, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(0x3D, 0x34, 0x8B));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(0xE7, 0x4C, 0x3C));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            if (validateForm()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void populateFields() {
        if (admin != null) {
            firstNameField.setText(admin.getFirstName());
            lastNameField.setText(admin.getLastName());
            emailField.setText(admin.getEmail());
            // We don't populate password for security, leave it blank unless user wants to change it
            if (admin.getPhone() != null) {
                phoneField.setText(admin.getPhone());
            }
            roleCombo.setSelectedItem(admin.getRole());
            statusCombo.setSelectedItem(admin.getStatus());
        }
    }

    private boolean validateForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (*).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (admin == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new admins.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    public Admin getAdmin() {
        Admin a = new Admin();
        if (admin != null) {
            a.setId(admin.getId());
        }
        a.setFirstName(firstNameField.getText().trim());
        a.setLastName(lastNameField.getText().trim());
        a.setEmail(emailField.getText().trim());
        
        String password = new String(passwordField.getPassword());
        if (!password.isEmpty()) {
            a.setHashedPassword(PasswordHasher.hash(password));
        } else if (admin != null) {
            a.setHashedPassword(admin.getHashedPassword());
        }
        
        a.setPhone(phoneField.getText().trim());
        a.setRole((AdminRole) roleCombo.getSelectedItem());
        a.setStatus((AdminStatus) statusCombo.getSelectedItem());
        return a;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}