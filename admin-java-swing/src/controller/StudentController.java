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
    }

    private void loadStudents() {
        try {
            String keyword = view.getSearchKeyword();
            List<Student> students;

            if (keyword.isEmpty()) {
                students = studentService.getAllStudents();
            } else {
                students = studentService.searchStudents(keyword);
            }

            view.displayStudents(students);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(view, "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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