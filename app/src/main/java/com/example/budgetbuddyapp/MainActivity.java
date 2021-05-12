package com.example.budgetbuddyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String dayStored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrieveSharedPreferenceInfo();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_settings, R.id.navigation_home, R.id.navigation_purchase_history, R.id.navigation_discounts)
                .build();

        if (dayStored == null) {
            SimpleDateFormat SDF = new SimpleDateFormat("MMddyyyy", Locale.US);
            dayStored = (SDF.format(Calendar.getInstance().getTime()));
        }

        boolean newDay = isNewDay();
        boolean newMonth = isNewMonth();

        Bundle bundle = new Bundle();
        bundle.putBoolean("newDay", newDay);
        bundle.putBoolean("newMonth", newMonth);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation, bundle);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private boolean isNewDay() {
        SimpleDateFormat SDF = new SimpleDateFormat("MMddyyyy", Locale.US);
        String today = (SDF.format(Calendar.getInstance().getTime()));

        if (dayStored.equals(today)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isNewMonth() {
        SimpleDateFormat SDF = new SimpleDateFormat("MMddyyyy", Locale.US);
        String today = (SDF.format(Calendar.getInstance().getTime()));

        if (dayStored.equals(today)) {
            return false;
        } else if (today.substring(2, 4).equals("01")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveSharedPreferenceInfo();
    }

    private void saveSharedPreferenceInfo() {
        // Refer to the SharedPreference Object.
        SharedPreferences appInfo = getSharedPreferences("mainActivityInfo", Context.MODE_PRIVATE);

        // Create an Shared Preferences Editor for Editing Shared Preferences.
        SharedPreferences.Editor editor = appInfo.edit();

        // Store what's important!
        SimpleDateFormat SDF = new SimpleDateFormat("MMddyyyy", Locale.US);
        String today = (SDF.format(Calendar.getInstance().getTime()));

        editor.putString("day", today);

        // Save your information.
        editor.apply();
    }


    private void retrieveSharedPreferenceInfo(){
        SharedPreferences appInfo = getSharedPreferences("mainActivityInfo", Context.MODE_PRIVATE);

        if (appInfo != null) {
            // Retrieving data from shared preferences hashmap.
            dayStored = appInfo.getString("day", "");
        }
    }
}