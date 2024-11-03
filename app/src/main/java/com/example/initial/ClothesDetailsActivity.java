package com.example.initial;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

public class ClothesDetailsActivity extends AppCompatActivity {

    private ImageView clothesImageView;
    private TextView nameTextView, categoryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_details);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Clothes Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button

        clothesImageView = findViewById(R.id.clothesImageView);
        nameTextView = findViewById(R.id.nameTextView);
        categoryTextView = findViewById(R.id.categoryTextView);

        // Get the data from the Intent extras
        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Set the data to the views
        nameTextView.setText(name);
        categoryTextView.setText(category);

        // Load the image using Picasso
        Picasso.get().load(imageUrl).into(clothesImageView);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Apply reverse transition
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
