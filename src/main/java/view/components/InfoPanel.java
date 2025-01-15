package view.components;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class InfoPanel extends JPanel {
    private final UUID userID;
    private final JLabel usernameLabel;
    private final JLabel numberAccountLabel;
    private final JLabel balanceLabel;

    public InfoPanel(UUID userId) {
        this.userID = userId;
        setLayout(new GridLayout(3, 1));

        usernameLabel = new JLabel();
        numberAccountLabel = new JLabel();
        balanceLabel = new JLabel();

        add(usernameLabel);
        add(numberAccountLabel);
        add(balanceLabel);
    }

    public void setName(String username) {
        usernameLabel.setText("Имя пользователя: " + username);
    }

    public void setNumberAccount(Long number) {
        numberAccountLabel.setText("Номер счета: " + number);
    }

    public void setBalance(Double balance) {
        balanceLabel.setText("Баланс: " + balance);
    }

    public UUID getUserId() {
        return userID;
    }
}
