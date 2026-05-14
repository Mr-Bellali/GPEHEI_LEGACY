package service;

import dao.impl.TeacherDAOImpl;
import dao.interfaces.TeacherDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Teacher;
import utils.PasswordHasher;
import utils.Validator;

import java.util.List;

public class TeacherService {

    private final TeacherDAO teacherDAO;

    public TeacherService() {
        this.teacherDAO = new TeacherDAOImpl();
    }

    public int createTeacher(Teacher teacher) throws ValidationException, DatabaseException {
        validateTeacher(teacher);

        if (teacherDAO.existsByEmail(teacher.getEmail())) {
            throw new ValidationException("Email already exists: " + teacher.getEmail());
        }

        teacher.setPassword(PasswordHasher.hash(teacher.getPassword()));
        return teacherDAO.insert(teacher);
    }

    public Teacher getTeacherById(int id) throws DatabaseException {
        return teacherDAO.findById(id);
    }

    public List<Teacher> getAllTeachers() throws DatabaseException {
        return getTeachersByStatus("ALL");
    }

    public List<Teacher> getTeachersByStatus(String status) throws DatabaseException {
        return teacherDAO.findByStatus(status);
    }

    public List<Teacher> searchTeachers(String keyword) throws DatabaseException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTeachers();
        }
        return teacherDAO.searchTeachers(keyword.trim());
    }

    public boolean updateTeacher(Teacher teacher) throws ValidationException, DatabaseException {
        validateTeacher(teacher);

        if (teacherDAO.existsByEmailExcludingId(teacher.getEmail(), teacher.getId())) {
            throw new ValidationException("Email already exists: " + teacher.getEmail());
        }

        Teacher existing = teacherDAO.findById(teacher.getId());
        if (existing != null && !existing.getPassword().equals(teacher.getPassword())) {
            teacher.setPassword(PasswordHasher.hash(teacher.getPassword()));
        }

        return teacherDAO.update(teacher);
    }

    public boolean deactivateTeacher(int id) throws DatabaseException {
        return teacherDAO.deactivate(id);
    }

    public boolean reactivateTeacher(int id) throws DatabaseException {
        return teacherDAO.reactivate(id);
    }

    public int getTotalTeachers() throws DatabaseException {
        return teacherDAO.getTotalCount();
    }

    public int getActiveTeachers() throws DatabaseException {
        return teacherDAO.getActiveCount();
    }

    private void validateTeacher(Teacher teacher) throws ValidationException {
        if (!Validator.isValidName(teacher.getFirstName())) {
            throw new ValidationException("First name must be at least 2 characters");
        }
        if (!Validator.isValidName(teacher.getLastName())) {
            throw new ValidationException("Last name must be at least 2 characters");
        }
        if (!Validator.isValidEmail(teacher.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        if (teacher.getPassword() != null && !teacher.getPassword().isEmpty()) {
            // Check if it's already hashed (64 chars for SHA-256) or if it needs validation
            if (teacher.getPassword().length() != 64) {
                if (!Validator.isValidPassword(teacher.getPassword())) {
                    throw new ValidationException("Password must be at least 6 characters with letters and numbers");
                }
            }
        }
    }
}
