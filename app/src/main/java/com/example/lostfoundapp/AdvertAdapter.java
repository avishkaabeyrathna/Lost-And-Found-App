package com.example.lostfoundapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    ArrayList<Advert> entries;

    public AdvertAdapter(ArrayList<Advert> list) {
        this.entries = list;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        Advert item = entries.get(position);

        holder.tvTitle.setText(item.postType + ": " + item.description);
        holder.tvCategory.setText("Category: " + item.category);
        holder.tvTimestamp.setText("Posted: " + item.timestamp);

        // Open details activity on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailActivity.class);
            intent.putExtra("advertId", item.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class AdvertViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvTimestamp;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}