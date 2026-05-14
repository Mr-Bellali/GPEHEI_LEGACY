package controller;

import javax.swing.*;
import model.Admin;
import service.AdminService;
import view.admin.AdminFormDialog;
import view.admin.AdminPanel;
import java.util.List;
import java.util.stream.Collectors;

public class AdminController {

    private final AdminPanel view;
    private final AdminService adminService;

    public AdminController(AdminPanel view) {
        this.view = view;
        this.adminService = new AdminService();
        initListeners();
        loadAdmins();
    }

    private void initListeners() {
        view.addAddListener(e -> addAdmin());
        view.addEditListener(e -> editAdmin());
        view.addDeleteListener(e -> deleteAdmin());
        view.addRefreshListener(e -> loadAdmins());
        view.addSearchListener(e -> loadAdmins());
    }

    private void loadAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        String keyword = view.getSearchKeyword().toLowerCase();
        
        if (!keyword.isEmpty()) {
            admins = admins.stream()
                    .filter(a -> a.getFirstName().toLowerCase().contains(keyword) ||
                                 a.getLastName().toLowerCase().contains(keyword) ||
                                 a.getEmail().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }
        
        view.displayAdmins(admins);
    }

    private void addAdmin() {
        AdminFormDialog dialog = new AdminFormDialog(null, "Add New Admin");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Admin admin = dialog.getAdmin();
            if (adminService.createAdmin(admin)) {
                JOptionPane.showMessageDialog(view, "Admin added successfully!");
                loadAdmins();
            } else {
                JOptionPane.showMessageDialog(view, "Error adding admin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editAdmin() {
        int id = view.getSelectedAdminId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select an admin to edit!");
            return;
        }

        Admin admin = adminService.getAdminById(id);
        if (admin != null) {
            AdminFormDialog dialog = new AdminFormDialog(null, "Edit Admin", admin);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                Admin updatedAdmin = dialog.getAdmin();
                if (adminService.updateAdmin(updatedAdmin)) {
                    JOptionPane.showMessageDialog(view, "Admin updated successfully!");
                    loadAdmins();
                } else {
                    JOptionPane.showMessageDialog(view, "Error updating admin.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteAdmin() {
        int id = view.getSelectedAdminId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select an admin to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Are you sure you want to delete this admin? (Soft delete)",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (adminService.deleteAdmin(id)) {
                JOptionPane.showMessageDialog(view, "Admin deleted successfully!");
                loadAdmins();
            } else {
                JOptionPane.showMessageDialog(view, "Error deleting admin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}