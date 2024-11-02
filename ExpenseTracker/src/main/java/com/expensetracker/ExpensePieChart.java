package com.expensetracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.HashMap;
import java.util.Map;

public class ExpensePieChart {

    private ObservableList<Expense> expenseList;

    public ExpensePieChart(ObservableList<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public PieChart generatePieChart() {
        // Calculate the total amount spent per category
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenseList) {
            String category = expense.getCategory();
            double amount = expense.getAmount();

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Create PieChart Data from categoryTotals
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Create the PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Expense Distribution by Category");

        // Customize pie chart appearance (optional)
        pieChart.setLabelsVisible(true); // Show labels
        pieChart.setLegendVisible(true); // Show legend

        return pieChart;
    }
}
