package dao.interfaces;

import model.Filiere;
import model.Groupe;
import exception.DatabaseException;
import java.util.List;

public interface AcademicDAO {
    List<Filiere> findAllFilieres() throws DatabaseException;
    List<model.Module> findModulesByFiliere(int filiereId) throws DatabaseException;
    List<Groupe> findGroupsByFiliere(int filiereId) throws DatabaseException;
    
    // Teacher-Module assignments
    void assignTeacherToModules(int teacherId, List<Integer> moduleIds) throws DatabaseException;
    List<model.Module> findModulesByTeacher(int teacherId) throws DatabaseException;
    void removeTeacherFromModules(int teacherId, List<Integer> moduleIds) throws DatabaseException;
    
    // Teacher-Project Supervision
    void assignTeacherToProjects(int teacherId, List<Integer> projectIds) throws DatabaseException;
    void removeTeacherFromProjects(int teacherId, List<Integer> projectIds) throws DatabaseException;
    void updateTeacherSupervision(int teacherId, List<Integer> projectIds) throws DatabaseException;
}
