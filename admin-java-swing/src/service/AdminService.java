package service;

import dao.impl.AdminDAOImpl;
import dao.interfaces.AdminDAO;
import model.Admin;
import java.util.List;

public class AdminService {
    private final AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAOImpl();
    }

    public Admin getAdminByEmail(String email) {
        return adminDAO.findByEmail(email);
    }

    public Admin getAdminById(int id) {
        return adminDAO.findById(id);
    }

    public List<Admin> getAllAdmins() {
        return adminDAO.findAll();
    }

    public boolean createAdmin(Admin admin) {
        return adminDAO.save(admin);
    }

    public boolean updateAdmin(Admin admin) {
        return adminDAO.update(admin);
    }

    public boolean deleteAdmin(int id) {
        return adminDAO.delete(id);
    }
}
