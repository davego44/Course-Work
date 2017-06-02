package com.davego44.familysync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davego44.familysync.helper.NavItem;
import com.davego44.familysync.R;

import java.util.ArrayList;

/**
 * A class that provides an adapted ArrayAdapter to display each navigation item in the
 * drawer layout
 *
 * @author David
 * @version 1.0
 */
public class NavAdapter<T> extends ArrayAdapter<T> {

    private LayoutInflater inflater;

    public NavAdapter(Context context, ArrayList<T> navItems) {
        super(context, R.layout.adapter_nav, navItems);
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
            convertView = inflater.inflate(R.layout.adapter_nav, parent, false);

        NavItem navItem = (NavItem) getItem(position);

        ImageView menuItemPicture = (ImageView) convertView.findViewById(R.id.menuItemPicture);
        TextView menuItemTitle = (TextView) convertView.findViewById(R.id.menuItemTitle);

        menuItemPicture.setImageResource(navItem.getPicture());
        menuItemTitle.setText(navItem.getTitle());
        return convertView;
    }
}
