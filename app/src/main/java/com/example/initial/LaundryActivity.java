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

//public class LaundryActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private ClothesAdapter clothesAdapter;
//    private List<Clothes> clothesList;
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//    private Button sendToLaundryButton, takeBackFromLaundryButton, viewInLaundryButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_laundry);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        clothesList = new ArrayList<>();
//        clothesAdapter = new ClothesAdapter(clothesList);
//        recyclerView.setAdapter(clothesAdapter);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//
//        sendToLaundryButton = findViewById(R.id.sendToLaundryButton);
//        takeBackFromLaundryButton = findViewById(R.id.takeBackFromLaundryButton);
//        viewInLaundryButton = findViewById(R.id.viewInLaundryButton);
//
//        sendToLaundryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadClothes(false); // Load clothes not in laundry to send them
//            }
//        });
//
//        viewInLaundryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadClothes(true); // Load clothes currently in laundry
//            }
//        });
//
//        takeBackFromLaundryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                takeBackClothes(); // Mark clothes as taken back from laundry
//            }
//        });
//    }
//
//    private void loadClothes(boolean inLaundry) {
//        String userId = mAuth.getCurrentUser().getUid();
//        db.collection("clothes")
//                .whereEqualTo("userId", userId)
//                .whereEqualTo("inLaundry", inLaundry)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null) {
//                            clothesList.clear();
//                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                Clothes clothes = document.toObject(Clothes.class);
//                                clothesList.add(clothes);
//                            }
//                            clothesAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        Toast.makeText(LaundryActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void sendClothesToLaundry() {
//        for (Clothes clothes : clothesList) {
//            if (clothes.isSelected()) {
//                db.collection("clothes").document(clothes.getId())
//                        .update("inLaundry", true)
//                        .addOnSuccessListener(aVoid -> Toast.makeText(LaundryActivity.this, "Clothes sent to laundry", Toast.LENGTH_SHORT).show())
//                        .addOnFailureListener(e -> Toast.makeText(LaundryActivity.this, "Failed to send clothes", Toast.LENGTH_SHORT).show());
//            }
//        }
//    }
//
//    private void takeBackClothes() {
//        for (Clothes clothes : clothesList) {
//            if (clothes.isSelected()) {
//                db.collection("clothes").document(clothes.getId())
//                        .update("inLaundry", false)
//                        .addOnSuccessListener(aVoid -> Toast.makeText(LaundryActivity.this, "Clothes taken back from laundry", Toast.LENGTH_SHORT).show())
//                        .addOnFailureListener(e -> Toast.makeText(LaundryActivity.this, "Failed to update clothes", Toast.LENGTH_SHORT).show());
//            }
//        }
//    }
//}


//public class LaundryActivity {
//}
