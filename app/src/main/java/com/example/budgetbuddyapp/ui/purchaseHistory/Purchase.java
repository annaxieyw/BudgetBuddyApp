package com.example.budgetbuddyapp.ui.purchaseHistory;

public class Purchase {
    private String date;
    private String name;
    private String amount;
    private String category;

    public Purchase() {
        date = "";
        name = "";
        amount = "";
        category = "";
    }

    public Purchase(String date, String name, String amount, String category) {
        this.date = date;
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

    public void setAmount(String amount){
        this.amount = amount;
    }

    public String getDate() { return date; }

    public String toString() {
        return "Date: " + date + "\nName: " + name + "\nAmount: " + amount + "\nCategory: " + category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}