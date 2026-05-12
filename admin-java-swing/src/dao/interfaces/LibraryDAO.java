package dao.interfaces;

import model.Library;
import exception.DatabaseException;
import java.util.List;

public interface LibraryDAO {
    List<Library> findAll() throws DatabaseException;
    void save(Library library) throws DatabaseException;
    void update(Library library) throws DatabaseException;
    void delete(int id) throws DatabaseException;
}
