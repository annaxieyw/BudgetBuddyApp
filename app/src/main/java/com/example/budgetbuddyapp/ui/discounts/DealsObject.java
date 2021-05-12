package com.example.budgetbuddyapp.ui.discounts;

import java.util.ArrayList;

// A DealsObject object will hold the list of deals and a title which shows what "kind" of deals they are (either recommended
// deals which are based on the user's budget or deals that are based on the user's interests)
public class DealsObject {

    private String title;
    private ArrayList<Deal> deals;

    public DealsObject() {
        title = "";
        deals = new ArrayList<Deal>();
    }

    public DealsObject(String title, ArrayList<Deal> deals) {
        this.title = title;
        this.deals = deals;
    }

    public ArrayList<Deal> getDeals() {
        return deals;
    }

    public void setDeals(ArrayList<Deal> deals) {
        this.deals = deals;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

