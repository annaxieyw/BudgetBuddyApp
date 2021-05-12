package com.example.budgetbuddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddyapp.ui.interest.InterestFragment;
import com.example.budgetbuddyapp.ui.preferences.PreferencesFragment;

public class RegistrationInterestActivity extends AppCompatActivity {
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_interest);

        // Create new fragment and transaction
        InterestFragment newFragment = new InterestFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.contentContainer3, newFragment);

        // Commit the transaction
        transaction.commitNow();

        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegistrationInterestActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
