package com.example.initial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button uploadClothesButton, viewClothesButton, manageLaundryButton, removeClothesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        uploadClothesButton = findViewById(R.id.uploadClothesButton);
        viewClothesButton = findViewById(R.id.viewClothesButton);
        manageLaundryButton = findViewById(R.id.manageLaundryButton);
        removeClothesButton = findViewById(R.id.removeClothesButton);


        uploadClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to UploadClothesActivity
                startActivity(new Intent(DashboardActivity.this, UploadClothesActivity.class));
            }
        });

        viewClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ViewClothesActivity
                startActivity(new Intent(DashboardActivity.this, ViewClothesActivity.class));
            }
        });

        manageLaundryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LaundryActivity
                startActivity(new Intent(DashboardActivity.this, LaundryActivity.class));
            }
        });
        removeClothesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, RemoveClothesActivity.class));
            }
        });

    }
}


//public class DashboardActivity {
//}
