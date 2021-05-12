package com.example.budgetbuddyapp.ui.addPurchase;

import android.content.Context;
import android.widget.ArrayAdapter;

public class HintAdapter extends ArrayAdapter<String> {

    public HintAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        int count = super.getCount();

        // Don't display last item. It is used as a hint.
        return count > 0 ? count - 1 : count;
    }
}
