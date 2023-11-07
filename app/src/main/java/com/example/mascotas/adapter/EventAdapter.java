package com.example.mascotas.adapter;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mascotas.R;
import com.example.mascotas.model.Event;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }

        TextView eventTitle = convertView.findViewById(R.id.eventTitle);
        TextView eventInfo = convertView.findViewById(R.id.eventInfo);
        TextView eventCost = convertView.findViewById(R.id.eventCost);
        TextView eventCoordinates = convertView.findViewById(R.id.eventCoordinates);

        eventTitle.setText(event.title);
        eventInfo.setText(event.information);
        eventCost.setText(event.cost);
        eventCoordinates.setText(event.coordinates);

        return convertView;
    }
}
