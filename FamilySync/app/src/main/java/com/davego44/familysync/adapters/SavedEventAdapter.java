package com.davego44.familysync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.davego44.familysync.R;
import com.davego44.familysync.firebase.Event;

import java.util.ArrayList;

/**
 * Created by daveg on 5/27/2017.
 */

public class SavedEventAdapter extends ArrayAdapter<Event> {
    private LayoutInflater inflater;

    public SavedEventAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.adapter_saved_event, events);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.adapter_saved_event, parent, false);
        TextView savedEventName = (TextView) convertView.findViewById(R.id.savedEventName);
        Event event = getItem(position);
        savedEventName.setText(event.getName());
        return convertView;
    }
}
