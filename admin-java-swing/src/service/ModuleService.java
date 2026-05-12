package service;

import dao.impl.ModuleDAOImpl;
import dao.interfaces.ModuleDAO;
import exception.DatabaseException;
import model.Module;
import model.ModuleStatus;
import model.ModuleType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleService {
    private final ModuleDAO moduleDAO;

    public ModuleService() {
        this.moduleDAO = new ModuleDAOImpl();
    }

    public List<Module> getAllModules() throws DatabaseException {
        return moduleDAO.findAll();
    }

    public List<Module> getModulesByFiliere(int filiereId) throws DatabaseException {
        return moduleDAO.findByFiliere(filiereId);
    }

    public List<Module> getParentModulesByFiliere(int filiereId) throws DatabaseException {
        List<Module> allModulesInFiliere = moduleDAO.findByFiliere(filiereId);
        return allModulesInFiliere.stream()
                .filter(m -> m.getType() == ModuleType.MOD) // Filter for Modules only
                .collect(Collectors.toList());
    }
    
    public Module getModuleById(int id) throws DatabaseException {
        return moduleDAO.findById(id);
    }

    public List<Module> getElements(int parentId) throws DatabaseException {
        return moduleDAO.findByParent(parentId);
    }

    public int createModule(Module m) throws DatabaseException {
        return moduleDAO.insert(m);
    }

    public boolean updateModule(Module m) throws DatabaseException {
        return moduleDAO.update(m);
    }

    public boolean deleteModule(int id) throws DatabaseException {
        return moduleDAO.delete(id);
    }
}
