package dao.interfaces;

import model.Student;
import exception.DatabaseException;
import java.util.List;

public interface StudentDAO {
    int insert(Student student) throws DatabaseException;
    Student findById(int id) throws DatabaseException;
    List<Student> findAll() throws DatabaseException;
    List<Student> findAllActive() throws DatabaseException;
    List<Student> findByStatus(String status) throws DatabaseException;
    List<Student> searchStudents(String keyword) throws DatabaseException;
    int getTotalCount() throws DatabaseException;
    int getActiveCount() throws DatabaseException;
    boolean existsByEmail(String email) throws DatabaseException;
    boolean existsByEmailExcludingId(String email, int excludeId) throws DatabaseException;
    boolean update(Student student) throws DatabaseException;
    boolean deactivate(int id) throws DatabaseException;
    boolean reactivate(int id) throws DatabaseException;
    int cleanUpInactive(int daysOld) throws DatabaseException;
}