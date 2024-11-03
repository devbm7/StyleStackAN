package com.example.initial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewLaundryActivity extends AppCompatActivity {

    private TextView totalClothesTextView;
    private RecyclerView recyclerView;
    private ClothesAdapter clothesAdapter;
    private List<Clothes> clothesList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button clearLaundryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_laundry);

        totalClothesTextView = findViewById(R.id.totalClothesTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clearLaundryButton = findViewById(R.id.clearLaundryButton);

        clothesList = new ArrayList<>();
        clothesAdapter = new ClothesAdapter(this, clothesList);
        recyclerView.setAdapter(clothesAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the toolbar
        getSupportActionBar().setTitle("View Laundry Clothes");

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadClothesInLaundry();

        clearLaundryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllClothesFromLaundry();
            }
        });
    }

    // Load clothes that are currently in laundry
    private void loadClothesInLaundry() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("clothes")
                .whereEqualTo("userId", userId)
                .whereEqualTo("inLaundry", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        clothesList.clear();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Clothes clothes = document.toObject(Clothes.class);
                                clothesList.add(clothes);
                            }
                            // Update RecyclerView
                            clothesAdapter.notifyDataSetChanged();
                            // Update total count
                            updateTotalClothesCount();
                        }
                    } else {
                        Toast.makeText(ViewLaundryActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update the text view with the total number of clothes in laundry
    private void updateTotalClothesCount() {
        int totalClothes = clothesList.size();
        totalClothesTextView.setText("Total Clothes in Laundry: " + totalClothes);
    }

    // Clear all clothes from laundry (set inLaundry to false)
    private void clearAllClothesFromLaundry() {
        for (Clothes clothes : clothesList) {
            db.collection("clothes").document(clothes.getId())
                    .update("inLaundry", false)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ViewLaundryActivity.this, "All clothes cleared from laundry", Toast.LENGTH_SHORT).show();
                        loadClothesInLaundry();  // Reload the list after clearing
                    })
                    .addOnFailureListener(e -> Toast.makeText(ViewLaundryActivity.this, "Failed to clear clothes from laundry", Toast.LENGTH_SHORT).show());
        }
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
