package com.example.mascotas.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mascotas.R;
import com.example.mascotas.adapter.EventAdapter;
import com.example.mascotas.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;



public class MasterActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Event> eventList;
    EventAdapter adapter;
    ListenerRegistration eventListener;
    CollectionReference eventRef = db.collection("eventos");
    FirebaseAuth auth;

    private Button btnLogOut;

    public void getEvents(){

        RecyclerView recyclerView = findViewById(R.id.recyclerEventList);
        eventList = new ArrayList<>();
        //adapter = new EventAdapter(eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        eventRef.get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                event.setTitle(document.getString("Titulo"));
                event.setInformation(document.getString("Creador"));
                eventList.add(event);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Toast.makeText(MasterActivity.this, "Error al obtener eventos", Toast.LENGTH_SHORT).show()
        );
    }

    public void getEvents2() {

        RecyclerView recyclerView = findViewById(R.id.recyclerEventList);
        eventList = new ArrayList<>();
        //adapter = new EventAdapter(eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        eventListener = eventRef.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(MasterActivity.this, "Error al obtener eventos", Toast.LENGTH_SHORT).show();
            }

            // Limpiar la lista antes de agregar los nuevos datos
            eventList.clear();

            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                Event event = document.toObject(Event.class);
                event.setTitle(document.getString("Titulo"));
                event.setInformation(document.getString("Creador"));
                eventList.add(event);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventListener.remove();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        getEvents2();
        btnLogOut = findViewById(R.id.btnrecyclerLogOut);
        auth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(view -> {

            Intent intent = new Intent(MasterActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
            auth.signOut();

        });
    }
}