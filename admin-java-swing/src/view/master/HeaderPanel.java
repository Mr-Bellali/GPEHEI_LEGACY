package view.master;

import javax.swing.*;
import java.awt.*;
import utils.SessionManager;
import utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class HeaderPanel extends JPanel {

    private JLabel welcomeLabel;
    private JLabel roleLabel;
    private JButton logoutButton;
    private JButton notificationsButton;

    public HeaderPanel(Runnable logoutCallback) {
        setLayout(new BorderLayout());
        setBackground(new Color(41, 128, 185));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setPreferredSize(new Dimension(0, 60));

        // Left side - Logo and welcome
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("GPEHEI");
        logoLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.WHITE);

        leftPanel.add(logoLabel);
        leftPanel.add(welcomeLabel);

        // Right side - Notifications and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        roleLabel = new JLabel();
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        roleLabel.setForeground(new Color(255, 255, 255, 200));

        notificationsButton = new JButton("🔔");
        notificationsButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        notificationsButton.setToolTipText("System Notifications");
        notificationsButton.setContentAreaFilled(false);
        notificationsButton.setBorderPainted(false);
        notificationsButton.setForeground(Color.WHITE);
        notificationsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.clearSession();
                logoutCallback.run();
            }
        });

        rightPanel.add(roleLabel);
        rightPanel.add(notificationsButton);
        rightPanel.add(logoutButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Load user info from JWT
        loadUserInfo();
    }

    public void setNotificationCount(int count) {
        if (count > 0) {
            notificationsButton.setText("🔔 (" + count + ")");
            notificationsButton.setForeground(new Color(255, 255, 100)); // Slight yellow for attention
        } else {
            notificationsButton.setText("🔔");
            notificationsButton.setForeground(Color.WHITE);
        }
        notificationsButton.revalidate();
        notificationsButton.repaint();
    }

    private void loadUserInfo() {
        try {
            String token = SessionManager.getToken();
            if (token != null) {
                Claims claims = JwtUtil.extractClaims(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                String name=claims.get("name",String.class);

                welcomeLabel.setText("Welcome, " + name);
                roleLabel.setText("Role: " + role);
            }
        } catch (Exception e) {
            welcomeLabel.setText("Welcome, User");
            roleLabel.setText("Role: Unknown");
        }
    }

    public JButton getNotificationsButton() {
        return notificationsButton;
    }
}