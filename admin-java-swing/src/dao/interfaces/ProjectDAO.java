package dao.interfaces;

import model.Project;
import exception.DatabaseException;
import java.util.List;

public interface ProjectDAO {
    List<Project> findAll() throws DatabaseException;
    List<Project> findActive() throws DatabaseException;
    void save(Project project) throws DatabaseException;
    void update(Project project) throws DatabaseException;
    void delete(int id) throws DatabaseException;
}
