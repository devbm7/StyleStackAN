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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewGroupLaundryActivity extends AppCompatActivity {

    private TextView groupNameTextView, totalLaundryTextView, memberLaundryCountTextView;
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
        totalLaundryTextView = findViewById(R.id.totalLaundryTextView);
        memberLaundryCountTextView = findViewById(R.id.memberLaundryCountTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothesList = new ArrayList<>();
        groupLaundryAdapter = new GroupLaundryAdapter(this, clothesList);
        recyclerView.setAdapter(groupLaundryAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

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
        final int[] totalLaundryCount = {0}; // Tracks total laundry count across all members
        Map<String, Integer> memberLaundryCounts = new HashMap<>(); // Tracks laundry count per member
        Map<String, String> memberNames = new HashMap<>(); // Tracks member names

        for (String memberId : memberIds) {
            db.collection("users").document(memberId).get()
                    .addOnSuccessListener(userDoc -> {
                        String firstName = userDoc.getString("firstName");
                        String lastName = userDoc.getString("lastName");
                        String fullName = firstName + " " + lastName;
                        memberNames.put(memberId, fullName);

                        db.collection("clothes")
                                .whereEqualTo("userId", memberId)
                                .whereEqualTo("inLaundry", true)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    int memberLaundryCount = 0;
                                    for (DocumentSnapshot doc : querySnapshot) {
                                        Clothes clothes = doc.toObject(Clothes.class);
                                        clothes.setOwnerName(fullName);
                                        clothesList.add(clothes);
                                        memberLaundryCount++;
                                        totalLaundryCount[0]++; // Increment total count
                                    }

                                    // Update member-specific laundry count
                                    memberLaundryCounts.put(fullName, memberLaundryCount);
                                    groupLaundryAdapter.notifyDataSetChanged();

                                    // Display updated counts
                                    displayLaundryCounts(totalLaundryCount[0], memberLaundryCounts);
                                });
                    });
        }
    }

    private void displayLaundryCounts(int totalLaundry, Map<String, Integer> memberLaundryCounts) {
        // Display total laundry count
        totalLaundryTextView.setText("Total Clothes in Laundry (Group): " + totalLaundry);

        // Display member-wise laundry counts
        StringBuilder memberCountsText = new StringBuilder("Laundry Count by Member:\n");
        for (Map.Entry<String, Integer> entry : memberLaundryCounts.entrySet()) {
            memberCountsText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" items\n");
        }
        memberLaundryCountTextView.setText(memberCountsText.toString());
    }
}
