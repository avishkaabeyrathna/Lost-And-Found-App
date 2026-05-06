package com.example.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wire up the main navigation buttons
        Button btnAddAdvert = findViewById(R.id.btnCreateAdvert);
        Button btnViewItems = findViewById(R.id.btnShowItems);

        btnAddAdvert.setOnClickListener(v -> {
            Intent navIntent = new Intent(MainActivity.this, CreateAdvertActivity.class);
            startActivity(navIntent);
        });

        btnViewItems.setOnClickListener(v -> {
            Intent navIntent = new Intent(MainActivity.this, ListAdvertActivity.class);
            startActivity(navIntent);
        });
    }
}