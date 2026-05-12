package service;

import dao.impl.ProjectSettingsDAOImpl;
import dao.interfaces.ProjectSettingsDAO;
import exception.DatabaseException;
import java.util.Map;

public class SettingsService {
    private final ProjectSettingsDAO dao;

    public SettingsService() {
        this.dao = new ProjectSettingsDAOImpl();
    }

    public void updateProjectCapacity(int filiereId, String promotion, int min, int max) throws DatabaseException {
        dao.updateCapacity(filiereId, promotion, min, max);
    }

    public Map<String, Object> getProjectSettings(int filiereId, String promotion) throws DatabaseException {
        return dao.getSettings(filiereId, promotion);
    }
}
