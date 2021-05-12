package com.example.budgetbuddyapp.ui.purchaseHistory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.budgetbuddyapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseHistoryRecyclerViewAdapter extends RecyclerView.Adapter<PurchaseHistoryRecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Purchase> purchases = new ArrayList<>();

    String WHITE = "#FFFFFF";
    String GRAY = "#FFFAF8FD";

    public PurchaseHistoryRecyclerViewAdapter(Context mContext, ArrayList<Purchase> purchases) {
        this.mContext = mContext;
        this.purchases = purchases;
    }

    //here we inflate our purchase history list item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_history_recycler, parent, false);
        return new ViewHolder(view);
    }

    //here we attach the data to our purchase history list item (this is like getView for custom array adapters)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);
        holder.pHNameTxt.setText(purchase.getName());
        holder.pHDateTxt.setText(PurchaseHistoryFragment.makePrettyDateString(purchase.getDate()));
        holder.pHCategoryTxt.setText(purchase.getCategory());
        holder.pHAmountTxt.setText(purchase.getAmount());

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor(WHITE));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor(GRAY));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }

    //this is what holds the individual list item layouts in memory
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView pHNameTxt;
        TextView pHDateTxt;
        TextView pHCategoryTxt;
        TextView pHAmountTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //grabbing references to text views from our list item
            pHNameTxt = itemView.findViewById(R.id.pHNametxt);
            pHDateTxt = itemView.findViewById(R.id.pHDateTxt);
            pHCategoryTxt = itemView.findViewById(R.id.phCategoryTxt);
            pHAmountTxt = itemView.findViewById(R.id.phAmountTxt);
        }
    }
}