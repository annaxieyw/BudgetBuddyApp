package com.example.budgetbuddyapp.ui.interest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.budgetbuddyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InterestFragment extends Fragment {
    private static final String DISCOUNT_API_URL = "https://api.discountapi.com/v2/categories?api_key=kLZeIsES";

    private SwitchCompat locationPermission;
    private TableLayout tableLayoutInterests;
    private Button saveButton;
    private Button[][] buttonArray;

    private boolean storedLocation;
    private int NUM_PER_ROW, size;

    private ArrayList<String> userInterests;
    private ArrayList<Interest> categoryArrayList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private InterestService interestsService;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_interest, container, false);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        // initialize other stuff
        locationPermission = (SwitchCompat) root.findViewById(R.id.locationPermission);
        tableLayoutInterests = (TableLayout) root.findViewById(R.id.tableLayoutInterests);
        saveButton = (Button) root.findViewById(R.id.saveButton);
        NUM_PER_ROW = 2;
        userInterests = new ArrayList<>();
        interestsService = new InterestService(getActivity().getApplicationContext());
        retrieveSharedPreferenceInfo();

        interestsService.getInterests().observe(getViewLifecycleOwner(),
                new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> strings) {
                userInterests = strings;
                addButtons();
            }
        });

        // makes it so that if switch is on, shared preferences is updated
        locationPermission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSharedPreferenceInfo(isChecked);
            }
        });

        // when saved, writes selected categories to database and switches to main activity
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mAuth.getUid();
                mDatabase.getReference("users/" + userId + "/interests").setValue(userInterests);
            }
        });

        return root;
    }

    // adds the buttons with onClicks to the TableLayout along with new rows
    public void addButtons() {
        interestsService.makeCall(DISCOUNT_API_URL, new InterestService.VolleyResponseListener() {
            @Override
            public void onResponse(ArrayList<Interest> categories) {
                categoryArrayList = categories;
                tableLayoutInterests = (TableLayout) root.findViewById(R.id.tableLayoutInterests);
                size = categoryArrayList.size();
                // new button array for the buttons
                buttonArray = new Button[size][NUM_PER_ROW];
                // rounds number up to make sure it has room for all buttons
                int numRows = size / NUM_PER_ROW + ((size % NUM_PER_ROW == 0) ? 0 : 1);
                Button b;

                int count = 0;
                for (int i = 0; i < numRows; i++) {
                    //create new row
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT));

                    for (int j = 0; j < NUM_PER_ROW; j++) {
                        if (count < size) {
                            b = new Button(getContext());
                            b.setId(count);
                            tableRow.addView(b);

                            String name = categories.get(count).getName();
                            b.setText(name);
                            b.setWidth(450);
                            b.setHeight(200);
                            b.setTextSize(10);
                            b.setGravity(Gravity.CENTER);
                            b.setBackgroundColor(Color.GRAY);

                            for(String interest : userInterests){
                                if(categoryArrayList.get(count).getSlug().equals(interest)){
                                    b.setBackgroundColor(Color.GREEN);
                                }
                            }
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectCategory(v);
                                }
                            });
                            count++;
                        }
                    }
                    // add new row to the table layout
                    tableLayoutInterests.addView(tableRow);
                }
            }
        });
    }

    public void selectCategory(View v) {
        Button b = (Button) v;
        ColorDrawable background = (ColorDrawable) (b.getBackground());
        String interest = categoryArrayList.get(b.getId()).getSlug();

        if (background.getColor() == Color.GRAY) {      // color was off
            // add interest category to user's interests array
            userInterests.add(interest);
            b.setBackgroundColor(Color.GREEN);
        } else {     //color was on
            //run through user's interests array and delete the corresponding interest
            for (int i = 0; i < userInterests.size(); i++) {
                if (userInterests.get(i).equals(interest)) {
                    userInterests.remove(i);
                    break;
                }
            }
            b.setBackgroundColor(Color.GRAY);
        }
    }

    // set shared preference
    private void saveSharedPreferenceInfo(boolean UseLocation) {
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("UseLocation", UseLocation);
        editor.apply();
    }

    private void saveSharedPreferenceInfo(String userInterests) {
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences("userInterests", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userInterests", userInterests);
        editor.apply();
    }


    // get shared preferences
    private void retrieveSharedPreferenceInfo() {
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        if (sharedPref != null) {
            // Retrieving data from shared preferences hashmap.
            storedLocation = sharedPref.getBoolean("UseLocation", false);
            locationPermission.setChecked(storedLocation);
        }
    }
}