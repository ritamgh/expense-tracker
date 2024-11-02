package com.expensetracker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    public void testAppInitialization() {
        // Simple test to check if the application initializes without errors
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }

    @Test
    public void testExpenseCreation() {
        Expense expense = new Expense(
                java.time.LocalDate.now(),
                100.0,
                "Food",
                "Lunch at restaurant"
        );
        assertEquals(100.0, expense.getAmount());
        assertEquals("Food", expense.getCategory());
        assertEquals("Lunch at restaurant", expense.getDescription());
    }

    // Add more tests as needed
}
