package com.example.dienstleistugapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText vN = findViewById(R.id.eT_Name);   // edittext vorname
        EditText nN = findViewById(R.id.eT_LN);     // edittext nachname
        EditText emailR = findViewById(R.id.eT_EmailR);   // edittext email
        EditText pwR = findViewById(R.id.eT_Password);    // // edittext password
        EditText pwR2 = findViewById(R.id.eT_Password2);   // edittext password2

        TextView loginR = findViewById(R.id.tV_LI);   //   logi? textview link zur login seite

        Button regitster = findViewById(R.id.b_register);   // registerbutton

        regitster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                // eingabe in string umwandeln !!
                String vName = vN.getText().toString().trim();
                String nName = nN.getText().toString().trim();
                String email = emailR.getText().toString().trim();
                String password = pwR.getText().toString().trim();
                String password2 = pwR2.getText().toString().trim();

                EditText[] fields = {vN, nN, emailR, pwR, pwR2};    //arraylist für eingabefelder

                // vollständige eingabe prüfen
                for (EditText field : fields) {
                    if (field.getText().toString().trim().isEmpty()) {   // wenn nicht allle felder ausgefüllt sind fehlermeldung
                        Toast.makeText(RegisterActivity.this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // passwörter übereinstimmigkeit prüfen
                if (!password.equals(password2)) {
                    Toast.makeText(RegisterActivity.this, "Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show();
                    return;
                }

                // email mit emails pattern vergleichen
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "bitte gültiges email eingeben", Toast.LENGTH_SHORT).show();
                    return;
                }

                // für save Register
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Name", vName);
                editor.putString("Lastname", nName);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Toast.makeText(RegisterActivity.this, "Registrierung erolgreich!", Toast.LENGTH_SHORT).show();


                // Wechsel zum HomeScreen
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // damit man nicht zurück zum Register kann


            }


        });

        loginR.setOnClickListener( v -> {
            Intent intentLG = new Intent(RegisterActivity.this, LoginMainActivity.class);
            startActivity(intentLG);
        });

    }
}