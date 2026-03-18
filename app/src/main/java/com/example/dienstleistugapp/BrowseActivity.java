package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class BrowseActivity extends AppCompatActivity {

    // Variablen
    private LinearLayout result;   //  speichert TF für suchergebnisse
    private FirebaseFirestore db;   // verbindung zu db
    private List<DocumentSnapshot> userList = new ArrayList<>();    // liste mit gefundnen user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);   // verbindet java mit xml

       // xml elemente mit code verbinden
        result = findViewById(R.id.resultLayout);
        Button backB = findViewById(R.id.b_backB);
        SearchView searchView = findViewById(R.id.searchView);

      // zurück zum HomeScreen
        backB.setOnClickListener(v -> {
            startActivity(new Intent(BrowseActivity.this, HomeActivity.class));
            finish();
        });


        // baut verbindung zum db ( ermöglicht die suche )
        db = FirebaseFirestore.getInstance();


        // SearchView Einstellungen
       searchView.setIconified(false);
       searchView.setSubmitButtonEnabled(true);
       searchView.setQueryHint("Service suchen");

       // listener für sucheingabe
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // wird aufgerufen wenn user auf suchen drückt
            @Override
            public boolean onQueryTextSubmit(String query) {

                String searchedService = query.trim().toLowerCase();

                // Prüfen ob suchfeld leer ist
                if (searchedService.isEmpty()) {
                    Toast.makeText(BrowseActivity.this, "Bitte Service eingeben", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // suchmethode aufrufen
                searchProviders(searchedService);  // aufruf der methode searchproviders und übergabe der variablenwert seardservice
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
         // methode für Anbietersuche in db
     private void searchProviders(String searchedService) {

        // Alle user dokumente aus firestore holen
        db.collection("users").get()   // holt alle user dokumente aus db

                .addOnSuccessListener(querySnapshot -> {

                            // alte suchergebnisse entfernen
                            result.removeAllViews();
                            userList.clear();

                            // alle gefundenen dokumente durchgehen
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String service = doc.getString("service");

                                // prüfen ob service mit sucheingabe übereinstimmt
                                if (service != null && service.toLowerCase().equals(searchedService)) {
                                    userList.add(doc);     // user zur liste hinzufügen

                                    String firstname = doc.getString("firstname");
                                    String lastname = doc.getString("lastname");
                                    String city = doc.getString("city");


                                    // TextView für jeden gefundenen Anbieter erstellen
                                    TextView item = new TextView(this);
                                    item.setText(
                                            (firstname != null ? firstname : "") + " " +
                                            (lastname != null ? lastname :"")+
                                            " - " + service + "(" + (city != null ? city : "") + ")"
                            );

                                    item.setTextSize(18);
                                    item.setPadding(20, 20, 20, 20);
                                    item.setClickable(true);

                                    // beim klick auf anbieter sein profil öffnen
                                    item.setOnClickListener(v -> openProfile(doc));

                                    result.addView(item);

                                }
                            }

                            // wenn kein ergebnissen ggefunden
                            if (result.getChildCount() == 0) {
                                Toast.makeText(this, "kein Anbieter gefunden", Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    // Profilscreen öffnen und Nutzerdaten übergeben
    private void openProfile(DocumentSnapshot doc) {
        Intent intent = new Intent (this, ProfileActivity.class);
        intent.putExtra("firstname", doc.getString("firstname"));
        intent.putExtra("lastname", doc.getString("lastname"));
        intent.putExtra("service", doc.getString("service"));
        intent.putExtra("city", doc.getString("city"));
        intent.putExtra("email", doc.getString("email"));
        startActivity(intent);
    }
}