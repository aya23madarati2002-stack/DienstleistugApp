package com.example.dienstleistugapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        TextView register = findViewById(R.id.tV_register);   // Regestrieren?  link

        Button button_login = findViewById(R.id.button_login);  //  loginbutton



        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = eF.getText().toString().trim();
                String password = pF.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginMainActivity.this, "Bitte Email und Passwort eingeben!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // für save login
                SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // Wechsel zum HomeScreen
                Intent intent = new Intent(LoginMainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // damit man nicht zurück zum LogIn kann



            }
        });

        register.setOnClickListener( v -> {
            Intent intentRL = new Intent(LoginMainActivity.this, RegisterActivity.class);
            startActivity(intentRL);
        });


    }
}