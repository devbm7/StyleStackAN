package com.example.initial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder> {

    private List<Clothes> clothesList;
    private Context context;

    public ClothesAdapter(Context context, List<Clothes> clothesList) {
        this.clothesList = clothesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothes_item, parent, false);
        return new ClothesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothesViewHolder holder, int position) {
        Clothes clothes = clothesList.get(position);
        holder.nameTextView.setText(clothes.getName());
        holder.categoryTextView.setText(clothes.getCategory());

        // Load image using Picasso
        Picasso.get().load(clothes.getImageUrl()).into(holder.imageView);

        // Set onClickListener to open the full view in ClothesDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClothesDetailsActivity.class);
            intent.putExtra("name", clothes.getName());
            intent.putExtra("category", clothes.getCategory());
            intent.putExtra("imageUrl", clothes.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clothesList.size();
    }

    public static class ClothesViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, categoryTextView;
        ImageView imageView;

        public ClothesViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

//public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder> {
//
//    private List<Clothes> clothesList;
//
//    public ClothesAdapter(List<Clothes> clothesList) {
//        this.clothesList = clothesList;
//    }
//
//    @NonNull
//    @Override
//    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothes_item_with_checkbox, parent, false);
//        return new ClothesViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ClothesViewHolder holder, int position) {
//        Clothes clothes = clothesList.get(position);
//        holder.nameTextView.setText(clothes.getName());
//        holder.categoryTextView.setText(clothes.getCategory());
//
//        // Load image using Picasso
//        Picasso.get().load(clothes.getImageUrl()).into(holder.imageView);
//
//        // Handle selection
//        holder.selectCheckBox.setChecked(clothes.isSelected());
//        holder.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> clothes.setSelected(isChecked));
//    }
//
//    @Override
//    public int getItemCount() {
//        return clothesList.size();
//    }
//
//    public static class ClothesViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView, categoryTextView;
//        ImageView imageView;
//        CheckBox selectCheckBox;
//
//        public ClothesViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.nameTextView);
//            categoryTextView = itemView.findViewById(R.id.categoryTextView);
//            imageView = itemView.findViewById(R.id.imageView);
//            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
//        }
//    }
//}
