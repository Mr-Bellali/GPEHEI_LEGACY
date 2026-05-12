package dao.interfaces;

import model.Groupe;
import exception.DatabaseException;
import java.util.List;

public interface GroupeDAO {
    int insert(Groupe groupe) throws DatabaseException;
    Groupe findById(int id) throws DatabaseException;
    List<Groupe> findAll() throws DatabaseException;
    List<Groupe> findByFiliere(int filiereId) throws DatabaseException;
    boolean update(Groupe groupe) throws DatabaseException;
    boolean delete(int id) throws DatabaseException;
}
