package controller;

import service.AuthService;
import utils.SessionManager;
import view.auth.LoginFrame;
import main.MainApp;
import javax.swing.*;

public class LoginController {

    private final LoginFrame view;
    private final AuthService authService;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.authService = new AuthService();
        initListeners();
    }

    private void initListeners() {
        view.addLoginListener(e -> login());
    }

    private void login() {
        try {
            String token = authService.login(
                    view.getEmail(),
                    view.getPassword()
            );

            if (token != null) {
                SessionManager.setToken(token);
                view.showMessage("Login success");

                // Navigate to master page
                MainApp.navigateToMasterPage();
            } else {
                view.showMessage("Wrong credentials");
            }
        } catch (Exception e) {
            view.showMessage(e.getMessage());
        }
    }
}