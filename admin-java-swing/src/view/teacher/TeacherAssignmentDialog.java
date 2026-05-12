package view.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import model.Filiere;
import model.Groupe;
import service.AcademicService;
import exception.DatabaseException;

public class TeacherAssignmentDialog extends JDialog {

    private final int teacherId;
    private final AcademicService academicService;
    
    private JComboBox<Filiere> filiereCombo;
    private JList<model.Module> moduleList;
    private DefaultListModel<model.Module> moduleListModel;
    private JList<Groupe> groupList;
    private DefaultListModel<Groupe> groupListModel;
    
    private boolean confirmed = false;

    public TeacherAssignmentDialog(JFrame parent, int teacherId, String teacherName) {
        super(parent, "Manage Assignments: " + teacherName, true);
        this.teacherId = teacherId;
        this.academicService = new AcademicService();
        
        initComponents();
        loadFilieres();
        loadCurrentAssignments();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top: Filiere Selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Filiere"));
        filiereCombo = new JComboBox<>();
        filiereCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Filiere) {
                    setText(((Filiere) value).getName());
                }
                return this;
            }
        });
        filiereCombo.addActionListener(e -> onFiliereSelected());
        topPanel.add(new JLabel("Filiere:"));
        topPanel.add(filiereCombo);
        add(topPanel, BorderLayout.NORTH);
        
        // Center: Modules and Groups
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Modules List
        moduleListModel = new DefaultListModel<>();
        moduleList = new JList<>(moduleListModel);
        moduleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        moduleList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof model.Module) {
                    setText(((model.Module) value).getName());
                }
                return this;
            }
        });
        centerPanel.add(new JScrollPane(moduleList));
        
        // Groups List (Placeholder as per user request to select multi)
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Groupe) {
                    setText(((Groupe) value).getName());
                }
                return this;
            }
        });
        centerPanel.add(new JScrollPane(groupList));
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom: Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Assignments");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> saveAssignments());
        cancelBtn.addActionListener(e -> dispose());
        
        bottomPanel.add(saveBtn);
        bottomPanel.add(cancelBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadFilieres() {
        try {
            List<Filiere> filieres = academicService.getAllFilieres();
            for (Filiere f : filieres) {
                filiereCombo.addItem(f);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading filieres: " + e.getMessage());
        }
    }

    private void onFiliereSelected() {
        Filiere selected = (Filiere) filiereCombo.getSelectedItem();
        if (selected != null) {
            try {
                // Load Modules
                moduleListModel.clear();
                List<model.Module> modules = academicService.getModulesByFiliere(selected.getId());
                for (model.Module m : modules) {
                    moduleListModel.addElement(m);
                }
                
                // Load Groups
                groupListModel.clear();
                List<Groupe> groups = academicService.getGroupsByFiliere(selected.getId());
                for (Groupe g : groups) {
                    groupListModel.addElement(g);
                }
                
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this, "Error loading details: " + e.getMessage());
            }
        }
    }

    private void loadCurrentAssignments() {
        // Logic to pre-select current modules for this teacher
        try {
            List<model.Module> currentModules = academicService.getTeacherModules(teacherId);
            // This is complex for a multi-select JList across different filieres
            // For now, we focus on the assignment workflow
        } catch (DatabaseException e) {
            // Silently fail or log
        }
    }

    private void saveAssignments() {
        List<model.Module> selectedModules = moduleList.getSelectedValuesList();
        List<Integer> moduleIds = new ArrayList<>();
        for (model.Module m : selectedModules) {
            moduleIds.add(m.getId());
        }
        
        try {
            academicService.updateTeacherAssignments(teacherId, moduleIds);
            confirmed = true;
            JOptionPane.showMessageDialog(this, "Assignments saved successfully!");
            dispose();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error saving assignments: " + e.getMessage());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
