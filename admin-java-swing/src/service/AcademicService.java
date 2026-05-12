package service;

import dao.impl.AcademicDAOImpl;
import dao.interfaces.AcademicDAO;
import exception.DatabaseException;
import model.Filiere;
import model.Groupe;
import java.util.List;

public class AcademicService {

    private final AcademicDAO academicDAO;

    public AcademicService() {
        this.academicDAO = new AcademicDAOImpl();
    }

    public List<Filiere> getAllFilieres() throws DatabaseException {
        return academicDAO.findAllFilieres();
    }

    public List<model.Module> getModulesByFiliere(int filiereId) throws DatabaseException {
        return academicDAO.findModulesByFiliere(filiereId);
    }

    public List<Groupe> getGroupsByFiliere(int filiereId) throws DatabaseException {
        return academicDAO.findGroupsByFiliere(filiereId);
    }

    public void updateTeacherAssignments(int teacherId, List<Integer> moduleIds) throws DatabaseException {
        // Simple logic: remove all and re-add for simplicity, or diff them
        // For now, let's just use the assign method which uses INSERT IGNORE
        academicDAO.assignTeacherToModules(teacherId, moduleIds);
    }
    
    public List<model.Module> getTeacherModules(int teacherId) throws DatabaseException {
        return academicDAO.findModulesByTeacher(teacherId);
    }

    public void updateTeacherSupervision(int teacherId, List<Integer> projectIds) throws DatabaseException {
        academicDAO.updateTeacherSupervision(teacherId, projectIds);
    }
}
