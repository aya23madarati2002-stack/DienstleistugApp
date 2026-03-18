package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            // user eingeloggt --> HomeActivity
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {

            // user nicht eingeloggt --> LoginActivity
            Intent intent = new Intent(this, LoginMainActivity.class);
            startActivity(intent);
        }

    }
}