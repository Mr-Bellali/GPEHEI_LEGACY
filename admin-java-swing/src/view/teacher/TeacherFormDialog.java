package view.teacher;

import javax.swing.*;
import java.awt.*;
import model.Teacher;
import model.TeacherStatus;
import utils.Validator;

public class TeacherFormDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField, filiereIdField;
    private JPasswordField passwordField;
    private JComboBox<String> statusCombo;

    private boolean confirmed = false;
    private Teacher teacher;

    public TeacherFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public TeacherFormDialog(JFrame parent, String title, Teacher teacher) {
        this(parent, title);
        this.teacher = teacher;
        populateFields();
    }

    private void initComponents() {
        setSize(450, 450);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
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

        // Filiere ID
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Filiere ID:"), gbc);
        gbc.gridx = 1;
        filiereIdField = new JTextField(20);
        mainPanel.add(filiereIdField, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});
        mainPanel.add(statusCombo, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setBackground(new Color(0x2C, 0x3E, 0x50));
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
        if (teacher != null) {
            firstNameField.setText(teacher.getFirstName());
            lastNameField.setText(teacher.getLastName());
            emailField.setText(teacher.getEmail());
            passwordField.setText(teacher.getPassword());
            if (teacher.getFiliereId() != null) {
                filiereIdField.setText(String.valueOf(teacher.getFiliereId()));
            }
            statusCombo.setSelectedItem(teacher.getStatus().name());
        }
    }

    private boolean validateForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String filiereIdStr = filiereIdField.getText().trim();

        if (!Validator.isValidName(firstName)) {
            JOptionPane.showMessageDialog(this, "Invalid first name!", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isValidName(lastName)) {
            JOptionPane.showMessageDialog(this, "Invalid last name!", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email!", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (teacher == null) {
            if (!Validator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "Weak password!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else if (!password.isEmpty() && password.length() != 64 && !Validator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Weak password!", "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!filiereIdStr.isEmpty()) {
            try {
                Integer.parseInt(filiereIdStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Filiere ID must be a number!", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public Teacher getTeacher() {
        Teacher t = new Teacher();
        t.setFirstName(firstNameField.getText().trim());
        t.setLastName(lastNameField.getText().trim());
        t.setEmail(emailField.getText().trim());
        t.setPassword(new String(passwordField.getPassword()));
        
        String filiereIdStr = filiereIdField.getText().trim();
        if (!filiereIdStr.isEmpty()) {
            t.setFiliereId(Integer.parseInt(filiereIdStr));
        }
        
        t.setStatus(TeacherStatus.valueOf((String) statusCombo.getSelectedItem()));
        return t;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
