package com.davego44.familysync.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.davego44.familysync.helper.Constants;
import com.davego44.familysync.adapters.DayAdapter;
import com.davego44.familysync.firebase.Event;
import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;
import com.davego44.familysync.activities.CalendarViewerActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Custom Calendar View
 *
 * A view that displays a calendar
 *
 * @author David
 * @version 1.0
 */
public class CalendarFragment extends android.support.v4.app.Fragment
{
    // internal components
    private TextView txtDate;
    private GridView calendarGrid;
    private Calendar currentCalendar;
    private Button nextButton;
    private Button prevButton;
    private Context mContext;
    private DayAdapter dayAdapter;

    private DatabaseReference eventUserRef;
    private DatabaseReference myEventUserRef;
    private DatabaseReference myGroupRef;

    private ArrayList<DatabaseReference> groupsToRemove;
    private ChildEventListener addEventChildListener;
    private ChildEventListener addEmailChildListener;

    private Calendar eventsCalendar;

    private String email;

    private static final int DAYS_COUNT = 42;

    public CalendarFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        assignUiElements(view);
        mContext = view.getContext();
        initControl();
        return view;
    }

    /**
     * Mathod that loads the component XML layout
     *
     */
    private void initControl()
    {
        currentCalendar = (Calendar) Calendar.getInstance().clone();
        groupsToRemove = new ArrayList<>();

        dayAdapter = new DayAdapter(mContext, currentCalendar, email);
        eventsCalendar = (Calendar) currentCalendar.clone();

        assignClickHandlers();

        updateCalendar();

        setupListeners();
    }

    /**
     * Method that sets up the UI elements
     */
    private void assignUiElements(View view)
    {
        nextButton = (Button)view.findViewById(R.id.nextButton);
        prevButton = (Button)view.findViewById(R.id.prevButton);

        txtDate = (TextView)view.findViewById(R.id.calendar_date_display);
        calendarGrid = (GridView)view.findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentCalendar.add(Calendar.MONTH, 1);
                dayAdapter.clearEvents();
                myEventUserRef.removeEventListener(addEventChildListener);
                myEventUserRef.addChildEventListener(addEventChildListener);
                for (DatabaseReference db : groupsToRemove) {
                    db.removeEventListener(addEventChildListener);
                    db.addChildEventListener(addEventChildListener);
                }
                updateCalendar();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentCalendar.add(Calendar.MONTH, -1);
                dayAdapter.clearEvents();
                myEventUserRef.removeEventListener(addEventChildListener);
                myEventUserRef.addChildEventListener(addEventChildListener);
                for (DatabaseReference db : groupsToRemove) {
                    db.removeEventListener(addEventChildListener);
                    db.addChildEventListener(addEventChildListener);
                }
                updateCalendar();
            }
        });

        calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date date = (Date)parent.getItemAtPosition(position);
                for (int i = 0; i < parent.getChildCount(); i++)
                    parent.getChildAt(i).setBackgroundResource(R.color.mainBackground);
                view.setBackgroundResource(R.color.selection);
                dayAdapter.setSelectedDate(date);
                ((CalendarViewerActivity)getActivity()).setBottomFragmentDate(date);
            }
        });
    }

    /**
     * Method that setups and adds the Firebase listeners for the user's events and the
     * user's group emails
     *
     * @param email For the selecting the right user
     * @param rootRef For grabbing information from Firebase
     */
    public void setGroupVariables(String email, DatabaseReference rootRef) {
        this.email = email;
        eventUserRef = rootRef.child(Constants.Firebase.EVENTS_USERS);
        myEventUserRef = eventUserRef.child(email);
        myGroupRef = rootRef.child(Constants.Firebase.USERS).child(email).child("group");
    }

    public void setupListeners() {
        addEventChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                Date eventDate = new Date();
                eventDate.setTime(event.getDate());
                eventsCalendar.setTime(eventDate);
                if ((eventsCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) &&
                        (eventsCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH))) {
                    dayAdapter.addEvent(event);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                dayAdapter.removeEvent(event);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        myEventUserRef.addChildEventListener(addEventChildListener);

        addEmailChildListener = myGroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                String emailT = groupUser.getEmail();
                if (!emailT.equals(CalendarFragment.this.email)) {
                    dayAdapter.addGroupUser(groupUser);
                    DatabaseReference db = eventUserRef.child(emailT);
                    db.addChildEventListener(addEventChildListener);
                    groupsToRemove.add(db);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                dayAdapter.selectedChanged(dataSnapshot.getValue(GroupUser.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                String emailT = groupUser.getEmail();
                if (!emailT.equals(CalendarFragment.this.email)) {
                    dayAdapter.removeGroupUser(groupUser);
                    DatabaseReference db = eventUserRef.child(Utilities.encodeEmail(emailT));
                    db.removeEventListener(addEventChildListener);
                    groupsToRemove.remove(db);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method that updates and displays the calendar
     */
    public void updateCalendar()
    {
        ArrayList<Date> datesToAdd = new ArrayList<>();
        Calendar calendar = (Calendar) currentCalendar.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill datesToAdd (42 days calendar)
        while (datesToAdd.size() < DAYS_COUNT)
        {
            datesToAdd.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        dayAdapter.setDays(datesToAdd);
        calendarGrid.setAdapter(dayAdapter);

        // update title
        SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.US);
        txtDate.setText(format.format(currentCalendar.getTime()));
    }

    public ArrayList<Event> getEvents() {
        return dayAdapter.getEvents();
    }

    public ArrayList<GroupUser> getGroupUsers() {
        return dayAdapter.getGroupUsers();
    }

    /**
     * Method that is in charge of Firebase listener cleanup
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        myEventUserRef.removeEventListener(addEventChildListener);
        myGroupRef.removeEventListener(addEmailChildListener);
        for (DatabaseReference db : groupsToRemove) {
            db.removeEventListener(addEventChildListener);
        }
    }

}
