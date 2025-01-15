package controller;

import model.Category;
import model.Operation;
import storage.AccountStorage;
import storage.CategoryStorage;
import storage.OperationStorage;
import view.components.OperationPanel;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class OperationController {
    private final OperationPanel view;
    private final AccountStorage accountStorage;
    private final OperationStorage operationStorage;
    private final CategoryStorage categoryStorage;

    public OperationController(OperationPanel view, AccountStorage accountStorage, OperationStorage operationStorage, CategoryStorage categoryStorage) {
        this.view = view;
        this.accountStorage = accountStorage;
        this.operationStorage = operationStorage;
        this.categoryStorage = categoryStorage;
    }

    public void addOperation() {
        var type = getOperationType();
        if (type == null) return;

        var categories = categoryStorage.loadAllByUserId(view.getUserID());
        var categoryId = getCategoryId(categories);

        var amountStr = JOptionPane.showInputDialog("Введите сумму (больше 0):");
        if (amountStr == null) return;

        var amount = parseAmount(amountStr);
        if (amount < 0) return;

        double balance = accountStorage.loadByUserId(view.getUserID()).getBalance();
        if (type.equals("Расход") && amount > balance) {
            view.showError("Недостаточно средств на счете.");
            return;
        }

        if (type.equals("Расход") && categoryId != null) {
            var category = categoryStorage.load(categoryId);
            double spentInCategory = getSpentInCategory(categoryId);
            double remainingLimit = category.getRemaining(spentInCategory);
            if (amount > remainingLimit) {
                view.showError("Лимит по категории исчерпан. Осталось: " + remainingLimit);
                return;
            }
        }

        var date = getCurrentDateTime();
        var accountId = accountStorage.loadByUserId(view.getUserID()).getId();
        var operation = new Operation(UUID.randomUUID(), accountId, categoryId, type, amount, date);

        operationStorage.save(operation);

        var account = accountStorage.loadByUserId(view.getUserID());
        var oldBalance = account.getBalance();
        var newBalance = type.equals("Расход") ? oldBalance - amount : oldBalance + amount;
        account.setBalance(newBalance);

        accountStorage.update(account);

        view.loadOperations();
    }

    private double getSpentInCategory(UUID categoryId) {
        var operations = operationStorage.loadAllByAccountId(accountStorage.loadByUserId(view.getUserID()).getId());
        return operations.stream()
                .filter(op -> op.type().equals("Расход") && op.categoryId() != null && op.categoryId().equals(categoryId))
                .mapToDouble(Operation::amount)
                .sum();
    }

    private String getOperationType() {
        var options = new String[]{"Доход", "Расход"};
        var choice = JOptionPane.showOptionDialog(view, "Выберите тип операции:", "Добавить операцию",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        return (choice == -1) ? null : options[choice];
    }

    private UUID getCategoryId(List<Category> categories) {
        var filteredCategories = categories.stream()
                .filter(category -> !"Перевод".equals(category.getName()))
                .toList();

        var categoryNames = new String[filteredCategories.size() + 1];
        categoryNames[0] = "Без категории";

        for (var i = 0; i < filteredCategories.size(); i++) {
            categoryNames[i + 1] = filteredCategories.get(i).getName();
        }

        var category = (String) JOptionPane.showInputDialog(view, "Выберите категорию:", "Добавить операцию",
                JOptionPane.QUESTION_MESSAGE, null, categoryNames, categoryNames[0]);

        if (category == null) return null;

        return category.equals("Без категории") ? null : filteredCategories.stream()
                .filter(c -> c.getName().equals(category))
                .findFirst()
                .map(Category::getId)
                .orElse(null);
    }

    private double parseAmount(String amountStr) {
        try {
            var amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                throw new NumberFormatException("Сумма должна быть больше 0.");
            }
            return amount;
        } catch (NumberFormatException e) {
            view.showError("Введите число");
            return -1;
        }
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private boolean isTypeMatching(Operation operation, String selectedType) {
        return selectedType.equals("Все типы") || operation.type().equals(selectedType);
    }

    private boolean isCategoryMatching(Operation operation, String selectedCategory) {
        if ("Все категории".equals(selectedCategory)) {
            return true;
        }

        if (operation.categoryId() == null) {
            return "Без категории".equals(selectedCategory);
        }

        var category = getCategoryById(operation.categoryId());
        if (category == null) {
            return "Без категории".equals(selectedCategory);
        }

        return category.getName().equals(selectedCategory);
    }

    private UUID getOrCreateTransferCategory() {
        var categories = categoryStorage.loadAllByUserId(view.getUserID());
        return categories.stream()
                .filter(cat -> "Перевод".equals(cat.getName()))
                .findFirst()
                .map(Category::getId)
                .orElseGet(() -> {
                    var newCategory = new Category(UUID.randomUUID(), view.getUserID(), "Перевод", Double.MAX_VALUE);
                    categoryStorage.save(newCategory);
                    return newCategory.getId();
                });
    }

    public double getTotalIncome(String selectedCategory, String selectedType) {
        var operations = getFilteredOperationsByUserId(selectedCategory, selectedType);
        return operations.stream()
                .filter(op -> op.type().equals("Доход"))
                .mapToDouble(Operation::amount)
                .sum();
    }

    public double getTotalExpense(String selectedCategory, String selectedType) {
        var operations = getFilteredOperationsByUserId(selectedCategory, selectedType);
        return operations.stream()
                .filter(op -> op.type().equals("Расход"))
                .mapToDouble(Operation::amount)
                .sum();
    }


    public List<Operation> getFilteredOperationsByUserId(String selectedCategory, String selectedType) {
        var accountId = accountStorage.loadByUserId(view.getUserID()).getId();
        var operations = operationStorage.loadAllByAccountId(accountId);

        return operations.stream()
                .filter(operation -> isTypeMatching(operation, selectedType) && isCategoryMatching(operation, selectedCategory))
                .toList();
    }

    public void loadCategories() {
        var categories = categoryStorage.loadAllByUserId(view.getUserID());
        view.updateCategoryFilter(categories);
    }

    public Category getCategoryById(UUID categoryId) {
        return categoryStorage.load(categoryId);
    }

    public void makeTransfer() {
        var recipientAccountNumber = JOptionPane.showInputDialog(view, "Введите номер счета получателя:");
        if (recipientAccountNumber == null || recipientAccountNumber.isBlank()) {
            view.showError("Номер счета не может быть пустым.");
            return;
        }

        var recipientAccount = accountStorage.loadByAccountNumber(Long.parseLong(recipientAccountNumber));
        if (recipientAccount == null) {
            view.showError("Клиент с указанным номером счета не найден.");
            return;
        }

        var amountStr = JOptionPane.showInputDialog(view, "Введите сумму перевода:");
        if (amountStr == null || amountStr.isBlank()) return;

        var amount = parseAmount(amountStr);
        if (amount < 0) return;

        var senderAccount = accountStorage.loadByUserId(view.getUserID());
        if (amount > senderAccount.getBalance()) {
            view.showError("Недостаточно средств на счете.");
            return;
        }

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);

        accountStorage.update(senderAccount);
        accountStorage.update(recipientAccount);

        var date = getCurrentDateTime();
        var transferCategoryId = getOrCreateTransferCategory();

        var senderOperation = new Operation(UUID.randomUUID(), senderAccount.getId(), transferCategoryId, "Расход", amount, date);
        operationStorage.save(senderOperation);

        var recipientOperation = new Operation(UUID.randomUUID(), recipientAccount.getId(), transferCategoryId, "Доход", amount, date);
        operationStorage.save(recipientOperation);

        view.loadOperations();
    }
}
