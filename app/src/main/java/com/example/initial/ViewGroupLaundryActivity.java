package com.example.initial;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewGroupLaundryActivity extends AppCompatActivity {

    private TextView groupNameTextView;
    private RecyclerView recyclerView;
    private GroupLaundryAdapter groupLaundryAdapter;
    private List<Clothes> clothesList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId, groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group_laundry);

        groupNameTextView = findViewById(R.id.groupNameTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothesList = new ArrayList<>();
        groupLaundryAdapter = new GroupLaundryAdapter(this, clothesList);
        recyclerView.setAdapter(groupLaundryAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        loadGroupLaundry();
    }

    private void loadGroupLaundry() {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDoc -> {
                    groupId = userDoc.getString("currentGroupId");

                    if (groupId != null) {
                        db.collection("groups").document(groupId).get()
                                .addOnSuccessListener(groupDoc -> {
                                    String groupName = groupDoc.getString("groupName");
                                    groupNameTextView.setText("Group: " + groupName);

                                    List<String> memberIds = (List<String>) groupDoc.get("members");
                                    fetchLaundryForGroupMembers(memberIds);
                                });
                    } else {
                        Toast.makeText(this, "You are not part of any group", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchLaundryForGroupMembers(List<String> memberIds) {
        clothesList.clear();

        for (String memberId : memberIds) {
            db.collection("users").document(memberId).get()
                    .addOnSuccessListener(userDoc -> {
                        String firstName = userDoc.getString("firstName");
                        String lastName = userDoc.getString("lastName");

                        db.collection("clothes")
                                .whereEqualTo("userId", memberId)
                                .whereEqualTo("inLaundry", true)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot) {
                                        Clothes clothes = doc.toObject(Clothes.class);
                                        clothes.setOwnerName(firstName + " " + lastName); // Set owner's name
                                        clothesList.add(clothes);
                                    }
                                    groupLaundryAdapter.notifyDataSetChanged();
                                });
                    });
        }
    }
}

//public class ViewGroupLaundryActivity extends AppCompatActivity {
//
//    private TextView groupNameTextView;
//    private RecyclerView recyclerView;
//    private GroupLaundryAdapter groupLaundryAdapter;
//    private List<Clothes> clothesList;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//    private String userId, groupId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_group_laundry);
//
//        groupNameTextView = findViewById(R.id.groupNameTextView);
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        clothesList = new ArrayList<>();
//        groupLaundryAdapter = new GroupLaundryAdapter(this, clothesList);
//        recyclerView.setAdapter(groupLaundryAdapter);
//
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        userId = mAuth.getCurrentUser().getUid();
//
//        loadGroupLaundry();
//    }
//
//    private void loadGroupLaundry() {
//        db.collection("users").document(userId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    groupId = documentSnapshot.getString("currentGroupId");
//
//                    if (groupId != null) {
//                        db.collection("groups").document(groupId)
//                                .get()
//                                .addOnSuccessListener(groupDoc -> {
//                                    String groupName = groupDoc.getString("groupName");
//                                    groupNameTextView.setText("Group: " + groupName);
//
//                                    db.collection("clothes")
//                                            .whereEqualTo("groupId", groupId)
//                                            .whereEqualTo("inLaundry", true)
//                                            .get()
//                                            .addOnSuccessListener(querySnapshot -> {
//                                                clothesList.clear();
//                                                for (DocumentSnapshot document : querySnapshot) {
//                                                    Clothes clothes = document.toObject(Clothes.class);
//                                                    clothesList.add(clothes);
//                                                }
//                                                groupLaundryAdapter.notifyDataSetChanged();
//                                            });
//                                });
//                    } else {
//                        Toast.makeText(this, "You are not part of any group", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}
//
//public class ViewGroupLaundryActivity {
//}
