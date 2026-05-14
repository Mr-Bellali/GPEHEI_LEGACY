package view.filiere;

import view.master.MasterPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.Filiere;

public class FilierePanel extends JPanel implements MasterPanel {

    private static final Color PRIMARY    = new Color(0x16, 0xA0, 0x85); // Emerald Green
    private static final Color SECONDARY  = new Color(0x1A, 0xBC, 0x9C);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);

    private JTable filiereTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton, importButton, exportButton;
    private JLabel totalLabel;

    public FilierePanel() {
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

        JLabel title = new JLabel("Filiere Management");
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

        totalLabel = new JLabel("Total filieres: 0");
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
        refreshButton = makeButton("Refresh", SECONDARY);
        importButton = makeButton("Import CSV", SECONDARY);
        exportButton = makeButton("Export CSV", SECONDARY);

        toolbar.add(importButton);
        toolbar.add(exportButton);
        toolbar.add(refreshButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);

        return toolbar;
    }

    private JScrollPane buildTable() {
        String[] columns = {"ID", "Full Name", "Short Name", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        filiereTable = new JTable(tableModel);
        filiereTable.setRowHeight(35);
        filiereTable.getTableHeader().setBackground(PRIMARY);
        filiereTable.getTableHeader().setForeground(WHITE);

        return new JScrollPane(filiereTable);
    }

    private JButton makeButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void displayFilieres(List<Filiere> filieres) {
        tableModel.setRowCount(0);
        for (Filiere f : filieres) {
            tableModel.addRow(new Object[]{f.getId(), f.getName(), f.getShortName(), f.getCreatedAt()});
        }
        totalLabel.setText("Total filieres: " + filieres.size());
    }

    public int getSelectedFiliereId() {
        int row = filiereTable.getSelectedRow();
        return row >= 0 ? (int) tableModel.getValueAt(row, 0) : -1;
    }

    public void addAddListener(ActionListener l) { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l) { editButton.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { deleteButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }
    public void addImportListener(ActionListener l) { importButton.addActionListener(l); }
    public void addExportListener(ActionListener l) { exportButton.addActionListener(l); }

    @Override public String getPanelName() { return "Filieres"; }
    @Override public void refreshData() {}
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() {}
    @Override public void onPanelHidden() {}
}
