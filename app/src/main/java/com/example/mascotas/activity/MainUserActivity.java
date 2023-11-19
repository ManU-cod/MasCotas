package com.example.mascotas.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mascotas.R;
import com.example.mascotas.adapter.EventAdapter;
import com.example.mascotas.model.Eventos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainUserActivity extends AppCompatActivity {

    Button btn_add,btn_exit;
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
        this.setTitle("Panel User");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color));
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btn_add = findViewById(R.id.btn_add);
        btn_exit = findViewById(R.id.btn_close);

        btn_add.setVisibility(View.GONE);
        btn_exit.setWidth(400);
        btn_exit.setGravity(View.TEXT_ALIGNMENT_CENTER);
        btn_exit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        btn_add.setOnClickListener(v -> {
            Intent intent = new Intent(MainUserActivity.this, Create_Event_Activity.class);
            startActivity(intent);
        });

        btn_exit.setOnClickListener(view -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
        });

        setUpRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {
        mRecycler = findViewById(R.id.recyclerViewSingle);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = mFirestore.collection("Eventos");
        FirestoreRecyclerOptions<Eventos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Eventos>().setQuery(query, Eventos.class).build();
        mAdapter = new EventAdapter(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        mRecycler.setAdapter(mAdapter);
    }

    private void search_view() {
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    public void textSearch(String s){
        FirestoreRecyclerOptions<Eventos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Eventos>()
                        .setQuery(query.orderBy("name")
                                .startAt(s).endAt(s+"~"), Eventos.class).build();
        mAdapter = new EventAdapter(firestoreRecyclerOptions, this);
        mAdapter.startListening();
        mRecycler.setAdapter(mAdapter);
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
