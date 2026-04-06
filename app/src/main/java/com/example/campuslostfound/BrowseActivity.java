package com.example.campuslostfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ItemAdapter adapter;
    List<ItemModel> allItems;
    DatabaseHelper db;
    SearchView searchView;
    Button btnAll, btnLost, btnFound;
    TextView tvEmpty;
    DatabaseReference firebaseRef;
    ValueEventListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        btnAll = findViewById(R.id.btnAll);
        btnLost = findViewById(R.id.btnLost);
        btnFound = findViewById(R.id.btnFound);
        tvEmpty = findViewById(R.id.tvEmpty);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Browse Items");
        }

        db = new DatabaseHelper(this);
        allItems = new ArrayList<>();

        adapter = new ItemAdapter(this, allItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup Firebase reference
        firebaseRef = com.google.firebase.database.FirebaseDatabase.getInstance(
                        "https://misplacedmate-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("items");

        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemModel item = new ItemModel();
                    item.setFirebaseId(snapshot.getKey());
                    item.setType(snapshot.child("type").getValue(String.class));
                    item.setItemName(snapshot.child("itemName").getValue(String.class));
                    item.setCategory(snapshot.child("category").getValue(String.class));
                    item.setLocation(snapshot.child("location").getValue(String.class));
                    item.setDate(snapshot.child("date").getValue(String.class));
                    item.setDescription(snapshot.child("description").getValue(String.class));
                    item.setContact(snapshot.child("contact").getValue(String.class));
                    item.setStatus(snapshot.child("status").getValue(String.class));
                    item.setImageBase64(snapshot.child("imageBase64").getValue(String.class));
                    allItems.add(item);
                }
                filterAndSearch(searchView.getQuery().toString());
                checkEmpty(allItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BrowseActivity.this,
                        "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        firebaseRef.addValueEventListener(firebaseListener);

        // Filter buttons
        btnAll.setSelected(true);
        btnAll.setOnClickListener(v -> {
            btnAll.setSelected(true);
            btnLost.setSelected(false);
            btnFound.setSelected(false);
            filterAndSearch(searchView.getQuery().toString());
        });

        btnLost.setOnClickListener(v -> {
            btnAll.setSelected(false);
            btnLost.setSelected(true);
            btnFound.setSelected(false);
            filterAndSearch(searchView.getQuery().toString());
        });

        btnFound.setOnClickListener(v -> {
            btnAll.setSelected(false);
            btnLost.setSelected(false);
            btnFound.setSelected(true);
            filterAndSearch(searchView.getQuery().toString());
        });

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAndSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndSearch(newText);
                return true;
            }
        });

        // Search bar text color fix
        try {
            int searchSrcTextId = getResources().getIdentifier(
                    "search_src_text", "id", "android");
            android.widget.EditText searchEditText =
                    searchView.findViewById(searchSrcTextId);
            if (searchEditText != null) {
                searchEditText.setTextColor(android.graphics.Color.BLACK);
                searchEditText.setHintTextColor(android.graphics.Color.GRAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterAndSearch(String query) {
        List<ItemModel> filteredList = new ArrayList<>();

        for (ItemModel item : allItems) {
            if (item == null) continue;

            boolean matchesSearch = true;
            boolean matchesFilter = true;

            if (query != null && !query.isEmpty()) {
                String itemName = item.getItemName();
                if (itemName != null) {
                    matchesSearch = itemName.toLowerCase()
                            .contains(query.toLowerCase());
                }
            }

            if (btnLost.isSelected()) {
                matchesFilter = "Lost".equals(item.getType());
            } else if (btnFound.isSelected()) {
                matchesFilter = "Found".equals(item.getType());
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(item);
            }
        }

        adapter.updateList(filteredList);
        checkEmpty(filteredList);
    }

    private void checkEmpty(List<ItemModel> list) {
        if (list == null || list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRef != null && firebaseListener != null) {
            firebaseRef.addValueEventListener(firebaseListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseRef != null && firebaseListener != null) {
            firebaseRef.removeEventListener(firebaseListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}