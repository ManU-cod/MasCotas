package com.example.mascotas.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mascotas.R;
import com.example.mascotas.activity.Create_Event_Activity;
import com.example.mascotas.activity.DetailsUserActivity;
import com.example.mascotas.model.Eventos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

public class UserEventAdapter extends FirestoreRecyclerAdapter<Eventos,UserEventAdapter.ViewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    public UserEventAdapter(@NonNull FirestoreRecyclerOptions<Eventos> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserEventAdapter.ViewHolder holder, int position, @NonNull Eventos Eventos) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        holder.titulo.setText(Eventos.getTitulo());
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
            Intent i = new Intent(activity, DetailsUserActivity.class);
            i.putExtra("id_Event", id);
            activity.startActivity(i);
        });
    }

    @NonNull
    @Override
    public UserEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_single, parent, false);
        return new UserEventAdapter.ViewHolder(v);
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

            btn_delete.setVisibility(View.GONE);

        }
    }
}

