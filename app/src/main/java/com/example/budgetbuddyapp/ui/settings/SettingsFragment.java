package com.example.budgetbuddyapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.ui.home.Bill;
import com.example.budgetbuddyapp.ui.home.HomeViewModel;
import com.example.budgetbuddyapp.ui.interest.InterestFragment;
import com.example.budgetbuddyapp.ui.preferences.Preferences;
import com.example.budgetbuddyapp.ui.preferences.PreferencesFragment;
import com.example.budgetbuddyapp.ui.preferences.PreferencesViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {
    private PreferencesViewModel preferencesViewModel;
    private HomeViewModel homeViewModel;

    private Button btnDone;
    private Button btnInterests;

    private ArrayList<Bill> bills;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        btnDone = (Button) root.findViewById(R.id.btnDone);
        btnInterests = (Button) root.findViewById(R.id.btnInterests);

        // Create new fragment and transaction
        PreferencesFragment newFragment = new PreferencesFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.contentContainer, newFragment);

        // Commit the transaction
        transaction.commitNow();

        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.contentContainer);
        if (currentFragment instanceof PreferencesFragment) {
            PreferencesFragment preferencesFragment = (PreferencesFragment) currentFragment;
            preferencesFragment.setCreateViewCallback(new PreferencesFragment.OnCreateViewCallback() {
                @Override
                public void onCreateView() {
                    preferencesFragment.setFirstNameTitle(getActivity().getResources().getString(R.string.change_fname));
                    preferencesFragment.setIncomeTitle(getActivity().getResources().getString(R.string.change_income));
                    preferencesFragment.showAdditionalIncome(true);
                    preferencesFragment.setBillTitle(getActivity().getResources().getString(R.string.change_bills));

                    homeViewModel.getBills().observe(getViewLifecycleOwner(), new Observer<ArrayList<Bill>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<Bill> allBills) {
                            bills = allBills;

                            preferencesFragment.setBills(bills);

                            preferencesViewModel.getPreferences().observe(getViewLifecycleOwner(), new Observer<Preferences>() {
                                @Override
                                public void onChanged(Preferences pref) {
                                    float baseIncome = pref.getBaseIncome();
                                    float additionalIncome = pref.getAdditionalIncome();
                                    String name = pref.getName();

                                    preferencesFragment.setName(name);
                                    preferencesFragment.setIncome(baseIncome);
                                    preferencesFragment.setAdditionalIncome(additionalIncome);
                                }
                            });
                        }
                    });
                }
            });
        }

        btnInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.contentContainer);
                if (currentFragment instanceof PreferencesFragment) {
                    PreferencesFragment preferencesFragment = (PreferencesFragment) currentFragment;

                    if (preferencesFragment.updateUser()) {
                        // Create new fragment and transaction
                        Fragment interestsFragment = new InterestFragment();
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                        transaction.replace(R.id.contentContainer, interestsFragment);
                        transaction.addToBackStack(null);

                        // Commit the transaction
                        transaction.commit();
                        getChildFragmentManager().executePendingTransactions();

                        btnInterests.setVisibility(View.GONE);
                    }
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes to next screen when done
                Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.contentContainer);
                if (currentFragment instanceof PreferencesFragment) {
                    PreferencesFragment preferencesFragment = (PreferencesFragment) currentFragment;

                    if (preferencesFragment.updateUser()) {
                        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                        navView.setSelectedItemId(R.id.navigation_home);
                    }
                } else {
                    BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                    navView.setSelectedItemId(R.id.navigation_home);
                }
            }
        });

        return root;
    }
}