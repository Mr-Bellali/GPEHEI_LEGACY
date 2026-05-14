package dao.interfaces;

import model.Admin;
import java.util.List;

public interface AdminDAO {
    Admin findByEmail(String email);
    Admin findById(int id);
    List<Admin> findAll();
    boolean save(Admin admin);
    boolean update(Admin admin);
    boolean delete(int id);
}