package dao.interfaces;

import exception.DatabaseException;
import java.util.List;

public interface ModuleDAO {
    int insert(model.Module module) throws DatabaseException;
    model.Module findById(int id) throws DatabaseException;
    List<model.Module> findAll() throws DatabaseException;
    List<model.Module> findByFiliere(int filiereId) throws DatabaseException;
    List<model.Module> findByParent(int parentId) throws DatabaseException;
    boolean update(model.Module module) throws DatabaseException;
    boolean delete(int id) throws DatabaseException;
}
