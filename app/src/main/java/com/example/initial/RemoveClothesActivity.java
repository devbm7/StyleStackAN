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


public class RemoveClothesActivity extends AppCompatActivity {

    private Spinner categorySpinner, clothesSpinner;
    private Button removeButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<String> clothesNames;
    private List<Clothes> clothesList; // Store the actual Clothes objects to easily delete

    private String selectedCategory = null;
    private Clothes selectedClothes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_clothes);

        categorySpinner = findViewById(R.id.categorySpinner);
        clothesSpinner = findViewById(R.id.clothesSpinner);
        removeButton = findViewById(R.id.removeButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        clothesNames = new ArrayList<>();
        clothesList = new ArrayList<>();

        loadStaticCategories();

        // Listener for category selection
        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (String) categorySpinner.getSelectedItem();
                loadClothesForCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Listener for clothes selection
        clothesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedClothes = clothesList.get(position); // Store the selected Clothes object
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedClothes = null;
            }
        });

        // Remove button click
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedClothes != null) {
                    removeClothes(selectedClothes);
                } else {
                    Toast.makeText(RemoveClothesActivity.this, "Please select a clothing item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Load static categories
    private void loadStaticCategories() {
        List<String> staticCategories = new ArrayList<>();
        staticCategories.add("Pant");
        staticCategories.add("Shirt");
        staticCategories.add("TShirt");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, staticCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    // Load clothes based on the selected category
    private void loadClothesForCategory(String category) {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("clothes")
                .whereEqualTo("userId", userId)
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        clothesList.clear();
                        clothesNames.clear();
                        clothesNames.add("Select Clothing");

                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Clothes clothes = document.toObject(Clothes.class);
                                clothesList.add(clothes);
                                clothesNames.add(clothes.getName());
                            }
                        }

                        ArrayAdapter<String> clothesAdapter = new ArrayAdapter<>(RemoveClothesActivity.this, android.R.layout.simple_spinner_item, clothesNames);
                        clothesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        clothesSpinner.setAdapter(clothesAdapter);
                    } else {
                        Toast.makeText(RemoveClothesActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Remove the selected clothes from Firestore
    private void removeClothes(Clothes clothes) {
        db.collection("clothes").document(clothes.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RemoveClothesActivity.this, "Clothes removed successfully", Toast.LENGTH_SHORT).show();
                    // Reload clothes list after deletion
                    loadClothesForCategory(selectedCategory);
                })
                .addOnFailureListener(e -> Toast.makeText(RemoveClothesActivity.this, "Failed to remove clothes", Toast.LENGTH_SHORT).show());
    }
}

//public class RemoveClothesActivity extends AppCompatActivity {
//
//    private Spinner categorySpinner, clothesSpinner;
//    private Button removeButton;
//    private FirebaseFirestore db;
//    private FirebaseAuth mAuth;
//
//    private List<String> categories;
//    private List<String> clothesNames;
//    private List<Clothes> clothesList; // Store the actual Clothes objects to easily delete
//
//    private String selectedCategory = null;
//    private Clothes selectedClothes = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_remove_clothes);
//
//        categorySpinner = findViewById(R.id.categorySpinner);
//        clothesSpinner = findViewById(R.id.clothesSpinner);
//        removeButton = findViewById(R.id.removeButton);
//
//        db = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//
//        categories = new ArrayList<>();
//        clothesNames = new ArrayList<>();
//        clothesList = new ArrayList<>();
//
//        loadCategories();
//
//        // Listener for category selection
//        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
//                selectedCategory = categories.get(position);
//                loadClothesForCategory(selectedCategory);
//            }
//
//            @Override
//            public void onNothingSelected(android.widget.AdapterView<?> parent) {
//                selectedCategory = null;
//            }
//        });
//
//        // Listener for clothes selection
//        clothesSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
//                selectedClothes = clothesList.get(position); // Store the selected Clothes object
//            }
//
//            @Override
//            public void onNothingSelected(android.widget.AdapterView<?> parent) {
//                selectedClothes = null;
//            }
//        });
//
//        // Remove button click
//        removeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (selectedClothes != null) {
//                    removeClothes(selectedClothes);
//                } else {
//                    Toast.makeText(RemoveClothesActivity.this, "Please select a clothing item", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    // Load the categories from Firestore
//    private void loadCategories() {
//        String userId = mAuth.getCurrentUser().getUid();
//        db.collection("clothes")
//                .whereEqualTo("userId", userId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        categories.clear();
//                        categories.add("Select Category");
//                        if (querySnapshot != null) {
//                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                String category = document.getString("category");
//                                if (!categories.contains(category)) {
//                                    categories.add(category);
//                                }
//                            }
//                        }
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(RemoveClothesActivity.this, android.R.layout.simple_spinner_item, categories);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        categorySpinner.setAdapter(adapter);
//                    } else {
//                        Toast.makeText(RemoveClothesActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Load clothes based on the selected category
//    private void loadClothesForCategory(String category) {
//        String userId = mAuth.getCurrentUser().getUid();
//        db.collection("clothes")
//                .whereEqualTo("userId", userId)
//                .whereEqualTo("category", category)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        clothesList.clear();
//                        clothesNames.clear();
//                        clothesNames.add("Select Clothing");
//
//                        if (querySnapshot != null) {
//                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                Clothes clothes = document.toObject(Clothes.class);
//                                clothesList.add(clothes);
//                                clothesNames.add(clothes.getName());
//                            }
//                        }
//
//                        ArrayAdapter<String> clothesAdapter = new ArrayAdapter<>(RemoveClothesActivity.this, android.R.layout.simple_spinner_item, clothesNames);
//                        clothesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        clothesSpinner.setAdapter(clothesAdapter);
//                    } else {
//                        Toast.makeText(RemoveClothesActivity.this, "Failed to load clothes", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    // Remove the selected clothes from Firestore
//    private void removeClothes(Clothes clothes) {
//        db.collection("clothes").document(clothes.getId())
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(RemoveClothesActivity.this, "Clothes removed successfully", Toast.LENGTH_SHORT).show();
//                    // Reload clothes list after deletion
//                    loadClothesForCategory(selectedCategory);
//                })
//                .addOnFailureListener(e -> Toast.makeText(RemoveClothesActivity.this, "Failed to remove clothes", Toast.LENGTH_SHORT).show());
//    }
//}

//public class RemoveClothesActivity {
//}
