package service;

import dao.impl.ProjectDAOImpl;
import dao.interfaces.ProjectDAO;
import exception.DatabaseException;
import model.Project;
import java.util.List;

public class ProjectService {

    private final ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAOImpl();
    }

    public List<Project> getAllProjects() throws DatabaseException {
        return projectDAO.findAll();
    }

    public List<Project> getActiveProjects() throws DatabaseException {
        return projectDAO.findActive();
    }

    public void saveProject(Project project) throws DatabaseException {
        projectDAO.save(project);
    }

    public void updateProject(Project project) throws DatabaseException {
        projectDAO.update(project);
    }

    public void deleteProject(int id) throws DatabaseException {
        projectDAO.delete(id);
    }
}
