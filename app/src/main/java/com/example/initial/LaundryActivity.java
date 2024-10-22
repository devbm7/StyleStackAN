package com.example.initial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LaundryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LaundryAdapter laundryAdapter;
    private List<Clothes> clothesList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothesList = new ArrayList<>();
        laundryAdapter = new LaundryAdapter(clothesList);
        recyclerView.setAdapter(laundryAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadClothes();
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
                            laundryAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(LaundryActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void markAsInLaundry(Clothes clothes) {
        db.collection("clothes").document(clothes.getId())
                .update("inLaundry", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(LaundryActivity.this, "Clothes sent to laundry", Toast.LENGTH_SHORT).show();
                    clothes.setInLaundry(true);
                    laundryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(LaundryActivity.this, "Failed to send clothes", Toast.LENGTH_SHORT).show());
    }

    public void takeBackFromLaundry(Clothes clothes) {
        db.collection("clothes").document(clothes.getId())
                .update("inLaundry", false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(LaundryActivity.this, "Clothes taken back", Toast.LENGTH_SHORT).show();
                    clothes.setInLaundry(false);
                    laundryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(LaundryActivity.this, "Failed to update clothes", Toast.LENGTH_SHORT).show());
    }
}
