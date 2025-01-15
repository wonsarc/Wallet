import controller.AuthController;
import storage.UserStorage;
import view.AuthView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var userStorage = new UserStorage();
            var authView = new AuthView();
            new AuthController(userStorage, authView);
        });
    }
}
