package view.components;

import controller.CategoryController;
import model.Category;
import storage.AccountStorage;
import storage.CategoryStorage;
import storage.OperationStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class CategoryPanel extends JPanel {
    private final JTable categoryTable;
    private final DefaultTableModel tableModel;
    private final CategoryController categoryController;
    private final UUID userID;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton updateButton;

    public CategoryPanel(UUID userID) {
        categoryController = new CategoryController(new AccountStorage(), new OperationStorage(), new CategoryStorage(), this);
        this.userID = userID;

        setLayout(new BorderLayout());

        String[] columnNames = {"Название", "Лимит", "Осталось"};
        tableModel = new DefaultTableModel(columnNames, 0);
        categoryTable = new JTable(tableModel);
        var scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);

        var buttonPanel = new JPanel();
        addButton = new JButton("Добавить категорию");
        deleteButton = new JButton("Удалить категорию");
        updateButton = new JButton("Изменить лимит");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        bindEvents();
        loadCategoriesForUser();
    }

    private void bindEvents() {
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        updateButton.addActionListener(e -> updateLimit());
    }

    private void loadCategoriesForUser() {
        var categories = categoryController.getCategoriesForUser(userID);
        loadCategories(categories);
    }

    private void addCategory() {
        var name = JOptionPane.showInputDialog("Введите имя категории:");
        if (isInvalidInput(name)) return;

        var limitStr = JOptionPane.showInputDialog("Введите лимит категории:");
        if (limitStr == null) return;

        try {
            var limit = Double.parseDouble(limitStr);
            if (limit >= 0) {
                if (categoryController.categoryExists(name, userID)) {
                    showError("Категория с таким именем уже существует.");
                } else {
                    categoryController.addCategory(name, limit, userID);
                }
            } else {
                showError("Введите корректное неотрицательное число для лимита.");
            }
        } catch (NumberFormatException e) {
            showError("Введите корректное число для лимита.");
        }
    }

    private void updateLimit() {
        var selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            var newLimitStr = JOptionPane.showInputDialog("Введите новый лимит для категории:");
            if (newLimitStr == null) return;

            try {
                var newLimit = Double.parseDouble(newLimitStr);
                if (newLimit >= 0) {
                    var name = getCategoryNameFromRow(selectedRow);
                    categoryController.updateCategoryLimit(name, newLimit, userID);
                } else {
                    showError("Введите корректное неотрицательное число для лимита.");
                }
            } catch (NumberFormatException e) {
                showError("Введите корректное число для лимита.");
            }
        } else {
            showError("Выберите категорию для изменения лимита.");
        }
    }

    private void deleteCategory() {
        var selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            var name = getCategoryNameFromRow(selectedRow);
            categoryController.deleteCategory(name, userID);
        } else {
            showError("Выберите категорию для удаления.");
        }
    }

    private String getCategoryNameFromRow(int row) {
        return (String) tableModel.getValueAt(row, 0);
    }

    private boolean isInvalidInput(String input) {
        return input == null || input.trim().isEmpty();
    }

    public void loadCategories(List<Category> categories) {
        tableModel.setRowCount(0);
        for (var category : categories) {
            if (category.getName().equals("Перевод")) {
                continue;
            }
            var spentInCategory = categoryController.getSpentInCategory(userID, category.getId());
            var remainingLimit = category.getRemaining(spentInCategory);
            tableModel.addRow(new Object[]{category.getName(), category.getLimit(), remainingLimit});
        }
    }

    public UUID getUserId() {
        return userID;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
