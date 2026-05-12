package view.teacher;

import view.master.MasterPanel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.Teacher;

public class TeachersPanel extends JPanel implements MasterPanel {

    private static final Color PRIMARY    = new Color(0x2C, 0x3E, 0x50);
    private static final Color SECONDARY  = new Color(0x34, 0x49, 0x5E);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);
    private static final Color GREEN      = new Color(0x27, 0xAE, 0x60);
    private static final Color ORANGE     = new Color(0xE6, 0x7E, 0x22);
    private static final Color PURPLE     = new Color(0x8E, 0x44, 0xAD);
    private static final Color DARK_ORANGE = new Color(0xD3, 0x54, 0x00);

    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deactivateButton, reactivateButton, refreshButton, assignmentsButton, supervisionButton;
    private JLabel totalLabel;

    public TeachersPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        initializeUI();
    }

    private void initializeUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(13, 22, 13, 22));

        JLabel title = new JLabel("Teachers Management");
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

        totalLabel = new JLabel("Total teachers: 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalLabel.setForeground(new Color(0x55, 0x55, 0x66));
        content.add(totalLabel, BorderLayout.SOUTH);

        return content;
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JButton searchBtn = makeButton("Search", SECONDARY);

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        addButton = makeButton("+ Add", PRIMARY);
        editButton = makeButton("Edit", SECONDARY);
        assignmentsButton = makeButton("Assignments", PURPLE);
        supervisionButton = makeButton("Supervision", DARK_ORANGE);
        deactivateButton = makeButton("Deactivate", RED);
        reactivateButton = makeButton("Reactivate", GREEN);
        refreshButton = makeButton("Refresh", GREEN);

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(assignmentsButton);
        buttonPanel.add(supervisionButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(reactivateButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(buttonPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JScrollPane buildTable() {
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Status", "Created", "Updated"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        teacherTable = new JTable(tableModel);
        styleTable(teacherTable);

        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.getViewport().setBackground(WHITE);

        return scrollPane;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(0x34, 0x49, 0x5E, 60));
        table.setBackground(WHITE);

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? WHITE : new Color(0xF0, 0xF0, 0xF8));
                }

                if (column == 4 && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "ACTIVE": c.setForeground(GREEN); break;
                        case "DISABLED": c.setForeground(RED); break;
                    }
                } else {
                    c.setForeground(new Color(0x33, 0x33, 0x44));
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        int[] widths = {50, 120, 120, 180, 100, 100, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private JPanel buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(LIGHT_GRAY);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 16));

        JLabel status = new JLabel("Ready");
        status.setFont(new Font("Arial", Font.PLAIN, 11));
        status.setForeground(new Color(0x55, 0x55, 0x66));

        statusBar.add(status, BorderLayout.WEST);
        return statusBar;
    }

    private JButton makeButton(String text, Color bg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void displayTeachers(List<Teacher> teachers) {
        tableModel.setRowCount(0);
        for (Teacher t : teachers) {
            Object[] row = {
                    t.getId(),
                    t.getFirstName(),
                    t.getLastName(),
                    t.getEmail(),
                    t.getStatus().name(),
                    t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate().toString() : "N/A",
                    t.getUpdatedAt() != null ? t.getUpdatedAt().toLocalDate().toString() : "N/A"
            };
            tableModel.addRow(row);
        }
        totalLabel.setText("Total teachers: " + teachers.size());
    }

    public int getSelectedTeacherId() {
        int row = teacherTable.getSelectedRow();
        if (row >= 0) {
            return (int) tableModel.getValueAt(row, 0);
        }
        return -1;
    }

    public String getSearchKeyword() {
        return searchField.getText().trim();
    }

    public void addAddListener(ActionListener l) { addButton.addActionListener(l); }
    public void addEditListener(ActionListener l) { editButton.addActionListener(l); }
    public void addDeactivateListener(ActionListener l) { deactivateButton.addActionListener(l); }
    public void addReactivateListener(ActionListener l) { reactivateButton.addActionListener(l); }
    public void addRefreshListener(ActionListener l) { refreshButton.addActionListener(l); }
    public void addSearchListener(ActionListener l) { searchField.addActionListener(l); }
    public void addAssignmentsListener(ActionListener l) { assignmentsButton.addActionListener(l); }
    public void addSupervisionListener(ActionListener l) { supervisionButton.addActionListener(l); }

    @Override
    public String getPanelName() { return "Teachers"; }

    @Override
    public void refreshData() { /* Will be handled by controller */ }

    @Override
    public JPanel getContentPanel() { return this; }

    @Override
    public void onPanelShown() { /* Will be handled by controller */ }

    @Override
    public void onPanelHidden() {}
}
