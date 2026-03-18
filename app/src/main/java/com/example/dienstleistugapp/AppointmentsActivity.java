package com.example.dienstleistugapp;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class AppointmentsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout apnmtList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        apnmtList = findViewById(R.id.apnmt_list);  // ohne LinearLayout weil wir die variable oben definiert haben
        Button back = findViewById(R.id.b_backA);   // back


        back.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String from = getIntent().getStringExtra("from");
                if ("HomeActivity".equals(from)) {
                    startActivity(new Intent(AppointmentsActivity.this, HomeActivity.class));
                } else if ("ProfileActivity".equals(from)) {
                    startActivity(new Intent(AppointmentsActivity.this, ProfileActivity.class));
                }
                finish();
            }
        });


        loadUserAppointments();

    }

    private void loadUserAppointments() {

        // aktuelle user id holen
        String userId = auth.getCurrentUser().getUid();
        Log.d("AppointmentsActivity", "Loading appointments for userId: " + userId);

        // firestore query
        db.collection("appointments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "booked")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("AppointmentsActivity", "Found " + querySnapshot.size() + " appointments");
                    // alte einträge löschen
                    apnmtList.removeAllViews();

                    // Termine überpfrüfen
                    if (querySnapshot.isEmpty()) {
                        // Fall 1: keine termine
                        TextView noAppointments = new TextView(this);
                        noAppointments.setText("No appointments yet");
                        noAppointments.setTextSize(18);
                        noAppointments.setPadding(20, 20, 20, 20);
                        apnmtList.addView(noAppointments);
                        return; // fertig nchts mehr zu tun

                    }

                    // Fall 2: es gibt termine
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        createAppointmentCard(doc);   // ruft neue methode : createAppointmentCard
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                });

    }

    private void createAppointmentCard(DocumentSnapshot doc) {
        // daten aus firestore dokument holen
        String appointmentId = doc.getId();
        String providerName = doc.getString("providerName");
        String service = doc.getString("service");
        String city = doc.getString("city");
        String date = doc.getString("date");
        String providerEmail = doc.getString("provideremail");

        View cardV = getLayoutInflater().inflate(R.layout.item_appointment_card, apnmtList, false);

        // views finden
        TextView providerTv = cardV.findViewById(R.id.card_p);
        TextView serviceTv = cardV.findViewById(R.id.card_s);
        TextView dataTv = cardV.findViewById(R.id.card_d);
        TextView cityTv = cardV.findViewById(R.id.card_c);

        Button resBtn = cardV.findViewById(R.id.btn_res);
        Button cancelBtn = cardV.findViewById(R.id.btn_cancel);

        Log.d("AppointmentsActivity", "Creating card for: " + providerName);
        Log.d("AppointmentsActivity", "Views found - providerTv: " + (providerTv != null) + ", serviceTv: " + (serviceTv != null));

        // Daten einfügen
        providerTv.setText("Provider: " + providerName);
        serviceTv.setText("Service: " + service);
        dataTv.setText("Date: " + date);
        cityTv.setText("City: " + city);

        resBtn.setOnClickListener(v -> {
            rescheduleAppointment(appointmentId, providerName, service, city, providerEmail);

        });

        cancelBtn.setOnClickListener(v -> {
            confirmCancelAppointment(appointmentId);

        });

        apnmtList.addView(cardV);

        Log.d("AppointmentsActivity", "Card added to list. Total children: " + apnmtList.getChildCount());


    }



    private void rescheduleAppointment(String appointmentId, String providerName, String service, String city, String providerEmail) {
        // alten termin stonieren
        db.collection("appointments")
                .document(appointmentId)
                .update("status", "cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Redirecting to reschedule ", Toast.LENGTH_SHORT).show();

                    String[] nameParts = providerName.split(" ");
                    String firstname = nameParts.length > 0 ? nameParts[0] : " ";
                    String lastname = nameParts.length > 1 ? nameParts[1] : " ";

                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("firstname", firstname);
                    intent.putExtra("lastname", lastname);
                    intent.putExtra("service", service);
                    intent.putExtra("city", city);
                    intent.putExtra("email", providerEmail);
                    intent.putExtra("from", "AppointmentActivity");
                    startActivity(intent);
                    finish();
                })

                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
    private void confirmCancelAppointment(String appointmentId) {
        new AlertDialog.Builder(this)   // erstellt einen pop up dialog
                .setTitle("Cancel Appointment")
                .setMessage("Are you sure you want to cancel this appointment?")
                .setPositiveButton("Yes", (dialog, which) ->{
                    cancelAppointment(appointmentId);

                })

                .setNegativeButton("No", null)
                .show();
    }

    private void cancelAppointment(String appointmentId) {
        db.collection("appointments")
                .document(appointmentId)
                .update("status", "cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment cancelled ", Toast.LENGTH_SHORT).show();
                    loadUserAppointments(); // liste neu laden
                })
                .addOnFailureListener(e->{
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });



    }

}