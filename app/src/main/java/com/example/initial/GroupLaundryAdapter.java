package com.example.initial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupLaundryAdapter extends RecyclerView.Adapter<GroupLaundryAdapter.LaundryViewHolder> {
    private List<Clothes> clothesList;
    private Context context;

    public GroupLaundryAdapter(Context context, List<Clothes> clothesList) {
        this.context = context;
        this.clothesList = clothesList;
    }

    @Override
    public LaundryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_laundry_item, parent, false);
        return new LaundryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LaundryViewHolder holder, int position) {
        Clothes clothes = clothesList.get(position);
        holder.clothesNameTextView.setText(clothes.getName());
        holder.categoryTextView.setText(clothes.getCategory());
        holder.ownerNameTextView.setText(String.format("Owner: %s", clothes.getOwnerName()));
    }

    @Override
    public int getItemCount() {
        return clothesList.size();
    }

    public class LaundryViewHolder extends RecyclerView.ViewHolder {
        TextView clothesNameTextView, categoryTextView, ownerNameTextView;

        public LaundryViewHolder(View itemView) {
            super(itemView);
            clothesNameTextView = itemView.findViewById(R.id.clothesNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
        }
    }
}

//public class GroupLaundryAdapter {
//}
