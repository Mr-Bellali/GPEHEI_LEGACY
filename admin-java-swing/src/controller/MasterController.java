package controller;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import view.master.*;
import view.dashboard.DashboardPanel;
import view.student.StudentsPanel;
import view.project.ProjectsPanel;
import service.DashboardService;

public class MasterController {

    private final MasterFrame masterFrame;
    private final Map<String, MasterPanel> panels;
    private MasterPanel currentPanel;
    private DashboardService dashboardService;

    public MasterController(MasterFrame masterFrame) {
        this.masterFrame = masterFrame;
        this.panels = new HashMap<>();
        this.dashboardService = new DashboardService();

        initializeMenuListeners();
        loadAllPanels();
        showDefaultPanel();
    }

    private void initializeMenuListeners() {
        SidebarPanel sidebar = masterFrame.getSidebarPanel();

        sidebar.addMenuActionListener(0, e -> switchToPanel("DASHBOARD"));
        sidebar.addMenuActionListener(1, e -> switchToPanel("STUDENTS"));
        sidebar.addMenuActionListener(2, e -> switchToPanel("PROJECTS"));
        sidebar.addMenuActionListener(3, e -> switchToPanel("TEACHERS"));
        sidebar.addMenuActionListener(4, e -> switchToPanel("REPORTS"));
        sidebar.addMenuActionListener(5, e -> switchToPanel("CHAT"));
        sidebar.addMenuActionListener(6, e -> switchToPanel("LIBRARY"));
        sidebar.addMenuActionListener(7, e -> switchToPanel("HOMEWORK"));
        sidebar.addMenuActionListener(8, e -> switchToPanel("GROUPS"));
        sidebar.addMenuActionListener(9, e -> switchToPanel("MODULES"));
        sidebar.addMenuActionListener(10, e -> switchToPanel("SETTINGS"));
    }

    private void loadAllPanels() {
        JPanel contentPanel = masterFrame.getContentPanel();

        // Dashboard panel
        DashboardPanel dashboardPanel = new DashboardPanel();
        contentPanel.add(dashboardPanel, "DASHBOARD");
        panels.put("DASHBOARD", dashboardPanel);

        // Students panel
        StudentsPanel studentsPanel = new StudentsPanel();
        StudentController studentController = new StudentController(studentsPanel); // Initialize controller
        contentPanel.add(studentsPanel, "STUDENTS");
        panels.put("STUDENTS", studentsPanel);

        // Projects panel
        ProjectsPanel projectsPanel = new ProjectsPanel();
        contentPanel.add(projectsPanel, "PROJECTS");
        panels.put("PROJECTS", projectsPanel);

        // Placeholder panels
        String[] futurePanels = {"TEACHERS", "REPORTS", "CHAT", "LIBRARY",
                "HOMEWORK", "GROUPS", "MODULES", "SETTINGS"};
        for (String panelName : futurePanels) {
            JPanel placeholderPanel = createPlaceholderPanel(panelName);
            contentPanel.add(placeholderPanel, panelName);
        }
    }

    private JPanel createPlaceholderPanel(String name) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(name + " - Coming Soon");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }

    private void showDefaultPanel() {
        switchToPanel("DASHBOARD");
        loadDashboardData(); // Load real data immediately
    }

    // Load real dashboard data from database
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Map<String, Integer> stats;

            @Override
            protected Void doInBackground() throws Exception {
                stats = dashboardService.getDashboardStats();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    DashboardPanel dashboard = (DashboardPanel) panels.get("DASHBOARD");
                    if (dashboard != null && stats != null) {
                        dashboard.updateStats(
                                stats.getOrDefault("totalStudents", 0),
                                stats.getOrDefault("activeProjects", 0),
                                stats.getOrDefault("pendingProjects", 0),
                                stats.getOrDefault("unreadAlerts", 0)
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Error loading dashboard: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void switchToPanel(String panelName) {
        MasterPanel panel = panels.get(panelName);

        if (panel != null) {
            if (currentPanel != null) {
                currentPanel.onPanelHidden();
            }

            CardLayout cardLayout = masterFrame.getCardLayout();
            JPanel contentPanel = masterFrame.getContentPanel();
            cardLayout.show(contentPanel, panelName);

            masterFrame.getFooterPanel().setStatus("Showing: " + panelName);

            // Load data if switching to dashboard
            if (panelName.equals("DASHBOARD")) {
                loadDashboardData();
            } else {
                panel.refreshData();
                panel.onPanelShown();
            }

            currentPanel = panel;
            highlightMenuButton(panelName);

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    private void highlightMenuButton(String panelName) {
        SidebarPanel sidebar = masterFrame.getSidebarPanel();
        int buttonIndex = getMenuIndexForPanel(panelName);

        if (buttonIndex >= 0 && buttonIndex < sidebar.getMenuButtons().size()) {
            sidebar.setActiveButton(sidebar.getMenuButtons().get(buttonIndex));
        }
    }

    private int getMenuIndexForPanel(String panelName) {
        switch (panelName) {
            case "DASHBOARD": return 0;
            case "STUDENTS": return 1;
            case "PROJECTS": return 2;
            case "TEACHERS": return 3;
            case "REPORTS": return 4;
            case "CHAT": return 5;
            case "LIBRARY": return 6;
            case "HOMEWORK": return 7;
            case "GROUPS": return 8;
            case "MODULES": return 9;
            case "SETTINGS": return 10;
            default: return -1;
        }
    }
}