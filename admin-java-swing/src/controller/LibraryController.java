package controller;

import model.Library;
import service.LibraryService;
import view.library.LibraryPanel;
import view.library.LibraryFormDialog;
import exception.DatabaseException;
import javax.swing.*;
import java.util.List;

public class LibraryController {
    private final LibraryPanel panel;
    private final LibraryService service;

    public LibraryController(LibraryPanel panel) {
        this.panel = panel;
        this.service = new LibraryService();
        initController();
    }

    private void initController() {
        panel.addAddListener(e -> addLibrary());
        panel.addEditListener(e -> editLibrary());
        panel.addDeleteListener(e -> deleteLibrary());
        panel.addRefreshListener(e -> refreshData());
        refreshData();
    }

    private void refreshData() {
        try {
            List<Library> libraries = service.getAllLibraries();
            Object[][] data = new Object[libraries.size()][3];
            for (int i = 0; i < libraries.size(); i++) {
                Library l = libraries.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getName();
                data[i][2] = l.getStatus();
            }
            panel.setData(data);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage());
        }
    }

    private void addLibrary() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        LibraryFormDialog dialog = new LibraryFormDialog(topFrame, "Add Library");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                service.saveLibrary(dialog.getLibrary());
                refreshData();
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage());
            }
        }
    }

    private void editLibrary() {
        Object[] rowData = panel.getSelectedRowData();
        if (rowData == null) return;
        int id = (int) rowData[0];
        try {
            List<Library> all = service.getAllLibraries();
            Library l = all.stream().filter(lib -> lib.getId() == id).findFirst().orElse(null);
            if (l != null) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
                LibraryFormDialog dialog = new LibraryFormDialog(topFrame, "Edit Library", l);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    service.updateLibrary(dialog.getLibrary());
                    refreshData();
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage());
        }
    }

    private void deleteLibrary() {
        int id = panel.getSelectedId();
        if (id == -1) return;
        int confirm = JOptionPane.showConfirmDialog(panel, "Delete this library?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteLibrary(id);
                refreshData();
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(panel, "Error: " + e.getMessage());
            }
        }
    }
}
