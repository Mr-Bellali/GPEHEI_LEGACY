package controller;

import javax.swing.*;
import model.Filiere;
import model.Module;
import model.ModuleType;
import service.FiliereService;
import service.ModuleService;
import service.AcademicService;
import view.module.ModuleFormDialog;
import view.module.ModulePanel;
import exception.DatabaseException;
import java.util.List;
import java.util.ArrayList;

public class ModuleController {
    private final ModulePanel view;
    private final ModuleService moduleService;
    private final FiliereService filiereService;
    private final AcademicService academicService; 

    public ModuleController(ModulePanel view) {
        this.view = view;
        this.moduleService = new ModuleService();
        this.filiereService = new FiliereService();
        this.academicService = new AcademicService();
        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addAddListener(e -> addModule());
        view.addEditListener(e -> editModule());
        view.addDeleteListener(e -> deleteModule());
        view.addRefreshListener(e -> loadData());
        view.addFilterListener(e -> loadData());
        view.addStatusFilterListener(e -> loadData());
        view.addImportListener(e -> importCsv());
        view.addExportListener(e -> exportCsv());
        // Listener for Filiere selection change to filter parent modules
        view.getFiliereCombo().addActionListener(e -> filterParentModules());
    }

    private void loadData() {
        try {
            String filter = view.getFilterType();
            String statusFilter = view.getFilterStatus();
            List<Module> list = moduleService.getAllModules();
            if ("Modules".equals(filter)) {
                list.removeIf(m -> m.getType() != ModuleType.MOD);
            } else if ("Elements".equals(filter)) {
                list.removeIf(m -> m.getType() != ModuleType.ELM);
            }
            if ("Active".equals(statusFilter)) {
                list.removeIf(m -> m.getStatus() != model.ModuleStatus.ACTIVE);
            } else if ("Disabled".equals(statusFilter)) {
                list.removeIf(m -> m.getStatus() != model.ModuleStatus.DISABLED);
            }
            view.displayModules(list);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading modules: " + e.getMessage());
        }
    }

    private void importCsv() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String[]> data = utils.CsvUtil.importFromCsv(fileChooser.getSelectedFile());
                for (String[] row : data) {
                    // Assuming CSV format matches: name, type(MOD/ELM), filiereId, parentModuleId, semester, status
                    Module m = new Module();
                    m.setName(row[0]);
                    m.setType(ModuleType.valueOf(row[1]));
                    m.setFiliereId(Integer.parseInt(row[2]));
                    if (!row[3].equals("-")) m.setParentModuleId(Integer.parseInt(row[3]));
                    m.setSemester(Integer.parseInt(row[4]));
                    m.setStatus(model.ModuleStatus.valueOf(row[5].toUpperCase()));
                    moduleService.createModule(m);
                }
                loadData();
                JOptionPane.showMessageDialog(view, "Modules imported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Error importing CSV: " + e.getMessage());
            }
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                utils.CsvUtil.exportToCsv(((javax.swing.JTable) view.getContentPanel().getComponent(1)).getModel(), fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(view, "Modules exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Error exporting CSV: " + e.getMessage());
            }
        }
    }

    private void addModule() {
        ModuleFormDialog dialog = new ModuleFormDialog(null, "Add Module/Element");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                moduleService.createModule(dialog.getModule());
                loadData();
                JOptionPane.showMessageDialog(view, "Module/Element added successfully!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }

    private void editModule() {
        int id = view.getSelectedModuleId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a module/element to edit!");
            return;
        }

        try {
            model.Module selected = moduleService.getModuleById(id);
            if (selected != null) {
                ModuleFormDialog dialog = new ModuleFormDialog(null, "Edit Module/Element", selected);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    moduleService.updateModule(dialog.getModule());
                    loadData();
                    JOptionPane.showMessageDialog(view, "Module/Element updated successfully!");
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void deleteModule() {
        int id = view.getSelectedModuleId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a module/element to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(view, "Delete this module/element?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                moduleService.deleteModule(id);
                loadData();
                JOptionPane.showMessageDialog(view, "Module/Element deleted!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }
    
    private void filterParentModules() {
        Filiere selectedFiliere = (Filiere) view.getFiliereCombo().getSelectedItem();
        if (selectedFiliere != null) {
            try {
                List<Module> parentModules = moduleService.getParentModulesByFiliere(selectedFiliere.getId());
                view.updateParentModuleList(parentModules); // Assume this method exists
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error loading parent modules: " + e.getMessage());
            }
        }
    }
}