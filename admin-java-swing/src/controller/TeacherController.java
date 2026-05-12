package controller;

import javax.swing.*;
import model.Teacher;
import model.TeacherStatus;
import service.TeacherService;
import view.teacher.TeacherFormDialog;
import view.teacher.TeachersPanel;
import view.teacher.TeacherAssignmentDialog;
import view.teacher.TeacherSupervisionDialog;
import exception.DatabaseException;
import exception.ValidationException;
import java.util.List;

public class TeacherController {

    private final TeachersPanel view;
    private final TeacherService teacherService;

    public TeacherController(TeachersPanel view) {
        this.view = view;
        this.teacherService = new TeacherService();
        initListeners();
        loadTeachers();
    }

    private void initListeners() {
        view.addAddListener(e -> addTeacher());
        view.addEditListener(e -> editTeacher());
        view.addAssignmentsListener(e -> manageAssignments());
        view.addSupervisionListener(e -> manageSupervision());
        view.addDeactivateListener(e -> deactivateTeacher());
        view.addReactivateListener(e -> reactivateTeacher());
        view.addRefreshListener(e -> loadTeachers());
        view.addSearchListener(e -> loadTeachers());
    }

    private void loadTeachers() {
        try {
            String keyword = view.getSearchKeyword();
            List<Teacher> teachers;

            if (keyword.isEmpty()) {
                teachers = teacherService.getAllTeachers();
            } else {
                teachers = teacherService.searchTeachers(keyword);
            }

            view.displayTeachers(teachers);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading teachers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTeacher() {
        TeacherFormDialog dialog = new TeacherFormDialog(null, "Add New Teacher");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                Teacher teacher = dialog.getTeacher();
                int id = teacherService.createTeacher(teacher);
                if (id > 0) {
                    JOptionPane.showMessageDialog(view, "Teacher added successfully!");
                    loadTeachers();
                }
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(view, e.getMessage(), "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error saving teacher: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editTeacher() {
        int id = view.getSelectedTeacherId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a teacher to edit!");
            return;
        }

        try {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher != null) {
                TeacherFormDialog dialog = new TeacherFormDialog(null, "Edit Teacher", teacher);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Teacher updatedTeacher = dialog.getTeacher();
                    updatedTeacher.setId(id);

                    if (teacherService.updateTeacher(updatedTeacher)) {
                        JOptionPane.showMessageDialog(view, "Teacher updated successfully!");
                        loadTeachers();
                    }
                }
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error updating teacher: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manageAssignments() {
        int id = view.getSelectedTeacherId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a teacher!");
            return;
        }

        try {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher != null) {
                String name = teacher.getFirstName() + " " + teacher.getLastName();
                TeacherAssignmentDialog dialog = new TeacherAssignmentDialog(null, id, name);
                dialog.setVisible(true);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void manageSupervision() {
        int id = view.getSelectedTeacherId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a teacher!");
            return;
        }

        try {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher != null) {
                String name = teacher.getFirstName() + " " + teacher.getLastName();
                TeacherSupervisionDialog dialog = new TeacherSupervisionDialog(null, id, name);
                dialog.setVisible(true);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void deactivateTeacher() {
        int id = view.getSelectedTeacherId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a teacher to deactivate!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Deactivate this teacher? They will be marked as DISABLED.",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (teacherService.deactivateTeacher(id)) {
                    JOptionPane.showMessageDialog(view, "Teacher deactivated!");
                    loadTeachers();
                }
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reactivateTeacher() {
        int id = view.getSelectedTeacherId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a teacher to reactivate!");
            return;
        }

        try {
            if (teacherService.reactivateTeacher(id)) {
                JOptionPane.showMessageDialog(view, "Teacher reactivated!");
                loadTeachers();
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
