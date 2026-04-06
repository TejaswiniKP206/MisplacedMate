package com.example.campuslostfound;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailActivity extends AppCompatActivity {

    TextView tvType, tvItemName, tvCategory, tvLocation,
            tvDate, tvDescription, tvContact;
    ImageView imgItem;
    Button btnResolved, btnDelete;
    DatabaseHelper db;
    int itemId;
    String firebaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        tvType = findViewById(R.id.tvType);
        tvItemName = findViewById(R.id.tvItemName);
        tvCategory = findViewById(R.id.tvCategory);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvContact = findViewById(R.id.tvContact);
        imgItem = findViewById(R.id.imgItem);
        btnResolved = findViewById(R.id.btnResolved);
        btnDelete = findViewById(R.id.btnDelete);

        db = new DatabaseHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Item Details");
        }

        // Get data from intent
        itemId = getIntent().getIntExtra("id", -1);
        firebaseId = getIntent().getStringExtra("firebaseId");
        firebaseId = getIntent().getStringExtra("firebaseId");
        android.util.Log.d("DEBUG", "firebaseId = " + firebaseId);
        String type = getIntent().getStringExtra("type");
        String itemName = getIntent().getStringExtra("itemName");
        String category = getIntent().getStringExtra("category");
        String location = getIntent().getStringExtra("location");
        String date = getIntent().getStringExtra("date");
        String description = getIntent().getStringExtra("description");
        String contact = getIntent().getStringExtra("contact");
        String imageUri = getIntent().getStringExtra("imageUri");
        String imageBase64 = getIntent().getStringExtra("imageBase64");

        // Set data to views
        tvItemName.setText(itemName);
        tvCategory.setText(category);
        tvLocation.setText(location);
        tvDate.setText(date);
        tvDescription.setText(description != null &&
                !description.isEmpty() ? description : "No description provided");
        tvContact.setText(contact);

        // Set type badge
        if (type != null && type.equals("Lost")) {
            tvType.setText("LOST");
            tvType.setBackgroundColor(0xFFE53935);
        } else {
            tvType.setText("FOUND");
            tvType.setBackgroundColor(0xFF43A047);
        }

        // Show image - try Base64 first then local URI
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] decodedBytes = android.util.Base64.decode(
                        imageBase64, android.util.Base64.DEFAULT);
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory
                        .decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imgItem.setVisibility(View.VISIBLE);
                imgItem.setImageBitmap(bitmap);
            } catch (Exception e) {
                imgItem.setVisibility(View.GONE);
            }
        } else if (imageUri != null && !imageUri.isEmpty()) {
            try {
                imgItem.setVisibility(View.VISIBLE);
                imgItem.setImageURI(Uri.parse(imageUri));
            } catch (Exception e) {
                imgItem.setVisibility(View.GONE);
            }
        }

        // Mark as resolved button
        btnResolved.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Mark as Resolved")
                    .setMessage("Has this item been returned to its owner?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete from Firebase first
                        if (firebaseId != null && !firebaseId.isEmpty()) {
                            FirebaseHelper.getInstance()
                                    .getReference()
                                    .child(firebaseId)
                                    .removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Also delete from SQLite
                                        if (itemId != -1) {
                                            db.deleteItem(itemId);
                                        }
                                        Toast.makeText(ItemDetailActivity.this,
                                                "Marked as resolved!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ItemDetailActivity.this,
                                                "Failed to resolve. Try again.",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // No Firebase ID just delete locally
                            if (itemId != -1) db.deleteItem(itemId);
                            Toast.makeText(this, "Marked as resolved!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Delete button
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Delete from Firebase first
                        if (firebaseId != null && !firebaseId.isEmpty()) {
                            FirebaseHelper.getInstance()
                                    .getReference()
                                    .child(firebaseId)
                                    .removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        // Also delete from SQLite
                                        if (itemId != -1) {
                                            db.deleteItem(itemId);
                                        }
                                        Toast.makeText(ItemDetailActivity.this,
                                                "Post deleted!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ItemDetailActivity.this,
                                                "Failed to delete. Try again.",
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // No Firebase ID just delete locally
                            if (itemId != -1) db.deleteItem(itemId);
                            Toast.makeText(this, "Post deleted!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}