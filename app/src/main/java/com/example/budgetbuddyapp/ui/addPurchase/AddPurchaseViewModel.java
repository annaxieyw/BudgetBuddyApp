package com.example.budgetbuddyapp.ui.addPurchase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.json.JSONObject;

public class AddPurchaseViewModel extends ViewModel {

    private MutableLiveData<JSONObject> json;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public AddPurchaseViewModel() {
        json = new MutableLiveData<JSONObject>();
        json.setValue(null);
    }

    public void writePurchase(Purchase purchase, String date) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        String userId = mAuth.getUid();

        mDatabase.getReference().child("users").child(userId).child("purchases").child(date).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Find the next purchase ID
                    int dateId;
                    if (task.getResult().getValue() == null) {
                        dateId = 0;
                    } else {
                        dateId = ((int) task.getResult().getChildrenCount());
                    }

                    // Write the purchase to Firebase
                    mDatabase.getReference("users/" + userId + "/purchases/" + date + "/" + dateId).setValue(purchase);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MutableLiveData<JSONObject> getTextFromImage(String encImage) throws InterruptedException {
        final JSONObject[] jsonRetrieved = {null};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String base64 = "data:image/png;base64," + encImage;
                    JSONObject jsonbody = new JSONObject();
                    jsonbody.put("file_name", "invoice.png");
                    jsonbody.put("file_data", base64);

                    Unirest.setTimeouts(0, 0);
                    HttpResponse<String> response = Unirest.post("https://api.veryfi.com/api/v7/partner/documents/")
                            .header("CLIENT-ID", "vrfw8zBZzXiDtJR8dbaCa3HOSF5SOp27HUj5Xtf")
                            .header("AUTHORIZATION", "apikey zwe.bu:8c8d2c43c539c5c04acfb9c7ad734fd1")
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Cookie", "csrftoken=1jhaRaLepqwvaNL5w5blAskFb7vJOcmCYeAdpmRLs2UJZ8c7yfQEMFaBrYGMRBvX")
                            .body(jsonbody.toString())
                            .asString();

                    jsonRetrieved[0] = new JSONObject(response.getBody());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();

        json.setValue(jsonRetrieved[0]);

        return json;
    }
}
