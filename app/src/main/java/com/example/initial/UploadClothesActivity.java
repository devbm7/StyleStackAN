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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.MediaType;
import okhttp3.RequestBody;



public class UploadClothesActivity extends AppCompatActivity {

    private ImageView clothesImageView;
    private EditText clothesNameEditText;
    private Spinner clothesCategorySpinner;
    private Button uploadButton, chooseImageButton, generateImageButton;
    private Uri imageUri;
    private String generatedImageUrl;

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
        generateImageButton = findViewById(R.id.generateAIImage);

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

        // Image selection button
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        // AI image generation button
        generateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clothesName = clothesNameEditText.getText().toString().trim();
                if (clothesName.isEmpty()) {
                    Toast.makeText(UploadClothesActivity.this, "Please enter a clothing name to generate an image", Toast.LENGTH_SHORT).show();
                } else {
                    generateAIImage(clothesName);
                }
            }
        });

        // Upload data button
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
            generatedImageUrl = null;  // Clear generated URL if a new image is chosen
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                clothesImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Update method to handle binary response data
    private void generateAIImage(String prompt) {
        String apiKey = "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"; // Replace with your actual API key
        String url = "https://api-inference.huggingface.co/models/prompthero/openjourney";

        OkHttpClient client = new OkHttpClient();
        String jsonBody = "{ \"inputs\": \"" + prompt + "\" }";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json") // Explicitly set Content-Type
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Failed to generate image", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    // Read the response as a byte array
                    byte[] imageBytes = response.body().bytes();

                    // Decode the byte array into a Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    // Check if the Bitmap was successfully created
                    if (bitmap != null) {
                        runOnUiThread(() -> clothesImageView.setImageBitmap(bitmap));
                        imageUri = null;  // Clear imageUri, as we're using the generated image
                    } else {
                        runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Error decoding image", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(UploadClothesActivity.this, "Failed to generate image", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String parseImageUrl(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        return jsonObject.getString("imageUrl");  // Adjust based on API response format
    }

    private void loadImageIntoImageView(String imageUrl) {
        Picasso.get().load(imageUrl).into(clothesImageView);
    }

    private void uploadClothesData() {
        final String clothesName = clothesNameEditText.getText().toString().trim();
        final String clothesCategory = clothesCategorySpinner.getSelectedItem().toString();

        if ((imageUri == null && generatedImageUrl == null) || clothesName.isEmpty()) {
            Toast.makeText(UploadClothesActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToFirebase();
        } else if (generatedImageUrl != null) {
            saveClothesDataToFirestore(generatedImageUrl);
        }
    }

    private void uploadImageToFirebase() {
        final String imageId = UUID.randomUUID().toString();
        final StorageReference ref = storageReference.child("clothes_images/" + imageId);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveClothesDataToFirestore(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void saveClothesDataToFirestore(String imageUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String imageId = UUID.randomUUID().toString();

            Map<String, Object> clothesData = new HashMap<>();
            clothesData.put("id", imageId);
            clothesData.put("name", clothesNameEditText.getText().toString().trim());
            clothesData.put("category", clothesCategorySpinner.getSelectedItem().toString());
            clothesData.put("imageUrl", imageUrl);
            clothesData.put("userId", userId);
            clothesData.put("inLaundry", false);

            db.collection("clothes").document(imageId)
                    .set(clothesData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UploadClothesActivity.this, "Clothes uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(UploadClothesActivity.this, "Failed to upload clothes data", Toast.LENGTH_SHORT).show());
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
