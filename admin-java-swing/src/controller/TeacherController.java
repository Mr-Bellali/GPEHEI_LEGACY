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
        view.addStatusFilterListener(e -> loadTeachers());
        view.addImportListener(e -> importCsv());
        view.addExportListener(e -> exportCsv());
    }

    private void loadTeachers() {
        try {
            String keyword = view.getSearchKeyword();
            String status = view.getSelectedStatus();
            List<Teacher> teachers;

            if (keyword.isEmpty()) {
                teachers = teacherService.getTeachersByStatus(status);
            } else {
                teachers = teacherService.searchTeachers(keyword);
                if (!"ALL".equals(status)) {
                    teachers.removeIf(t -> !t.getStatus().name().equals(status));
                }
            }

            view.displayTeachers(teachers);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading teachers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Teachers to CSV");
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new java.io.File(file.getAbsolutePath() + ".csv");
            }
            try {
                utils.CsvUtil.exportToCsv(view.getTableModel(), file);
                JOptionPane.showMessageDialog(view, "Export successful!");
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(view, "Error exporting: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Teachers from CSV");
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String[]> data = utils.CsvUtil.importFromCsv(fileChooser.getSelectedFile());
                int count = 0;
                for (String[] row : data) {
                    if (row.length >= 4) {
                        Teacher t = new Teacher();
                        t.setFirstName(row[1].trim());
                        t.setLastName(row[2].trim());
                        t.setEmail(row[3].trim());
                        t.setPassword("Teacher123");
                        t.setStatus(TeacherStatus.ACTIVE);
                        try {
                            teacherService.createTeacher(t);
                            count++;
                        } catch (Exception ex) {}
                    }
                }
                JOptionPane.showMessageDialog(view, "Imported " + count + " teachers.");
                loadTeachers();
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(view, "Error importing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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
