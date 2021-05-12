package com.example.budgetbuddyapp.ui.interest;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.budgetbuddyapp.ui.discounts.RequestQueueSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class InterestService {
    private Context mContext;

    private MutableLiveData<ArrayList<String>> userInterests;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public InterestService(Context mContext) {
        userInterests = new MutableLiveData<ArrayList<String>>();
        this.mContext = mContext;
    }

    public LiveData<ArrayList<String>> getInterests(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        String userId = mAuth.getUid();
        ArrayList<String> uIArray = new ArrayList<>();
        mDatabase.getReference().child("users").child(userId).child("interests").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebaseError", "Error getting data", task.getException());
                }
                else {
                    Log.e("firebaseError", "no firebase error");
                    Iterable<DataSnapshot> interests = task.getResult().getChildren();
                    for (DataSnapshot s : interests) {
                        uIArray.add(s.getValue().toString());
                    }
                }
            }
        });
        userInterests.setValue(uIArray);
        return userInterests;
    }

    // call api, parse json
    public void makeCall(String url, VolleyResponseListener volleyResponseListener) {
        ArrayList<Interest> interests = new ArrayList<Interest>();

        // Step 1: Create a new request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Step 2a: Grab the JSON data
                    // we only grab the "categories" JSON Array
                    JSONArray jInterests = response.getJSONArray("categories");
                    int length = jInterests.length();

                    // Step 2b: Parse the JSON data
                    // for every JSON object interest in the array, we grab it and put it into
                    // a JSONObject and then use that info to make an Interest object.
                    for (int i = 0; i < length; i++) {
                        JSONObject jInterest = jInterests.getJSONObject(i).getJSONObject("category");
                        Interest interest = new Interest();
                        interest.setSlug(jInterest.getString("slug"));
                        interest.setName(jInterest.getString("name"));

                        try {
                            interest.setParent_slug(jInterest.getString("parent_slug"));
                        } catch (JSONException e) {
                            interest.setParent_slug("");
                        }
                        interests.add(interest);
                    }

                    volleyResponseListener.onResponse(interests);
                } catch (JSONException e) {
                    Log.i("json error", "JSON ERROR");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley error", "VOLLEY ERROR");
            }
        });

        //Step 3: Add the request to the queue
        //The request has been created, but we need to add it to a RequestQueue in order to actually send the request.
        //We use a singleton class so we only ever make one RequestQueue. and any subsequent calls will just be added to the queue.
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);
    }

    //this is the interface for our listener
    public interface VolleyResponseListener {
        void onResponse(ArrayList<Interest> categories);
    }
}