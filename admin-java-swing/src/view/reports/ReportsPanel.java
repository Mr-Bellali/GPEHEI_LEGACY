package view.reports;

import javax.swing.*;
import java.awt.*;
import view.master.MasterPanel;

public class ReportsPanel extends JPanel implements MasterPanel {

    public ReportsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(244, 244, 248));
        initComponents();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(41, 128, 185));
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JLabel title = new JLabel("System Reports");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(2, 2, 20, 20));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        main.add(createReportCard("Student Statistics", "Total enrolled: 0\nActive: 0\nInactive: 0"));
        main.add(createReportCard("Project Summary", "Total: 0\nIn Progress: 0\nCompleted: 0"));
        main.add(createReportCard("Teacher Workload", "Total teachers: 0\nAvg projects/teacher: 0"));
        main.add(createReportCard("Academic Performance", "Avg grades: N/A\nModules completed: 0"));

        add(new JScrollPane(main), BorderLayout.CENTER);
    }

    private JPanel createReportCard(String title, String summary) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 18));
        t.setForeground(new Color(61, 52, 139));
        card.add(t, BorderLayout.NORTH);

        JTextArea s = new JTextArea(summary);
        s.setEditable(false);
        s.setFont(new Font("Arial", Font.PLAIN, 14));
        s.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(s, BorderLayout.CENTER);

        JButton viewBtn = new JButton("View Detailed Report");
        card.add(viewBtn, BorderLayout.SOUTH);

        return card;
    }

    @Override public String getPanelName() { return "Reports"; }
    @Override public void refreshData() {
        // Here we could fetch real stats
    }
    @Override public JPanel getContentPanel() { return this; }
    @Override public void onPanelShown() { refreshData(); }
    @Override public void onPanelHidden() {}
}
