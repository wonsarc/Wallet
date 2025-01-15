package controller;

import model.User;
import storage.AccountStorage;
import storage.UserStorage;
import view.AuthView;
import view.MainView;
import view.RegistrationView;

import java.util.Objects;

public class AuthController {
    private final AuthView view;
    private final UserStorage userStorage;

    public AuthController(UserStorage userStorage, AuthView view) {
        this.userStorage = userStorage;
        this.view = view;
        initialize();
    }

    private void initialize() {
        view.setEnterButtonAction(e -> handleLogin());
        view.setRegistrationButtonAction(e -> proceedToRegistrationScreen());
        view.setExitButtonAction(e -> view.exit());
    }

    public void handleLogin() {
        var username = view.getLoginInput();
        var password = view.getPasswordInput();

        if (isInputInvalid(username, password)) {
            return;
        }

        var currentUser = userStorage.loadByUsername(username);

        if (isLoginInvalid(currentUser, password)) {
            view.showError("Неверный логин или пароль");
            return;
        }

        proceedToMainScreen(currentUser);
        view.close();
    }

    public void proceedToRegistrationScreen() {
        var accountStorage = new AccountStorage();
        var registrationView = new RegistrationView();
        new RegistrationController(userStorage, accountStorage, registrationView);
    }

    private void proceedToMainScreen(User user) {
        var mainView = new MainView(user);
        new MainController(userStorage, mainView);
    }

    private boolean isInputInvalid(String username, String password) {
        if (isBlank(username)) {
            view.showError("Введите логин");
            return true;
        }

        if (isBlank(password)) {
            view.showError("Введите пароль");
            return true;
        }

        return false;
    }

    private boolean isLoginInvalid(User user, String password) {
        return user == null || !Objects.equals(user.password(), password);
    }

    private boolean isBlank(String input) {
        return input == null || input.isBlank();
    }
}
