package controller;

import model.Account;
import model.User;
import storage.AccountStorage;
import storage.UserStorage;
import utils.AccountNumberGenerator;
import view.RegistrationView;

import java.util.UUID;

public class RegistrationController {
    private final RegistrationView view;
    private final UserStorage userStorage;
    private final AccountStorage accountStorage;

    public RegistrationController(UserStorage userStorage, AccountStorage accountStorage, RegistrationView view) {
        this.userStorage = userStorage;
        this.accountStorage = accountStorage;
        this.view = view;
        initialize();
    }

    private void initialize() {
        view.setRegisterButtonAction(e -> handleRegistration());
        view.setCloseButtonAction(e -> view.close());
    }

    private boolean isInputInvalid(String username, String password, String confirmPassword) {
        if (isBlank(username)) {
            view.showError("Введите логин");
            return true;
        }

        if (isBlank(password)) {
            view.showError("Введите пароль");
            return true;
        }

        if (!password.equals(confirmPassword)) {
            view.showError("Пароли не сходятся");
            return true;
        }

        return false;
    }

    private boolean isBlank(String input) {
        return input == null || input.isBlank();
    }

    private boolean isUsernameTaken(String username) {
        return userStorage.loadByUsername(username) != null;
    }

    private void registerUser(String username, String password) {
        var newUser = new User(UUID.randomUUID(), username, password);
        userStorage.save(newUser);

        var account = new Account(UUID.randomUUID(), newUser.id(), AccountNumberGenerator.generateAccountNumber(newUser.id()), 0);
        accountStorage.save(account);
    }

    public void handleRegistration() {
        var username = view.getLoginInput();
        var password = view.getPasswordInput();
        var confirmPassword = view.getConfirmPasswordInput();

        if (isInputInvalid(username, password, confirmPassword)) {
            return;
        }

        if (isUsernameTaken(username)) {
            view.showError("Логин уже занят");
            return;
        }

        registerUser(username, password);
        view.showSuccess("Успешная регистрация!");
        view.close();
    }
}
