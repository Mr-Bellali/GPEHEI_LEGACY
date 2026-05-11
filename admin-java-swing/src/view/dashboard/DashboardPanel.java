package view.dashboard;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import view.master.MasterPanel;

public class DashboardPanel extends JPanel implements MasterPanel {

    // Colors
    private static final Color PRIMARY    = new Color(0x3D, 0x34, 0x8B);
    private static final Color SECONDARY  = new Color(0x76, 0x78, 0xED);
    private static final Color WHITE      = new Color(0xFF, 0xFF, 0xFF);
    private static final Color LIGHT_GRAY = new Color(0xD1, 0xD1, 0xD1);
    private static final Color BG_PAGE    = new Color(0xF4, 0xF4, 0xF8);
    private static final Color ORANGE     = new Color(0xE6, 0x7E, 0x22);
    private static final Color RED        = new Color(0xE7, 0x4C, 0x3C);
    private static final Color GREEN      = new Color(0x27, 0xAE, 0x60);

    private JLabel totalStudentsValue;
    private JLabel activeProjectsValue;
    private JLabel pendingProjectsValue;
    private JLabel unreadAlertsValue;
    private JLabel lastUpdatedLabel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        buildUI();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCards(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(WHITE);

        lastUpdatedLabel = new JLabel("Last updated: Never");
        lastUpdatedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        lastUpdatedLabel.setForeground(new Color(255, 255, 255, 180));

        header.add(title, BorderLayout.WEST);
        header.add(lastUpdatedLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCards() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_PAGE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);

        // Create value labels
        totalStudentsValue = new JLabel("0", SwingConstants.CENTER);
        activeProjectsValue = new JLabel("0", SwingConstants.CENTER);
        pendingProjectsValue = new JLabel("0", SwingConstants.CENTER);
        unreadAlertsValue = new JLabel("0", SwingConstants.CENTER);

        // Add cards
        grid.add(createCard("📊 Total Students", totalStudentsValue,
                "All registered students", SECONDARY));
        grid.add(createCard("📁 Active Projects", activeProjectsValue,
                "Currently running projects", PRIMARY));
        grid.add(createCard("⏳ Pending Projects", pendingProjectsValue,
                "Waiting for approval", ORANGE));
        grid.add(createCard("🔔 System Alerts", unreadAlertsValue,
                "Unread notifications", RED));

        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createCard(String title, JLabel valueLabel, String subtitle, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);

                // Card background
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                // Top color bar
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth() - 3, 5, 5, 5);

                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(0x66, 0x66, 0x77));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value (big number)
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitleLabel.setForeground(LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        JLabel status = new JLabel("✅ System running normally");
        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(new Color(0x55, 0x55, 0x66));

        JLabel version = new JLabel("GPEHEI v1.0");
        version.setFont(new Font("Arial", Font.PLAIN, 12));
        version.setForeground(new Color(0x55, 0x55, 0x66));

        footer.add(status, BorderLayout.WEST);
        footer.add(version, BorderLayout.EAST);
        return footer;
    }

    // Method to update dashboard with real data
    public void updateStats(int totalStudents, int activeProjects,
                            int pendingProjects, int unreadAlerts) {
        totalStudentsValue.setText(String.valueOf(totalStudents));
        activeProjectsValue.setText(String.valueOf(activeProjects));
        pendingProjectsValue.setText(String.valueOf(pendingProjects));
        unreadAlertsValue.setText(String.valueOf(unreadAlerts));

        // Update last refresh time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        lastUpdatedLabel.setText("Last updated: " + sdf.format(new Date()));
    }

    // Set loading state
    public void showLoading() {
        totalStudentsValue.setText("...");
        activeProjectsValue.setText("...");
        pendingProjectsValue.setText("...");
        unreadAlertsValue.setText("...");
    }

    // MasterPanel interface methods
    @Override
    public String getPanelName() {
        return "Dashboard";
    }

    @Override
    public void refreshData() {
        showLoading();
        // Data will be loaded by updateStats() called from controller
    }

    @Override
    public JPanel getContentPanel() {
        return this;
    }

    @Override
    public void onPanelShown() {
        System.out.println("Dashboard shown - refresh data");
    }

    @Override
    public void onPanelHidden() {
        System.out.println("Dashboard hidden");
    }
}