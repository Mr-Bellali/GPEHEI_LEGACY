package view.groupe;

import view.master.MasterPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.Groupe;
import model.Filiere;
import service.FiliereService;
import exception.DatabaseException;

public class GroupePanel extends JPanel implements MasterPanel {

    private static final Color PRIMARY    = new Color(0x8E, 0x44, 0xAD); // Wisteria Purple
    private static final Color SECONDARY  = new Color(0x9B, 0x59, 0xB6);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);
    private static final Color GREEN      = new Color(0x27, 0xAE, 0x60);

    private JTable groupTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JLabel totalLabel;
    
    private FiliereService filiereService;

    public GroupePanel() {
        this.filiereService = new FiliereService();
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        initializeUI();
    }

    private void initializeUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));

        JLabel title = new JLabel("Group Management");
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

        totalLabel = new JLabel("Total groups: 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        content.add(totalLabel, BorderLayout.SOUTH);

        return content;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);

        addButton = makeButton("+ Add", PRIMARY);
        editButton = makeButton("Edit", SECONDARY);
        deleteButton = makeButton("Delete", RED);
        refreshButton = makeButton("Refresh", GREEN);

        toolbar.add(refreshButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        return toolbar;
    }

    private JScrollPane buildTable() {
        String[] columns = {"ID", "Group Name", "Filiere", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        groupTable = new JTable(tableModel);
        groupTable.setRowHeight(35);
        groupTable.getTableHeader().setBackground(PRIMARY);
        groupTable.getTableHeader().setForeground(WHITE);
        
        // Add custom renderer for Filiere Name
        groupTable.getColumnModel().getColumn(2).setCellRenderer(new FiliereNameRenderer());

        return new JScrollPane(groupTable);
    }

    private JButton makeButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void displayGroups(List<Groupe> groups) {
        tableModel.setRowCount(0);
        for (Groupe g : groups) {
            tableModel.addRow(new Object[]{
                g.getId(), 
                g.getName(), 
                g.getFiliereId(), // Display ID for now
                g.getStatus().name()
            });
        }
        totalLabel.setText("Total groups: " + groups.size());
    }

    public int getSelectedGroupId() {
        int row = groupTable.getSelectedRow();
        return row >= 0 ? (int) tableModel.getValueAt(row, 0) : -1;
    }

    public void addAddListener(ActionListener l) { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l) { editButton.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { deleteButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }

    @Override public String getPanelName() { return "Groups"; }
    @Override public void refreshData() {}
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() {}
    @Override public void onPanelHidden() {}

    // Custom renderer for Filiere Name
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
}
