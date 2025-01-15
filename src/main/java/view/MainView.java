package view;

import controller.CategoryController;
import controller.InfoController;
import controller.MainController;
import controller.OperationController;
import model.User;
import storage.AccountStorage;
import storage.CategoryStorage;
import storage.OperationStorage;
import storage.UserStorage;
import view.components.CategoryPanel;
import view.components.InfoPanel;
import view.components.OperationPanel;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private final MainController mainController;
    private final InfoController infoController;
    private final OperationController operationController;
    private final CategoryController categoryController;

    private final User user;

    private InfoPanel infoPanel;
    private OperationPanel operationPanel;
    private CategoryPanel categoryPanel;

    private JButton changeUserButton;
    private JButton exitButton;

    public MainView(User user) {
        this(user, new UserStorage(), new AccountStorage(), new OperationStorage(), new CategoryStorage());
    }

    public MainView(User user, UserStorage userStorage, AccountStorage accountStorage,
                    OperationStorage operationStorage, CategoryStorage categoryStorage) {
        this.user = user;

        this.mainController = new MainController(userStorage, this);
        this.infoPanel = new InfoPanel(user.id());
        this.infoController = new InfoController(userStorage, accountStorage, infoPanel);
        this.operationPanel = new OperationPanel(user.id());
        this.operationController = new OperationController(operationPanel, accountStorage, operationStorage, categoryStorage);
        this.categoryPanel = new CategoryPanel(user.id());
        this.categoryController = new CategoryController(accountStorage, operationStorage, categoryStorage, categoryPanel);

        createMainScreen();
        bindEvents();
    }

    private void createMainScreen() {
        setTitle("Мой кошелек");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var tabbedPane = new JTabbedPane();
        addTabs(tabbedPane);

        var buttonPanel = new JPanel();
        changeUserButton = new JButton("Сменить пользователя");
        exitButton = new JButton("Выйти");

        buttonPanel.add(changeUserButton);
        buttonPanel.add(exitButton);

        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addTabs(JTabbedPane tabbedPane) {
        tabbedPane.addTab("Инфо", infoPanel);
        tabbedPane.addTab("Операции", operationPanel);
        tabbedPane.addTab("Категории", categoryPanel);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0) {
                infoController.updateInfo();
            }
            if (selectedIndex == 1) {
                operationController.loadCategories();
            }

            if (selectedIndex == 2) {
                categoryController.updateCategories();
            }
        });
    }

    private void bindEvents() {
        changeUserButton.addActionListener(e -> mainController.switchUser());
        exitButton.addActionListener(e -> mainController.exitApplication());
    }

    public void confirmExit() {
        var confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти?",
                "Выход",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void close() {
        dispose();
    }
}