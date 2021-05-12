package com.example.budgetbuddyapp.ui.preferences;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.ui.home.Bill;
import com.example.budgetbuddyapp.ui.home.HomeViewModel;
import com.example.budgetbuddyapp.ui.purchaseHistory.Purchase;
import com.example.budgetbuddyapp.ui.purchaseHistory.PurchaseHistoryViewModel;

import java.util.ArrayList;

public class PreferencesFragment extends Fragment {

    // Create a callback listener so that we have a way of knowing when the fragment is done
    // being created in contexts that this fragment is being used in
    protected OnCreateViewCallback createViewCallback = null;

    public void setCreateViewCallback(OnCreateViewCallback createViewCallback) {
        this.createViewCallback = createViewCallback;
    }

    public interface OnCreateViewCallback {
        void onCreateView();
    }

    private PreferencesViewModel preferencesViewModel;
    private PurchaseHistoryViewModel purchaseHistoryViewModel;
    private HomeViewModel homeViewModel;

    // visible fields
    private EditText firstNameEnter, incomeEnter, additionalIncomeEnter;
    private Button addBillButton, addAdditionalIncomeButton, subtractAdditionalIncomeButton;
    private TableLayout tableLayout;
    private TextView firstNameTitle, incomeTitle, billTitle, additionalIncomeTitle, additionalIncomeTotal;

    private float currAdditionalIncome;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preferences, container, false);

        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        purchaseHistoryViewModel = new ViewModelProvider(this)
                .get(PurchaseHistoryViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // initialize TableLayout
        tableLayout = (TableLayout) root.findViewById(R.id.tableLayout);

        // initialize EditTexts
        firstNameEnter = (EditText) root.findViewById(R.id.firstNameEnter);
        incomeEnter = (EditText) root.findViewById(R.id.incomeEnter);
        additionalIncomeEnter = (EditText) root.findViewById(R.id.additionalIncomeEnter);

        // initialize TextViews
        firstNameTitle = (TextView) root.findViewById(R.id.firstNameTitle);
        incomeTitle = (TextView) root.findViewById(R.id.incomeTitle);
        additionalIncomeTitle = (TextView) root.findViewById(R.id.additionalIncomeTitle);
        additionalIncomeTotal = (TextView) root.findViewById(R.id.additionalIncomeTotal);
        billTitle = (TextView) root.findViewById(R.id.billTitle);

        // initialize Buttons
        addBillButton = (Button) root.findViewById(R.id.addBillButton);
        addAdditionalIncomeButton = (Button) root.findViewById(R.id.addAdditionalIncomeButton);
        subtractAdditionalIncomeButton = (Button) root.findViewById(R.id.subtractAdditionalIncomeButton);


        addAdditionalIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float additionalIncome = Float.valueOf(additionalIncomeEnter.getText().toString());
                currAdditionalIncome = currAdditionalIncome + additionalIncome;

                doAdditionalIncome();
            }
        });

        subtractAdditionalIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float additionalIncome = Float.valueOf(additionalIncomeEnter.getText().toString());
                currAdditionalIncome = currAdditionalIncome - additionalIncome;

                doAdditionalIncome();
            }
        });

        // onClickListener for add bill button
        addBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create row for bill name
                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.WRAP_CONTENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                // Create new row fields for entering bill
                EditText billName = new EditText(getContext());
                billName.setHint("Bill name");
                billName.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                EditText billAmount = new EditText(getContext());
                billAmount.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                billAmount.setHint("Amount");
                billAmount.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                // add bill row to table
                tableRow.addView(billName);
                tableRow.addView(billAmount);
                tableLayout.addView(tableRow);
            }
        });

        if (createViewCallback != null) {
            createViewCallback.onCreateView();
            createViewCallback = null;
        }

        return root;
    }

    // need to have these setter functions so that the titles can change depending on
    // where the fragment is being hosted
    public void setFirstNameTitle(String title) {
        firstNameTitle.setText(title);
    }

    public void setIncomeTitle(String title) {
        incomeTitle.setText(title);
    }

    public void setBillTitle(String title) {
        billTitle.setText(title);
    }

    public void setName(String name) {
        firstNameEnter.setText(name);
    }

    public void setIncome(float income) { incomeEnter.setText("$" + String.format("%.2f", income)); }

    public void setAdditionalIncome(float additionalIncome) {
        additionalIncomeTotal.setText("$" + String.format("%.2f", additionalIncome));
        currAdditionalIncome = additionalIncome;
    }

    public void setBills(ArrayList<Bill> bills) {
        if (bills.size() != 1 && !bills.isEmpty()) {
            for (int i = 1; i < bills.size(); i++) {
                addBillButton.performClick();
            }
        }

        int i = 0;
        for (Bill b : bills) {
            View view = tableLayout.getChildAt(i);

            if (view instanceof TableRow) {
                // gets values of bill name and amount for each row
                TableRow row = (TableRow) view;
                TextView tempName = (TextView) row.getChildAt(0);
                tempName.setText(b.getName());

                TextView tempAmount = (TextView) row.getChildAt(1);
                tempAmount.setText(String.valueOf(b.getAmount()));

                i += 1;
            } else {
                break;
            }
        }
    }

    public void showAdditionalIncome(Boolean show) {
        if (show) {
            additionalIncomeTotal.setVisibility(View.VISIBLE);
            additionalIncomeTitle.setVisibility(View.VISIBLE);
            additionalIncomeEnter.setVisibility(View.VISIBLE);
            addAdditionalIncomeButton.setVisibility(View.VISIBLE);
            subtractAdditionalIncomeButton.setVisibility(View.VISIBLE);
        } else {
            additionalIncomeTotal.setVisibility(View.GONE);
            additionalIncomeTitle.setVisibility(View.GONE);
            additionalIncomeEnter.setVisibility(View.GONE);
            addAdditionalIncomeButton.setVisibility(View.GONE);
            subtractAdditionalIncomeButton.setVisibility(View.GONE);
        }
    }

    private void doAdditionalIncome() {
        additionalIncomeTotal.setText("$" + String.format("%.2f", (currAdditionalIncome)));
        additionalIncomeEnter.getText().clear();

        preferencesViewModel.writeAdditionalIncome(currAdditionalIncome);
    }

    // add user info to database
    public boolean updateUser() {
        // Take the value of the edit texts
        String name;
        Float income;
        String bname;
        String bamount;
        String temp;
        name = firstNameEnter.getText().toString();
        temp = incomeEnter.getText().toString();

        // Validations for input name and income
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_first_name,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (temp.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_income,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        // writes name, income to database
        income = Float.valueOf(temp.replace("$", ""));
        preferencesViewModel.writeIncome(income);
        preferencesViewModel.writeName(name);

        ArrayList<Bill> bills = new ArrayList<>();
        // loops through tableLayout getting each row
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View view = tableLayout.getChildAt(i);
            //makes sure there is a row here
            if (view instanceof TableRow) {
                //gets values of bill name and amount for each row
                TableRow row = (TableRow) view;
                TextView tempName = (TextView) row.getChildAt(0);
                bname = tempName.getText().toString();
                TextView tempAmount = (TextView) row.getChildAt(1);
                bamount = tempAmount.getText().toString();

                // if name is empty but amount is not, warn the user
                if (bname.equals("") && !bamount.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_bill_name, Toast.LENGTH_LONG).show();
                    return false;
                }
                // if amount is empty but name is not, warn the user
                else if (!bname.equals("") && bamount.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_bill_amount, Toast.LENGTH_LONG).show();
                    return false;
                }
                // both fields are filled, add the new bill to database with id of the row number
                else if (!bname.equals("") && !bamount.equals("")) {
                    bills.add(new Bill(bname, Integer.parseInt(bamount)));
                    preferencesViewModel.writeBillName(bname, i);
                    preferencesViewModel.writeBillAmount(Float.valueOf(bamount), i);
                }
                // if both fields are empty, do nothing and go the the next row
                else if (bname.equals("") && bamount.equals("")) {
                    continue;
                }
            }

            // if there are no more rows break
            else {
                break;
            }
        }

        // update the daily budget since the income and bills have changed
        purchaseHistoryViewModel.getPurchaseHistory()
                .observe(getViewLifecycleOwner(), new Observer<ArrayList<Purchase>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<Purchase> allPurchases) {
                        homeViewModel.updateDailyBudget(income + currAdditionalIncome,
                                allPurchases, bills);
                    }
                });

        return true;
    }
}