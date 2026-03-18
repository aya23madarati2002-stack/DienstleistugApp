package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button back = findViewById(R.id.bS_back);   // back
        Button logout = findViewById(R.id.b_logout);   // logout
        Button resetpws = findViewById(R.id.b_resetpsw);   // reset password
        Button changeS = findViewById(R.id.b_changeService);   // change service
        Button changePN = findViewById(R.id.b_changePN);    // change profile name
        Button changeC = findViewById(R.id.b_changeCity);   // cahnge City


        // zurück zu HomeActivity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentS = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intentS);
                finish();
            }
        });

        // nutzer ausloggen und zu LoginMainActivity weiterleiten
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this , LoginMainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

       // passwort reset email an die aktuelle email adresse senden
        resetpws.setOnClickListener(v-> {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if (email != null){
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        });
            }else {
                Toast.makeText(this, "No email found", Toast.LENGTH_SHORT).show();
            }

        });


        // service über einen dialog ändern
        changeS.setOnClickListener(v-> {
            // popup dialog
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Change Service");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50,20,50,20);

            final EditText serviceInput = new EditText(this);
            serviceInput.setHint("Service");
            layout.addView(serviceInput);

            builder.setView(layout);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newService = serviceInput.getText().toString().trim();

                if (!newService.isEmpty()){
                    updateService(newService);   // service aktualisieren

                }else {
                    Toast.makeText(this, "Please fill the field", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // vor- und nachname über dialog ändern
        changePN.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change Profile Name");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50,20,50,20);

            final EditText fistnameInput = new EditText(this);
            fistnameInput.setHint("First Name");
            layout.addView(fistnameInput);

            final EditText lastnameInput = new EditText(this);
            lastnameInput.setHint("Last Name");
            layout.addView(lastnameInput);

            builder.setView(layout);

            builder.setPositiveButton("Save", (dialog, which)->{
                String newFirstname = fistnameInput.getText().toString().trim();
                String newLastname = lastnameInput.getText().toString().trim();

                if (!newFirstname.isEmpty() && !newLastname.isEmpty()){
                    updateProfileName(newFirstname, newLastname);
                }else {
                    Toast.makeText(this, "Please fill both fields ", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

       // stadt übers dialog ändern
        changeC.setOnClickListener(v-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Change City");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50,20,50,20);

            final EditText cityInput = new EditText(this);
            cityInput.setHint("City");
            layout.addView(cityInput);

            builder.setView(layout);

            builder.setPositiveButton("Save", (dialog, which)->{
                String newCity = cityInput.getText().toString().trim();

                if (!newCity.isEmpty()){
                    updateCity(newCity);
                } else {
                    Toast.makeText(this, "Plese fill the field", Toast.LENGTH_SHORT).show();
                }
            });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();

        });



    }


    private void updateService(String newService) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // service feld updaten
        Map<String, Object> updates = new HashMap<>();
        updates.put("service", newService);

        db.collection("users").document(userId).update(updates).addOnSuccessListener( a->{
            Toast.makeText(this, "Service updated", Toast.LENGTH_SHORT).show();
        })
                .addOnFailureListener(e->{
                    Toast.makeText(this, "Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void updateProfileName(String newFirstname, String newLastname) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // felder firstname und lastname aktualisieren
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstname", newFirstname);
        updates.put("lastname", newLastname);

        db.collection("users")
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid->{
                    Toast.makeText(this, "Profile name updateted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e->{
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCity(String newCity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // city feld aktualisieren
        Map<String, Object> updates = new HashMap<>();
        updates.put("city", newCity);

        db.collection("users").document(userId).update(updates).addOnSuccessListener(a->{
            Toast.makeText(this, "City updated", Toast.LENGTH_SHORT).show();
        })
                .addOnFailureListener(e->{
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}