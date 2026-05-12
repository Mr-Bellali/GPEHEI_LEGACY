package service;

import dao.impl.AlertDAOImpl;
import dao.interfaces.AlertDAO;
import exception.DatabaseException;
import model.Alert;
import java.util.List;

public class AlertService {
    private final AlertDAO alertDAO;

    public AlertService() {
        this.alertDAO = new AlertDAOImpl();
    }

    public List<Alert> getAllAlerts() throws DatabaseException {
        return alertDAO.findAll();
    }

    public List<Alert> getUnreadAlerts() throws DatabaseException {
        return alertDAO.findAllUnread();
    }

    public void markAsRead(int id) throws DatabaseException {
        alertDAO.markAsRead(id);
    }

    public void markAllAsRead() throws DatabaseException {
        alertDAO.markAllAsRead();
    }
}
