package com.example.lostfoundapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.Address;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Locale;
import java.util.List;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup postTypeGroup;
    private Spinner categorySpinner;
    private EditText nameTxt, phoneTxt, descTxt, dateTxt, locationTxt;
    private Button chooseImgBtn, saveBtn, currentLocationBtn;

    private String imageUriString = "";
    private double selectedLat = 0.0;
    private double selectedLng = 0.0;
    private DatabaseHelper db;
    private FusedLocationProviderClient fusedLocationClient;

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

    // Places autocomplete result
    private final ActivityResultLauncher<Intent> placeLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());

                    locationTxt.setText(place.getAddress());

                    if (place.getLatLng() != null) {
                        selectedLat = place.getLatLng().latitude;
                        selectedLng = place.getLatLng().longitude;
                    }
                }
            });

    // Location permission launcher
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        postTypeGroup = findViewById(R.id.radioPostType);
        categorySpinner = findViewById(R.id.spinnerCategory);

        nameTxt = findViewById(R.id.etName);
        phoneTxt = findViewById(R.id.etPhone);
        descTxt = findViewById(R.id.etDescription);
        dateTxt = findViewById(R.id.etDate);
        locationTxt = findViewById(R.id.etLocation);

        chooseImgBtn = findViewById(R.id.btnChooseImage);
        currentLocationBtn = findViewById(R.id.btnCurrentLocation);
        saveBtn = findViewById(R.id.btnSave);

        setupCategoryList();

        chooseImgBtn.setOnClickListener(v -> pickImage.launch(new String[]{"image/*"}));
        locationTxt.setOnClickListener(v -> openPlaceAutocomplete());
        currentLocationBtn.setOnClickListener(v -> checkLocationPermission());
        saveBtn.setOnClickListener(v -> saveAdvert());
    }

    private void setupCategoryList() {
        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, advertCategories);

        // I usually set this as well, otherwise the dropdown can look a bit plain on some devices.
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fields
        ).build(this);

        placeLauncher.launch(intent);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedLat = location.getLatitude();
            selectedLng = location.getLongitude();

            String addressText = "Current Location";

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(selectedLat, selectedLng, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    addressText = addresses.get(0).getAddressLine(0);
                }
            } catch (Exception e) {
                addressText = "Current Location";
            }

            locationTxt.setText(addressText);
            Toast.makeText(this, "Current location selected", Toast.LENGTH_SHORT).show();
        });
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

        if (imageUriString.length() == 0) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLat == 0.0 && selectedLng == 0.0) {
            Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show();
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
                createdAt,
                selectedLat,
                selectedLng
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