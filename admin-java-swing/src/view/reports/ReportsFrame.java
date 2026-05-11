package view.reports;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ReportsFrame extends JFrame {

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

    // ── Summary card labels ─────────────────────────────────
    private JLabel totalStudentsLbl;
    private JLabel totalProjectsLbl;
    private JLabel completionRateLbl;
    private JLabel pendingLbl;

    // ── Table ──────────────────────────────────────────────
    private JTable            reportTable;
    private DefaultTableModel tableModel;

    // ── Controls ───────────────────────────────────────────
    private JComboBox<String> reportTypeBox;
    private JComboBox<String> periodBox;
    private JButton           generateButton;
    private JButton           exportButton;
    private JButton           printButton;
    private JButton           logoutButton;

    public ReportsFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("GPEHEI - Reports");
        setSize(1050, 660);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PAGE);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(),    BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── SIDEBAR ────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(SIDEBAR_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(210, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(22, 0, 22, 0));

        JLabel brand = new JLabel("  GPEHEI");
        brand.setFont(new Font("Georgia", Font.BOLD, 17));
        brand.setForeground(WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 14, 16, 0));
        p.add(brand);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(210, 1));
        sep.setForeground(new Color(255, 255, 255, 40));
        p.add(sep);
        p.add(Box.createVerticalStrut(14));

        addNavBtn(p, "Dashboard", false);
        addNavBtn(p, "Students",  false);
        addNavBtn(p, "Projects",  false);
        addNavBtn(p, "Reports",   true);   // active
        addNavBtn(p, "Settings",  false);
        return p;
    }

    private void addNavBtn(JPanel sidebar, String text, boolean active) {
        JButton btn = new JButton("  " + text) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered || active) {
                    g2.setColor(new Color(255, 255, 255, 22));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(210, 42));
        btn.setPreferredSize(new Dimension(210, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(4));
    }

    // ── MAIN ───────────────────────────────────────────────
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

        JLabel title = new JLabel("Reports & Statistics");
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        logoutButton = makeBtn("Logout", RED);
        logoutButton.setPreferredSize(new Dimension(90, 34));
        right.add(logoutButton);

        p.add(title, BorderLayout.WEST);
        p.add(right,  BorderLayout.EAST);
        return p;
    }

    private JPanel buildContent() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BG_PAGE);
        p.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // ── Top: summary cards + controls ──
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);

        // Summary cards row
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0));
        cards.setOpaque(false);

        totalStudentsLbl  = new JLabel("—");
        totalProjectsLbl  = new JLabel("—");
        completionRateLbl = new JLabel("—");
        pendingLbl        = new JLabel("—");

        cards.add(buildSummaryCard("Total Students",   totalStudentsLbl,  SECONDARY));
        cards.add(buildSummaryCard("Total Projects",   totalProjectsLbl,  PRIMARY));
        cards.add(buildSummaryCard("Completion Rate",  completionRateLbl, GREEN));
        cards.add(buildSummaryCard("Pending Reviews",  pendingLbl,        ORANGE));

        // Controls row
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        JLabel typeLbl = new JLabel("Report Type:");
        typeLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        typeLbl.setForeground(new Color(0x55,0x55,0x66));

        reportTypeBox = new JComboBox<>(new String[]{
                "Student Summary", "Project Summary", "Completion Report", "Supervisor Report"
        });
        reportTypeBox.setFont(new Font("Arial", Font.PLAIN, 13));
        reportTypeBox.setPreferredSize(new Dimension(180, 34));

        JLabel periodLbl = new JLabel("Period:");
        periodLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        periodLbl.setForeground(new Color(0x55,0x55,0x66));

        periodBox = new JComboBox<>(new String[]{
                "All Time", "This Year", "This Semester", "Last Month"
        });
        periodBox.setFont(new Font("Arial", Font.PLAIN, 13));
        periodBox.setPreferredSize(new Dimension(140, 34));

        generateButton = makeBtn("Generate", PRIMARY);
        generateButton.setPreferredSize(new Dimension(100, 34));

        exportButton = makeBtn("Export CSV", GREEN);
        exportButton.setPreferredSize(new Dimension(110, 34));

        printButton = makeBtn("Print", SECONDARY);
        printButton.setPreferredSize(new Dimension(80, 34));

        controls.add(typeLbl);
        controls.add(reportTypeBox);
        controls.add(periodLbl);
        controls.add(periodBox);
        controls.add(generateButton);
        controls.add(exportButton);
        controls.add(printButton);

        top.add(cards,    BorderLayout.NORTH);
        top.add(controls, BorderLayout.SOUTH);

        // ── Table ──
        String[] cols = {"#", "Title / Name", "Category", "Value / Count", "Period", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(tableModel);
        styleTable(reportTable);

        JScrollPane scroll = new JScrollPane(reportTable);
        scroll.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scroll.getViewport().setBackground(WHITE);

        p.add(top,    BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildSummaryCard(String title, JLabel valueLbl, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,14));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 12, 12);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight()-4, 5, 5);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 80));
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 12));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLbl.setForeground(new Color(0x88,0x88,0x99));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLbl.setFont(new Font("Georgia", Font.BOLD, 26));
        valueLbl.setForeground(accent);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLbl);
        return card;
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
        t.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object val, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(table, val, sel, focus, row, col);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : new Color(0xF0,0xF0,0xF8));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setForeground(sel ? PRIMARY : new Color(0x33,0x33,0x44));
                return this;
            }
        });

        int[] widths = {45, 260, 140, 120, 120, 95};
        for (int i = 0; i < widths.length; i++)
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private JButton makeBtn(String text, Color bg) {
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
    /** Set summary card numbers from DB */
    public void setSummary(int students, int projects, String completionRate, int pending) {
        totalStudentsLbl.setText(String.valueOf(students));
        totalProjectsLbl.setText(String.valueOf(projects));
        completionRateLbl.setText(completionRate);   // e.g. "74%"
        pendingLbl.setText(String.valueOf(pending));
    }

    /** Fill report table */
    public void setReportData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) tableModel.addRow(row);
    }

    public String getReportType() { return (String) reportTypeBox.getSelectedItem(); }
    public String getPeriod()     { return (String) periodBox.getSelectedItem(); }

    public void addGenerateListener(ActionListener l) { generateButton.addActionListener(l); }
    public void addExportListener(ActionListener l)   { exportButton.addActionListener(l); }
    public void addPrintListener(ActionListener l)    { printButton.addActionListener(l); }
    public void addLogoutListener(ActionListener l)   { logoutButton.addActionListener(l); }

    public void showMessage(String msg) { JOptionPane.showMessageDialog(this, msg); }
}