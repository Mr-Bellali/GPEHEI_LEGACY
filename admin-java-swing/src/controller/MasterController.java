package controller;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import view.master.*;
import view.dashboard.DashboardPanel;
import view.student.StudentsPanel;
import view.teacher.TeachersPanel;
import view.filiere.FilierePanel;
import view.module.ModulePanel;
import view.groupe.GroupePanel;
import view.settings.SettingsPanel;
import view.project.ProjectsPanel;
import view.library.LibraryPanel;
import view.reports.ReportsPanel;
import view.master.AlertDialog;
import service.DashboardService;
import service.AlertService;

public class MasterController {

    private final MasterFrame masterFrame;
    private final Map<String, MasterPanel> panels;
    private MasterPanel currentPanel;
    private DashboardService dashboardService;
    private AlertService alertService;

    public MasterController(MasterFrame masterFrame) {
        this.masterFrame = masterFrame;
        this.panels = new HashMap<>();
        this.dashboardService = new DashboardService();
        this.alertService = new AlertService();

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
        sidebar.addMenuActionListener(4, e -> switchToPanel("FILIERES"));
        sidebar.addMenuActionListener(5, e -> switchToPanel("MODULES"));
        sidebar.addMenuActionListener(6, e -> switchToPanel("REPORTS"));
        sidebar.addMenuActionListener(7, e -> switchToPanel("LIBRARY"));
        sidebar.addMenuActionListener(8, e -> switchToPanel("HOMEWORK"));
        sidebar.addMenuActionListener(9, e -> switchToPanel("GROUPS"));
        sidebar.addMenuActionListener(10, e -> switchToPanel("SETTINGS"));

        // Header notifications
        masterFrame.getHeaderPanel().getNotificationsButton().addActionListener(e -> {
            AlertDialog dialog = new AlertDialog(masterFrame);
            dialog.setVisible(true);
            loadDashboardData(); // Refresh unread count on dashboard
        });
    }

    private void loadAllPanels() {
        JPanel contentPanel = masterFrame.getContentPanel();

        // Dashboard panel
        DashboardPanel dashboardPanel = new DashboardPanel();
        contentPanel.add(dashboardPanel, "DASHBOARD");
        panels.put("DASHBOARD", dashboardPanel);

        // Students panel
        StudentsPanel studentsPanel = new StudentsPanel();
        new StudentController(studentsPanel);
        contentPanel.add(studentsPanel, "STUDENTS");
        panels.put("STUDENTS", studentsPanel);

        // Teachers panel
        TeachersPanel teachersPanel = new TeachersPanel();
        new TeacherController(teachersPanel);
        contentPanel.add(teachersPanel, "TEACHERS");
        panels.put("TEACHERS", teachersPanel);

        // Filieres panel
        FilierePanel filierePanel = new FilierePanel();
        new FiliereController(filierePanel);
        contentPanel.add(filierePanel, "FILIERES");
        panels.put("FILIERES", filierePanel);

        // Modules panel
        ModulePanel modulePanel = new ModulePanel();
        new ModuleController(modulePanel);
        contentPanel.add(modulePanel, "MODULES");
        panels.put("MODULES", modulePanel);
        
        // Groups panel
        GroupePanel groupPanel = new GroupePanel();
        new GroupeController(groupPanel);
        contentPanel.add(groupPanel, "GROUPS");
        panels.put("GROUPS", groupPanel);

        // Settings panel
        SettingsPanel settingsPanel = new SettingsPanel();
        contentPanel.add(settingsPanel, "SETTINGS");
        panels.put("SETTINGS", settingsPanel);

        // Projects panel
        ProjectsPanel projectsPanel = new ProjectsPanel();
        new ProjectController(projectsPanel);
        contentPanel.add(projectsPanel, "PROJECTS");
        panels.put("PROJECTS", projectsPanel);

        // Library panel
        LibraryPanel libraryPanel = new LibraryPanel();
        new LibraryController(libraryPanel);
        contentPanel.add(libraryPanel, "LIBRARY");
        panels.put("LIBRARY", libraryPanel);

        // Reports panel
        ReportsPanel reportsPanel = new ReportsPanel();
        contentPanel.add(reportsPanel, "REPORTS");
        panels.put("REPORTS", reportsPanel);

        // Placeholder panels
        String[] futurePanels = {"HOMEWORK"};
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
        loadDashboardData();
    }

    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Map<String, Integer> stats;
            @Override protected Void doInBackground() throws Exception {
                stats = dashboardService.getDashboardStats();
                return null;
            }
            @Override protected void done() {
                try {
                    get();
                    DashboardPanel dashboard = (DashboardPanel) panels.get("DASHBOARD");
                    int unread = stats.getOrDefault("unreadAlerts", 0);
                    if (dashboard != null && stats != null) {
                        dashboard.updateStats(
                                stats.getOrDefault("totalStudents", 0),
                                stats.getOrDefault("activeProjects", 0),
                                stats.getOrDefault("pendingProjects", 0),
                                unread
                        );
                    }
                    masterFrame.getHeaderPanel().setNotificationCount(unread);
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    public void switchToPanel(String panelName) {
        MasterPanel panel = panels.get(panelName);
        if (panel != null) {
            if (currentPanel != null) currentPanel.onPanelHidden();
            CardLayout cardLayout = masterFrame.getCardLayout();
            JPanel contentPanel = masterFrame.getContentPanel();
            cardLayout.show(contentPanel, panelName);
            masterFrame.getFooterPanel().setStatus("Showing: " + panelName);
            if (panelName.equals("DASHBOARD")) loadDashboardData();
            else {
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
            case "FILIERES": return 4;
            case "MODULES": return 5;
            case "REPORTS": return 6;
            case "LIBRARY": return 7;
            case "HOMEWORK": return 8;
            case "GROUPS": return 9;
            case "SETTINGS": return 10;
            default: return -1;
        }
    }
}
