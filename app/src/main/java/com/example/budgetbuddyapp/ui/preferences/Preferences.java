package com.example.budgetbuddyapp.ui.preferences;

public class Preferences {
    private float baseIncome;
    private float additionalIncome;
    private String name;

    public Preferences() {
        baseIncome = 0;
        additionalIncome = 0;
        name = "";
    }

    public Preferences(float baseIncome, float additionalIncome, String name) {
        this.baseIncome = baseIncome;
        this.additionalIncome = additionalIncome;
        this.name = name;
    }

    public float getBaseIncome() { return baseIncome; }

    public float getAdditionalIncome() { return additionalIncome; }

    public String getName() { return name; }
}
