package com.example.lostfoundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper db;

    private EditText radiusTxt;
    private Button btnApplyRadius;

    private double userLat;
    private double userLng;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getUserLocation();
                } else {
                    Toast.makeText(this, "Location permission required for radius search", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        radiusTxt = findViewById(R.id.etRadius);
        btnApplyRadius = findViewById(R.id.btnApplyRadius);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnApplyRadius.setOnClickListener(v -> showItemsWithinRadius());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
                return;
            }

            userLat = location.getLatitude();
            userLng = location.getLongitude();

            LatLng userPosition = new LatLng(userLat, userLng);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 12));
            googleMap.addMarker(new MarkerOptions()
                    .position(userPosition)
                    .title("You are here"));

            showItemsWithinRadius();
        });
    }

    private void showItemsWithinRadius() {
        if (googleMap == null) {
            return;
        }

        String radiusInput = radiusTxt.getText().toString().trim();

        if (radiusInput.isEmpty()) {
            Toast.makeText(this, "Enter radius in km", Toast.LENGTH_SHORT).show();
            return;
        }

        double radiusKm;

        try {
            radiusKm = Double.parseDouble(radiusInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid radius", Toast.LENGTH_SHORT).show();
            return;
        }

        googleMap.clear();

        LatLng userPosition = new LatLng(userLat, userLng);

        googleMap.addMarker(new MarkerOptions()
                .position(userPosition)
                .title("You are here"));

        ArrayList<Advert> adverts = db.getAllAdverts();

        int count = 0;

        for (Advert advert : adverts) {
            float[] results = new float[1];

            Location.distanceBetween(
                    userLat,
                    userLng,
                    advert.latitude,
                    advert.longitude,
                    results
            );

            double distanceKm = results[0] / 1000.0;

            if (distanceKm <= radiusKm) {
                LatLng itemPosition = new LatLng(advert.latitude, advert.longitude);

                googleMap.addMarker(new MarkerOptions()
                        .position(itemPosition)
                        .title(advert.postType + ": " + advert.description)
                        .snippet("Category: " + advert.category + " | " +
                                String.format("%.2f", distanceKm) + " km away"));

                count++;
            }
        }

        Toast.makeText(this, "Showing " + count + " item(s)", Toast.LENGTH_SHORT).show();
    }
}