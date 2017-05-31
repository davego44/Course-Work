package com.davego44.familysync.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davego44.familysync.helper.EmptyView;
import com.davego44.familysync.firebase.Event;
import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class EventAdapter extends ArrayAdapter<Event> {

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<GroupUser> groupUsers;
    private String userEmail;

    public EventAdapter(Context context, ArrayList<Event> events, String userEmail) {
        super(context, R.layout.adapter_event, events);
        inflater = LayoutInflater.from(context);
        mContext = context;
        groupUsers = new ArrayList<>();
        this.userEmail = userEmail;
    }

    public void addGroupUser(GroupUser groupUser) {
        groupUsers.add(groupUser);
    }

    public void selectedChanged(GroupUser groupUser) {
        ArrayList<Event> hciEvents = new ArrayList<>();

        for (GroupUser gUser : groupUsers) {
            if (gUser.equals(groupUser)) {
                gUser.setShow(groupUser.getShow());
                break;
            }
        }

        notifyDataSetInvalidated();
    }

    public ArrayList<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    public void removeGroupUser(GroupUser groupUser) {
        groupUsers.remove(groupUser);
        boolean change = false;
        ArrayList<Event> hciEvents = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            Event event = getItem(i);
            if (event.getAuthor().equals(groupUser.getEmail())) {
                hciEvents.add(event);
                change = true;
            }
        }
        for (Event event : hciEvents)
            remove(event);
        if (change)
            notifyDataSetChanged();
    }

    public void orderEvent() {
        sort(new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                long number = lhs.getDate() - rhs.getDate();
                return (int)number;
            }
        });
    }

    public void clearGroupUsers() {
        groupUsers.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GroupUser myGroupUser = new GroupUser();
        Event event = getItem(position);
        String email = event.getAuthor();
        boolean show = true;
        for (GroupUser groupUser : groupUsers) {
            if (email.equals(groupUser.getEmail())) {
                show = groupUser.getShow();
                myGroupUser = groupUser;
                break;
            }
        }
        if (!show)
            return new EmptyView(getContext());

        if (convertView == null || convertView instanceof EmptyView)
            convertView = inflater.inflate(R.layout.adapter_event, parent, false);

        TextView eventTimeHour = (TextView) convertView.findViewById(R.id.eventTimeHour);
        TextView eventTimeHourSecond = (TextView) convertView.findViewById(R.id.eventTimeHourSecond);
        TextView eventTimeMin = (TextView) convertView.findViewById(R.id.eventTimeMin);
        TextView amPmTextView = (TextView) convertView.findViewById(R.id.amPmTextView);
        TextView eventNotesTextView = (TextView) convertView.findViewById(R.id.eventNotesTextView);
        TextView eventName = (TextView) convertView.findViewById(R.id.eventName);
        TextView eventLocation = (TextView) convertView.findViewById(R.id.eventLocation);
        LinearLayout colorBoxEventsLL = (LinearLayout) convertView.findViewById(R.id.colorBoxEventsLL);

        colorBoxEventsLL.removeAllViews();
        Calendar calendar = (Calendar) Calendar.getInstance().clone();
        calendar.setTime(new Date(event.getDate()));

        eventName.setText(event.getName());
        eventLocation.setText(event.getLocation());
        eventNotesTextView.setText(event.getMessage());

        String time = String.valueOf(calendar.get(Calendar.HOUR));
        time = time.equals("0") ? "12" : time;

        char c = time.charAt(0);
        eventTimeHour.setText(String.valueOf(c));
        if (time.length() > 1) {
            c = time.charAt(1);
            eventTimeHourSecond.setText(String.valueOf(c));
            eventTimeHourSecond.setVisibility(View.VISIBLE);
        } else {
            eventTimeHourSecond.setVisibility(View.GONE);
        }


        time = calendar.get(Calendar.MINUTE) < 10 ? "0" + String.valueOf(calendar.get(Calendar.MINUTE)) : String.valueOf(calendar.get(Calendar.MINUTE));
        eventTimeMin.setText(time);

        time = calendar.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm";
        amPmTextView.setText(time);

        LinearLayout layout = Utilities.getColorBox(getContext());
        if (!email.equals(userEmail))
            layout.setBackgroundResource(myGroupUser.getColorRes());
        colorBoxEventsLL.addView(layout);

        return convertView;
    }
}
