package dao.interfaces;

import model.Teacher;
import exception.DatabaseException;
import java.util.List;

public interface TeacherDAO {
    int insert(Teacher teacher) throws DatabaseException;
    Teacher findById(int id) throws DatabaseException;
    List<Teacher> findAll() throws DatabaseException;
    List<Teacher> findAllActive() throws DatabaseException;
    List<Teacher> searchTeachers(String keyword) throws DatabaseException;
    int getTotalCount() throws DatabaseException;
    int getActiveCount() throws DatabaseException;
    boolean existsByEmail(String email) throws DatabaseException;
    boolean existsByEmailExcludingId(String email, int excludeId) throws DatabaseException;
    boolean update(Teacher teacher) throws DatabaseException;
    boolean deactivate(int id) throws DatabaseException;
    boolean reactivate(int id) throws DatabaseException;
}
