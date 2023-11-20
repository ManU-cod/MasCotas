package com.example.mascotas.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import com.example.mascotas.R;
import com.example.mascotas.adapter.EventAdapter;
import com.example.mascotas.adapter.TurnoAdapter;
import com.example.mascotas.model.Eventos;
import com.example.mascotas.model.Turnos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Main_Turno_Activity extends AppCompatActivity {
    Button  btn_return;
    TurnoAdapter mAdapter;
    RecyclerView mRecycler;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    SearchView search_view;
    Query query;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_turno);
        this.setTitle("Turnos");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setUpRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        String userId = mAuth.getCurrentUser().getUid();
        mRecycler = findViewById(R.id.recyclerTurnoSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("Turnos").whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<Turnos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Turnos>().setQuery(query, Turnos.class).build();
        mAdapter = new TurnoAdapter(firestoreRecyclerOptions,this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Main_admin_Activity.class);
        startActivity(intent);
        return true;
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