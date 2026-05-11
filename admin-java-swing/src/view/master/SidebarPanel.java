package view.master;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {

    private List<JButton> menuButtons;
    private JButton activeButton;

    public SidebarPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(52, 73, 94));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        menuButtons = new ArrayList<>();

        // Menu items with icons
        String[][] menuItems = {
                {"📊", "Dashboard"},
                {"👨‍🎓", "Students"},
                {"📁", "Projects"},
                {"👩‍🏫", "Teachers"},
                {"📈", "Reports"},
                {"💬", "Chat"},
                {"📚", "Library"},
                {"📝", "Homework"},
                {"👥", "Groups"},
                {"📦", "Modules"},
                {"⚙️", "Settings"}
        };

        // Add spacer at top
        add(Box.createVerticalStrut(20));

        // Create menu buttons
        for (String[] item : menuItems) {
            JButton menuButton = createMenuButton(item[0] + "  " + item[1]);
            menuButtons.add(menuButton);
            add(menuButton);
            add(Box.createVerticalStrut(2));
        }

        // Add glue to push everything to top
        add(Box.createVerticalGlue());

        // Select Dashboard by default
        if (!menuButtons.isEmpty()) {
            setActiveButton(menuButtons.get(0));
        }
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(44, 62, 80));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });

        return button;
    }

    public void setActiveButton(JButton button) {
        // Reset previous active button
        if (activeButton != null) {
            activeButton.setBackground(new Color(52, 73, 94));
            activeButton.setForeground(Color.WHITE);
        }

        // Set new active button
        activeButton = button;
        activeButton.setBackground(new Color(41, 128, 185));
        activeButton.setForeground(Color.WHITE);
    }

    public List<JButton> getMenuButtons() {
        return menuButtons;
    }

    public void addMenuActionListener(int index, java.awt.event.ActionListener listener) {
        if (index >= 0 && index < menuButtons.size()) {
            menuButtons.get(index).addActionListener(listener);
        }
    }
}