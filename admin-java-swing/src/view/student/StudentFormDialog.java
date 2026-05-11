package view.student;

import javax.swing.*;
import java.awt.*;
import model.Student;
import model.StudentStatus;
import utils.Validator;

public class StudentFormDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField, phoneField, cinField, cneField;
    private JPasswordField passwordField;
    private JComboBox<String> statusCombo;
    private JSpinner studentNumberSpinner;

    private boolean confirmed = false;
    private Student student;

    public StudentFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public StudentFormDialog(JFrame parent, String title, Student student) {
        this(parent, title);
        this.student = student;
        populateFields();
    }

    private void initComponents() {
        setSize(500, 550);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        int row = 0;

        // Student Number
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Student #:"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel numberModel = new SpinnerNumberModel(1000, 1000, 9999, 1);
        studentNumberSpinner = new JSpinner(numberModel);
        mainPanel.add(studentNumberSpinner, gbc);
        row++;

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
        if (student != null) {
            passwordField.setText(student.getPassword());
        }
        mainPanel.add(passwordField, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        mainPanel.add(phoneField, gbc);
        row++;

        // CIN
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("CIN:"), gbc);
        gbc.gridx = 1;
        cinField = new JTextField(20);
        mainPanel.add(cinField, gbc);
        row++;

        // CNE
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("CNE:"), gbc);
        gbc.gridx = 1;
        cneField = new JTextField(20);
        mainPanel.add(cneField, gbc);
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
        if (student != null) {
            studentNumberSpinner.setValue(student.getStudentNumber());
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());
            emailField.setText(student.getEmail());
            passwordField.setText(student.getPassword());

            if (student.getPhone() != null) {
                phoneField.setText(student.getPhone());
            }
            if (student.getCin() != null) {
                cinField.setText(student.getCin());
            }
            if (student.getCne() != null) {
                cneField.setText(student.getCne());
            }

            statusCombo.setSelectedItem(student.getStatus().name());
        }
    }

    private boolean validateForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate First Name
        if (!Validator.isValidName(firstName)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid first name! Must be at least 2 characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }

        // Validate Last Name
        if (!Validator.isValidName(lastName)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid last name! Must be at least 2 characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }

        // Validate Email
        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid email format! Example: user@domain.com",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // Validate Password (only for new students)
        if (student == null) {
            if (!Validator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this,
                        "Weak password! Must be at least 6 characters with letters and numbers.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                return false;
            }
        } else {
            // For editing, password is optional (keep old if empty)
            if (!password.isEmpty() && !Validator.isValidPassword(password)) {
                JOptionPane.showMessageDialog(this,
                        "Weak password! Must be at least 6 characters with letters and numbers.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                return false;
            }
        }

        // Validate Phone (optional but must be valid if provided)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !Validator.isValidLength(phone, 10, 12)) {
            JOptionPane.showMessageDialog(this,
                    "Phone number must be 10-12 characters!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        return true;
    }

    public Student getStudent() {
        Student s = new Student();
        s.setStudentNumber((Integer) studentNumberSpinner.getValue());
        s.setFirstName(firstNameField.getText().trim());
        s.setLastName(lastNameField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setPassword(new String(passwordField.getPassword()));
        s.setPhone(phoneField.getText().trim());
        s.setCin(cinField.getText().trim());
        s.setCne(cneField.getText().trim());
        s.setStatus(StudentStatus.valueOf((String) statusCombo.getSelectedItem()));
        return s;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}