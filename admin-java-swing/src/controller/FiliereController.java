package controller;

import javax.swing.*;
import model.Filiere;
import service.FiliereService;
import view.filiere.FiliereFormDialog;
import view.filiere.FilierePanel;
import exception.DatabaseException;
import java.util.List;

public class FiliereController {
    private final FilierePanel view;
    private final FiliereService service;

    public FiliereController(FilierePanel view) {
        this.view = view;
        this.service = new FiliereService();
        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addAddListener(e -> addFiliere());
        view.addEditListener(e -> editFiliere());
        view.addDeleteListener(e -> deleteFiliere());
        view.addRefreshListener(e -> loadData());
        view.addImportListener(e -> importCsv());
        view.addExportListener(e -> exportCsv());
    }

    private void importCsv() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(view.getContentPanel()) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String[]> data = utils.CsvUtil.importFromCsv(fileChooser.getSelectedFile());
                for (String[] row : data) {
                    Filiere f = new Filiere();
                    f.setName(row[0]);
                    f.setShortName(row[1]);
                    service.createFiliere(f);
                }
                loadData();
                JOptionPane.showMessageDialog(view.getContentPanel(), "Filieres imported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getContentPanel(), "Error importing CSV: " + e.getMessage());
            }
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(view.getContentPanel()) == JFileChooser.APPROVE_OPTION) {
            try {
                utils.CsvUtil.exportToCsv(((javax.swing.JTable) view.getContentPanel().getComponent(1)).getModel(), fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(view.getContentPanel(), "Filieres exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view.getContentPanel(), "Error exporting CSV: " + e.getMessage());
            }
        }
    }

    private void loadData() {
        try {
            view.displayFilieres(service.getAllFilieres());
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void addFiliere() {
        FiliereFormDialog dialog = new FiliereFormDialog(null, "Add Filiere");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                service.createFiliere(dialog.getFiliere());
                loadData();
                JOptionPane.showMessageDialog(view, "Filiere added successfully!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }

    private void editFiliere() {
        int id = view.getSelectedFiliereId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a filiere to edit!");
            return;
        }

        try {
            List<Filiere> filieres = service.getAllFilieres();
            Filiere selected = filieres.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
            
            if (selected != null) {
                FiliereFormDialog dialog = new FiliereFormDialog(null, "Edit Filiere", selected);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    service.updateFiliere(dialog.getFiliere());
                    loadData();
                    JOptionPane.showMessageDialog(view, "Filiere updated successfully!");
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void deleteFiliere() {
        int id = view.getSelectedFiliereId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a filiere to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(view, "Delete this filiere?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteFiliere(id);
                loadData();
                JOptionPane.showMessageDialog(view, "Filiere deleted!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }
}
