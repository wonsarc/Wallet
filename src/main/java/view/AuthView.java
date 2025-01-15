package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AuthView extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton enterButton;
    private JButton registrationButton;
    private JButton exitButton;

    public AuthView() {
        createLoginScreen();
    }

    private void createLoginScreen() {
        setTitle("Авторизация");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Введите Логин:"), gbc);

        loginField = new JTextField(20);
        gbc.gridx = 1;
        add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Введите Пароль:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        enterButton = new JButton("Войти");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(enterButton, gbc);

        registrationButton = new JButton("Регистрация");
        gbc.gridy = 3;
        add(registrationButton, gbc);

        exitButton = new JButton("Выход");
        gbc.gridy = 4;
        add(exitButton, gbc);

        setVisible(true);
    }

    public void setEnterButtonAction(ActionListener actionListener) {
        enterButton.addActionListener(actionListener);
    }

    public void setRegistrationButtonAction(ActionListener actionListener) {
        registrationButton.addActionListener(actionListener);
    }

    public void setExitButtonAction(ActionListener actionListener) {
        exitButton.addActionListener(actionListener);
    }

    public String getLoginInput() {
        return loginField.getText().trim();
    }

    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    public void close() {
        dispose();
    }

    public void exit() {
        System.exit(0);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
