package com.example.dienstleistugapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private String selectedDate = "";
    private MaterialCalendarView calendarV;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // liste der bereits gebuchten tage - diese werden im kalender gesperrt
    private List<CalendarDay> bookedDays = new ArrayList<>();  // für bereits gebuchten Termine

    // klassen variablen für text views um sie zugänglich für alle methoden zu machen
    private TextView nametv;
    private TextView lastnametv;
    private TextView servicetv;
    private TextView citytv;
    private TextView emailtv;

    // Buttons
    private Button BA;
    private Button confirmDateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // views mit xml elemente verknüpfen
         nametv = findViewById(R.id.tv_profile_name);
         servicetv = findViewById(R.id.tv_profile_service);
         citytv = findViewById(R.id.tv_profile_city);
         emailtv = findViewById(R.id.tv_profile_email);

        Button GB = findViewById(R.id.b_go_back);   // Go backbutton
        BA = findViewById(R.id.b_book_A);   // book appointment button

        calendarV = findViewById(R.id.calendarView);
        confirmDateBtn = findViewById(R.id.b_confirm_date);

        // firebase initialisieren
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // daten aus intent holen (werden von browseActivity übergeben)
        String firstname= getIntent().getStringExtra("firstname");
        String lastname = getIntent().getStringExtra("lastname");
        String service = getIntent().getStringExtra("service");
        String city = getIntent().getStringExtra("city");
        String email = getIntent().getStringExtra("email");

        // wenn kein name & email übergeben wurden --> eigenes profil laden
        if (firstname== null || email == null){
            loadOwnProfile();

        } else {
            // anbieter profil anzeigen
            displayProviderProfile( firstname, lastname, service, city, email);
        }



        db.collection("appointments")
                .whereEqualTo("provideremail", email)
                        .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot){
                                        String date = doc.getString("date");

                                        if (date != null){
                                            // Datum aufteilen: DD.MM.YYYY
                                            String[] parts = date.split("\\.");
                                            int day = Integer.parseInt(parts[0]);
                                            int month = Integer.parseInt(parts[1]) -1;
                                            int year = Integer.parseInt(parts[2]);

                                            bookedDays.add(CalendarDay.from(year, month, day));

                                        }
                                    }
                                    // Gebuchte Tage im kalender sperren
                                    blockBookedDays();
                                });



        // kalender & bestätigung Button versteckt am anfang
        calendarV.setVisibility(View.GONE);
        confirmDateBtn.setVisibility(View.GONE);


        // kalender ein- und ausblenden beim klick auf BA ( Book Appointment)
        BA.setOnClickListener(a->{
            if (calendarV.getVisibility()== View.GONE){
                calendarV.setVisibility(View.VISIBLE); // kalender anzeigen
               // calender.bringToFront();
                //calender.requestLayout();
                BA.setText("Hide Calender ");
            }else {
                calendarV.setVisibility((View.GONE));
                confirmDateBtn.setVisibility(View.GONE);
                BA.setText(R.string.b_book_A);
            }
        });

        // datum auswahl im kalender
        calendarV.setOnDateChangedListener((widget, date, selected) -> {
            if (bookedDays.contains(date)) {
                Toast.makeText(this, "Date is already booked", Toast.LENGTH_SHORT).show();
                confirmDateBtn.setVisibility(View.GONE);

            } else {
                // Datum als String speichern im format DD:MM:YYYY
                selectedDate = date.getDay() + "." + (date.getMonth() + 1) + "." + date.getYear();
                confirmDateBtn.setVisibility(View.VISIBLE);

            }
        });



        // Termin in db speichern und zu AppointmentsActivity wechseln
        confirmDateBtn.setOnClickListener(v -> {
            if (!selectedDate.isEmpty()) {
                saveAppointmentToFirestore(selectedDate, email, firstname, lastname, service, city);


            } else {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show();
            }
        });

        // zurück button : intent zur vorherigen activity
        GB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String from = getIntent().getStringExtra("from");
                if ("HomeActivity".equals(from)){
                    startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                finish();
                } else if ("BrowseActivity".equals(from)) {
                    startActivity(new Intent(ProfileActivity.this,BrowseActivity.class));
               finish();
                } else {
                    finish();
                }


            }
        });
    }



    private void loadOwnProfile() {

        // prüfen ob der Nutzer eingeloggt ist
        if (auth.getCurrentUser() == null) {
        Toast.makeText(this, "Not Logged in", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginMainActivity.class));
        finish();
        return;
    }
        String userId = auth.getCurrentUser().getUid();

        // user daten aus Firestore collection "users" laden
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("ProfileActivity", "Document exists: " + documentSnapshot.exists());

                    if (documentSnapshot.exists()){
                        // Daten aus dem dokument auslesen
                        String firstname = documentSnapshot.getString("firstname");
                        String lastname = documentSnapshot.getString("lastname");
                        String service = documentSnapshot.getString("service");
                        String city = documentSnapshot.getString("city");
                        String email = documentSnapshot.getString("email");

                        // daten in die view schreiben
                        nametv.setText(firstname+ " " + lastname);
                        servicetv.setText("Service: " + service);
                        citytv.setText("City: "+ city);
                        emailtv.setText("Email: "+ email);


                        // buchungselemente ausblenden ( eigenes profil nicht buchbar)
                        BA.setVisibility(View.GONE);
                        calendarV.setVisibility(View.GONE);
                        confirmDateBtn.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e->{
                    Log.e("ProfileActivity", "Error loading profile: " + e.getMessage());
                    Toast.makeText(this,"Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    private void displayProviderProfile(String firstname, String lastname, String service, String city, String email) {
       //  profil daten in die view schreiben
        nametv.setText(firstname + " " + lastname);
        servicetv.setText("Service: " + service);
        citytv.setText("City: " + city);
        emailtv.setText("Email: " + email);


        // buchung Button sichtbar machen
        BA.setVisibility(View.VISIBLE);

        db.collection("appointments")
                .whereEqualTo("provideremail", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot){
                        String date = doc.getString("date");

                        if (date != null){
                            String[] parts = date.split("\\.");
                            int day = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]) - 1;
                            int year = Integer.parseInt(parts[2]);

                            bookedDays.add(CalendarDay.from(year, month, day));

                        }
                    }
                    blockBookedDays();
                });

    }

    private void blockBookedDays() {
        calendarV.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {

                // nur gebuchte tage dekorrieren (grau)
                return bookedDays.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled(true);    // Tag nicht klickbar
                view.addSpan(new ForegroundColorSpan(Color.GRAY));   // Tag grau einfärben

            }
        });
    }

    private void saveAppointmentToFirestore(String date, String providerEmail,String firstname, String lastname, String service, String city){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // id des aktuelle eingeloggter user holen
        String userId = auth.getCurrentUser().getUid();

        // id für diesen termin generieren
        String appointmentId = db.collection("appointments").document().getId();

        // daten in einer map speichern
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userId", userId);
        appointment.put("provideremail", providerEmail);
        appointment.put("providerName", firstname + " " + lastname);
        appointment.put("service", service);
        appointment.put("city", city);
        appointment.put("date", date);
        appointment.put("timestamp", System.currentTimeMillis());
        appointment.put("status", "booked");

        // Termin in firestore speichern
        db.collection("appointments")
                .document(appointmentId)
                .set(appointment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment booked!", Toast.LENGTH_SHORT).show();


                    // nach erfolgreicher buchung zur AppointmentsActivity wechseln
                    Intent intent = new Intent(ProfileActivity.this, AppointmentsActivity.class);
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("firstname", firstname);
                    intent.putExtra("lastname", lastname);
                    intent.putExtra("service", service);
                    intent.putExtra("city", city);
                    //intent.putExtra("email", email);
                    startActivity(intent);

                })
                .addOnFailureListener(e->{
                    Toast.makeText(this,"Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });





    }
}