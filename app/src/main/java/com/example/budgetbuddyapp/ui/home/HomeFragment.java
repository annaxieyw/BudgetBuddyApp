package com.example.budgetbuddyapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.ui.addPurchase.AddPurchaseFragment;
import com.example.budgetbuddyapp.ui.purchaseHistory.Purchase;
import com.example.budgetbuddyapp.ui.purchaseHistory.PurchaseHistoryViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private PurchaseHistoryViewModel purchaseHistoryViewModel;

    private TextView txtDailyBudget;
    private Button btnAddPurchase, btnAddIncome, btnViewPurchaseHistory;

    private boolean newDay, newMonth;
    private int income, dailyBudget;
    private ArrayList<Bill> bills;
    private ArrayList<Purchase> purchases;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        purchaseHistoryViewModel = new ViewModelProvider(this)
                .get(PurchaseHistoryViewModel.class);

        txtDailyBudget = root.findViewById(R.id.txtDailyBudget);
        btnAddPurchase = root.findViewById(R.id.btnAddPurchase);
        btnAddIncome = root.findViewById(R.id.btnAddIncome);
        btnViewPurchaseHistory = root.findViewById(R.id.btnViewPurchaseHistory);

        // Get the boolean determining if user is opening the app on a new day or a new month
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            newDay = bundle.getBoolean("newDay");
            newMonth = bundle.getBoolean("newMonth");
        } else if (savedInstanceState != null) {
            newDay = savedInstanceState.getBoolean("newDay");
            newMonth = savedInstanceState.getBoolean("newMonth");
        } else {
            newDay = false;
            newMonth = false;
        }

        // Get all the necessary parts needed to calculated the remaining daily budget accurately
        homeViewModel.getIncome(newMonth).observe(getViewLifecycleOwner(), new Observer<Budget>() {
            @Override
            public void onChanged(Budget budget) {
                income = budget.getTotalIncome();
                dailyBudget = budget.getDailyBudget();

                homeViewModel.getBills().observe(getViewLifecycleOwner(),
                        new Observer<ArrayList<Bill>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Bill> allBills) {
                        bills = allBills;

                        purchaseHistoryViewModel.getPurchaseHistory()
                                .observe(getViewLifecycleOwner(), new Observer<ArrayList<Purchase>>() {
                            @Override
                            public void onChanged(@Nullable ArrayList<Purchase> allPurchases) {
                                purchases = allPurchases;

                                homeViewModel.getRemainingDailyBudget(income, dailyBudget,
                                        purchases, bills, newDay).observe(getViewLifecycleOwner(),
                                        new Observer<String>() {
                                    @Override
                                    public void onChanged(@Nullable String s) {
                                        txtDailyBudget.setText(s);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        btnAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment addPurchaseFragment = new AddPurchaseFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.nav_host_fragment, addPurchaseFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        btnAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setSelectedItemId(R.id.navigation_settings);
            }
        });

        btnViewPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setSelectedItemId(R.id.navigation_purchase_history);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Once the user has arrived on this page, we've calculated we can set the newDay and
        // newMonth to false as we've already made the necessary calculations
        outState.putBoolean("newDay", false);
        outState.putBoolean("newMonth", false);
    }
}