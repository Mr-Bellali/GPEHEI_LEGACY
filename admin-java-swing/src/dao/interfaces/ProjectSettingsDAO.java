package dao.interfaces;

import exception.DatabaseException;
import java.util.List;
import java.util.Map;

public interface ProjectSettingsDAO {
    void updateCapacity(int filiereId, String promotion, int min, int max) throws DatabaseException;
    void updateDeadline(int filiereId, String promotion, java.time.LocalDateTime deadline) throws DatabaseException;
    Map<String, Object> getSettings(int filiereId, String promotion) throws DatabaseException;
}
