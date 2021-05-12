package com.example.budgetbuddyapp.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budgetbuddyapp.ui.purchaseHistory.Purchase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeViewModel extends ViewModel {
    private static final String FIREBASE_FORMAT = "MMddyyyy";

    private MutableLiveData<String> txtDailyBudget;
    private MutableLiveData<ArrayList<Bill>> liveBills;
    private MutableLiveData<Budget> liveIncome;
    private MutableLiveData<Integer> liveDailyBudget;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private String userId;

    public HomeViewModel() {
        txtDailyBudget = new MutableLiveData<>();
        txtDailyBudget.setValue("Loading...");

        liveBills = new MutableLiveData<>();
        liveBills.setValue(new ArrayList<Bill>());

        liveIncome = new MutableLiveData<>();
        liveIncome.setValue(new Budget());

        liveDailyBudget = new MutableLiveData<>();
        liveDailyBudget.setValue(0);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        userId = mAuth.getUid();
    }

    public float updateDailyBudget(float income, ArrayList<Purchase> purchases, ArrayList<Bill> bills) {
        // Grab today's date as a string
        SimpleDateFormat SDF = new SimpleDateFormat(FIREBASE_FORMAT, Locale.US);
        String today = (SDF.format(Calendar.getInstance().getTime()));

        float dailyBudget = income;

        // Subtract each bill amount from the income
        for (Bill b : bills) {
            dailyBudget -= b.getAmount();
        }

        // Subtract each purchase made within the month but not on today
        for (Purchase p : purchases) {
            if (p.getDate().substring(0, 2).equals(today.substring(0, 2))
                    && !p.getDate().substring(0, 4).equals(today.substring(0, 4))) {
                dailyBudget -= Integer.parseInt(p.getAmount());
            }
        }

        // Divide the remaining spendable income by the number of days left in the month
        Calendar calendar = Calendar.getInstance();
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int daysLeft = lastDay - currentDay;
        dailyBudget /= daysLeft;

        // Set the dailyBudget in Firebase
        mDatabase.getReference("users/" + userId + "/dailyBudget").setValue(dailyBudget);

        return dailyBudget;
    }

    public LiveData<String> getRemainingDailyBudget(float income, float dailyBudget,
                                                    ArrayList<Purchase> purchases,
                                                    ArrayList<Bill> bills, boolean newDay) {

        // If the user has opened the app for the first time today, recalculate the daily budget
        // to include purchases they made yesterday
        if (newDay) {
            dailyBudget = updateDailyBudget(income, purchases, bills);
        }

        float remainingDailyBudget = dailyBudget;

        SimpleDateFormat SDF = new SimpleDateFormat(FIREBASE_FORMAT, Locale.US);
        String today = (SDF.format(Calendar.getInstance().getTime()));

        // Subtract all the purchases they've already made today from their dailyBudget
        for (Purchase p : purchases) {
            if (p.getDate().substring(0, 4).equals(today.substring(0, 4))) {
                remainingDailyBudget -= Double.parseDouble(p.getAmount());
            }
        }

        // Display their remainingDailyBudget
        txtDailyBudget.setValue("$" + String.format("%.2f", (Float.valueOf(remainingDailyBudget))));

        return txtDailyBudget;
    }

    public LiveData<Budget> getIncome(boolean newMonth) {
        mDatabase.getReference("users/" + userId).addValueEventListener(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // If it's a new month, the additionalIncome should be reset to 0
                if (newMonth) {
                    mDatabase.getReference("users/" + userId +
                            "/additionalIncome").setValue(0);
                }

                Budget budget = snapshot.getValue(Budget.class);

                liveIncome.setValue(budget);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed");
            }
        });

        return liveIncome;
    }

    public LiveData<ArrayList<Bill>> getBills() {
        mDatabase.getReference().child("users").child(userId).child("bills").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting bill data", task.getException());
                } else {
                    ArrayList<Bill> allBills = new ArrayList<>();

                    for (DataSnapshot b : task.getResult().getChildren()) {
                        String name = b.child("name").getValue().toString();
                        int amount = Integer.parseInt(b.child("amount").getValue().toString());

                        Bill bill = new Bill(name, amount);
                        allBills.add(0, bill);
                    }

                    liveBills.setValue(allBills);
                }
            }
        });

        return liveBills;
    }
}