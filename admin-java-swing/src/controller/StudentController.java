package controller;

import javax.swing.*;
import model.Student;
import model.StudentStatus;
import service.StudentService;
import view.student.StudentFormDialog;
import view.student.StudentsPanel;
import exception.DatabaseException;
import exception.ValidationException;
import java.util.List;

public class StudentController {

    private final StudentsPanel view;
    private final StudentService studentService;

    public StudentController(StudentsPanel view) {
        this.view = view;
        this.studentService = new StudentService();
        initListeners();
        loadStudents();
    }

    private void initListeners() {
        view.addAddListener(e -> addStudent());
        view.addEditListener(e -> editStudent());
        view.addDeactivateListener(e -> deactivateStudent());
        view.addReactivateListener(e -> reactivateStudent());
        view.addRefreshListener(e -> loadStudents());
        view.addSearchListener(e -> loadStudents());
        view.addStatusFilterListener(e -> loadStudents());
        view.addImportListener(e -> importCsv());
        view.addExportListener(e -> exportCsv());
    }

    private void loadStudents() {
        try {
            String keyword = view.getSearchKeyword();
            String status = view.getSelectedStatus();
            List<Student> students;

            if (keyword.isEmpty()) {
                students = studentService.getStudentsByStatus(status);
            } else {
                students = studentService.searchStudents(keyword);
                // Filter by status if not ALL
                if (!"ALL".equals(status)) {
                    students.removeIf(s -> !s.getStatus().name().equals(status));
                }
            }

            view.displayStudents(students);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Students to CSV");
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
        fileChooser.setDialogTitle("Import Students from CSV");
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            try {
                List<String[]> data = utils.CsvUtil.importFromCsv(fileChooser.getSelectedFile());
                int count = 0;
                for (String[] row : data) {
                    if (row.length >= 5) {
                        Student s = new Student();
                        s.setFirstName(row[2].trim());
                        s.setLastName(row[3].trim());
                        s.setEmail(row[4].trim());
                        s.setPassword("Student123"); // Default password
                        s.setStatus(StudentStatus.ACTIVE);
                        try {
                            studentService.createStudent(s);
                            count++;
                        } catch (Exception ex) {
                            // Skip or log error
                        }
                    }
                }
                JOptionPane.showMessageDialog(view, "Imported " + count + " students.");
                loadStudents();
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(view, "Error importing: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addStudent() {
        StudentFormDialog dialog = new StudentFormDialog(null, "Add New Student");
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                Student student = dialog.getStudent();
                int id = studentService.createStudent(student);
                if (id > 0) {
                    JOptionPane.showMessageDialog(view, "Student added successfully!");
                    loadStudents();
                }
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(view, e.getMessage(), "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error saving student: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editStudent() {
        int id = view.getSelectedStudentId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a student to edit!");
            return;
        }

        try {
            Student student = studentService.getStudentById(id);
            if (student != null) {
                StudentFormDialog dialog = new StudentFormDialog(null, "Edit Student", student);
                dialog.setVisible(true);

                if (dialog.isConfirmed()) {
                    Student updatedStudent = dialog.getStudent();
                    updatedStudent.setId(id);

                    if (studentService.updateStudent(updatedStudent)) {
                        JOptionPane.showMessageDialog(view, "Student updated successfully!");
                        loadStudents();
                    }
                }
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error updating student: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deactivateStudent() {
        int id = view.getSelectedStudentId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a student to deactivate!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Deactivate this student? They will be marked as INACTIVE.",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (studentService.deactivateStudent(id)) {
                    JOptionPane.showMessageDialog(view, "Student deactivated!");
                    loadStudents();
                }
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reactivateStudent() {
        int id = view.getSelectedStudentId();
        if (id <= 0) {
            JOptionPane.showMessageDialog(view, "Please select a student to reactivate!");
            return;
        }

        try {
            if (studentService.reactivateStudent(id)) {
                JOptionPane.showMessageDialog(view, "Student reactivated!");
                loadStudents();
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}