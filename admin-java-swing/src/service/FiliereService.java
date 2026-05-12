package service;

import dao.impl.FiliereDAOImpl;
import dao.interfaces.FiliereDAO;
import exception.DatabaseException;
import model.Filiere;
import java.util.List;

public class FiliereService {
    private final FiliereDAO filiereDAO;

    public FiliereService() {
        this.filiereDAO = new FiliereDAOImpl();
    }

    public List<Filiere> getAllFilieres() throws DatabaseException {
        return filiereDAO.findAll();
    }
    
    public Filiere getFiliereById(int id) throws DatabaseException {
        return filiereDAO.findById(id);
    }

    public int createFiliere(Filiere f) throws DatabaseException {
        return filiereDAO.insert(f);
    }

    public boolean updateFiliere(Filiere f) throws DatabaseException {
        return filiereDAO.update(f);
    }

    public boolean deleteFiliere(int id) throws DatabaseException {
        return filiereDAO.delete(id);
    }
}
