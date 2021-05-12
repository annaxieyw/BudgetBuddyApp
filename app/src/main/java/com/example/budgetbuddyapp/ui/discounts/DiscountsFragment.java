package com.example.budgetbuddyapp.ui.discounts;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.budgetbuddyapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscountsFragment extends Fragment {

    RecyclerView recyclerView;
    SearchView searchView;
    MainRecyclerViewAdapter mainRecyclerViewAdapter;
    ArrayList<DealsObject> dealsObjects;        // this will store all of our lists of deals along with that list's title
    ArrayList<String> interests;        // this will store all of the user's interests as specified by the user in the interests page (will grab from firebase)

    private DatabaseReference userRef;      // reference to the user in firebase
    private double dailyBudget = 0;     // user's daily budget (will grab from firebase)

    private final String URL = "https://api.discountapi.com/v2/deals"; // this is the url we use for the api call which includes the api key I requested from discount api
    private final String ADD_CATEGORY = "&category_slugs=";       // needs to be concatenated with URL if we specify a category in our api call
    private final String ADD_ONLINE = "&online=true";              // needs to be concatenated with URL to specify we are only looking at online deals
    private final String ADD_KEY = "&api_key=kLZeIsES";
    private final String ADD_NUM_DEALS = "?per_page=10";
    private final int NUM_DEALS_PER_PAGE = 10; // this correlates with the per_page search parameter and will be concatenated with URL.
    private String ADD_LOCATION = "";  // this correlates with the location search parameter and will be concatenated with URL if the user presses the Location Call button

    private String SHARED_PREF = "SharedPref";      // string to grab our shared preferences
    private String USE_LOCATION = "UseLocation";       // string to grab the use location boolean
    boolean useLocation;        // set via sharedpreferences, if true we will grab deals that are close to the user's location
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discounts, container, false);

        //URL = URL + NUM_DEALS_PER_PAGE + ADD_ONLINE;  // the api call will give us NUM_DEALS_PER_PAGE discounts back and only do online deals

        dealsObjects = new ArrayList<DealsObject>();
        recyclerView = root.findViewById(R.id.recyclerView);
        searchView = root.findViewById(R.id.searchView);

        // setting up our recycler adapter for our nested lists
        mainRecyclerViewAdapter = new MainRecyclerViewAdapter(getActivity().getApplicationContext(), dealsObjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(mainRecyclerViewAdapter);

        // check shared preferences if the user wants to use their location
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        useLocation = sharedPreferences.getBoolean(USE_LOCATION, false);

        if (useLocation) {        // if the user said they want to use their location, we set up the location string which will be concatenated with URL
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
            setLocationString();
        }

        // grabbing the reference to our user in firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        String userId = mAuth.getUid();
        userRef = mDatabase.getReference().child("users").child(userId);

        // we need to use listeners whenever we grab data from firebase because it is done asynchronously
        // instead of async/await, we use nested callbacks (listeners) in order to ensure things happen in a particular order
        userRef.child("dailyBudget").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dailyBudget = Double.parseDouble(task.getResult().getValue().toString());
                DiscountsService discountsService = new DiscountsService(getActivity().getApplicationContext());      //discountServices holds all the functionality to make api calls

                // we make an api call to grab recommended deals
                discountsService.makeCall(URL + ADD_NUM_DEALS + ADD_KEY, "Today's Recommended Deals Based On Your Budget", false, dailyBudget, new DiscountsService.VolleyResponseListener() {
                    @Override
                    public void onResponse(ArrayList<Deal> recDeals, String title) {
                        // once we have the response, we add it to our arraylist of deals and update our adapter by calling populateView
                        populateView(recDeals, title);

                        // next, we get location-based deals if the user chose to use location
                        if (useLocation) {
                            discountsService.makeCall((URL + ADD_NUM_DEALS + ADD_KEY + ADD_LOCATION), "Deals Based On Your Location", false, dailyBudget, new DiscountsService.VolleyResponseListener() {
                                @Override
                                public void onResponse(ArrayList<Deal> locDeals, String title) {
                                    populateView(locDeals, title);
                                }
                            });
                        }

                        // now that we have the recommended deals shown, we want to get deals that are relevent to the user's interests
                        interests = new ArrayList<String>();        // just in case something goes wrong, set interests to a new empty array list
                        userRef.child("interests").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                try {
                                    interests = (ArrayList<String>) task.getResult().getValue();        // once we have the response, we need to save it as an ArrayList of Strings so we can work with it
                                    String interestURL;
                                    for (String interest : interests) {       // we need to make a new call for every interest
                                        interestURL = URL + ADD_NUM_DEALS + ADD_ONLINE + ADD_CATEGORY + interest + ADD_KEY;        // we need a different url for every interest
                                        discountsService.makeCall(interestURL, interest.substring(0, 1).toUpperCase() + interest.substring(1), true, dailyBudget, new DiscountsService.VolleyResponseListener() {

                                            @Override
                                            public void onResponse(ArrayList<Deal> interestDeals, String title) {
                                                // once we have the response, we add it to our arraylist of deals and update our adapter by calling populateView
                                                if (interestDeals.size() > 0) {
                                                    populateView(interestDeals, title);
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    Log.i("LOOK HERE", "did not get interests from firebase, error was " + e.toString());
                                }
                            }
                        });

                    }

                });
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Create new fragment and transaction
                Fragment discountsSearchFragment = new DiscountsSearchFragment();

                Bundle bundle = new Bundle();
                bundle.putString("search url", s);

                discountsSearchFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                transaction.replace(R.id.nav_host_fragment, discountsSearchFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return root;
    }

    // adds new deals to our view along with the title
    private void populateView(ArrayList<Deal> newDeals, String title) {
        DealsObject interestDealsObject = new DealsObject(title, newDeals);
        dealsObjects.add(interestDealsObject);
        mainRecyclerViewAdapter.notifyDataSetChanged();
    }

    // makeCall makes the request using Volley using specified URL.
    // Volley is an Android library which makes networking easier and faster. Network requests are done asynchronously and held in a queue.
    // Step 1: create a new request
    // Step 2: tell it what to do with the response
    //   2a: grab the JSON data
    //   2b: parse the JSON data
    // Step 3: add the request to the request queue
    // (base code taken from https://developer.android.com/training/volley/request)
    private void makeCall(String discountURL, RecyclerView.Adapter adapter) {
        ArrayList<Deal> deals = new ArrayList<Deal>();
        DealsObject dO = new DealsObject("ldkjadjksl", deals);
        dealsObjects.add(dO);

        // Step 1: Create a new request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, discountURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Step 2a: Grab the JSON data
                    JSONArray jDeals = response.getJSONArray("deals");  //we only grab the "deals" JSON Array

                    // Step 2b: Parse the JSON data
                    // for every JSON object deal in the array, we grab it and put it into a JSONObject and then use that info to make a Deal object.
                    for (int i = 0; i < NUM_DEALS_PER_PAGE; i++) {
                        JSONObject jDeal = jDeals.getJSONObject(i).getJSONObject("deal");
                        Deal deal = new Deal();
                        deal.setId(jDeal.getInt("id"));
                        deal.setTitle(jDeal.getString("title"));
                        deal.setShort_title(jDeal.getString("short_title"));
                        deal.setDescription(jDeal.getString("description"));
                        deal.setUrl(jDeal.getString("url"));
                        deal.setPrice(jDeal.getDouble("price"));
                        deal.setValue(jDeal.getDouble("value"));
                        deal.setDiscount_amount(jDeal.getDouble("discount_amount"));
                        deal.setImage_url(jDeal.getString("image_url"));

                        // if the user did a basic call, there is a high chance it's an online deal and there won't be a latitude and longitude
                        try {
                            JSONObject jMerchant = jDeal.getJSONObject("merchant");
                            deal.setLatitude(Double.toString(jMerchant.getDouble("latitude")));
                            deal.setLongitude(Double.toString(jMerchant.getDouble("longitude")));
                        } catch (JSONException e) {       // in the case there is no latitude and longitude, simply set them to N/A
                            deal.setLatitude("N/A");
                            deal.setLongitude("N/A");
                        }

                        Log.i("MINA LOOK", "Added deal");
                        dO.getDeals().add(deal);
                    }
                    // mainRecyclerViewAdapter.notifyDataSetChanged();
                    // notify the arrayAdapter so it can update listView
                } catch (JSONException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Volley error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        // Step 3: Add the request to the queue
        // The request has been created, but we need to add it to a RequestQueue in order to actually send the request.
        // We use a singleton class so we only ever make one RequestQueue. and any subsequent calls will just be added to the queue.
        RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }

    // setLocationString will get the last known location of the user and properly set the locationString so it can be concatenated to URL
    // (reference on how to get user location: https://www.youtube.com/watch?v=Ak1O9Gip-pg&ab_channel=AndroidCoding)
    private void setLocationString() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // if we have permission to get user location, grab the location
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {   // it is possible that location is null
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        ADD_LOCATION = "&location=" + lat + ", " + lon;   // this is just the correct way the URL should be formatted
                    }
                }
            });
        } else {
            // if we don't have permission to get user location, ask for it
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            setLocationString();
        }
    }
}
