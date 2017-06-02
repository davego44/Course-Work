package com.davego44.familysync.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davego44.familysync.helper.Constants;
import com.davego44.familysync.helper.EmptyView;
import com.davego44.familysync.firebase.Event;
import com.davego44.familysync.adapters.EventAdapter;
import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;
import com.davego44.familysync.activities.CalendarViewerActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Custom Events View Fragment
 *
 * A fragment that displays a list of events of selected day
 *
 * @author David
 * @version 1.0
 */
public class EventViewFragment extends Fragment {

    private EventAdapter eventAdapter;
    private ArrayList<DatabaseReference> groupsToRemove;
    private ChildEventListener addEventChildListener;
    private ChildEventListener addEmailChildListener;
    private Calendar selectedDateCalendar;
    private Calendar eventsCalendar;
    private Date eventDate;
    private ImageButton maximizeButton;
    private ImageButton minimizeButton;
    private TextView todayDateTextView;
    private DatabaseReference eventUserRef;
    private DatabaseReference myEventUserRef;
    private DatabaseReference myGroupRef;
    private String email;

    /**
     * Initializes the UI Elements and other data
     */
    public EventViewFragment(){
        selectedDateCalendar = (Calendar) Calendar.getInstance().clone();
        eventsCalendar = (Calendar) selectedDateCalendar.clone();
        groupsToRemove = new ArrayList<>();
        eventDate = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_view, container, false);

        eventAdapter = new EventAdapter(view.getContext(), new ArrayList<Event>(), email);

        ListView eventListView = (ListView) view.findViewById(R.id.eventListView);
        eventListView.setEmptyView(new EmptyView(getContext()));
        eventListView.setAdapter(eventAdapter);

        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event nowEvent = (Event) parent.getItemAtPosition(position);
                if (!nowEvent.getAuthor().equals(email)) {
                    Toast.makeText(getContext(), "Cannot edit an event that is not yours", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_delete_event);
                dialog.setTitle("Confirm Delete");
                Button confirmDeleteEventButton = (Button) dialog.findViewById(R.id.confirmDeleteEventButton);
                Button cancelDeleteEventButton = (Button) dialog.findViewById(R.id.cancelDeleteEventButton);
                confirmDeleteEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myEventUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                    Event event = snap.getValue(Event.class);
                                    if (nowEvent.equals(event)) {
                                        snap.getRef().removeValue();
                                        Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                cancelDeleteEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                Toast.makeText(getContext(), "Editing an event is outside of the scope, only deleting is possible", Toast.LENGTH_LONG).show();
                dialog.show();
            }
        });

        todayDateTextView = (TextView) view.findViewById(R.id.todayDateTextView);
        todayDateTextView.setText(DateFormat.getDateInstance(DateFormat.FULL).format(selectedDateCalendar.getTime()));

        maximizeButton = (ImageButton) view.findViewById(R.id.maximizeButton);
        minimizeButton = (ImageButton) view.findViewById(R.id.minimizeButton);

        setupButtonListeners();

        setupListeners();

        return view;
    }

    public void setGroupVariables(String email, DatabaseReference rootRef) {
        this.email = email;
        eventUserRef = rootRef.child(Constants.Firebase.EVENTS_USERS);
        myEventUserRef = eventUserRef.child(email);
        myGroupRef = rootRef.child(Constants.Firebase.USERS).child(email).child("group");
    }

    public void setupListeners(){
        addEventChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                eventDate.setTime(event.getDate());
                eventsCalendar.setTime(eventDate);
                if ((eventsCalendar.get(Calendar.YEAR) == selectedDateCalendar.get(Calendar.YEAR)) &&
                        (eventsCalendar.get(Calendar.MONTH) == selectedDateCalendar.get(Calendar.MONTH)) &&
                        (eventsCalendar.get(Calendar.DATE) == selectedDateCalendar.get(Calendar.DATE))) {
                    eventAdapter.add(event);
                    eventAdapter.orderEvent();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                eventAdapter.remove(event);
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
                String snap_email = groupUser.getEmail();
                if (!snap_email.equals(email)) {
                    eventAdapter.addGroupUser(groupUser);
                    DatabaseReference db = eventUserRef.child(snap_email);
                    db.addChildEventListener(addEventChildListener);
                    groupsToRemove.add(db);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                eventAdapter.selectedChanged(dataSnapshot.getValue(GroupUser.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                GroupUser groupUser = dataSnapshot.getValue(GroupUser.class);
                String snap_email = groupUser.getEmail();
                if (!snap_email.equals(email)) {
                    eventAdapter.removeGroupUser(groupUser);
                    DatabaseReference db = eventUserRef.child(Utilities.encodeEmail(snap_email));
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

    public void setupButtonListeners(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CalendarViewerActivity)getActivity()).zoomInOut();
                if (minimizeButton.getVisibility() == View.GONE) {
                    minimizeButton.setVisibility(View.VISIBLE);
                    maximizeButton.setVisibility(View.GONE);
                } else {
                    minimizeButton.setVisibility(View.GONE);
                    maximizeButton.setVisibility(View.VISIBLE);
                }
            }
        };
        maximizeButton.setOnClickListener(onClickListener);
        minimizeButton.setOnClickListener(onClickListener);
    }

    public void setDate(Date date) {
        selectedDateCalendar.setTime(date);
        if (todayDateTextView != null) {
            todayDateTextView.setText(DateFormat.getDateInstance(DateFormat.FULL).format(selectedDateCalendar.getTime()));
            eventAdapter.clear();
            myEventUserRef.removeEventListener(addEventChildListener);
            myEventUserRef.addChildEventListener(addEventChildListener);
            for (DatabaseReference db : groupsToRemove) {
                db.removeEventListener(addEventChildListener);
                db.addChildEventListener(addEventChildListener);
            }
        }
    }

    public Date getDate() {
        return selectedDateCalendar.getTime();
    }

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
