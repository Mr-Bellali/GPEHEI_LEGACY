package dao.interfaces;

import exception.DatabaseException;

public interface DashboardDAO {
    int getTotalStudents() throws DatabaseException;
    int getActiveProjects() throws DatabaseException;
    int getPendingProjects() throws DatabaseException;
    int getUnreadAlerts() throws DatabaseException;
}