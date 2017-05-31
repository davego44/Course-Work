package com.davego44.familysync.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davego44.familysync.firebase.Event;
import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A class that provides an adapted ArrayAdapter to display each day
 *
 * This DayAdapter is in charge of how each individual day is displayed
 * in the gridView in the custom CalendarView. Determines the style and
 * allows events to be added and updated automatically.
 *
 * @author David
 * @version 1.0
 */
public class DayAdapter extends ArrayAdapter<Date> {
    private LayoutInflater inflater;
    private Calendar currentCalendar;
    private ArrayList<Event> events;
    private ArrayList<GroupUser> groupUsers;
    private String userEmail;
    private ArrayList<String> eventEmails;
    private boolean todayStartFlag;
    private Date selectedDate;

    /**
     * Method that sets up the adapter
     *
     * @param context For inflating
     * @param currentCalendar For getting today's date
     */
    public DayAdapter(Context context, Calendar currentCalendar, String userEmail){
        super(context, R.layout.adapter_day, new ArrayList<Date>());
        inflater = LayoutInflater.from(context);
        this.currentCalendar = currentCalendar;
        this.userEmail = userEmail;
        events = new ArrayList<>();
        groupUsers = new ArrayList<>();
        eventEmails = new ArrayList<>();
        todayStartFlag = false;
        selectedDate = currentCalendar.getTime();
    }

    /**
     * Method that sets the internal array equal to the array passed
     *
     * @param days ArrayList for the internal array to be set to
     */
    public void setDays(ArrayList<Date> days) {
        clear();
        addAll(days);
    }

    /**
     * Method that adds an event to the internal array and updates the views
     *
     * @param event Event to add
     */
    public void addEvent(Event event) {
        events.add(event);
        notifyDataSetChanged();
    }

    /**
     * Method that removes an event and updates
     *
     * @param event Event to remove
     */
    public void removeEvent(Event event) {
        events.remove(event);
        notifyDataSetChanged();

    }

    /**
     * Method that clears the events and updates
     */
    public void clearEvents() {
        events.clear();
        notifyDataSetChanged();
    }

    public void selectedChanged(GroupUser groupUser) {
        for (GroupUser gUser : groupUsers) {
            if (gUser.equals(groupUser)) {
                gUser.setShow(groupUser.getShow());
                break;
            }
        }
        notifyDataSetInvalidated();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addGroupUser(GroupUser groupUser) {
        groupUsers.add(groupUser);
    }

    public void removeGroupUser(GroupUser groupUser) {
        groupUsers.remove(groupUser);
        boolean change = false;
        ArrayList<Event> Events = new ArrayList<>();
        for (Event event : events) {
            if (event.getAuthor().equals(groupUser.getEmail())) {
                Events.add(event);
                change = true;
            }
        }
        for (Event event : Events)
            events.remove(event);
        if (change)
            notifyDataSetChanged();
    }

    public void clearGroupUser() {
        groupUsers.clear();
    }

    public void setSelectedDate(Date date) {
        selectedDate = date;
    }

    public ArrayList<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    /**
     * Method that is in charge of how each day is displayed
     *
     * @param position Position number of item selected
     * @param convertView The view being edited
     * @param parent The ViewGroup of the view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date date = getItem(position);

        Calendar inspectingCal = (Calendar) Calendar.getInstance().clone();
        Calendar todayCal = (Calendar)inspectingCal.clone();

        inspectingCal.setTime(date);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.adapter_day, parent, false);

        if (selectedDate != null) {
            Calendar selectCal = (Calendar) todayCal.clone();
            selectCal.setTime(selectedDate);
            if ((inspectingCal.get(Calendar.DATE) == selectCal.get(Calendar.DATE)) &&
                    (inspectingCal.get(Calendar.MONTH) == selectCal.get(Calendar.MONTH)) &&
                    (inspectingCal.get(Calendar.YEAR) == selectCal.get(Calendar.YEAR)))
                convertView.setBackgroundResource(R.color.selection);
        }

        TextView textView = ((TextView)convertView.findViewById(R.id.messageText));
        textView.setText(String.valueOf(inspectingCal.get(Calendar.DAY_OF_MONTH)));

        LinearLayout dayItemLayout = (LinearLayout) convertView.findViewById(R.id.dayItemLayout);

        LinearLayout colorBoxLinearLayout = (LinearLayout) convertView.findViewById(R.id.colorBoxLinearLayout);

        if (currentCalendar.get(Calendar.YEAR) != inspectingCal.get(Calendar.YEAR) ||
                currentCalendar.get(Calendar.MONTH) != inspectingCal.get(Calendar.MONTH)) {
            //a day not in the month we are currently looking at
            textView.setTypeface(null, Typeface.NORMAL);
        } else if (inspectingCal.get(Calendar.DATE) == todayCal.get(Calendar.DATE) &&
                inspectingCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH) &&
                inspectingCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR)) {
            //today's date
            textView.setTypeface(null, Typeface.BOLD_ITALIC);
            textView.setTextColor(Color.parseColor("#b54d40"));
            if (!todayStartFlag) {
                convertView.setBackgroundResource(R.color.selection);
                todayStartFlag = true;
            }

        } else {
            //day in the month we are currently looking at
            textView.setTypeface(null, Typeface.BOLD);
        }

        colorBoxLinearLayout.removeAllViews();
        eventEmails.clear();
        Date eventDate = new Date();
        for (Event event : events) {

            eventDate.setTime(event.getDate());
            todayCal.setTime(eventDate);

            if ((inspectingCal.get(Calendar.DATE) == todayCal.get(Calendar.DATE)) &&
                    (inspectingCal.get(Calendar.MONTH) == todayCal.get(Calendar.MONTH)) &&
                    (inspectingCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR))) {

                String email = event.getAuthor();
                LinearLayout layout = Utilities.getColorBox(getContext());

                boolean show = true;
                for (GroupUser groupUser : groupUsers) {
                    if (email.equals(groupUser.getEmail())){
                        show = groupUser.getShow();
                        layout.setBackgroundResource(groupUser.getColorRes());
                        break;
                    }
                }
                if (!show)
                    continue;

                for (String eventEmail: eventEmails) {
                    if (email.equals(eventEmail)) {
                        show = false;
                        break;
                    }
                }
                if (!show)
                    continue;

                eventEmails.add(email);
                colorBoxLinearLayout.addView(layout);
            }
        }

        return convertView;
    }

}
