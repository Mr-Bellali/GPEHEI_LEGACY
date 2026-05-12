package view.filiere;

import javax.swing.*;
import java.awt.*;
import model.Filiere;

public class FiliereFormDialog extends JDialog {
    private JTextField nameField, shortNameField;
    private boolean confirmed = false;
    private Filiere filiere;

    public FiliereFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public FiliereFormDialog(JFrame parent, String title, Filiere f) {
        this(parent, title);
        this.filiere = f;
        nameField.setText(f.getName());
        shortNameField.setText(f.getShortName());
    }

    private void initComponents() {
        setSize(500, 300); // Increased size
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15); // More padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(30); // Increased width
        nameField.setPreferredSize(new Dimension(300, 35)); // Specific size
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Short Name:"), gbc);
        gbc.gridx = 1;
        shortNameField = new JTextField(15); // Increased width
        shortNameField.setPreferredSize(new Dimension(150, 35)); // Specific size
        add(shortNameField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.setBackground(new Color(0x2C, 0x3E, 0x50));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(120, 35));

        cancelBtn.setBackground(new Color(0xE7, 0x4C, 0x3C));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        
        saveBtn.addActionListener(e -> { 
            if(validateForm()) {
                confirmed = true; 
                dispose(); 
            }
        });
        cancelBtn.addActionListener(e -> dispose());
        
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private boolean validateForm() {
        if(nameField.getText().trim().isEmpty() || shortNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    public Filiere getFiliere() {
        Filiere f = new Filiere();
        if (this.filiere != null) f.setId(this.filiere.getId()); // Pass ID for updates
        f.setName(nameField.getText().trim());
        f.setShortName(shortNameField.getText().trim());
        return f;
    }

    public boolean isConfirmed() { return confirmed; }
}
