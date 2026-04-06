package com.example.campuslostfound;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView tvLostCount, tvFoundCount;
    CardView cardReportLost, cardReportFound, cardBrowse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLostCount = findViewById(R.id.tvLostCount);
        tvFoundCount = findViewById(R.id.tvFoundCount);
        cardReportLost = findViewById(R.id.cardReportLost);
        cardReportFound = findViewById(R.id.cardReportFound);
        cardBrowse = findViewById(R.id.cardBrowse);

        cardReportLost.setOnClickListener(v ->
                startActivity(new Intent(this, ReportLostActivity.class)));

        cardReportFound.setOnClickListener(v ->
                startActivity(new Intent(this, ReportFoundActivity.class)));

        cardBrowse.setOnClickListener(v ->
                startActivity(new Intent(this, BrowseActivity.class)));

        // Load live counts from Firebase
        FirebaseHelper.getInstance().getReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int lostCount = 0;
                        int foundCount = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String type = snapshot.child("type").getValue(String.class);
                            if ("Lost".equals(type)) lostCount++;
                            else if ("Found".equals(type)) foundCount++;
                        }
                        tvLostCount.setText(String.valueOf(lostCount));
                        tvFoundCount.setText(String.valueOf(foundCount));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}