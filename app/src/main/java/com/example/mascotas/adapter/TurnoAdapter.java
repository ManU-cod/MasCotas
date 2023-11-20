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
import com.example.mascotas.model.Turnos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class TurnoAdapter extends FirestoreRecyclerAdapter<Turnos, TurnoAdapter.viewHolder> {

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    public TurnoAdapter(@NonNull FirestoreRecyclerOptions<Turnos> options,Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull TurnoAdapter.viewHolder holder, int position, @NonNull Turnos Turnos) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        // Bind de los datos a las vistas en el ViewHolder
        holder.titulo.setText(Turnos.getTitulo());
        holder.estado.setText(Turnos.getEstado());
        // ... otros campos de la vista

        String photoPet = Turnos.getPhoto();
        try {
            if (!photoPet.equals("")){
                Picasso.with(activity.getApplicationContext())
                        .load(photoPet)
                        .resize(150, 150)
                        .into(holder.photo_pet);
            }
        }catch (Exception e){
            Log.d("Exception", "e: "+e);
        }
        holder.titulo.setOnClickListener(v -> IraDetalles(id));
        holder.btn_delete.setOnClickListener(v -> deleteEventos(id));

    }

    private void IraDetalles(String id) {
        mFirestore.collection("Turnos").document(id).get().addOnSuccessListener(documentSnapshot ->{
            String eventId = documentSnapshot.getString("id");
            //Intent i = new Intent(activity, Create_Event_Activity.class);
            //i.putExtra("id_Turno", eventId);
            //activity.startActivity(i);
            Toast.makeText(activity, "Detalles correctamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteEventos(String id) {
        mFirestore.collection("Turnos").document(id).get().addOnSuccessListener(documentSnapshot ->{
            String eventId = documentSnapshot.getString("id");
            mFirestore.collection("Eventos").document(eventId).get().addOnSuccessListener(Snapshots ->{
                String Ids = Snapshots.getString("id");
                int eventinscriptos = Integer.parseInt(String.valueOf(Snapshots.getLong("inscriptos")));
                HashMap<String, Object> map = new HashMap<>();
                map.put("inscriptos", eventinscriptos - 1);
                mFirestore.collection("Eventos").document(Ids).update(map);
            });
            mFirestore.collection("Turnos").document(id).delete().addOnSuccessListener(unused ->
                Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e ->
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show());
        });

    }

    @NonNull
    @Override
    public TurnoAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_turno_single, parent, false);
        return new viewHolder(v);
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView titulo, estado;
        ImageView  photo_pet, btn_delete;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo_event);
            estado = itemView.findViewById(R.id.estadotxt);
            photo_pet = itemView.findViewById(R.id.photo);
            btn_delete = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}
