package com.example.initial;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RemoveClothesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothesAdapter clothesAdapter;
    private List<Clothes> clothesList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_clothes);

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
        getSupportActionBar().setTitle("Remove Clothes");

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Implement swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Clothes clothes = clothesList.get(position);
                removeClothes(clothes);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadClothes() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("clothes")
                .whereEqualTo("userId", userId)
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
                            clothesAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(RemoveClothesActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeClothes(Clothes clothes) {
        db.collection("clothes").document(clothes.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    clothesList.remove(clothes);
                    clothesAdapter.notifyDataSetChanged();
                    Toast.makeText(RemoveClothesActivity.this, "Clothes removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(RemoveClothesActivity.this, "Failed to remove clothes", Toast.LENGTH_SHORT).show());
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

