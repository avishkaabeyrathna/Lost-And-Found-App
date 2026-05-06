package com.example.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup postTypeGroup;
    private Spinner categorySpinner;
    private EditText nameTxt, phoneTxt, descTxt, dateTxt, locationTxt;
    private Button chooseImgBtn, saveBtn;

    private String imageUriString = "";
    private DatabaseHelper db;

    // Keeping this here for now since these categories are small and unlikely to change often.
    private final String[] advertCategories = {
            "Electronics",
            "Pets",
            "Wallets",
            "Keys",
            "Other"
    };

    private final ActivityResultLauncher<String[]> pickImage =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }

                imageUriString = uri.toString();

                // Need this so the app can still read the image later after the picker closes.
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = new DatabaseHelper(this);

        postTypeGroup = findViewById(R.id.radioPostType);
        categorySpinner = findViewById(R.id.spinnerCategory);

        nameTxt = findViewById(R.id.etName);
        phoneTxt = findViewById(R.id.etPhone);
        descTxt = findViewById(R.id.etDescription);
        dateTxt = findViewById(R.id.etDate);
        locationTxt = findViewById(R.id.etLocation);

        chooseImgBtn = findViewById(R.id.btnChooseImage);
        saveBtn = findViewById(R.id.btnSave);

        setupCategoryList();

        chooseImgBtn.setOnClickListener(v -> pickImage.launch(new String[]{"image/*"}));
        saveBtn.setOnClickListener(v -> saveAdvert());
    }

    private void setupCategoryList() {
        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, advertCategories);

        // I usually set this as well, otherwise the dropdown can look a bit plain on some devices.
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void saveAdvert() {
        String postType = getSelectedPostType();

        if (postType == null) {
            Toast.makeText(this, "Select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = categorySpinner.getSelectedItem().toString();

        String itemName = nameTxt.getText().toString().trim();
        String contactPhone = phoneTxt.getText().toString().trim();
        String advertDescription = descTxt.getText().toString().trim();
        String itemDate = dateTxt.getText().toString().trim();
        String foundOrLostLocation = locationTxt.getText().toString().trim();

        if (hasMissingInput(itemName, contactPhone, advertDescription, itemDate, foundOrLostLocation)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Maybe later: show a preview thumbnail before saving.
        // previewImage(imageUriString);

        if (imageUriString.length() == 0) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdAt = getCurrentTimestamp();

        boolean wasInserted = db.insertAdvert(
                postType,
                category,
                itemName,
                contactPhone,
                advertDescription,
                itemDate,
                foundOrLostLocation,
                imageUriString,
                createdAt
        );

        if (wasInserted) {
            Toast.makeText(this, "Advert saved", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
    }

    private String getSelectedPostType() {
        int selectedId = postTypeGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioLost) {
            return "Lost";
        }

        if (selectedId == R.id.radioFound) {
            return "Found";
        }

        return null;
    }

    private boolean hasMissingInput(String name, String phone, String description,
                                    String date, String place) {
        // Written out this way because it is easier to tweak individual checks later.
        boolean nameMissing = name.isEmpty();
        boolean phoneMissing = phone.isEmpty();
        boolean descriptionMissing = description.isEmpty();
        boolean dateMissing = date.isEmpty();
        boolean locationMissing = place.isEmpty();

        return nameMissing || phoneMissing || descriptionMissing || dateMissing || locationMissing;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat displayDateFormat =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return displayDateFormat.format(new Date());
    }
}