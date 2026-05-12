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
        // Listener for Filiere selection change to filter parent modules
        view.getFiliereCombo().addActionListener(e -> filterParentModules());
    }

    private void loadData() {
        try {
            String filter = view.getFilterType();
            List<Module> list = moduleService.getAllModules();
            if ("Modules".equals(filter)) {
                list.removeIf(m -> m.getType() != ModuleType.MOD);
            } else if ("Elements".equals(filter)) {
                list.removeIf(m -> m.getType() != ModuleType.ELM);
            }
            view.displayModules(list);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading modules: " + e.getMessage());
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