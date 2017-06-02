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

/**
 * This class handles how each item in the spinner is displayed
 */
public class SavedEventAdapter extends ArrayAdapter<Event> {
    private LayoutInflater inflater;

    public SavedEventAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.adapter_saved_event, events);
        inflater = LayoutInflater.from(context);
    }

    /**
     * Handles the creation of the view
     * @param position Position of the item
     * @param convertView The view of the item
     * @param parent The parent of the view
     * @return The new view
     */
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
