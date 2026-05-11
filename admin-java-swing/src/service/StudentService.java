package service;

import dao.impl.StudentDAOImpl;
import dao.interfaces.StudentDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Student;
import utils.PasswordHasher;
import utils.Validator;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
    }

    public int createStudent(Student student) throws ValidationException, DatabaseException {
        validateStudent(student);

        if (studentDAO.existsByEmail(student.getEmail())) {
            throw new ValidationException("Email already exists: " + student.getEmail());
        }

        student.setPassword(PasswordHasher.hash(student.getPassword()));
        return studentDAO.insert(student);
    }

    public Student getStudentById(int id) throws DatabaseException {
        return studentDAO.findById(id);
    }

    public List<Student> getAllStudents() throws DatabaseException {
        return studentDAO.findAllActive();
    }

    public List<Student> searchStudents(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentDAO.searchStudents(keyword.trim());
    }

    public boolean updateStudent(Student student) throws ValidationException, DatabaseException {
        validateStudent(student);

        if (studentDAO.existsByEmailExcludingId(student.getEmail(), student.getId())) {
            throw new ValidationException("Email already exists: " + student.getEmail());
        }

        Student existing = studentDAO.findById(student.getId());
        if (existing != null && !existing.getPassword().equals(student.getPassword())) {
            student.setPassword(PasswordHasher.hash(student.getPassword()));
        }

        return studentDAO.update(student);
    }

    public boolean deactivateStudent(int id) throws DatabaseException {
        return studentDAO.deactivate(id);
    }

    public boolean reactivateStudent(int id) throws DatabaseException {
        return studentDAO.reactivate(id);
    }

    public int cleanUpInactiveStudents(int daysOld) throws DatabaseException {
        return studentDAO.cleanUpInactive(daysOld);
    }

    public int getTotalStudents() throws DatabaseException {
        return studentDAO.getTotalCount();
    }

    public int getActiveStudents() throws DatabaseException {
        return studentDAO.getActiveCount();
    }

    private void validateStudent(Student student) throws ValidationException {
        if (!Validator.isValidName(student.getFirstName())) {
            throw new ValidationException("First name must be at least 2 characters");
        }
        if (!Validator.isValidName(student.getLastName())) {
            throw new ValidationException("Last name must be at least 2 characters");
        }
        if (!Validator.isValidEmail(student.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (student.getPassword() != null && !student.getPassword().isEmpty()) {
            if (!Validator.isValidPassword(student.getPassword())) {
                throw new ValidationException("Password must be at least 6 characters with letters and numbers");
            }
        }
    }
}