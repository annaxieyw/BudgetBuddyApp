package com.example.budgetbuddyapp.ui.addPurchase;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetbuddyapp.R;
import com.example.budgetbuddyapp.ui.purchaseConfirmation.PurchaseConfirmationFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPurchaseFragment extends Fragment {
    private static final int RESULT_OK = -1;
    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private static final String FILE_PROVIDER_PATH = "com.example.android.fileprovider";
    private static final String FIREBASE_DATE_FORMAT = "MMddyyyy";
    private static final String IMAGE_FILE_FORMAT = "yyyyMMdd_HHmmss";
    private static final String DISPLAY_FORMAT = "MM/dd/yyyy";

    private final Calendar myCalendar = Calendar.getInstance();

    private AddPurchaseViewModel addPurchaseViewModel;

    private Bitmap selectedImage;
    private Button btnSubmit, btnUpload, btnScan;
    private EditText edtDate, edtName, edtAmount;
    private Spinner spnCategory;
    private String encImage, currentPhotoPath, category;
    public Uri imageUri;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_purchase, container, false);

        addPurchaseViewModel = new ViewModelProvider(this).get(AddPurchaseViewModel.class);

        edtDate = (EditText) root.findViewById(R.id.edtDate);
        edtName = (EditText) root.findViewById(R.id.edtName);
        edtAmount = (EditText) root.findViewById(R.id.edtAmount);
        spnCategory = (Spinner) root.findViewById(R.id.spnCategory);
        btnSubmit = (Button) root.findViewById(R.id.btnSubmit);
        btnUpload = (Button) root.findViewById(R.id.btnUpload);
        btnScan = (Button) root.findViewById(R.id.btnScan);

        // Grab the categories from string resources
        String[] categories = getActivity().getResources().getStringArray(R.array.categories_array);

        // Create an ArrayAdapter using the categories array and a default spinner layout
        HintAdapter hintAdapter = new HintAdapter(getContext(), android.R.layout.simple_list_item_1, categories);
        // Specify the layout to use when the list of choices appears
        hintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnCategory.setAdapter(hintAdapter);
        // Show the spinner hint
        spnCategory.setSelection(hintAdapter.getCount());
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // When a date is selected using the date picker,
                // update the EditText for date accordingly
                updateLabel();
            }
        };

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(container.getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date input validation
                if (edtDate.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_date, Toast.LENGTH_SHORT).show();
                }
                // Name input validation
                else if (edtName.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_name, Toast.LENGTH_SHORT).show();
                }
                // Amount input validation
                else if (edtAmount.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_amount, Toast.LENGTH_SHORT).show();
                }
                // Category input validation
                else if (category.equals(categories[categories.length - 1])) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_category, Toast.LENGTH_SHORT).show();
                }
                // All input fields look good, submit the purchase to Firebase
                else {
                    SimpleDateFormat SDF = new SimpleDateFormat(FIREBASE_DATE_FORMAT, Locale.US);
                    String date = (SDF.format(myCalendar.getTime()));

                    Purchase purchase = new Purchase(edtName.getText().toString(), edtAmount.getText().toString(), category);

                    addPurchaseViewModel.writePurchase(purchase, date);

                    // Create new fragment and transaction
                    Fragment purchaseConfirmationFragment = new PurchaseConfirmationFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    Bundle bundle = new Bundle();
                    bundle.putString("name", edtName.getText().toString());
                    bundle.putString("amount", edtAmount.getText().toString());
                    bundle.putString("date", date);
                    bundle.putString("category", category);

                    purchaseConfirmationFragment.setArguments(bundle);

                    // Replace whatever is in the nav_host_fragment view with this fragment,
                    // and add the transaction to the back stack
                    transaction.replace(R.id.nav_host_fragment, purchaseConfirmationFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        return root;
    }

    // Start the intent to take a picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_PATH, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(IMAGE_FILE_FORMAT).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void updateLabel() {
        SimpleDateFormat SDF = new SimpleDateFormat(DISPLAY_FORMAT, Locale.US);
        edtDate.setText(SDF.format(myCalendar.getTime()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void choosePicture() {
        // uses an intent to helps open up the gallery and allows the user to select image from their device.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        onActivityResult(1, RESULT_OK, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the request is to upload picture
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            InputStream imageStream = null;

            try {
                imageStream = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            selectedImage = BitmapFactory.decodeStream(imageStream);

            try {
                decodeImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // if the request is to scan a picture
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            selectedImage = BitmapFactory.decodeFile(currentPhotoPath, options);

            try {
                decodeImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void decodeImage() throws InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] b = baos.toByteArray();
        encImage = Base64.encodeToString(b, Base64.DEFAULT);

        addPurchaseViewModel.getTextFromImage(encImage).observe(getViewLifecycleOwner(), new Observer<JSONObject>() {
            @Override
            public void onChanged(@Nullable JSONObject json) {
                if (json != null) {
                    try {
                        // Get the transaction date
                        String date = json.getString("date");
                        // Get the total payment (include tax)
                        String total = json.getString("total");
                        // Get the vendor object to get the name of the purchase
                        JSONObject vendor = json.getJSONObject("vendor");
                        String vendorName = vendor.getString("name");

                        // Get the date in the correct format
                        SimpleDateFormat JSONDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        Date jsonDate = JSONDateFormat.parse(date);

                        // Parse the year from the date
                        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.US);
                        int year = Integer.parseInt(yearFormat.format(jsonDate));
                        // Parse the month from the date
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US);
                        int month = Integer.parseInt(monthFormat.format(jsonDate));
                        // Parse the day from the date
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
                        int day = Integer.parseInt(dayFormat.format(jsonDate));

                        // Set all user inputs to be the parsed out values
                        edtAmount.setText(total);
                        edtName.setText(vendorName);
                        myCalendar.set(year, month, day);
                        updateLabel();
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}