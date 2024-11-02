package com.expensetracker;

import java.time.LocalDate;

public class Expense {
    private int id;
    private LocalDate date;
    private double amount;
    private String category;
    private String description;

    // Constructors
    public Expense(int id, LocalDate date, double amount, String category, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public Expense(LocalDate date, double amount, String category, String description) {
        this(0, date, amount, category, description);
    }

    // Getters
    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    // Setters (if needed)
    public void setId(int id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
