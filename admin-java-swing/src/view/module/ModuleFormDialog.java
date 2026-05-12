package view.module;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.ModuleType;
import model.Filiere;
import model.ModuleStatus;
import service.FiliereService;
import service.ModuleService;
import exception.DatabaseException;

public class ModuleFormDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<ModuleType> typeCombo;
    private JComboBox<Filiere> filiereCombo;
    private JComboBox<model.Module> parentCombo;
    private JSpinner semesterSpinner;
    private JComboBox<ModuleStatus> statusCombo;
    
    private boolean confirmed = false;
    private model.Module module;
    private FiliereService filiereService;
    private ModuleService moduleService;

    public ModuleFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        this.filiereService = new FiliereService();
        this.moduleService = new ModuleService();
        initComponents();
        loadData();
        setLocationRelativeTo(parent);
    }

    public ModuleFormDialog(JFrame parent, String title, model.Module m) {
        this(parent, title);
        this.module = m;
        populateFields();
    }

    private void initComponents() {
        setSize(550, 550); // Increased size
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // More padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(30);
        nameField.setPreferredSize(new Dimension(300, 35));
        add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(ModuleType.values());
        typeCombo.setPreferredSize(new Dimension(300, 35));
        typeCombo.addActionListener(e -> toggleParentCombo());
        add(typeCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Filiere:"), gbc);
        gbc.gridx = 1;
        filiereCombo = new JComboBox<>();
        filiereCombo.setPreferredSize(new Dimension(300, 35));
        filiereCombo.addActionListener(e -> refreshParentModules());
        add(filiereCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Parent Module:"), gbc);
        gbc.gridx = 1;
        parentCombo = new JComboBox<>();
        parentCombo.setPreferredSize(new Dimension(300, 35));
        parentCombo.setEnabled(false);
        add(parentCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        semesterSpinner.setPreferredSize(new Dimension(300, 35));
        add(semesterSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(ModuleStatus.values());
        statusCombo.setPreferredSize(new Dimension(300, 35));
        add(statusCombo, gbc);
        row++;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = new JButton("Save Module");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(0x2C, 0x3E, 0x50));
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
        
        // Custom renderer for JComboBox
        filiereCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Filiere) setText(((Filiere) value).getName());
                return this;
            }
        });
        
        parentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof model.Module) setText(((model.Module) value).getName());
                return this;
            }
        });
    }

    private void loadData() {
        try {
            List<Filiere> filieres = filiereService.getAllFilieres();
            for (Filiere f : filieres) filiereCombo.addItem(f);
            
            refreshParentModules();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void refreshParentModules() {
        parentCombo.removeAllItems();
        Filiere selected = (Filiere) filiereCombo.getSelectedItem();
        if (selected == null) return;

        try {
            List<model.Module> modules = moduleService.getAllModules();
            for (model.Module m : modules) {
                if (m.getType() == ModuleType.MOD && m.getFiliereId() == selected.getId()) {
                    // Don't include the module itself as its own parent when editing
                    if (this.module == null || m.getId() != this.module.getId()) {
                        parentCombo.addItem(m);
                    }
                }
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if(nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Module Name is required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if(filiereCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a Filiere!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void toggleParentCombo() {
        parentCombo.setEnabled(typeCombo.getSelectedItem() == ModuleType.ELM);
    }

    private void populateFields() {
        if (module != null) {
            nameField.setText(module.getName());
            typeCombo.setSelectedItem(module.getType());
            statusCombo.setSelectedItem(module.getStatus());
            semesterSpinner.setValue(module.getSemester() != null ? module.getSemester() : 1);
            
            // Pre-select Filiere (simplified)
            for (int i = 0; i < filiereCombo.getItemCount(); i++) {
                if (filiereCombo.getItemAt(i).getId() == module.getFiliereId()) {
                    filiereCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // Pre-select Parent (simplified)
            if (module.getParentModuleId() != null) {
                for (int i = 0; i < parentCombo.getItemCount(); i++) {
                    if (parentCombo.getItemAt(i).getId() == module.getParentModuleId()) {
                        parentCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    public model.Module getModule() {
        model.Module m = new model.Module();
        if (this.module != null) m.setId(this.module.getId());
        m.setName(nameField.getText().trim());
        m.setType((ModuleType) typeCombo.getSelectedItem());
        Filiere f = (Filiere) filiereCombo.getSelectedItem();
        if (f != null) m.setFiliereId(f.getId());
        if (m.getType() == ModuleType.ELM) {
            model.Module parent = (model.Module) parentCombo.getSelectedItem();
            if (parent != null) m.setParentModuleId(parent.getId());
        }
        m.setSemester((Integer) semesterSpinner.getValue());
        m.setStatus((ModuleStatus) statusCombo.getSelectedItem());
        return m;
    }

    public boolean isConfirmed() { return confirmed; }
}
