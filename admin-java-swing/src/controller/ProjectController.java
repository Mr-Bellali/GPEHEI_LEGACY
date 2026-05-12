package controller;

import model.Project;
import service.ProjectService;
import view.project.ProjectsPanel;
import view.project.ProjectFormDialog;
import exception.DatabaseException;
import javax.swing.*;
import java.util.List;

public class ProjectController {
    private final ProjectsPanel panel;
    private final ProjectService service;

    public ProjectController(ProjectsPanel panel) {
        this.panel = panel;
        this.service = new ProjectService();
        initController();
    }

    private void initController() {
        panel.addAddListener(e -> addProject());
        panel.addEditListener(e -> editProject());
        panel.addDeleteListener(e -> deleteProject());
        panel.addRefreshListener(e -> refreshData());
        refreshData();
    }

    private void refreshData() {
        try {
            List<Project> projects = service.getAllProjects();
            Object[][] data = new Object[projects.size()][7];
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                data[i][0] = p.getId();
                data[i][1] = p.getTitle();
                data[i][2] = "N/A"; // Supervisor (to be implemented)
                data[i][3] = 0;     // Students count (to be implemented)
                data[i][4] = "N/A"; // Start date
                data[i][5] = "N/A"; // End date
                data[i][6] = p.getStatus() != null ? capitalize(p.getStatus().name()) : "In_Progress";
            }
            panel.setProjects(data);
        } catch (DatabaseException e) {
            panel.showMessage("Error loading projects: " + e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void addProject() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        ProjectFormDialog dialog = new ProjectFormDialog(topFrame, "Add Project");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                service.saveProject(dialog.getProject());
                refreshData();
            } catch (DatabaseException e) {
                panel.showMessage("Error saving project: " + e.getMessage());
            }
        }
    }

    private void editProject() {
        Object[] rowData = panel.getSelectedRowData();
        if (rowData == null) {
            panel.showMessage("Please select a project to edit");
            return;
        }
        int id = (int) rowData[0];
        try {
            // Fetch the full project object (simplified here, in real app might need getById)
            List<Project> all = service.getAllProjects();
            Project p = all.stream().filter(proj -> proj.getId() == id).findFirst().orElse(null);
            
            if (p != null) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                ProjectFormDialog dialog = new ProjectFormDialog(topFrame, "Edit Project", p);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    service.updateProject(dialog.getProject());
                    refreshData();
                }
            }
        } catch (DatabaseException e) {
            panel.showMessage("Error updating project: " + e.getMessage());
        }
    }

    private void deleteProject() {
        Object[] rowData = panel.getSelectedRowData();
        if (rowData == null) {
            panel.showMessage("Please select a project to delete");
            return;
        }
        int id = (int) rowData[0];
        int confirm = JOptionPane.showConfirmDialog(panel, 
            "Are you sure you want to delete this project?", "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteProject(id);
                refreshData();
            } catch (DatabaseException e) {
                panel.showMessage("Error deleting project: " + e.getMessage());
            }
        }
    }
}
