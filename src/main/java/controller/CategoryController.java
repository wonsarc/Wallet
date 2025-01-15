package controller;

import model.Category;
import model.Operation;
import storage.AccountStorage;
import storage.CategoryStorage;
import storage.OperationStorage;
import view.components.CategoryPanel;

import java.util.List;
import java.util.UUID;

public class CategoryController {
    private final CategoryPanel view;
    private final AccountStorage accountStorage;
    private final OperationStorage operationStorage;
    private final CategoryStorage categoryStorage;

    public CategoryController(
            AccountStorage accountStorage,
            OperationStorage operationStorage,
            CategoryStorage categoryStorage,
            CategoryPanel view
    ) {
        this.accountStorage = accountStorage;
        this.operationStorage = operationStorage;
        this.categoryStorage = categoryStorage;
        this.view = view;
    }

    public void updateCategories() {
        var categories = getCategoriesForUser(view.getUserId());
        view.loadCategories(categories);
    }

    public void addCategory(String name, double limit, UUID userId) {
        if (categoryExists(name, userId)) {
            view.showError("Категория с таким именем уже существует.");
            return;
        }
        var newCategory = new Category(UUID.randomUUID(), userId, name, limit);
        categoryStorage.save(newCategory);
        updateCategories();
    }

    public void updateCategoryLimit(String name, double limit, UUID userId) {
        var category = categoryStorage.loadByUserIdAndName(userId, name);
        if (category != null) {
            category.setLimit(limit);
            categoryStorage.update(category);
            updateCategories();
        } else {
            view.showError("Категория не найдена.");
        }
    }

    public void deleteCategory(String name, UUID userId) {
        var category = categoryStorage.loadByUserIdAndName(userId, name);
        if (category != null) {
            categoryStorage.delete(category.getId());
            updateCategories();
        } else {
            view.showError("Категория не найдена.");
        }
    }

    public boolean categoryExists(String name, UUID userId) {
        return categoryStorage.loadByUserIdAndName(userId, name) != null;
    }

    public double getSpentInCategory(UUID userId, UUID categoryId) {
        var operations = operationStorage.loadAllByAccountId(accountStorage.loadByUserId(userId).getId());
        return operations.stream()
                .filter(op -> op.type().equals("Расход") && op.categoryId() != null && op.categoryId().equals(categoryId))
                .mapToDouble(Operation::amount)
                .sum();
    }

    public List<Category> getCategoriesForUser(UUID userId) {
        return categoryStorage.loadAllByUserId(userId);
    }
}
