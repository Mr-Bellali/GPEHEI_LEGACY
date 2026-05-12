package service;

import dao.impl.LibraryDAOImpl;
import dao.interfaces.LibraryDAO;
import exception.DatabaseException;
import model.Library;
import java.util.List;

public class LibraryService {
    private final LibraryDAO libraryDAO;

    public LibraryService() {
        this.libraryDAO = new LibraryDAOImpl();
    }

    public List<Library> getAllLibraries() throws DatabaseException {
        return libraryDAO.findAll();
    }

    public void saveLibrary(Library library) throws DatabaseException {
        libraryDAO.save(library);
    }

    public void updateLibrary(Library library) throws DatabaseException {
        libraryDAO.update(library);
    }

    public void deleteLibrary(int id) throws DatabaseException {
        libraryDAO.delete(id);
    }
}
