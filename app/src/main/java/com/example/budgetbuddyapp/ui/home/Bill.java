package com.example.budgetbuddyapp.ui.home;

public class Bill {
    private String name;
    private int amount;

    public Bill() {
        name = "";
        amount = 0;
    }

    public Bill(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
