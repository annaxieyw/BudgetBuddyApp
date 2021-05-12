package com.example.budgetbuddyapp.ui.discounts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddyapp.R;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<DealsObject> dealsObjects = new ArrayList<DealsObject>();

    public MainRecyclerViewAdapter(Context mContext, ArrayList<DealsObject> dealsObjects) {
        this.mContext = mContext;
        this.dealsObjects = dealsObjects;
    }

    // here we inflate our main recycler list item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_main_recycler, parent, false);
        return new ViewHolder(view);
    }

    // here we attach the data to our main recycler list item (this is like getView for custom array adapters)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // getting the corresponding values
        DealsObject dealsObject = dealsObjects.get(position);
        holder.listTitleTxt.setText(dealsObject.getTitle());

        // setting up the sub list
        RecyclerView recyclerView = holder.dealsListRecyclerView;
        SubRecyclerViewAdapter subRecyclerViewAdapter = new SubRecyclerViewAdapter(mContext, dealsObject.getDeals());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(subRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return dealsObjects.size();
    }

    // this is what holds the individual list item layouts in memory
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView listTitleTxt;
        RecyclerView dealsListRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // grabbing references to the ImageView and TextView in our list item
            listTitleTxt = itemView.findViewById(R.id.listTitleTxt);
            dealsListRecyclerView = itemView.findViewById(R.id.dealsListRecyclerView);
        }
    }
}
