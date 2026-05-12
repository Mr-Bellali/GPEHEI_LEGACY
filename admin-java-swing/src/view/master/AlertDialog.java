package view.master;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.Alert;
import service.AlertService;
import exception.DatabaseException;

public class AlertDialog extends JDialog {
    private final AlertService alertService;
    private JPanel listPanel;

    public AlertDialog(JFrame parent) {
        super(parent, "System Notifications", true);
        this.alertService = new AlertService();
        setSize(400, 500);
        setLocationRelativeTo(parent);
        initComponents();
        loadAlerts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadAlerts() {
        listPanel.removeAll();
        try {
            List<Alert> alerts = alertService.getAllAlerts();
            if (alerts.isEmpty()) {
                JLabel emptyLabel = new JLabel("No alerts found", SwingConstants.CENTER);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                listPanel.add(emptyLabel);
            } else {
                for (Alert a : alerts) {
                    listPanel.add(createAlertItem(a));
                }
            }
        } catch (DatabaseException e) {
            listPanel.add(new JLabel("Error loading alerts: " + e.getMessage()));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createAlertItem(Alert a) {
        JPanel item = new JPanel(new BorderLayout());
        item.setMaximumSize(new Dimension(400, 100)); // Slightly taller
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Background color based on read/unread status
        item.setBackground(a.isRead() ? new Color(245, 245, 245) : Color.WHITE);

        String icon = a.getType().equals("warning") ? "⚠️ " : "ℹ️ ";
        String readStatus = a.isRead() ? " [READ]" : " [NEW]";
        JLabel msgLabel = new JLabel("<html><b>" + icon + "</b> " + a.getMessage() + 
                                   "<br><font color='#888888' size='2'>" + a.getCreatedAt() + readStatus + "</font></html>");
        item.add(msgLabel, BorderLayout.CENTER);

        if (!a.isRead()) {
            JButton validBtn = new JButton("Valid & Affect Task");
            validBtn.setBackground(new Color(41, 128, 185));
            validBtn.setForeground(Color.WHITE);
            validBtn.setFont(new Font("Arial", Font.BOLD, 10));
            validBtn.addActionListener(e -> {
                try {
                    alertService.markAsRead(a.getId());
                    loadAlerts();
                } catch (DatabaseException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });
            item.add(validBtn, BorderLayout.EAST);
        } else {
            JLabel doneLabel = new JLabel("✔️ Validated");
            doneLabel.setForeground(new Color(39, 174, 96));
            doneLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            item.add(doneLabel, BorderLayout.EAST);
        }

        return item;
    }
}
