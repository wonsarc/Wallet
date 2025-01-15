package controller;

import storage.UserStorage;
import view.AuthView;
import view.MainView;

import javax.swing.*;

public class MainController {
    private final MainView view;
    private final UserStorage userStorage;

    public MainController(UserStorage userStorage, MainView view) {
        this.view = view;
        this.userStorage = userStorage;
    }

    public void switchUser() {
        view.close();
        SwingUtilities.invokeLater(() -> {
            var authView = new AuthView();
            new AuthController(userStorage, authView);
        });
    }

    public void exitApplication() {
        view.confirmExit();
    }
}
