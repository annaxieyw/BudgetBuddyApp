package com.example.budgetbuddyapp.ui.preferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PreferencesViewModel extends ViewModel {
    private MutableLiveData<Preferences> livePreferences;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private String userId;

    public PreferencesViewModel() {
        livePreferences = new MutableLiveData<>();
        livePreferences.setValue(new Preferences());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        userId = mAuth.getUid();
    }

    // writes first name to database
    public void writeName(String name) {
        mDatabase.getReference("users/" + userId + "/name").setValue(name);
    }

    // writes income to database
    public void writeIncome(Float income) {
        mDatabase.getReference("users/" + userId + "/baseIncome").setValue(income);
    }

    // writes additional income to database
    public void writeAdditionalIncome(Float additionalIncome) {
        mDatabase.getReference("users/" + userId + "/additionalIncome").setValue(additionalIncome);
    }

    // writes bill name to database
    public void writeBillName(String billName, int id) {
        mDatabase.getReference("users/" + userId + "/bills/" + id + "/name").setValue(billName);
    }

    // writes bill amount to database
    public void writeBillAmount(float billAmount, int id) {
        mDatabase.getReference("users/" + userId + "/bills/" + id + "/amount").setValue(billAmount);
    }

    // gets all the preferences
    public LiveData<Preferences> getPreferences() {
        mDatabase.getReference("users/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Preferences preferences = snapshot.getValue(Preferences.class);
                livePreferences.setValue(preferences);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The preferences read failed");
            }
        });

        return livePreferences;
    }
}