package com.example.mascotas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mascotas.R;
import com.example.mascotas.model.Eventos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class EventAdapter extends FirestoreRecyclerAdapter<Eventos,EventAdapter.ViewHolder> {
    /** Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     * @param options
     */
    public EventAdapter(@NonNull FirestoreRecyclerOptions<Eventos> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Eventos Eventos) {
        holder.titulo.setText(Eventos.getTitulo());
        holder.cupo.setText(Integer.toString(Eventos.getCupo()));
        holder.inscripto.setText(Integer.toString(Eventos.getInscriptos()));
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_event_single, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, cupo, inscripto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo_event);
            cupo = itemView.findViewById(R.id.cupo_event);
            inscripto = itemView.findViewById(R.id.inscripto_event);
        }
    }
}