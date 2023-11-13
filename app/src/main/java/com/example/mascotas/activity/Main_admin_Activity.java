package com.example.mascotas.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import com.example.mascotas.R;
import com.example.mascotas.adapter.EventAdapter;
import com.example.mascotas.model.Eventos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Main_admin_Activity extends AppCompatActivity {

    Button btn_add;
    EventAdapter mAdapter;
    RecyclerView mRecycler;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        mFirestore = FirebaseFirestore.getInstance();
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("Eventos");
        FirestoreRecyclerOptions<Eventos> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Eventos>().setQuery(query, Eventos.class).build();

        mAdapter = new EventAdapter (firestoreRecyclerOptions) ;
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(Main_admin_Activity.this, Create_Event_Activity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

}