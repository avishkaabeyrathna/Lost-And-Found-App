package com.example.lostfoundapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdvertActivity extends AppCompatActivity {

    Spinner catSelect;
    Button btnApply;
    RecyclerView mainList;

    DatabaseHelper db;
    AdvertAdapter listAdap;

    final String[] catOpts = {"All", "Electronics", "Pets", "Wallets", "Keys", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_advert);

        db = new DatabaseHelper(this);

        catSelect = findViewById(R.id.spinnerFilter);
        btnApply = findViewById(R.id.btnFilter);
        mainList = findViewById(R.id.recyclerView);

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, catOpts);
        catSelect.setAdapter(spinnerAdapter);

        mainList.setLayoutManager(new LinearLayoutManager(this));

        refreshData();

        btnApply.setOnClickListener(v -> {
            String currCat = catSelect.getSelectedItem().toString();

            ArrayList<Advert> records;

            if (currCat.equals("All")) {
                records = db.getAllAdverts();
            } else {
                records = db.getAdvertsByCategory(currCat);
            }

            listAdap = new AdvertAdapter(records);
            mainList.setAdapter(listAdap);

            Toast.makeText(this,
                    "Showing " + records.size() + " item(s) for " + currCat,
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshData() {
        ArrayList<Advert> records = db.getAllAdverts();
        listAdap = new AdvertAdapter(records);
        mainList.setAdapter(listAdap);
    }
}