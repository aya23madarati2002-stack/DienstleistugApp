package com.example.dienstleistugapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        EditText eF = findViewById(R.id.eTEmail);       // edittext email
        EditText pF = findViewById(R.id.eTPassword);     // edittext password

        TextView fP = findViewById(R.id.tV_forget_password);  // passwortvergessen? link
        TextView register = findViewById(R.id.tV_register);   // Regestrieren?  link Register screen

        Button button_login = findViewById(R.id.button_login);  //  loginbutton



        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = eF.getText().toString().trim();
                String password = pF.getText().toString().trim();

                // Überprüfung ob Email und Passwort nicht leer sind
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginMainActivity.this, "Bitte Email und Passwort eingeben!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Authentication Instanz für Login
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(LoginMainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginMainActivity.this, HomeActivity.class);

                            // Flags verhindert dass der Nutzer per System-Zurück wieder zum Login kommt
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginMainActivity.this, "Login failed: ", Toast.LENGTH_SHORT).show();
                        });
                            }
        });

        register.setOnClickListener( v -> {

            // Wechsel zu Registerscreen
            Intent intentRL = new Intent(LoginMainActivity.this, RegisterActivity.class);
            startActivity(intentRL);
        });

        // Klick auf "Passwort vergessen?" Link
        fP.setOnClickListener(v-> {

                    // Popup-Dialog erstellen
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Reset Password");
                    builder.setMessage("Enter your email adress");

                    // input field
                    final EditText emailInput = new EditText(this);
                    emailInput.setHint("Email");
                    emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    builder.setView(emailInput);

                    builder.setPositiveButton("Send", (dialog, which) -> {
                        String email = emailInput.getText().toString().trim();

                        if (!email.isEmpty() && email.contains("@")) {

                            // Passwort reset Email senden
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                    .addOnSuccessListener(a -> {


                                        Toast.makeText(this, "Password reset email send to " + email, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {

                            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                });


    }
}