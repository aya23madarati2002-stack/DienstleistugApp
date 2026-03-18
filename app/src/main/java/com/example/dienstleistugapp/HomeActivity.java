package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button browse = findViewById(R.id.b_browse);   // Browse Button
        Button pfl = findViewById(R.id.b_profile);   // profile Button
        Button apntm = findViewById(R.id.b_apntm);   // appointment Button
        Button settings = findViewById(R.id.b_settings);   // settings Button


       // listener zum wechsel zu browse activity
        browse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentB = new Intent(HomeActivity.this, BrowseActivity.class);
                startActivity(intentB);

            }
        });

        // listener zum wechsel zu profile activity
        pfl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentP = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intentP);
                intentP.putExtra("from", "HomeActivity");

            }
        });

        // listener zum wechsel zu appointment activity
        apntm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentA = new Intent(HomeActivity.this, AppointmentsActivity.class);
                startActivity(intentA);
                intentA.putExtra("from", "HomeActivity");

            }
        });

        // listener zum wechsel zu settings activity
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentS = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intentS);

            }

        });

    }
}