package dao.interfaces;

import model.Alert;
import exception.DatabaseException;
import java.util.List;

public interface AlertDAO {
    List<Alert> findAll() throws DatabaseException;
    List<Alert> findAllUnread() throws DatabaseException;
    void markAsRead(int id) throws DatabaseException;
    void markAllAsRead() throws DatabaseException;
}
