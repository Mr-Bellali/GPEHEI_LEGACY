package service;

import dao.impl.DashboardDAOImpl;
import dao.interfaces.DashboardDAO;
import exception.DatabaseException;
import java.util.HashMap;
import java.util.Map;

public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAOImpl();
    }

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();

        try {
            stats.put("totalStudents", dashboardDAO.getTotalStudents());
            stats.put("activeProjects", dashboardDAO.getActiveProjects());
            stats.put("pendingProjects", dashboardDAO.getPendingProjects());
            stats.put("unreadAlerts", dashboardDAO.getUnreadAlerts());
        } catch (DatabaseException e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            // Return zeros if database fails
            stats.put("totalStudents", 0);
            stats.put("activeProjects", 0);
            stats.put("pendingProjects", 0);
            stats.put("unreadAlerts", 0);
        }

        return stats;
    }

    public int getTotalStudents() {
        try {
            return dashboardDAO.getTotalStudents();
        } catch (DatabaseException e) {
            System.err.println("Error getting total students: " + e.getMessage());
            return 0;
        }
    }

    public int getActiveProjects() {
        try {
            return dashboardDAO.getActiveProjects();
        } catch (DatabaseException e) {
            System.err.println("Error getting active projects: " + e.getMessage());
            return 0;
        }
    }

    public int getPendingProjects() {
        try {
            return dashboardDAO.getPendingProjects();
        } catch (DatabaseException e) {
            System.err.println("Error getting pending projects: " + e.getMessage());
            return 0;
        }
    }

    public int getUnreadAlerts() {
        try {
            return dashboardDAO.getUnreadAlerts();
        } catch (DatabaseException e) {
            System.err.println("Error getting unread alerts: " + e.getMessage());
            return 0;
        }
    }
}