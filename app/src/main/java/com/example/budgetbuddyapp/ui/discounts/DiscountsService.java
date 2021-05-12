package com.example.budgetbuddyapp.ui.discounts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscountsService {

    Context mContext;

    public DiscountsService(Context mContext) {
        this.mContext = mContext;
    }

    //makeCall will make the api call to discount api, create a list of deals, and then send back that list through a volleyResponseListener.
    //Data can't just be returned via a return statement because calls are done asynchronously, so Java will jump to the return statement while call works in the background.
    //this means when we get to the return statement, the response hasn't actually come back yet, so we'd be returning an empty object.
    //the parameters for makeCall are as follows:
    //discountURL: the String url we use to make the api call
    //title: the title of the list of deals we are getting (this is what is shown above the vertical list of deals in the app)
    //limitPrice: whether or not this list of deals we are getting should be limited to deals within the daily budget (also passed as a parameter)
    //dailyBudget: if limitPrice = false, we limit the deals to those which are within the user's budget
    //volleyResponseListener: we need a listener because the api calls are done asynchronously, and we need a way to send the data back once the response is received.
    public void makeCall(String discountURL, String title, boolean limitPrice, double dailyBudget, VolleyResponseListener volleyResponseListener){
        ArrayList<Deal> deals = new ArrayList<Deal>();

        //We use Volley to handle api requests.
        //Volley is an Android library which makes networking easier and faster. Network requests are done asynchronously and held in a queue.
        //Step 1: create a new request
        //Step 2: tell it what to do with the response
        //   2a: grab the JSON data
        //   2b: parse the JSON data
        //Step 3: add the request to the request queue
        //(base code taken from https://developer.android.com/training/volley/request)

        //Step 1: Create a new request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, discountURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Step 2a: Grab the JSON data
                    JSONArray jDeals = response.getJSONArray("deals");  //we only grab the "deals" JSON Array
                    int nD = jDeals.length();

                    //Step 2b: Parse the JSON data
                    //for every JSON object deal in the array, we grab it and put it into a JSONObject and then use that info to make a Deal object.
                    //it could be a case that there is not numDeals deals. This is more likely for calls that have a specific query
                    for (int i = 0; i < nD; i++) {
                        Log.i("MINA LOOK", "Adding deal");
                        try{
                            JSONObject jDeal = jDeals.getJSONObject(i).getJSONObject("deal");
                            if((!limitPrice) || (jDeal.getDouble("price") <= dailyBudget)){      //if limitPrice=true and the deal is not within the daily budget, don't add that deal
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
                                try{
                                    JSONObject jMerchant = jDeal.getJSONObject("merchant");
                                    deal.setLatitude(Double.toString(jMerchant.getDouble("latitude")));
                                    deal.setLongitude(Double.toString(jMerchant.getDouble("longitude")));
                                }catch (JSONException e){       //in the case there is no latitude and longitude, simply set them to N/A
                                    deal.setLatitude("N/A");
                                    deal.setLongitude("N/A");
                                }
                                Log.i("MINA LOOK", "Added deal");
                                deals.add(deal);    //add the new Deal to our deals list so it can later populate our ListView
                            }
                        }catch (Exception e){
                            Log.i("MINA LOOK", "out of bounds for search");
                        }

                    }
                    Log.i("MINA LOOK", "in service, deals is of length " + deals.size());
                    //once we've gotten all our deals, notify the listener and send the data
                    volleyResponseListener.onResponse(deals, title);

                } catch (JSONException e) {
                    Toast.makeText(mContext, "JSON error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "Volley error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        /*  //this is in case discount api is slow
        request.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        */

        //Step 3: Add the request to the queue
        //The request has been created, but we need to add it to a RequestQueue in order to actually send the request.
        //We use a singleton class so we only ever make one RequestQueue. and any subsequent calls will just be added to the queue.
        RequestQueueSingleton.getInstance(mContext).addToRequestQueue(request);

    }


    //this is the interface for our listener. onRecResponse is called if the response is for recommended deals. onInterestResponse
    //is called if the response is for deals based on user interests. This is separated because
    public interface VolleyResponseListener {
        void onResponse(ArrayList<Deal> recDeals, String title);
    }
}