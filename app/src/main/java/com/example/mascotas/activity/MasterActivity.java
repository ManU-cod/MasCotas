package com.example.mascotas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mascotas.R;
import com.example.mascotas.adapter.EventAdapter;
import com.example.mascotas.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MasterActivity extends AppCompatActivity {
    private ListView eventListView;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        eventListView = findViewById(R.id.eventListView);
        databaseReference = FirebaseDatabase.getInstance().getReference("eventos");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    events.add(event);
                }

                EventAdapter adapter = new EventAdapter(MasterActivity.this, events);
                eventListView.setAdapter(adapter);

                eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Event selectedEvent = events.get(position);
                        Intent intent = new Intent(MasterActivity.this, DetailActivity.class);
                        intent.putExtra("event", (CharSequence) selectedEvent);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si es necesario
            }
        });
    }
}
