package com.example.budgetbuddyapp.ui.addPurchase;

public class Purchase {
    protected String name;
    protected String amount;
    protected String category;

    public Purchase() {
        name = "";
        amount = "";
        category = "";
    }

    public Purchase(String name, String amount, String category) {
        this.name = name;
        this.amount = amount;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() { return category; }
}
