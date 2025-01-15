package controller;

import storage.AccountStorage;
import storage.UserStorage;
import view.components.InfoPanel;

public class InfoController {
    private final InfoPanel view;
    private final UserStorage userStorage;
    private final AccountStorage accountStorage;

    public InfoController(UserStorage userStorage, AccountStorage accountStorage, InfoPanel view) {
        this.userStorage = userStorage;
        this.accountStorage = accountStorage;
        this.view = view;
        updateInfo();
    }

    public void updateInfo() {
        var userId = view.getUserId();

        var user = userStorage.load(userId);
        view.setName(user.username());

        var account = accountStorage.loadByUserId(userId);
        view.setNumberAccount(account.getAccountNumber());
        view.setBalance(account.getBalance());
    }
}
