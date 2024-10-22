package com.example.initial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LaundryAdapter extends RecyclerView.Adapter<LaundryAdapter.LaundryViewHolder> {

    private List<Clothes> clothesList;
    private LaundryActivity laundryActivity;

    public LaundryAdapter(List<Clothes> clothesList) {
        this.clothesList = clothesList;
    }

    @NonNull
    @Override
    public LaundryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laundry_item, parent, false);
        laundryActivity = (LaundryActivity) parent.getContext();
        return new LaundryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryViewHolder holder, int position) {
        Clothes clothes = clothesList.get(position);
        holder.nameTextView.setText(clothes.getName());
        holder.statusTextView.setText(clothes.isInLaundry() ? "In Laundry" : "Not in Laundry");

        holder.sendToLaundryButton.setVisibility(clothes.isInLaundry() ? View.GONE : View.VISIBLE);
        holder.takeBackButton.setVisibility(clothes.isInLaundry() ? View.VISIBLE : View.GONE);

        holder.sendToLaundryButton.setOnClickListener(v -> laundryActivity.markAsInLaundry(clothes));
        holder.takeBackButton.setOnClickListener(v -> laundryActivity.takeBackFromLaundry(clothes));
    }

    @Override
    public int getItemCount() {
        return clothesList.size();
    }

    public static class LaundryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, statusTextView;
        Button sendToLaundryButton, takeBackButton;

        public LaundryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            sendToLaundryButton = itemView.findViewById(R.id.sendToLaundryButton);
            takeBackButton = itemView.findViewById(R.id.takeBackButton);
        }
    }
}
