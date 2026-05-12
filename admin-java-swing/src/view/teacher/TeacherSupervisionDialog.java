package view.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import model.Project;
import service.ProjectService;
import service.AcademicService;
import exception.DatabaseException;

public class TeacherSupervisionDialog extends JDialog {

    private final int teacherId;
    private final ProjectService projectService;
    private final AcademicService academicService;
    
    private JList<Project> projectList;
    private DefaultListModel<Project> projectListModel;
    
    private boolean confirmed = false;

    public TeacherSupervisionDialog(JFrame parent, int teacherId, String teacherName) {
        super(parent, "Manage Project Supervision: " + teacherName, true);
        this.teacherId = teacherId;
        this.projectService = new ProjectService();
        this.academicService = new AcademicService();
        
        initComponents();
        loadProjects();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Assign to Projects"));
        
        projectListModel = new DefaultListModel<>();
        projectList = new JList<>(projectListModel);
        projectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        projectList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Project) {
                    setText(((Project) value).getTitle());
                }
                return this;
            }
        });
        
        centerPanel.add(new JScrollPane(projectList), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Supervision");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveSupervision());
        cancelBtn.addActionListener(e -> dispose());
        
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadProjects() {
        try {
            List<Project> projects = projectService.getActiveProjects();
            projectListModel.clear();
            for (Project p : projects) {
                projectListModel.addElement(p);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading projects: " + e.getMessage());
        }
    }

    private void saveSupervision() {
        List<Project> selected = projectList.getSelectedValuesList();
        List<Integer> projectIds = new ArrayList<>();
        for (Project p : selected) {
            projectIds.add(p.getId());
        }
        
        try {
            academicService.updateTeacherSupervision(teacherId, projectIds);
            
            confirmed = true;
            JOptionPane.showMessageDialog(this, "Supervision assignments saved!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
