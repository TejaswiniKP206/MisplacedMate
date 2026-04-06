package com.example.campuslostfound;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportFoundActivity extends AppCompatActivity {

    EditText etItemName, etLocation, etDate, etDescription, etContact;
    Spinner spinnerCategory;
    Button btnSubmitFound, btnAddImage;
    ImageButton btnCancelImage;
    ImageView imgPreview;
    FrameLayout imageContainer;
    Uri selectedImageUri = null;
    Uri cameraImageUri = null;

    String[] categories = {"Electronics", "Stationery", "Clothing",
            "Accessories", "Books", "ID Card", "Other"};

    ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPreview.setImageURI(uri);
                    imageContainer.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Image selected!", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraImageUri != null) {
                    selectedImageUri = cameraImageUri;
                    imgPreview.setImageURI(null);
                    imgPreview.setImageURI(cameraImageUri);
                    imageContainer.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Photo taken!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_found);

        etItemName = findViewById(R.id.etItemName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etContact = findViewById(R.id.etContact);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmitFound = findViewById(R.id.btnSubmitFound);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnCancelImage = findViewById(R.id.btnCancelImage);
        imgPreview = findViewById(R.id.imgPreview);
        imageContainer = findViewById(R.id.imageContainer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Report Found Item");
        }

        // Spinner with black text
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.BLACK);
                ((TextView) view).setTextSize(16);
                view.setBackgroundColor(Color.TRANSPARENT);
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.BLACK);
                ((TextView) view).setTextSize(16);
                ((TextView) view).setPadding(16, 16, 16, 16);
                view.setBackgroundColor(Color.WHITE);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        btnAddImage.setOnClickListener(v -> showImagePickerDialog());

        btnCancelImage.setOnClickListener(v -> {
            selectedImageUri = null;
            imgPreview.setImageURI(null);
            imageContainer.setVisibility(View.GONE);
            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
        });

        btnSubmitFound.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String contact = etContact.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String imageUri = "";
            if (selectedImageUri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(
                            selectedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    imageUri = selectedImageUri.toString();
                } catch (Exception e) {
                    imageUri = selectedImageUri.toString();
                }
            }

            if (itemName.isEmpty() || location.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to SQLite
            DatabaseHelper db = new DatabaseHelper(this);
            db.insertItem("Found", itemName, category, location,
                    date, description, contact, imageUri);

            // Save to Firebase
            FirebaseHelper.getInstance().saveItem(this, "Found", itemName, category,
                    location, date, description, contact, selectedImageUri);

            Toast.makeText(this, "Found item reported successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take a Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA);
                }
            } else if (which == 1) {
                galleryLauncher.launch("image/*");
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error opening camera: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}