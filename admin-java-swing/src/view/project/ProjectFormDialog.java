package view.project;

import javax.swing.*;
import java.awt.*;
import model.Project;
import model.ProjectStatus;

public class ProjectFormDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<ProjectStatus> statusCombo;
    
    private boolean confirmed = false;
    private Project project;

    public ProjectFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    public ProjectFormDialog(JFrame parent, String title, Project p) {
        this(parent, title);
        this.project = p;
        populateFields();
    }

    private void initComponents() {
        setSize(500, 400);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Project Title:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(30);
        titleField.setPreferredSize(new Dimension(250, 35));
        add(titleField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(descriptionArea);
        scroll.setPreferredSize(new Dimension(250, 100));
        add(scroll, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(ProjectStatus.values());
        statusCombo.setPreferredSize(new Dimension(250, 35));
        add(statusCombo, gbc);
        row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = new JButton("Save Project");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.setBackground(new Color(0x3D, 0x34, 0x8B));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setPreferredSize(new Dimension(130, 35));
        
        cancelBtn.setBackground(new Color(0xE7, 0x4C, 0x3C));
        cancelBtn.setForeground(Color.WHITE);
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

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private boolean validateForm() {
        if(titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project Title is required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void populateFields() {
        if (project != null) {
            titleField.setText(project.getTitle());
            descriptionArea.setText(project.getDescription());
            statusCombo.setSelectedItem(project.getStatus());
        }
    }

    public Project getProject() {
        Project p = new Project();
        if (this.project != null) p.setId(this.project.getId());
        p.setTitle(titleField.getText().trim());
        p.setDescription(descriptionArea.getText().trim());
        p.setStatus((ProjectStatus) statusCombo.getSelectedItem());
        return p;
    }

    public boolean isConfirmed() { return confirmed; }
}
