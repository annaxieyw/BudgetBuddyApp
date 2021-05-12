package com.example.budgetbuddyapp.ui.discounts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.budgetbuddyapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubRecyclerViewAdapter extends RecyclerView.Adapter<SubRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Deal> deals = new ArrayList<Deal>();

    public SubRecyclerViewAdapter(Context mContext, ArrayList<Deal> deals) {
        this.mContext = mContext;
        this.deals = deals;
    }

    // here we inflate our sub recycler list item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_sub_recycler_discount, parent, false);
        return new ViewHolder(view);
    }

    // here we attach the data to our sub recycler list item (this is like getView for custom array adapters)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deal deal = deals.get(position);
        Glide.with(mContext).asBitmap().load(deal.getImage_url()).into(holder.disImg);       // Glide allows us to easily grab images from a url
        holder.disTitleTxt.setText(deal.getShort_title());

        holder.disImg.setOnClickListener(new View.OnClickListener() {       // when the image is clicked, open the url for the deal in a browser
            @Override
            public void onClick(View view) {
                String url = deal.getUrl();
                Intent openUrl = new Intent(Intent.ACTION_VIEW);
                openUrl.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                openUrl.setData(Uri.parse(url));
                mContext.startActivity(openUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    // this is what holds the individual list item layouts in memory
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView disImg;
        TextView disTitleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // grabbing references to the ImageView and TextView in our list item
            disImg = itemView.findViewById(R.id.disImg);
            disTitleTxt = itemView.findViewById(R.id.disTitleTxt);
        }
    }
}
