package controller;

import javax.swing.*;
import model.Filiere;
import model.Groupe;
import service.FiliereService;
import service.GroupeService;
import view.groupe.GroupeFormDialog;
import view.groupe.GroupePanel;
import exception.DatabaseException;
import java.util.List;

public class GroupeController {
    private final GroupePanel view;
    private final GroupeService service;
    private final FiliereService filiereService; // Added for fetching Filiere names

    public GroupeController(GroupePanel view) {
        this.view = view;
        this.service = new GroupeService();
        this.filiereService = new FiliereService();
        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addAddListener(e -> addGroup());
        view.addEditListener(e -> editGroup());
        view.addDeleteListener(e -> deleteGroup());
        view.addRefreshListener(e -> loadData());
    }

    private void loadData() {
        try {
            List<Groupe> groups = service.getAllGroups();
            // We need to fetch Filiere names to display them properly in the view
            view.displayGroups(groups); // This method will need to be updated to display Filiere names
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void addGroup() {
        GroupeFormDialog dialog = new GroupeFormDialog(null, "Add Group");
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                service.createGroup(dialog.getGroupe());
                loadData();
                JOptionPane.showMessageDialog(view, "Group added successfully!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }

    private void editGroup() {
        int id = view.getSelectedGroupId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a group to edit!");
            return;
        }

        try {
            List<Groupe> groups = service.getAllGroups();
            Groupe selected = groups.stream().filter(g -> g.getId() == id).findFirst().orElse(null);
            
            if (selected != null) {
                GroupeFormDialog dialog = new GroupeFormDialog(null, "Edit Group", selected);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    service.updateGroup(dialog.getGroupe());
                    loadData();
                    JOptionPane.showMessageDialog(view, "Group updated successfully!");
                }
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void deleteGroup() {
        int id = view.getSelectedGroupId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a group to delete!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(view, "Delete this group?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteGroup(id);
                loadData();
                JOptionPane.showMessageDialog(view, "Group deleted!");
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
            }
        }
    }
}
