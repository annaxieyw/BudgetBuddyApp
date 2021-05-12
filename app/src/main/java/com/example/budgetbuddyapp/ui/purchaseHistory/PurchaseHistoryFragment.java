package com.example.budgetbuddyapp.ui.purchaseHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.example.budgetbuddyapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class PurchaseHistoryFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String FIREBASE_FORMAT = "MMddyyyy";

    private PurchaseHistoryViewModel purchaseHistoryViewModel;

    private RecyclerView pHRecyclerView;
    private PurchaseHistoryRecyclerViewAdapter purchaseHistoryRecyclerViewAdapter;

    private String[] categories;    // all purchase categories

    // for our line graph
    private AnyChartView lineGraphChartView;
    private Cartesian cartesian;
    private Set set;
    private ArrayList<DataEntry> lineDataMonth;     //holds all our data entries for purchases done for the past 28 days
    private ArrayList<DataEntry> lineDataWeek;      //holds all our data entries for purchases done for the past 7 days

    // for our pie chart
    private AnyChartView pieChartView;
    private Pie pie;
    private ArrayList<DataEntry> pieDataMonth;      //holds all our data entries for purchases done for the past 28 days
    private ArrayList<DataEntry> pieDataWeek;       //holds all our data entries for purchases done for the past 7 days

    private ArrayList<Purchase> purchases;          //all purchases made by the user, will be grabbed from firebase

    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<Date> dates;      //all dates we are interested in for our graphs(28 days ago to today)
    private ArrayList<ArrayList<Integer>> totalForDate;     //purchase totals by category for each date

    int DAYS_IN_A_WEEK = 7;
    int DAYS_IN_A_MONTH = 28;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        purchaseHistoryViewModel = new ViewModelProvider(this).get(PurchaseHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_purchase_history, container, false);

        purchases = new ArrayList<>();

        pHRecyclerView = root.findViewById(R.id.pHRecyclerView);
        purchaseHistoryRecyclerViewAdapter = new PurchaseHistoryRecyclerViewAdapter(root.getContext(), purchases);
        pHRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false));
        pHRecyclerView.setAdapter(purchaseHistoryRecyclerViewAdapter);

        categories = Arrays.copyOfRange(getResources().getStringArray(R.array.categories_array), 0, 9);
        simpleDateFormat = new SimpleDateFormat(FIREBASE_FORMAT);

        //setting up the two spinners
        Spinner graphSpinner = root.findViewById(R.id.graphSpinner);
        Spinner timeSpinner = root.findViewById(R.id.timeSpinner);
        ArrayAdapter<CharSequence> gAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graphsSpinner, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> tAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.timePeriodSpinner, android.R.layout.simple_spinner_item);
        gAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphSpinner.setAdapter(gAdapter);
        timeSpinner.setAdapter(tAdapter);

        dates = new ArrayList<>();
        totalForDate = new ArrayList<>();
        lineDataMonth = new ArrayList<>();
        lineDataWeek = new ArrayList<>();
        set = Set.instantiate();
        setArrays();  // dates = Arraylist of dates from 30 days ago to today.
                      // totalForDate = 2d Arraylist -> Arraylist of Integers for each date in dates
                      // the order of the Integers stored corresponds to the order in categories[]

        // here is where we get the data from firebase
        purchaseHistoryViewModel.getPurchaseHistory().observe(getViewLifecycleOwner(), new Observer<ArrayList<Purchase>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Purchase> purchaseHistory) {      //this will get called whenever there are changes to the firebase data
                // populating our view with the purchase history data
                if (purchaseHistory.size() > 0) {
                    purchases.clear();
                    purchases.addAll(purchaseHistory.subList(0, purchaseHistory.size()-1));
                    purchaseHistoryRecyclerViewAdapter.notifyDataSetChanged();
                }

                int totalBills = 0;
                for (int i = 0; i < purchaseHistory.size()-1; i++) {
                    Purchase pHPurshase = (Purchase) purchaseHistory.get(i);
                    String purchaseDateStr = pHPurshase.getDate();

                    //Grabbing data in order to create our graphs
                    int indexOfDate = -1;
                    try {
                        indexOfDate = getIndexOfDate(purchaseDateStr);  //we only care about purchases made within 28 days. If getIndexOfDate returns -1,
                    } catch (ParseException e) {                        //that means the date is not within 28 days
                        e.printStackTrace();
                    }

                    if (indexOfDate != -1) {      //if there does exist an index (ie it's within 28 days, then we need this data for our graph
                        int indexOfCategory = getIndexOfMatchingCat(pHPurshase.getCategory());  //find the index of the category
                        if (indexOfCategory != -1) {    //if the category index is found
                            //add the purchase amount to the corresponding date and the corresponding category
                            Integer purchaseAmount = new Integer(Integer.parseInt(pHPurshase.getAmount()));
                            ArrayList<Integer> arrayToChange = totalForDate.get(indexOfDate);
                            Integer prevTotal = arrayToChange.get(indexOfCategory);
                            totalForDate.get(indexOfDate).set(indexOfCategory, prevTotal + purchaseAmount);
                        }
                    }
                    //totalBills is used for the pie graph. It holds the user's inputted monthly bills
                    totalBills = Integer.parseInt(purchaseHistory.get(purchaseHistory.size() - 1).getAmount());
                }

                //setting up our line graph using AnyChart
                lineGraphChartView = root.findViewById(R.id.lineGraphChartView);
                APIlib.getInstance().setActiveAnyChartView(lineGraphChartView);     //need this to be able to change data and update the graph
                for (int i = 0; i < dates.size(); i++) {
                    //for every date, create a new data entry which holds the date and then the amount the user spent on each category on that day
                    ArrayList<Integer> current = totalForDate.get(i);
                    CustomDataEntry newEntry = new CustomDataEntry(makePrettyDateString(simpleDateFormat.format(dates.get(i))),
                            current.get(0), current.get(1), current.get(2), current.get(3), current.get(4), current.get(5), current.get(6), current.get(7), current.get(8));
                    //we keep 2 arrays of entries, one for month and one for week
                    lineDataMonth.add(newEntry);
                    if (i > DAYS_IN_A_MONTH - DAYS_IN_A_WEEK) {
                        lineDataWeek.add(newEntry);
                    }
                }

                set.data(lineDataWeek);     //default to week
                cartesian = AnyChart.line();
                for (int i = 0; i < categories.length; i++) {       //setting up the lines in our line graph
                    Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value" + (i + 1) + "' }");
                    Line series1 = cartesian.line(series1Mapping);
                    series1.name(categories[i]);
                }

                cartesian.yAxis(0).title(getString(R.string.graph_yaxis));
                cartesian.xAxis(0).title(getString(R.string.graph_xaxis));

                cartesian.legend().enabled(true);
                lineGraphChartView.setChart(cartesian);     //actually sets the graph to our AnyChartView

                //setting up our pie graph using AnyChart
                pieDataMonth = new ArrayList<>();   //again, we have 2 arrays of entries, one for month and one for week
                pieDataWeek = new ArrayList<>();

                //get the total money spent per category in the last week and month
                int[] totalsByCategoryMonth = totalUpAllCategories(DAYS_IN_A_MONTH);
                int[] totalsByCategoryWeek = totalUpAllCategories(DAYS_IN_A_WEEK);

                //we add the first data manually because total bills consists of additional bills + monthly bills

                String total_bills = getString(R.string.total_bills);
                pieDataMonth.add(new ValueDataEntry(total_bills, (totalBills + totalsByCategoryMonth[0])));
                pieDataWeek.add(new ValueDataEntry(total_bills, ((totalBills / 4) + totalsByCategoryWeek[0])));

                for (int i = 1; i < categories.length; i++) {       //add a new entry for each category
                    int catAmountMonth = totalsByCategoryMonth[i];
                    if (catAmountMonth > 0) {       //only add the category to the pie graph if the amount spent in that category is more than 0
                        pieDataMonth.add(new ValueDataEntry(categories[i], catAmountMonth));
                    }

                    //do the same for week
                    int catAmountWeek = totalsByCategoryWeek[i];
                    if (catAmountWeek > 0) {
                        pieDataWeek.add(new ValueDataEntry(categories[i], catAmountWeek));
                    }
                }

                pieChartView = root.findViewById(R.id.pieChartView);
                APIlib.getInstance().setActiveAnyChartView(pieChartView);       //need this to be able to change data and update the graph
                pie = AnyChart.pie();
                pie.data(pieDataWeek);      //default to week
                pieChartView.setChart(pie);     //actually sets the graph to our AnyChartView
            }
        });

        //make listeners for our spinners
        graphSpinner.setOnItemSelectedListener(this);
        timeSpinner.setOnItemSelectedListener(this);

        return root;
    }

    //createCatByAmtArray will create an empty array of Integers which
    //can be used to store purchase amounts. The category of the purchase
    //can be found by the index
    private ArrayList<Integer> createCatByAmtArray() {
        ArrayList<Integer> totalsByCategory = new ArrayList<>();
        for (int i = 0; i < categories.length; i++) {
            totalsByCategory.add(0);
        }
        return totalsByCategory;
    }

    //getIndexOfMatchingCat takes a String category and returns where that
    //string is found in categories[].
    private int getIndexOfMatchingCat(String category) {
        for (int i = 0; i < categories.length; i++) {
            if (category.equals(categories[i])) {
                return i;
            }
        }
        return -1;
    }

    //getIndexofDate takes a String dateStr and returns where that date
    //is found in ArrayList dates
    private int getIndexOfDate(String dateStr) throws ParseException {
        Date dateFromStr = simpleDateFormat.parse(dateStr);
        for (int i = 0; i < dates.size(); i++) {
            if (simpleDateFormat.format(dates.get(i)).equals(simpleDateFormat.format(dateFromStr))) {
                return i;
            }
        }
        return -1;
    }

    //totalUpAllCategories will total up the values in totalForDate
    //totalUntil days back. It returns an array of ints which hold
    //the total purchase amounts for each category
    private int[] totalUpAllCategories(int totalUntil) {
        int[] totals = new int[categories.length];
        for (int i = 0; i < categories.length; i++) {
            totals[i] = 0;
        }

        int until = DAYS_IN_A_MONTH - totalUntil;
        for (int i = DAYS_IN_A_MONTH - 1; i >= DAYS_IN_A_MONTH - totalUntil; i--) {
            for (int j = 0; j < categories.length; j++) {
                totals[j] += totalForDate.get(i).get(j);
            }
        }

        String printout = "";
        for (int a : totals) {
            printout = printout + ", " + a;
        }

        return totals;
    }

    //makePrettyDateString will take a string "MMddYYY" and return "MM/dd/YYYY"
    //used to make the labels on the x axis of the line graph look pretty
    public static String makePrettyDateString(String ugly) {
        String pretty = ugly.substring(0, 2) + "/" + ugly.substring(2, 4) + "/" + ugly.substring(4, ugly.length());
        return pretty;
    }

    //setArrays will set dates and totalForDates.
    //dates will be filled with Date objects, starting from 30 days ago to today
    //totalForDates will be of the same length as dates, and be filled with empty
    //category arrays
    private void setArrays() {
        calendar = Calendar.getInstance();
        dates.add(0, calendar.getTime());
        totalForDate.add(0, createCatByAmtArray());
        for (int i = 0; i < DAYS_IN_A_MONTH-1; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dates.add(0, calendar.getTime());
            totalForDate.add(0, createCatByAmtArray());
        }
    }

    //this is the listener for our spinners
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItemStr = adapterView.getItemAtPosition(i).toString();

        switch (selectedItemStr) {
            //the views for line graph and pie chart are simply stacked onto each other, so to show
            //one we just have to bring it to the front
            case "Pie Chart":
                pieChartView.bringToFront();
                break;
            case "Line Graph":
                lineGraphChartView.bringToFront();
                break;
            //changing the time period will update both graphs
            //need to do APIlib.getInstance()..... every time to update graphs (according to AnyChart documentation)
            case "Week":
                APIlib.getInstance().setActiveAnyChartView(lineGraphChartView);
                set.data(lineDataWeek);
                APIlib.getInstance().setActiveAnyChartView(pieChartView);
                pie.data(pieDataWeek);
                break;
            case "Month":
                APIlib.getInstance().setActiveAnyChartView(lineGraphChartView);
                set.data(lineDataMonth);
                APIlib.getInstance().setActiveAnyChartView(pieChartView);
                pie.data(pieDataMonth);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //custom data entry for our line graph
    private class CustomDataEntry extends ValueDataEntry {

        //each entry consists of a date, and then a number for each category
        CustomDataEntry(String x, Number value1, Number value2, Number value3, Number value4,
                        Number value5, Number value6, Number value7, Number value8, Number value9) {
            super(x, value1);
            setValue("value2", value2);
            setValue("value3", value3);
            setValue("value4", value4);
            setValue("value5", value5);
            setValue("value6", value6);
            setValue("value7", value7);
            setValue("value8", value8);
            setValue("value9", value9);
        }
    }
}