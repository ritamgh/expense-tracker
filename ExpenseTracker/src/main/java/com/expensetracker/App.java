package com.expensetracker;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class App extends Application {

    private DatabaseHandler dbHandler;
    private ObservableList<Expense> expenseList;

    @Override
    public void start(Stage primaryStage) {
        try {
            dbHandler = new DatabaseHandler();
            expenseList = FXCollections.observableArrayList(dbHandler.getAllExpenses());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to connect to the database.");
            return;
        }

        // UI Elements
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("date-picker");

        Label amountLabel = new Label("Amount:");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.getStyleClass().add("text-field");

        // Initialize the PieChart
        PieChart pieChart = new PieChart();
        updatePieChart(pieChart, expenseList);  // Initialize pie chart with data

        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(
            "Food",
            "Personal Expenses",
            "Shopping",
            "Investing",
            "Savings",
            "EMI/Rent",
            "Transport",
            "Others"
        );
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.getStyleClass().add("combo-box");

        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        descriptionField.getStyleClass().add("text-field");

        Button addButton = new Button("Add Expense");
        addButton.getStyleClass().add("button");

        Button editButton = new Button("Edit Expense");
        editButton.getStyleClass().add("button");

        Button deleteButton = new Button("Delete Expense");
        deleteButton.getStyleClass().add("button");

        // Date Filtering Elements
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.getStyleClass().add("date-picker");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.getStyleClass().add("date-picker");

        Button filterButton = new Button("Filter");
        filterButton.getStyleClass().add("button");

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by description or category");
        searchField.getStyleClass().add("search-bar");

        // TableView
        TableView<Expense> tableView = new TableView<>();
        tableView.setEditable(true);
        tableView.getStyleClass().add("table-view");

        TableColumn<Expense, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(dateColumn, amountColumn, categoryColumn, descriptionColumn);
        tableView.setItems(expenseList);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Add Expense Button Action
        addButton.setOnAction(event -> {
            try {
                LocalDate date = datePicker.getValue();
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryComboBox.getValue();
                String description = descriptionField.getText();

                if (category == null || category.isEmpty()) {
                    showAlert("Input Error", "Please select a category.");
                    return;
                }

                Expense expense = new Expense(date, amount, category, description);
                dbHandler.addExpense(expense);
                expenseList.add(expense);
                clearFields(amountField, categoryComboBox, descriptionField);
                updatePieChart(pieChart, expenseList);  // Update pie chart when adding an expense
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter a valid amount.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Unable to add expense to the database.");
            }
        });

        // Edit Expense Button Action
        editButton.setOnAction(event -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense == null) {
                showAlert("Selection Error", "Please select an expense to edit.");
                return;
            }
            try {
                LocalDate date = datePicker.getValue();
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryComboBox.getValue();
                String description = descriptionField.getText();

                selectedExpense.setDate(date);
                selectedExpense.setAmount(amount);
                selectedExpense.setCategory(category);
                selectedExpense.setDescription(description);

                dbHandler.updateExpense(selectedExpense);  // Update in DB
                tableView.refresh();  // Refresh table
                clearFields(amountField, categoryComboBox, descriptionField);
                updatePieChart(pieChart, expenseList);  // Update pie chart when editing an expense
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter a valid amount.");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Unable to edit expense in the database.");
            }
        });

        // Delete Expense Button Action
        deleteButton.setOnAction(event -> {
            Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
            if (selectedExpense == null) {
                showAlert("Selection Error", "Please select an expense to delete.");
                return;
            }
            try {
                dbHandler.deleteExpense(selectedExpense);  // Delete from DB
                expenseList.remove(selectedExpense);  // Remove from table
                updatePieChart(pieChart, expenseList);  // Update pie chart when deleting an expense
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Unable to delete expense from the database.");
            }
        });

        // Filter by Date
        filterButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            List<Expense> filteredExpenses = expenseList.stream()
                    .filter(exp -> (startDate == null || !exp.getDate().isBefore(startDate)) &&
                                   (endDate == null || !exp.getDate().isAfter(endDate)))
                    .collect(Collectors.toList());
            tableView.setItems(FXCollections.observableArrayList(filteredExpenses));
            updatePieChart(pieChart, FXCollections.observableArrayList(filteredExpenses));  // Update pie chart after filtering
        });

        // Search Functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            List<Expense> filteredExpenses = expenseList.stream()
                    .filter(exp -> exp.getDescription().toLowerCase().contains(newValue.toLowerCase()) ||
                                   exp.getCategory().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
            tableView.setItems(FXCollections.observableArrayList(filteredExpenses));
            updatePieChart(pieChart, FXCollections.observableArrayList(filteredExpenses));  // Update pie chart after searching
        });


        HBox inputBox = new HBox(15, dateLabel, datePicker, amountLabel, amountField, categoryLabel, categoryComboBox, descriptionLabel, descriptionField, addButton, editButton, deleteButton);
        inputBox.setAlignment(Pos.CENTER);
        inputBox.setPadding(new Insets(20));

        HBox filterBox = new HBox(15, startDatePicker, endDatePicker, filterButton, searchField);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(20));

        // Create VBox to stack tableView and pieChart vertically
        VBox tableAndChartBox = new VBox(20, tableView, pieChart);
        tableAndChartBox.setAlignment(Pos.CENTER);
        tableAndChartBox.setPadding(new Insets(20));

        VBox root = new VBox(30, inputBox, filterBox, tableAndChartBox);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 1200, 800);
        // Try to load the stylesheet
        URL stylesheet = getClass().getResource("/styles.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        } else {
            // Log an error, but do not fail the application
            System.err.println("Warning: styles.css not found. Skipping stylesheet loading.");
        }

        primaryStage.setTitle("Expense Tracker - Enhanced with Pie Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Update PieChart based on the expense list
    private void updatePieChart(PieChart pieChart, ObservableList<Expense> expenses) {
        pieChart.getData().clear();
        expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)))
                .forEach((category, totalAmount) -> {
                    PieChart.Data slice = new PieChart.Data(category, totalAmount);
                    pieChart.getData().add(slice);
                });
        pieChart.setTitle("Expense Distribution by Category");
    }

    private void clearFields(TextField amountField, ComboBox<String> categoryComboBox, TextField descriptionField) {
        amountField.clear();
        categoryComboBox.setValue(null);
        descriptionField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
