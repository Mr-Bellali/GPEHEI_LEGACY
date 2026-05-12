package dao.interfaces;

import model.Filiere;
import exception.DatabaseException;
import java.util.List;

public interface FiliereDAO {
    int insert(Filiere filiere) throws DatabaseException;
    Filiere findById(int id) throws DatabaseException;
    List<Filiere> findAll() throws DatabaseException;
    boolean update(Filiere filiere) throws DatabaseException;
    boolean delete(int id) throws DatabaseException;
}
