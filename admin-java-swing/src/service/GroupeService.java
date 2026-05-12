package service;

import dao.impl.GroupeDAOImpl;
import dao.interfaces.GroupeDAO;
import exception.DatabaseException;
import model.Groupe;
import java.util.List;

public class GroupeService {
    private final GroupeDAO groupeDAO;

    public GroupeService() {
        this.groupeDAO = new GroupeDAOImpl();
    }

    public List<Groupe> getAllGroups() throws DatabaseException {
        return groupeDAO.findAll();
    }

    public List<Groupe> getGroupsByFiliere(int filiereId) throws DatabaseException {
        return groupeDAO.findByFiliere(filiereId);
    }

    public int createGroup(Groupe g) throws DatabaseException {
        return groupeDAO.insert(g);
    }

    public boolean updateGroup(Groupe g) throws DatabaseException {
        return groupeDAO.update(g);
    }

    public boolean deleteGroup(int id) throws DatabaseException {
        return groupeDAO.delete(id);
    }
}
