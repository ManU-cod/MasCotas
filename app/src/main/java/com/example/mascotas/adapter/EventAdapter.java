package com.example.mascotas.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mascotas.R;
import com.example.mascotas.activity.Create_Event_Activity;
import com.example.mascotas.model.Eventos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class EventAdapter extends FirestoreRecyclerAdapter<Eventos,EventAdapter.ViewHolder> {
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    public EventAdapter(@NonNull FirestoreRecyclerOptions<Eventos> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Eventos Eventos) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.titulo.setText(Eventos.getTitulo());
        //holder.cupo.setText(Integer.toString(Eventos.getCupo()));
        holder.estado.setText(Eventos.getEstado());

        String photoPet = Eventos.getPhoto();
        try {
            if (!photoPet.equals(""))
                Picasso.with(activity.getApplicationContext())
                        .load(photoPet)
                        .resize(150, 150)
                        .into(holder.photo_pet);
        }catch (Exception e){
            Log.d("Exception", "e: "+e);
        }

        holder.btn_edit.setOnClickListener(v -> {
            Intent i = new Intent(activity, Create_Event_Activity.class);
            i.putExtra("id_Event", id);
            activity.startActivity(i);
        });
        holder.btn_delete.setOnClickListener(v -> deleteEventos(id));
    }

    private void deleteEventos(String id) {
        mFirestore.collection("Eventos").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, cupo, estado;
        ImageView btn_delete, btn_edit, photo_pet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo_event);
            //cupo = itemView.findViewById(R.id.cupo_event);
            estado = itemView.findViewById(R.id.estadotxt);
            photo_pet = itemView.findViewById(R.id.photo);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
            btn_edit = itemView.findViewById(R.id.btn_editar);

        }
    }
}