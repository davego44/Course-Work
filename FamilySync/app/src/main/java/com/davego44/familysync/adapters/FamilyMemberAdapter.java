package com.davego44.familysync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;

import java.util.ArrayList;

/**
 * A class that provides an adapted ArrayAdapter to display each event
 *
 * NOT USED IN ACTUAL PROJECT!!
 *
 * @author David
 * @version 1.0
 */
public class FamilyMemberAdapter extends ArrayAdapter<GroupUser> {

    private LayoutInflater inflater;

    public FamilyMemberAdapter(Context context, ArrayList<GroupUser> navItems) {
        super(context, R.layout.adapter_family_member, navItems);
        inflater = LayoutInflater.from(context);
    }

    public void removeGroupUser(GroupUser groupUser){
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getEmail().equals(groupUser.getEmail())) {
                remove(getItem(i));
                return;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.adapter_family_member, parent, false);
        GroupUser navItem = getItem(position);
        LinearLayout wholeLayout = (LinearLayout) convertView.findViewById(R.id.wholeLayout);
        ImageView menuItemPicture = (ImageView) convertView.findViewById(R.id.menuItemPicture);
        TextView menuItemTitle = (TextView) convertView.findViewById(R.id.menuItemTitle);
        TextView emailItem = (TextView) convertView.findViewById(R.id.emailItem);
        if (navItem.getShow())
            wholeLayout.setBackgroundResource(R.color.selection);
        else
            wholeLayout.setBackgroundResource(0);
        menuItemPicture.setImageResource(R.mipmap.ic_launcher);
        emailItem.setText(navItem.getName());
        menuItemTitle.setText(Utilities.decodeEmail(navItem.getEmail()));
        LinearLayout colorBoxNavLL = (LinearLayout) convertView.findViewById(R.id.colorBoxNavLL);
        colorBoxNavLL.setBackgroundResource(navItem.getColorRes());
        return convertView;
    }
}
