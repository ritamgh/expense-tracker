package com.expensetracker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:mariadb://localhost:3306/expensetrackerdb";
    private static final String USER = "expenseuser";
    private static final String PASSWORD = "expensetracker";

    private Connection connection;

    public DatabaseHandler() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void addExpense(Expense expense) throws SQLException {
        String query = "INSERT INTO expenses (date, amount, category, description) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setDate(1, Date.valueOf(expense.getDate()));
        stmt.setDouble(2, expense.getAmount());
        stmt.setString(3, expense.getCategory());
        stmt.setString(4, expense.getDescription());
        stmt.executeUpdate();
    }

    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Expense expense = new Expense(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getDouble("amount"),
                rs.getString("category"),
                rs.getString("description")
            );
            expenses.add(expense);
        }
        return expenses;
    }

    public void updateExpense(Expense expense) throws SQLException {
        String query = "UPDATE expenses SET date = ?, amount = ?, category = ?, description = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setDate(1, Date.valueOf(expense.getDate()));
        stmt.setDouble(2, expense.getAmount());
        stmt.setString(3, expense.getCategory());
        stmt.setString(4, expense.getDescription());
        stmt.setInt(5, expense.getId());
        stmt.executeUpdate();
    }
    
    public void deleteExpense(Expense expense) throws SQLException {
        String query = "DELETE FROM expenses WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, expense.getId());
        stmt.executeUpdate();
    }
    
}
