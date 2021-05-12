package com.example.budgetbuddyapp.ui.home;

public class Budget {
    private int additionalIncome;
    private int baseIncome;
    private int dailyBudget;

    public Budget() {
        additionalIncome = 0;
        baseIncome = 0;
        dailyBudget = 0;
    }

    public Budget(int additionalIncome, int baseIncome, int dailyBudget) {
        this.additionalIncome = additionalIncome;
        this.baseIncome = baseIncome;
        this.dailyBudget = dailyBudget;
    }

    public int getAdditionalIncome() {
        return additionalIncome;
    }

    public int getBaseIncome() {
        return baseIncome;
    }

    public int getDailyBudget() { return dailyBudget; }

    public int getTotalIncome() {
        return (additionalIncome + baseIncome);
    }
}
