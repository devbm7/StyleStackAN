package com.example.initial;

import android.os.Bundle;
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

public class ViewClothesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothesAdapter clothesAdapter;
    private List<Clothes> clothesList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_clothes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothesList = new ArrayList<>();
        clothesAdapter = new ClothesAdapter(this, clothesList);
        recyclerView.setAdapter(clothesAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadClothes();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the toolbar
        getSupportActionBar().setTitle("View Clothes");

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void loadClothes() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("clothes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            clothesList.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Clothes clothes = document.toObject(Clothes.class);
                                clothesList.add(clothes);
                            }
                            clothesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ViewClothesActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
                    }
                });
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

