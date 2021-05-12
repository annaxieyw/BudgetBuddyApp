package com.example.budgetbuddyapp.ui.discounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddyapp.R;

import java.util.ArrayList;

public class DiscountsSearchFragment extends Fragment {
    private SearchView searchDiscountsSV;
    private RecyclerView searchRecyclerView;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;

    private ArrayList<Deal> deals;

    private final String URL = "https://api.discountapi.com/v2/deals"; // this is the url we use for the api call which includes the api key I requested from discount api
    private final String ADD_QUERY = "&query=";
    private final String ADD_ONLINE = "&online=true";              // needs to be concatenated with URL to specify we are only looking at online deals
    private final String ADD_KEY = "&api_key=kLZeIsES";
    private final String ADD_NUM_DEALS = "?per_page=10";
    private final int NUM_DEALS_PER_PAGE = 20; // this correlates with the per_page search parameter and will be concatenated with URL.

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discounts_search, container, false);

        searchDiscountsSV = root.findViewById(R.id.searchDiscountsSV);
        searchRecyclerView = root.findViewById(R.id.searchRecyclerView);
        deals = new ArrayList<Deal>();

        // LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false)
        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(root.getContext(), deals);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(searchRecyclerView.getContext(), RecyclerView.VERTICAL);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false));
        searchRecyclerView.addItemDecoration(dividerItemDecoration);
        searchRecyclerView.setAdapter(searchRecyclerViewAdapter);

        DiscountsService discountsService = new DiscountsService(getActivity().getApplicationContext());
        String query;
        Bundle bundle = getArguments();
        if (bundle != null) {
            query = bundle.getString("search url");
            String queryURL = buildURL(query);

            discountsService.makeCall(queryURL,"", false, 0, new DiscountsService.VolleyResponseListener() {
                @Override
                public void onResponse(ArrayList<Deal> newDeals, String title) {
                    populateView(newDeals);
                }
            });
        }

        searchDiscountsSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String queryURL = buildURL(s);
                discountsService.makeCall(queryURL,"", false, 0, new DiscountsService.VolleyResponseListener() {
                    @Override
                    public void onResponse(ArrayList<Deal> newDeals, String title) {
                        populateView(newDeals);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return root;
    }

    private String buildURL(String query){
        String q = URL + ADD_NUM_DEALS + ADD_QUERY + query + ADD_KEY + ADD_ONLINE;
        return q;
    }

    //adds new deals to our view along with the title
    private void populateView(ArrayList<Deal> newDeals){
        deals.clear();
        deals.addAll(newDeals);
        searchRecyclerViewAdapter.notifyDataSetChanged();
    }
}
