package com.example.initial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadClothesActivity extends AppCompatActivity {

    private ImageView clothesImageView;
    private EditText clothesNameEditText;
    private Spinner clothesCategorySpinner;
    private Button uploadButton, chooseImageButton, generateAIImageButton;
    private Uri imageUri;
    private Bitmap aiGeneratedBitmap;  // Store the generated AI Bitmap
    private boolean isAIGeneratedImage = false;

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

        // Initialize UI components
        clothesImageView = findViewById(R.id.clothesImageView);
        clothesNameEditText = findViewById(R.id.clothesNameEditText);
        clothesCategorySpinner = findViewById(R.id.clothesCategorySpinner);
        uploadButton = findViewById(R.id.uploadButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        generateAIImageButton = findViewById(R.id.generateAIImage);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Clothes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up the spinner with categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothesCategorySpinner.setAdapter(adapter);
        clothesCategorySpinner.setSelection(0);

        // Image selection from gallery
        chooseImageButton.setOnClickListener(view -> chooseImage());

        // AI image generation button
        generateAIImageButton.setOnClickListener(view -> {
            String clothesName = clothesNameEditText.getText().toString().trim();
            if (clothesName.isEmpty()) {
                Toast.makeText(UploadClothesActivity.this, "Please enter a name for the clothes to generate an image", Toast.LENGTH_SHORT).show();
            } else {
                generateAIImage(clothesName);
            }
        });

        // Upload button handler
        uploadButton.setOnClickListener(view -> uploadClothesData());
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
            isAIGeneratedImage = false;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                clothesImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generateAIImage(String prompt) {
        String apiKey = "hf_xxxxxxxxxxxxxxxxxxxxxxxxxx";
        String url = "https://api-inference.huggingface.co/models/prompthero/openjourney";

        OkHttpClient client = new OkHttpClient();
        String specificPrompt = "A " + prompt + " lying on a plain white background";
        String jsonBody = "{ \"inputs\": \"" + specificPrompt + "\" }";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Failed to generate image", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] imageBytes = response.body().bytes();
                    aiGeneratedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (aiGeneratedBitmap != null) {
                        runOnUiThread(() -> {
                            clothesImageView.setImageBitmap(aiGeneratedBitmap);
                            isAIGeneratedImage = true;
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Error decoding image", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Failed to generate image", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void uploadClothesData() {
        final String clothesName = clothesNameEditText.getText().toString().trim();
        final String clothesCategory = clothesCategorySpinner.getSelectedItem().toString();

        if ((imageUri == null && !isAIGeneratedImage) || clothesName.isEmpty()) {
            Toast.makeText(UploadClothesActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        final String imageId = UUID.randomUUID().toString();
        final StorageReference ref = storageReference.child("clothes_images/" + imageId);

        if (isAIGeneratedImage && aiGeneratedBitmap != null) {
            // Convert Bitmap to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            aiGeneratedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            // Upload byte array to Firebase
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> clothesData = new HashMap<>();
                            clothesData.put("id", imageId);
                            clothesData.put("name", clothesName);
                            clothesData.put("category", clothesCategory);
                            clothesData.put("imageUrl", uri.toString());
                            clothesData.put("userId", user.getUid());
                            clothesData.put("inLaundry", false);

                            db.collection("clothes").document(imageId)
                                    .set(clothesData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(UploadClothesActivity.this, "Clothes uploaded successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload clothes data", Toast.LENGTH_SHORT).show());
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload AI-generated image", Toast.LENGTH_SHORT).show());
        } else if (!isAIGeneratedImage) {
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> clothesData = new HashMap<>();
                            clothesData.put("id", imageId);
                            clothesData.put("name", clothesName);
                            clothesData.put("category", clothesCategory);
                            clothesData.put("imageUrl", uri.toString());
                            clothesData.put("userId", user.getUid());
                            clothesData.put("inLaundry", false);

                            db.collection("clothes").document(imageId)
                                    .set(clothesData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(UploadClothesActivity.this, "Clothes uploaded successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload clothes data", Toast.LENGTH_SHORT).show());
                        }
                    }))
                    .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
