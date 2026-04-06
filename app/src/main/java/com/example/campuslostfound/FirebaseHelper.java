package com.example.campuslostfound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private DatabaseReference databaseReference;
    private static FirebaseHelper instance;

    public FirebaseHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://misplacedmate-default-rtdb.asia-southeast1.firebasedatabase.app"
        );
        databaseReference = database.getReference("items");
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // Convert image URI to Base64 string
    public String convertImageToBase64(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver()
                    .openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            // Resize bitmap to reduce size
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveItem(Context context, String type, String itemName,
                         String category, String location, String date,
                         String description, String contact, Uri imageUri) {
        String itemId = databaseReference.push().getKey();
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("type", type);
        itemMap.put("itemName", itemName);
        itemMap.put("category", category);
        itemMap.put("location", location);
        itemMap.put("date", date);
        itemMap.put("description", description);
        itemMap.put("contact", contact);
        itemMap.put("status", "Active");

        // Debug log
        android.util.Log.d("FIREBASE", "imageUri = " + imageUri);

        // Convert image to Base64 if available
        if (imageUri != null) {
            android.util.Log.d("FIREBASE", "Converting image to Base64...");
            String base64Image = convertImageToBase64(context, imageUri);
            android.util.Log.d("FIREBASE", "Base64 result = " +
                    (base64Image != null ? "SUCCESS length=" + base64Image.length() : "NULL"));
            if (base64Image != null) {
                itemMap.put("imageBase64", base64Image);
            }
        } else {
            android.util.Log.d("FIREBASE", "imageUri is NULL - no image to save");
        }

        if (itemId != null) {
            databaseReference.child(itemId).setValue(itemMap)
                    .addOnSuccessListener(aVoid -> {
                        android.util.Log.d("FIREBASE", "Data saved successfully!");
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.d("FIREBASE", "Failed to save: " + e.getMessage());
                    });
        }
    }

    public void deleteItem(String firebaseId) {
        if (firebaseId != null) {
            databaseReference.child(firebaseId).removeValue();
        }
    }

    public DatabaseReference getReference() {
        return databaseReference;
    }
}