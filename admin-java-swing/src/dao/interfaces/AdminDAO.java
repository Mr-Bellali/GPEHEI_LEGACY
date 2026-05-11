package dao.interfaces;

import model.Admin;

public interface AdminDAO {

    Admin findByEmail(String email);

}