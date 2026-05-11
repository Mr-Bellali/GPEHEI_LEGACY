package view.project;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import view.master.MasterPanel;

public class ProjectsPanel extends JPanel implements MasterPanel {

    // ── Color Palette ──────────────────────────────────────
    private static final Color PRIMARY    = new Color(0x3D, 0x34, 0x8B);
    private static final Color SECONDARY  = new Color(0x76, 0x78, 0xED);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color SIDEBAR_BG = new Color(0x2E, 0x28, 0x72);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);
    private static final Color GREEN      = new Color(0x27, 0xAE, 0x60);
    private static final Color ORANGE     = new Color(0xE6, 0x7E, 0x22);

    private JTable            projectTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;
    private JComboBox<String> statusFilter;
    private JButton           addButton;
    private JButton           editButton;
    private JButton           deleteButton;
    private JButton           refreshButton;
    private JButton           logoutButton;
    private JLabel            totalLabel;

    public ProjectsPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        add(buildMain(), BorderLayout.CENTER);
    }

    private JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_PAGE);
        p.add(buildHeader(),  BorderLayout.NORTH);
        p.add(buildContent(), BorderLayout.CENTER);
        p.add(buildStatus(),  BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(PRIMARY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));

        JLabel title = new JLabel("Projects Management");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(WHITE);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(18);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        statusFilter = new JComboBox<>(new String[]{"All Status","Active","Completed","On Hold","Cancelled"});
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilter.setPreferredSize(new Dimension(140, 34));

        JButton searchBtn = makeActionButton("Search", SECONDARY);
        searchBtn.setPreferredSize(new Dimension(90, 34));

        searchPanel.add(searchField);
        searchPanel.add(statusFilter);
        searchPanel.add(searchBtn);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        addButton     = makeActionButton("+ Add",    PRIMARY);
        editButton    = makeActionButton("✎ Edit",   SECONDARY);
        deleteButton  = makeActionButton("✕ Delete", RED);
        refreshButton = makeActionButton("↻ Refresh",GREEN);
        for (JButton b : new JButton[]{refreshButton, addButton, editButton, deleteButton})
            b.setPreferredSize(new Dimension(90, 34));
        btnPanel.add(refreshButton);
        btnPanel.add(addButton);
        btnPanel.add(editButton);
        btnPanel.add(deleteButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(btnPanel,    BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Project Title", "Supervisor", "Students", "Start Date", "End Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        projectTable = new JTable(tableModel);
        styleTable(projectTable);

        JScrollPane scroll = new JScrollPane(projectTable);
        scroll.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scroll.getViewport().setBackground(WHITE);

        totalLabel = new JLabel("Total projects: 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalLabel.setForeground(new Color(0x55, 0x55, 0x66));

        p.add(toolbar,    BorderLayout.NORTH);
        p.add(scroll,     BorderLayout.CENTER);
        p.add(totalLabel, BorderLayout.SOUTH);
        return p;
    }

    private void styleTable(JTable t) {
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0x76, 0x78, 0xED, 60));
        t.setSelectionForeground(PRIMARY);
        t.setBackground(WHITE);
        t.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        t.getTableHeader().setBackground(PRIMARY);
        t.getTableHeader().setForeground(WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 38));

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(table, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : new Color(0xF0, 0xF0, 0xF8));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (col == 6 && val != null) {
                    switch (val.toString()) {
                        case "Active":    setForeground(GREEN);  break;
                        case "Completed": setForeground(SECONDARY); break;
                        case "On Hold":   setForeground(ORANGE); break;
                        case "Cancelled": setForeground(RED);    break;
                        default:          setForeground(new Color(0x33,0x33,0x44));
                    }
                } else {
                    setForeground(sel ? PRIMARY : new Color(0x33,0x33,0x44));
                }
                return this;
            }
        });

        int[] widths = {55, 220, 160, 80, 100, 100, 95};
        for (int i = 0; i < widths.length; i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private JButton makeActionButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setForeground(WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(LIGHT_GRAY);
        p.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));
        JLabel s = new JLabel("Ready");
        s.setFont(new Font("Arial", Font.PLAIN, 11));
        s.setForeground(new Color(0x55,0x55,0x66));
        p.add(s, BorderLayout.WEST);
        return p;
    }

    // ── PUBLIC API ─────────────────────────────────────────
    public void setProjects(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) tableModel.addRow(row);
        totalLabel.setText("Total projects: " + tableModel.getRowCount());
    }

    public void addProjectRow(Object[] row) {
        tableModel.addRow(row);
        totalLabel.setText("Total projects: " + tableModel.getRowCount());
    }

    public int getSelectedRow() { return projectTable.getSelectedRow(); }

    public Object[] getSelectedRowData() {
        int r = projectTable.getSelectedRow();
        if (r < 0) return null;
        Object[] d = new Object[tableModel.getColumnCount()];
        for (int i = 0; i < d.length; i++) d[i] = tableModel.getValueAt(r, i);
        return d;
    }

    public void removeSelectedRow() {
        int r = projectTable.getSelectedRow();
        if (r >= 0) {
            tableModel.removeRow(r);
            totalLabel.setText("Total projects: " + tableModel.getRowCount());
        }
    }

    public String getSearchText()       { return searchField.getText().trim(); }
    public String getStatusFilter()     { return (String) statusFilter.getSelectedItem(); }

    public void addAddListener(ActionListener l)     { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l)    { editButton.addActionListener(l); }
    public void addDeleteListener(ActionListener l)  { deleteButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }

    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }

    // ═══════════ MasterPanel Interface Methods ═══════════
    @Override
    public String getPanelName() {
        return "Projects";
    }

    @Override
    public void refreshData() {
        System.out.println("Refreshing projects data...");
    }

    @Override
    public JPanel getContentPanel() {
        return this;
    }

    @Override
    public void onPanelShown() {
        refreshData();
    }

    @Override
    public void onPanelHidden() {
        // Cleanup if needed
    }
}