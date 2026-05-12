package service;

import dao.impl.AdminDAOImpl;
import model.Admin;
import model.AdminStatus;
import utils.JwtUtil;
import utils.PasswordHasher;
import utils.Validator;

public class AuthService {

    private final AdminDAOImpl adminDAO;

    public AuthService() {
        this.adminDAO = new AdminDAOImpl();
    }


    public String login(
            String email,
            String password
    ) {

        // 1 Validation
        validateLoginInput(
                email,
                password
        );

        // 2 DB
        Admin admin =
                adminDAO.findByEmail(
                        email
                );

        if(admin == null){
            return null;
        }

        // 3 Status check
        if(admin.getStatus()
                != AdminStatus.ACTIVE){

            throw new RuntimeException(
                    "Account disabled"
            );
        }

        // 4 SHA256
        boolean validPassword =
                PasswordHasher.verify(
                        password,
                        admin.getPassword()
                );

        if(!validPassword){
            return null;
        }

        // 5 JWT
        return JwtUtil.generateToken(
                admin.getId(),
                admin.getEmail(),
                admin.getRole().name(),
                admin.getFirstName()

        );
    }


    private void validateLoginInput(
            String email,
            String password
    ) {

        if(!Validator.isValidEmail(
                email
        )){

            throw new RuntimeException(
                    "Invalid email"
            );
        }

        if(!Validator.isNotEmpty(
                password
        )){

            throw new RuntimeException(
                    "Password required"
            );
        }
    }
}