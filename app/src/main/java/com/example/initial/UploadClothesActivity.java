package com.example.initial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadClothesActivity extends AppCompatActivity {

    private ImageView clothesImageView;
    private EditText clothesNameEditText;
    private Spinner clothesCategorySpinner;
    private Button uploadButton, chooseImageButton;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String[] categories = {"Pant", "Shirt", "TShirt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_clothes);

        clothesImageView = findViewById(R.id.clothesImageView);
        clothesNameEditText = findViewById(R.id.clothesNameEditText);
        clothesCategorySpinner = findViewById(R.id.clothesCategorySpinner);
        uploadButton = findViewById(R.id.uploadButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        // Set up the spinner with categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothesCategorySpinner.setAdapter(adapter);
        clothesCategorySpinner.setSelection(0);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadClothesData();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Clothes Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                clothesImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadClothesData() {
        final String clothesName = clothesNameEditText.getText().toString().trim();
        final String clothesCategory = clothesCategorySpinner.getSelectedItem().toString();

        if (imageUri == null || clothesName.isEmpty()) {
            Toast.makeText(UploadClothesActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the image first
        final String imageId = UUID.randomUUID().toString();
        final StorageReference ref = storageReference.child("clothes_images/" + imageId);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Once the image is uploaded, save the metadata to Firestore
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        Map<String, Object> clothesData = new HashMap<>();
                        clothesData.put("name", clothesName);
                        clothesData.put("category", clothesCategory);
                        clothesData.put("imageUrl", uri.toString());
                        clothesData.put("userId", userId);
                        clothesData.put("inLaundry", false);

                        db.collection("clothes").document(imageId)
                                .set(clothesData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UploadClothesActivity.this, "Clothes uploaded successfully", Toast.LENGTH_SHORT).show();
                                    finish(); // Go back to the previous screen
                                })
                                .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload clothes data", Toast.LENGTH_SHORT).show());
                    }
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }
}


//public class UploadClothesActivity {
//}
