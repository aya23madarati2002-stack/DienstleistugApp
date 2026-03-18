  package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText vN, nN, ct, sv, emailR, pwR, pwR2; // Deklaration der EditText-Felder als Klassenvariablen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisierung der EditText-Felder
        vN = findViewById(R.id.eT_Name);
        nN = findViewById(R.id.eT_LN);
        ct = findViewById(R.id.et_city);
        sv = findViewById(R.id.et_service);
        emailR = findViewById(R.id.eT_EmailR);
        pwR = findViewById(R.id.eT_Password);
        pwR2 = findViewById(R.id.eT_Password2);

        TextView loginR = findViewById(R.id.tV_LI);
        Button registerButton = findViewById(R.id.b_register);

        // Registrieren-Button
        registerButton.setOnClickListener(this::registerUser);

        // Login-Link
        loginR.setOnClickListener(l -> startActivity(new Intent(this, LoginMainActivity.class)));
    }

    private void registerUser(View v) {
        String vName = vN.getText().toString().trim();
        String nName = nN.getText().toString().trim();
        String city = ct.getText().toString().trim();
        String service = sv.getText().toString().trim().toLowerCase();
        String email = emailR.getText().toString().trim();
        String password = pwR.getText().toString().trim();
        String password2 = pwR2.getText().toString().trim();

        // Eingabevalidierung
        if (!validateInput(vName, nName, city, service, email, password, password2)) {
            return; // Abbruch der Registrierung, wenn die Validierung fehlschlägt
        }

        // Firebase Auth Registrierung
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        String uid = auth.getCurrentUser().getUid();
                        saveUserToFirestore(uid, vName, nName, city, service, email);
                    } else {
                        // Verbesserte Fehlerbehandlung für Firebase Auth Fehler
                        String errorMessage = "Registrierung fehlgeschlagen: ";
                        if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthException) {
                                errorMessage += ((FirebaseAuthException) task.getException()).getErrorCode() + " - " + task.getException().getMessage();
                            } else {
                                errorMessage += task.getException().getMessage();
                            }
                        } else {
                            errorMessage += "Unbekannter Fehler";
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput(String vName, String nName, String city, String service, String email, String password, String password2) {
        // Alle Felder prüfen
        if (TextUtils.isEmpty(vName) || TextUtils.isEmpty(nName) || TextUtils.isEmpty(city) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Passwortprüfung
        if (!password.equals(password2)) {
            Toast.makeText(this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
            return false;
        }

        // E-Mail prüfen
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Bitte gültige E-Mail eingeben", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Passwortstärke prüfen (optional, aber empfohlen)
        if (password.length() < 6) {
            Toast.makeText(this, "Passwort muss mindestens 6 Zeichen lang sein", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserToFirestore(String uid, String vName, String nName, String city, String service, String email) {

        // Nutzerdaten in HashMap speichern
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", vName);
        user.put("lastname", nName);
        user.put("city", city);
        user.put("email", email);
        if (!service.isEmpty()) user.put("service", service);

        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Fehler beim Speichern: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
