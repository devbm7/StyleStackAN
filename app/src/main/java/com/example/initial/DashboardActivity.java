package com.example.initial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button uploadClothesButton, viewClothesButton, manageLaundryButton, removeClothesButton;
    private Button profileButton, viewLaundryButton, viewGroupLaundryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        uploadClothesButton = findViewById(R.id.uploadClothesButton);
        viewClothesButton = findViewById(R.id.viewClothesButton);
        manageLaundryButton = findViewById(R.id.manageLaundryButton);
        viewLaundryButton = findViewById(R.id.viewLaundryButton);
        viewGroupLaundryButton = findViewById(R.id.viewGroupLaundryButton);
        removeClothesButton = findViewById(R.id.removeClothesButton);
        profileButton = findViewById(R.id.profileButton);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the toolbar
        getSupportActionBar().setTitle("Style Stack");


        uploadClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to UploadClothesActivity
                startActivity(new Intent(DashboardActivity.this, UploadClothesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        viewClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ViewClothesActivity
                Intent intent = new Intent(DashboardActivity.this, ViewClothesActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // Apply animations
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        manageLaundryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LaundryActivity
                startActivity(new Intent(DashboardActivity.this, LaundryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        viewLaundryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LaundryActivity
                startActivity(new Intent(DashboardActivity.this, ViewLaundryActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        viewGroupLaundryButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view){
               startActivity(new Intent(DashboardActivity.this, ViewGroupLaundryActivity.class));
               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
           }
        });
        removeClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, RemoveClothesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // Apply reverse transition
    }
}