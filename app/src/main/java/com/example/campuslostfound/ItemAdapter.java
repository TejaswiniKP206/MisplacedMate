package com.example.campuslostfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    Context context;
    List<ItemModel> itemList;

    public ItemAdapter(Context context, List<ItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            ItemModel item = itemList.get(position);

            holder.tvItemName.setText(item.getItemName());
            holder.tvCategory.setText(item.getCategory());
            holder.tvLocation.setText("Location: " + item.getLocation());
            holder.tvDate.setText("Date: " + item.getDate());
            holder.tvContact.setText("Contact: " + item.getContact());

            // Set type badge color
            if (item.getType() != null && item.getType().equals("Lost")) {
                holder.tvType.setText("LOST");
                holder.tvType.setBackgroundColor(0xFFE53935);
            } else {
                holder.tvType.setText("FOUND");
                holder.tvType.setBackgroundColor(0xFF43A047);
            }

            // Try Base64 image first (from Firebase)
            if (item.getImageBase64() != null && !item.getImageBase64().isEmpty()) {
                try {
                    byte[] decodedBytes = android.util.Base64.decode(
                            item.getImageBase64(), android.util.Base64.DEFAULT);
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory
                            .decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    holder.imgItem.setVisibility(View.VISIBLE);
                    holder.imgItem.setImageBitmap(bitmap);
                } catch (Exception e) {
                    holder.imgItem.setVisibility(View.GONE);
                }
// Fall back to local URI
            } else if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                try {
                    holder.imgItem.setVisibility(View.VISIBLE);
                    holder.imgItem.setImageURI(null);
                    holder.imgItem.setImageURI(Uri.parse(item.getImageUri()));
                } catch (Exception e) {
                    holder.imgItem.setVisibility(View.GONE);
                }
            } else {
                holder.imgItem.setVisibility(View.GONE);
            }

            // Click to open detail screen
            holder.itemView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra("id", item.getId());
                    intent.putExtra("type", item.getType());
                    intent.putExtra("itemName", item.getItemName());
                    intent.putExtra("category", item.getCategory());
                    intent.putExtra("location", item.getLocation());
                    intent.putExtra("date", item.getDate());
                    intent.putExtra("description", item.getDescription());
                    intent.putExtra("contact", item.getContact());
                    intent.putExtra("imageUri", item.getImageUri());
                    intent.putExtra("status", item.getStatus());
                    intent.putExtra("firebaseId", item.getFirebaseId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("imageBase64", item.getImageBase64());
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateList(List<ItemModel> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvCategory, tvLocation,
                tvDate, tvContact, tvType;
        ImageView imgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvType = itemView.findViewById(R.id.tvType);
            imgItem = itemView.findViewById(R.id.imgItem);
        }
    }
}