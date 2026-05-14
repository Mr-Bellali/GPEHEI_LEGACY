package view.module;

import view.master.MasterPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.Module;
import model.ModuleType;
import model.Filiere;
import model.ModuleStatus;
import service.FiliereService;
import service.ModuleService;
import service.AcademicService;
import exception.DatabaseException;

public class ModulePanel extends JPanel implements MasterPanel {

    private static final Color PRIMARY    = new Color(0x29, 0x80, 0xB9); // Belize Hole Blue
    private static final Color SECONDARY  = new Color(0x34, 0x98, 0xDB);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);

    private JTable moduleTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, importButton, exportButton;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private JComboBox<Filiere> filiereCombo; // Renamed from filiereFilter for consistency
    private JComboBox<Module> parentModuleCombo; // Renamed from parentModuleFilter

    private FiliereService filiereService;
    private ModuleService moduleService;
    private AcademicService academicService;

    public ModulePanel() {
        this.filiereService = new FiliereService();
        this.moduleService = new ModuleService();
        this.academicService = new AcademicService();
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        initializeUI();
        loadFilters();
    }

    private void initializeUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));

        JLabel title = new JLabel("Module & Element Management");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(WHITE);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BG_PAGE);
        content.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        content.add(buildToolbar(), BorderLayout.NORTH);
        content.add(buildTable(), BorderLayout.CENTER);

        return content;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);

        typeFilter = new JComboBox<>(new String[]{"All", "Modules", "Elements"});
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Disabled"});
        filiereCombo = new JComboBox<>(); // Renamed field
        parentModuleCombo = new JComboBox<>(); // Renamed field
        parentModuleCombo.setEnabled(false); // Initially disabled
        
        addButton = makeButton("+ Add", PRIMARY);
        editButton = makeButton("Edit", SECONDARY);
        deleteButton = makeButton("Delete", RED);
        refreshButton = makeButton("Refresh", SECONDARY);
        importButton = makeButton("Import CSV", SECONDARY);
        exportButton = makeButton("Export CSV", SECONDARY);

        toolbar.add(new JLabel("Filter Type:"));
        toolbar.add(typeFilter);
        toolbar.add(new JLabel("Status:"));
        toolbar.add(statusFilter);
        toolbar.add(new JLabel("Filiere:"));
        toolbar.add(filiereCombo); // Using renamed field
        toolbar.add(new JLabel("Parent Module:"));
        toolbar.add(parentModuleCombo); // Using renamed field
        toolbar.add(importButton);
        toolbar.add(exportButton);
        toolbar.add(refreshButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        return toolbar;
    }

    private JScrollPane buildTable() {
        String[] columns = {"ID", "Name", "Type", "Filiere", "Parent Module", "Semester", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        moduleTable = new JTable(tableModel);
        moduleTable.setRowHeight(35);
        moduleTable.getTableHeader().setBackground(PRIMARY);
        moduleTable.getTableHeader().setForeground(WHITE);
        
        // Custom renderers for better display
        moduleTable.getColumnModel().getColumn(3).setCellRenderer(new FiliereNameRenderer());
        moduleTable.getColumnModel().getColumn(4).setCellRenderer(new ParentModuleNameRenderer());

        return new JScrollPane(moduleTable);
    }

    private JButton makeButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void displayModules(List<Module> modules) {
        tableModel.setRowCount(0);
        for (Module m : modules) {
            tableModel.addRow(new Object[]{
                m.getId(), 
                m.getName(), 
                m.getType().name(), 
                m.getFiliereId(), // Display ID for now, will be rendered by custom renderer
                m.getParentModuleId() != null ? m.getParentModuleId() : "-",
                m.getSemester() != null ? m.getSemester() : "-",
                m.getStatus().name()
            });
        }
    }

    public int getSelectedModuleId() {
        int row = moduleTable.getSelectedRow();
        return row >= 0 ? (int) tableModel.getValueAt(row, 0) : -1;
    }

    public String getFilterStatus() {
        return (String) statusFilter.getSelectedItem();
    }
    
    public String getFilterType() {
        return (String) typeFilter.getSelectedItem();
    }
    
    public void addImportListener(ActionListener l) { importButton.addActionListener(l); }
    public void addExportListener(ActionListener l) { exportButton.addActionListener(l); }
    public void addStatusFilterListener(ActionListener l) { statusFilter.addActionListener(l); }
    
    public Filiere getSelectedFiliere() {
        return (Filiere) filiereCombo.getSelectedItem();
    }
    
    public Module getSelectedParentModule() {
        return (Module) parentModuleCombo.getSelectedItem();
    }
    
    public void updateParentModuleList(List<Module> parentModules) {
        parentModuleCombo.removeAllItems();
        parentModuleCombo.addItem(new Module(-1, "None", ModuleType.MOD, null, -1, ModuleStatus.ACTIVE, null, null)); // Add placeholder for "None"
        for (Module m : parentModules) {
            parentModuleCombo.addItem(m);
        }
        parentModuleCombo.setEnabled(true);
    }

    public void addAddListener(ActionListener l) { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l) { editButton.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { deleteButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }
    public void addFilterListener(ActionListener l) { typeFilter.addActionListener(l); }
    public void addFiliereFilterListener(ActionListener l) { filiereCombo.addActionListener(l); } // Listener for Filiere filter
    public void addParentModuleFilterListener(ActionListener l) { parentModuleCombo.addActionListener(l); } // Listener for Parent Module filter
    
    public JComboBox<Filiere> getFiliereCombo() { return filiereCombo; }
    public JComboBox<Module> getParentModuleCombo() { return parentModuleCombo; }

    @Override public String getPanelName() { return "Modules"; }
    @Override public void refreshData() {}
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() {}
    @Override public void onPanelHidden() {}

    private void loadFilters() {
        try {
            List<Filiere> filieres = filiereService.getAllFilieres();
            filiereCombo.addItem(new Filiere(-1, "All", "All", null, null)); // Add "All" option
            for (Filiere f : filieres) filiereCombo.addItem(f);
            
            // Configure renderer for Filiere filter
            filiereCombo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Filiere) setText(((Filiere) value).getName());
                    return this;
                }
            });
            
            // Configure renderer for Parent Module filter
            parentModuleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Module) setText(((Module) value).getName());
                    return this;
                }
            });
            
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading filters: " + e.getMessage());
        }
    }

    // Custom renderer for Filiere Name in table
    private class FiliereNameRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Integer) {
                try {
                    Filiere f = filiereService.getFiliereById((Integer) value);
                    setText(f != null ? f.getName() : "N/A");
                } catch (DatabaseException e) {
                    setText("Error");
                    e.printStackTrace();
                }
            }
            return this;
        }
    }

    // Custom renderer for Parent Module Name in table
    private class ParentModuleNameRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Integer && (Integer) value != 0) {
                try {
                    model.Module parentModule = moduleService.getModuleById((Integer) value);
                    setText(parentModule != null ? parentModule.getName() : "N/A");
                } catch (DatabaseException e) {
                    setText("Error");
                    e.printStackTrace();
                }
            } else {
                setText("-");
            }
            return this;
        }
    }
}
