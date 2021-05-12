package com.example.budgetbuddyapp.ui.purchaseConfirmation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.ui.addPurchase.AddPurchaseFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PurchaseConfirmationFragment extends Fragment {
    private static final String DISPLAY_FORMAT = "MM/dd/yyyy";

    private TextView txtDate;
    private TextView txtName;
    private TextView txtAmount;
    private TextView txtCategory;

    private Button btnPurchaseHistory;
    private Button btnAddPurchase;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_purchase_confirmation, container, false);

        txtDate = (TextView) root.findViewById(R.id.txtDate);
        txtName = (TextView) root.findViewById(R.id.txtName);
        txtAmount = (TextView) root.findViewById(R.id.txtAmount);
        txtCategory = (TextView) root.findViewById(R.id.txtCategory);

        btnPurchaseHistory = (Button) root.findViewById(R.id.btnPurchaseHistory);
        btnAddPurchase = (Button) root.findViewById(R.id.btnAddPurchase);

        Bundle bundle = this.getArguments(); //this means get me the bundle attached, to the intent that spawned me.
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        String category = bundle.getString("category");
        String amount = bundle.getString("amount");

        SimpleDateFormat JSONDateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
        try {
            Date jsonDate = JSONDateFormat.parse(date);

            SimpleDateFormat SDF = new SimpleDateFormat(DISPLAY_FORMAT, Locale.US);
            txtDate.setText("Date: " + SDF.format(jsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtName.setText("Name: " + name);
        txtAmount.setText("Amount: " + amount);
        txtCategory.setText("Category: " + category);

        btnPurchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
                navView.setSelectedItemId(R.id.navigation_purchase_history);
            }
        });

        btnAddPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new fragment and transaction
                Fragment addPurchaseFragment = new AddPurchaseFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment, and add the transaction to the back stack
                transaction.replace(R.id.nav_host_fragment, addPurchaseFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return root;
    }
}