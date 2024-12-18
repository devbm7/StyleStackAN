package com.example.initial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;



public class ProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText;
    private EditText groupNameEditText;
    private ImageView profileImageView;
    private Button uploadImageButton, saveProfileButton;
    private Button joinGroupButton, leaveGroupButton;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userId;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        profileImageView = findViewById(R.id.profileImageView);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        joinGroupButton = findViewById(R.id.joinGroupButton);
        leaveGroupButton = findViewById(R.id.leaveGroupButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userId = mAuth.getCurrentUser().getUid();

        loadUserProfile();


        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title of the toolbar
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");

        // Enable the back button (optional)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
            }
        });

        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    joinGroup(groupName);
                } else {
                    Toast.makeText(ProfileActivity.this, "Enter a group name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                            firstNameEditText.setText(firstName);
                            lastNameEditText.setText(lastName);

                            if (profileImageUrl != null) {
                                loadProfileImage(profileImageUrl);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadProfileImage(String url) {
        Picasso.get().load(url).into(profileImageView);
    }

    private void saveUserProfile() {
        final String firstName = firstNameEditText.getText().toString().trim();
        final String lastName = lastNameEditText.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            final String userId = user.getUid();

            if (imageUri != null) {
                // Upload the profile image first
                final String imageId = UUID.randomUUID().toString();
                final StorageReference ref = storageReference.child("profile_images/" + imageId);

                ref.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Once the image is uploaded, save the profile data
                            saveProfileData(userId, firstName, lastName, uri.toString());
                        }))
                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to upload profile image", Toast.LENGTH_SHORT).show());
            } else {
                // If no new image is uploaded, just save the text data
                saveProfileData(userId, firstName, lastName, null);
            }
        }
    }

    private void saveProfileData(String userId, String firstName, String lastName, String profileImageUrl) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("firstName", firstName);
        profileData.put("lastName", lastName);
        if (profileImageUrl != null) {
            profileData.put("profileImageUrl", profileImageUrl);
        }

        db.collection("users").document(userId)
                .set(profileData)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    private void joinGroup(String groupName){
        // Create or join the group
        db.collection("groups")
                .whereEqualTo("groupName", groupName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Group exists - join it
                        String groupId = task.getResult().getDocuments().get(0).getId();
                        addUserToGroup(groupId);
                    } else {
                        // Group does not exist - create and join it
                        Map<String, Object> groupData = new HashMap<>();
                        groupData.put("groupName", groupName);
                        groupData.put("members", new ArrayList<>(Collections.singletonList(userId)));

                        db.collection("groups").add(groupData)
                                .addOnSuccessListener(documentReference -> addUserToGroup(documentReference.getId()))
                                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show());
                    }
                });
    }
    private void addUserToGroup(String groupId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("currentGroupId", groupId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Joined group", Toast.LENGTH_SHORT).show();
                    db.collection("groups").document(groupId)
                            .update("members", FieldValue.arrayUnion(userId));
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to join group", Toast.LENGTH_SHORT).show());
    }

    private void leaveGroup() {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            String groupId = documentSnapshot.getString("currentGroupId");

            if (groupId != null) {
                userRef.update("currentGroupId", null)
                        .addOnSuccessListener(aVoid -> {
                            db.collection("groups").document(groupId)
                                    .update("members", FieldValue.arrayRemove(userId));
                            Toast.makeText(ProfileActivity.this, "Left group", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to leave group", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ProfileActivity.this, "Not part of any group", Toast.LENGTH_SHORT).show();
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

//public class ProfileActivity extends AppCompatActivity {
//
//    private EditText groupNameEditText;
//    private Button joinGroupButton, leaveGroupButton;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//    private String userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        groupNameEditText = findViewById(R.id.groupNameEditText);
//        joinGroupButton = findViewById(R.id.joinGroupButton);
//        leaveGroupButton = findViewById(R.id.leaveGroupButton);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        userId = mAuth.getCurrentUser().getUid();
//
//        joinGroupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String groupName = groupNameEditText.getText().toString().trim();
//                if (!groupName.isEmpty()) {
//                    joinGroup(groupName);
//                } else {
//                    Toast.makeText(ProfileActivity.this, "Enter a group name", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                leaveGroup();
//            }
//        });
//    }
//
//    private void joinGroup(String groupName) {
//        // Create or join the group
//        db.collection("groups")
//                .whereEqualTo("groupName", groupName)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        // Group exists - join it
//                        String groupId = task.getResult().getDocuments().get(0).getId();
//                        addUserToGroup(groupId);
//                    } else {
//                        // Group does not exist - create and join it
//                        Map<String, Object> groupData = new HashMap<>();
//                        groupData.put("groupName", groupName);
//                        groupData.put("members", new ArrayList<>(Collections.singletonList(userId)));
//
//                        db.collection("groups").add(groupData)
//                                .addOnSuccessListener(documentReference -> addUserToGroup(documentReference.getId()))
//                                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to create group", Toast.LENGTH_SHORT).show());
//                    }
//                });
//    }
//
//    private void addUserToGroup(String groupId) {
//        DocumentReference userRef = db.collection("users").document(userId);
//        userRef.update("currentGroupId", groupId)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(ProfileActivity.this, "Joined group", Toast.LENGTH_SHORT).show();
//                    db.collection("groups").document(groupId)
//                            .update("members", FieldValue.arrayUnion(userId));
//                })
//                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to join group", Toast.LENGTH_SHORT).show());
//    }
//
//    private void leaveGroup() {
//        DocumentReference userRef = db.collection("users").document(userId);
//
//        userRef.get().addOnSuccessListener(documentSnapshot -> {
//            String groupId = documentSnapshot.getString("currentGroupId");
//
//            if (groupId != null) {
//                userRef.update("currentGroupId", null)
//                        .addOnSuccessListener(aVoid -> {
//                            db.collection("groups").document(groupId)
//                                    .update("members", FieldValue.arrayRemove(userId));
//                            Toast.makeText(ProfileActivity.this, "Left group", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to leave group", Toast.LENGTH_SHORT).show());
//            } else {
//                Toast.makeText(ProfileActivity.this, "Not part of any group", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
