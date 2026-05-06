package com.example.lostfoundapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView txtInfo;
    private ImageView imgView;
    private Button btnDelete;
    private DatabaseHelper db;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        db = new DatabaseHelper(this);

        txtInfo = findViewById(R.id.tvDetails);
        imgView = findViewById(R.id.imageItem);
        btnDelete = findViewById(R.id.btnRemove);

        id = getIntent().getIntExtra("advertId", -1);

        if (id == -1) {
            Toast.makeText(this, "Advert ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Advert item = db.getAdvertById(id);

        if (item == null) {
            Toast.makeText(this, "Advert not found in database", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Build display string
        txtInfo.setText(
                item.postType + " Item\n\n" +
                        "Category: " + item.category + "\n" +
                        "Name: " + item.name + "\n" +
                        "Phone: " + item.phone + "\n" +
                        "Description: " + item.description + "\n" +
                        "Date: " + item.date + "\n" +
                        "Location: " + item.location + "\n" +
                        "Posted: " + item.timestamp
        );

        // Show image if available
        if (item.imageUri != null && !item.imageUri.isEmpty()) {
            imgView.setImageURI(Uri.parse(item.imageUri));
        }

        btnDelete.setOnClickListener(v -> {
            db.deleteAdvert(id);
            Toast.makeText(this, "Advert removed", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}