package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegistrationView extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton closeButton;

    public RegistrationView() {
        createRegistrationScreen();
    }

    private void createRegistrationScreen() {
        setTitle("Регистрация");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Введите Логин:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginField = new JTextField(20);
        add(loginField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Введите Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Повторите Пароль:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        registerButton = new JButton("Зарегистрироваться");
        add(registerButton, gbc);

        gbc.gridy = 4;
        closeButton = new JButton("Закрыть");
        add(closeButton, gbc);

        setVisible(true);
    }

    public void setRegisterButtonAction(ActionListener actionListener) {
        registerButton.addActionListener(actionListener);
    }

    public void setCloseButtonAction(ActionListener actionListener) {
        closeButton.addActionListener(actionListener);
    }

    public String getLoginInput() {
        return loginField.getText().trim();
    }

    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPasswordInput() {
        return new String(confirmPasswordField.getPassword());
    }

    public void close() {
        dispose();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Уведомление", JOptionPane.INFORMATION_MESSAGE);
    }
}
