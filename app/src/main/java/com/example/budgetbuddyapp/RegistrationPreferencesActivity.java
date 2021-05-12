package com.example.budgetbuddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.example.budgetbuddyapp.ui.home.Bill;
import com.example.budgetbuddyapp.ui.preferences.Preferences;
import com.example.budgetbuddyapp.ui.preferences.PreferencesFragment;

import java.util.ArrayList;

public class RegistrationPreferencesActivity extends AppCompatActivity {
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_preferences);

        btnNext = (Button) findViewById(R.id.btnNext);

        // Create new fragment and transaction
        PreferencesFragment newFragment = new PreferencesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.contentContainer2, newFragment);

        // Commit the transaction
        transaction.commitNow();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.contentContainer2);
        if (currentFragment instanceof PreferencesFragment) {
            PreferencesFragment preferencesFragment = (PreferencesFragment) currentFragment;
            preferencesFragment.setCreateViewCallback(new PreferencesFragment.OnCreateViewCallback() {
                @Override
                public void onCreateView() {
                    preferencesFragment.setFirstNameTitle(getApplicationContext().getResources().getString(R.string.setup_fname));
                    preferencesFragment.setIncomeTitle(getApplicationContext().getResources().getString(R.string.setup_income));
                    preferencesFragment.showAdditionalIncome(false);
                    preferencesFragment.setBillTitle(getApplicationContext().getResources().getString(R.string.setup_bills));
                }
            });
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes to next screen when done
                if (currentFragment instanceof PreferencesFragment) {
                    PreferencesFragment preferencesFragment = (PreferencesFragment) currentFragment;

                    if (preferencesFragment.updateUser()) {
                        Intent intent = new Intent(RegistrationPreferencesActivity.this, RegistrationInterestActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}