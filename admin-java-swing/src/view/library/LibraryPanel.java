package view.library;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import view.master.MasterPanel;

public class LibraryPanel extends JPanel implements MasterPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;

    public LibraryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(244, 244, 248));
        initComponents();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(61, 52, 139));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("Library Management");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        addButton = new JButton("+ Add Library");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        toolbar.add(refreshButton);
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        
        String[] cols = {"ID", "Library Name", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        
        JPanel content = new JPanel(new BorderLayout());
        content.add(toolbar, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(content, BorderLayout.CENTER);
    }

    public void setData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) tableModel.addRow(row);
    }

    public int getSelectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return -1;
        return (int) tableModel.getValueAt(r, 0);
    }

    public Object[] getSelectedRowData() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        Object[] d = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < d.length; i++) d[i] = tableModel.getValueAt(r, i);
        return d;
    }

    public void addAddListener(ActionListener l) { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l) { editButton.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { deleteButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }

    @Override public String getPanelName() { return "Library"; }
    @Override public void refreshData() { /* Handled by controller */ }
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() { /* Handled by controller */ }
    @Override public void onPanelHidden() {}
}
