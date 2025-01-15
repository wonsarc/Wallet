package view.components;

import controller.OperationController;
import model.Category;
import storage.AccountStorage;
import storage.CategoryStorage;
import storage.OperationStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class OperationPanel extends JPanel {
    private DefaultTableModel tableModel;
    private final UUID userId;
    private final OperationController operationController;
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JComboBox<String> categoryFilterComboBox;
    private JComboBox<String> typeFilterComboBox;

    public OperationPanel(UUID userId) {
        this.userId = userId;
        this.operationController = new OperationController(this, new AccountStorage(), new OperationStorage(), new CategoryStorage());
        setLayout(new BorderLayout());

        initializeTable();
        initializeButtons();
        initializeFilterAndSummaryComponents();
        loadOperations();
    }


    private void initializeFilterAndSummaryComponents() {
        var filterAndSummaryPanel = new JPanel();
        filterAndSummaryPanel.setLayout(new GridLayout(2, 1));

        categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.addItem("Все категории");
        operationController.loadCategories();

        categoryFilterComboBox.addActionListener(e -> {
            loadOperations();
        });

        typeFilterComboBox = new JComboBox<>(new String[]{"Все типы", "Доход", "Расход"});
        typeFilterComboBox.addActionListener(e -> loadOperations());

        var filterPanel = new JPanel();
        filterPanel.add(new JLabel("Категория:"));
        filterPanel.add(categoryFilterComboBox);
        filterPanel.add(new JLabel("Тип:"));
        filterPanel.add(typeFilterComboBox);

        filterAndSummaryPanel.add(filterPanel);

        var summaryPanel = new JPanel();
        totalIncomeLabel = new JLabel("Общий доход: 0");
        totalExpenseLabel = new JLabel("Общие расходы: 0");
        summaryPanel.add(totalIncomeLabel);
        summaryPanel.add(totalExpenseLabel);

        filterAndSummaryPanel.add(summaryPanel);

        add(filterAndSummaryPanel, BorderLayout.NORTH);
    }


    public void updateSummary() {
        String selectedCategory = (String) categoryFilterComboBox.getSelectedItem();
        String selectedType = (String) typeFilterComboBox.getSelectedItem();

        double totalIncome = operationController.getTotalIncome(selectedCategory, selectedType);
        double totalExpense = operationController.getTotalExpense(selectedCategory, selectedType);

        totalIncomeLabel.setText("Общий доход: " + totalIncome);
        totalExpenseLabel.setText("Общие расходы: " + totalExpense);
    }

    private void initializeTable() {
        String[] columnNames = {"Категория", "Тип", "Сумма", "Дата"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        var operationTable = new JTable(tableModel);

        var scrollPane = new JScrollPane(operationTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeButtons() {
        var buttonPanel = new JPanel();

        var addOperationButton = new JButton("Добавить операцию");
        addOperationButton.addActionListener(e -> operationController.addOperation());
        buttonPanel.add(addOperationButton);

        var transferButton = new JButton("Сделать перевод");
        transferButton.addActionListener(e -> operationController.makeTransfer());
        buttonPanel.add(transferButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadOperations() {
        tableModel.setRowCount(0);

        var selectedCategory = (String) categoryFilterComboBox.getSelectedItem();
        var selectedType = (String) typeFilterComboBox.getSelectedItem();

        var operations = operationController.getFilteredOperationsByUserId(selectedCategory, selectedType);

        for (var operation : operations) {
            String categoryName;
            if (operation.categoryId() != null) {
                var category = operationController.getCategoryById(operation.categoryId());
                categoryName = (category != null) ? category.getName() : "Без категории";
            } else {
                categoryName = "Без категории";
            }
            tableModel.addRow(new Object[]{
                    categoryName,
                    operation.type(),
                    operation.amount(),
                    operation.date()
            });
        }
        updateSummary();
    }

    public void updateCategoryFilter(List<Category> categories) {
        categoryFilterComboBox.removeActionListener(e -> loadOperations());

        categoryFilterComboBox.removeAllItems();
        categoryFilterComboBox.addItem("Все категории");
        categoryFilterComboBox.addItem("Без категории");
        categoryFilterComboBox.addItem("Перевод");

        for (var category : categories) {
            var name = category.getName();
            if (!"Перевод".equals(name)) {
                categoryFilterComboBox.addItem(name);
            }
        }

        categoryFilterComboBox.addActionListener(e -> loadOperations());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public UUID getUserID() {
        return userId;
    }
}
