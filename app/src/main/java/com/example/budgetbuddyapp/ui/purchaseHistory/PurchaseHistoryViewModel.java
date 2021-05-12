package com.example.budgetbuddyapp.ui.purchaseHistory;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PurchaseHistoryViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Purchase>> purchaseHistory;
    private Integer totalBills;
    private int tb;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public PurchaseHistoryViewModel() {
        purchaseHistory = new MutableLiveData<>();
        purchaseHistory.setValue(new ArrayList<>());
        totalBills = new Integer(0);
        tb = 0;

    }

    public LiveData<ArrayList<Purchase>> getPurchaseHistory() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        String userId = mAuth.getUid();

        mDatabase.getReference().child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<Purchase> ph = new ArrayList<>();
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Iterable<DataSnapshot> bills = task.getResult().child("bills").getChildren();
                    for (DataSnapshot d : bills) {
                        String a = d.child("amount").getValue().toString();
                        tb += Integer.parseInt(a);
                    }
                    totalBills = new Integer(tb);

                    Iterable<DataSnapshot> purch = task.getResult().child("purchases").getChildren();
                    for (DataSnapshot d : purch) {
                        String date = d.getKey();

                        for (DataSnapshot p : d.getChildren()) {
                            String name = p.child("name").getValue().toString();
                            String amount = p.child("amount").getValue().toString();
                            String category = p.child("category").getValue().toString();

                            Purchase purchase = new Purchase(date, name, amount, category);
                            ph.add(0, purchase);
                        }
                    }
                    //workaround to send back the total bill amount as well
                    Purchase totalBillPurchase = new Purchase("dummy", "dummy", totalBills.toString(), "dummy");
                    ph.add(totalBillPurchase);
                    purchaseHistory.setValue(ph);
                }
            }
        });

        return purchaseHistory;
    }
}