package com.davego44.familysync.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.davego44.familysync.R;
import com.davego44.familysync.adapters.SavedEventAdapter;
import com.davego44.familysync.firebase.Event;
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
 * Created by daveg on 5/27/2017.
 */

public class AddEventDialog {
    private Dialog dialog;
    private DatabaseReference userEventRef;
    private DatabaseReference userSavedEventRef;
    private EditText titleEditText;
    private EditText locationEditText;
    private EditText notesEditText;
    private TextView timeTextView;
    private TextView dateTitleTextView;
    private Spinner spinner;
    private SavedEventAdapter adapter;
    private ChildEventListener childEventListener;
    private Calendar selectedDateCal;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private String email;

    public AddEventDialog(final Context context, DatabaseReference userEventRef, DatabaseReference userSavedEventRef, String email) {
        dialog = new Dialog(context);
        dialog.setTitle("Create an Event");
        dialog.setContentView(R.layout.dialog_create_event);

        selectedDateCal = (Calendar) Calendar.getInstance().clone();

        this.email = email;
        this.userEventRef = userEventRef;
        this.userSavedEventRef = userSavedEventRef;

        setupTimeDialogListener();
        setupViews();
        setupSavedEventListener();
        setupButtonListeners();
    }

    //setup the UI elements
    private void setupViews() {
        titleEditText = (EditText) dialog.findViewById(R.id.titleEditText);
        locationEditText = (EditText) dialog.findViewById(R.id.locationEditText);
        notesEditText = (EditText) dialog.findViewById(R.id.notesEditText);

        timeTextView = (TextView) dialog.findViewById(R.id.timeTextView);
        dateTitleTextView = (TextView) dialog.findViewById(R.id.dateTitleTextView);

        spinner = (Spinner) dialog.findViewById(R.id.eventSpinner);
        adapter = new SavedEventAdapter(dialog.getContext(), new ArrayList<Event>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.add(new Event("NONE","","",0,""));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                if (event.getName().equals("NONE"))
                    return;
                titleEditText.setText(event.getName());
                locationEditText.setText(event.getLocation());
                notesEditText.setText(event.getMessage());
                Calendar inspectingCal = (Calendar) selectedDateCal.clone();
                inspectingCal.setTime(new Date(event.getDate()));
                selectedDateCal.set(Calendar.HOUR_OF_DAY, inspectingCal.get(Calendar.HOUR_OF_DAY));
                selectedDateCal.set(Calendar.MINUTE, inspectingCal.get(Calendar.MINUTE));
                timeTextView.setText(getTimeString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //sets up the listeners for the time selector provided by android studios
    private void setupTimeDialogListener() {
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDateCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateCal.set(Calendar.MINUTE, minute);
                timeTextView.setText(getTimeString());
            }
        };
    }

    private void setupSavedEventListener() {
        if (childEventListener != null)
            userSavedEventRef.removeEventListener(childEventListener);
        childEventListener = userSavedEventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove(dataSnapshot.getValue(Event.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    //add all the button listeners for the internal dialog's buttons
    private void setupButtonListeners() {
        Button addEventButton = (Button) dialog.findViewById(R.id.addEventButton);
        Button saveEventButton = (Button) dialog.findViewById(R.id.saveEventButton);
        Button deleteSavedEventButton = (Button) dialog.findViewById(R.id.deleteSavedEventButton);
        final Button cancelEventButton = (Button) dialog.findViewById(R.id.cancelEventButton);

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timeDialog = new TimePickerDialog(dialog.getContext(), timeSetListener, 0, 0, false);
                timeDialog.show();
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String notes = notesEditText.getText().toString();

                if (title.equals("")) {
                    titleEditText.setError("Please enter an event title");
                } else if (title.length() < 3) {
                    titleEditText.setError("Event title must be longer than 2 characters");
                } else if (title.length() > 19) {
                    titleEditText.setError("Event title must be below 20 characters");
                } else {
                    Event event = new Event(title, notes, location, selectedDateCal.getTime().getTime(), email);
                    userEventRef.push().setValue(event);
                    dialog.cancel();
                }
            }
        });

        cancelEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childEventListener != null)
                    userSavedEventRef.removeEventListener(childEventListener);
                dialog.cancel();
            }
        });

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                if (title.equals("")) {
                    titleEditText.setError("Please enter an event title");
                } else if (title.length() < 3) {
                    titleEditText.setError("Event title must be longer than 2 characters");
                } else if (title.length() > 19) {
                    titleEditText.setError("Event title must be below 20 characters");
                } else {
                    final Event event = new Event(title, notes, location, selectedDateCal.getTime().getTime(), email);
                    userSavedEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 9) {
                                Toast.makeText(dialog.getContext(), "No more than 10 events can be saved in this scope", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                if (event.getName().equals(dataSnap.getValue(Event.class).getName())) {
                                    Toast.makeText(dialog.getContext(), "An event with this name has already been saved", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            userSavedEventRef.push().setValue(event);
                            Toast.makeText(dialog.getContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        deleteSavedEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Event Event = (Event) spinner.getSelectedItem();
                if (Event.getName().equals("NONE"))
                    return;
                userSavedEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            if (Event.equals(dataSnap.getValue(Event.class))) {
                                dataSnap.getRef().removeValue();
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
    }

    public String getTimeString() {
        String time = String.valueOf(selectedDateCal.get(Calendar.HOUR));
        if (time.equals("0"))
            time = "12";
        time += ":";
        if (selectedDateCal.get(Calendar.MINUTE) < 10)
            time += "0" + String.valueOf(selectedDateCal.get(Calendar.MINUTE));
        else
            time += String.valueOf(selectedDateCal.get(Calendar.MINUTE));
        time += selectedDateCal.get(Calendar.AM_PM) == Calendar.AM ? " am" : " pm";
        return time;
    }

    /**
     * Method that displays the internal dialog to add events
     */
    public void show(Date date) {
        selectedDateCal.setTime(date);
        selectedDateCal.set(Calendar.HOUR_OF_DAY, 17);
        selectedDateCal.set(Calendar.MINUTE, 0);
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL);
        String selectedDay = dateFormatter.format(date);
        dateTitleTextView.setText(selectedDay);
        titleEditText.setText("");
        locationEditText.setText("");
        notesEditText.setText("");
        timeTextView.setText(getTimeString());
        spinner.setSelection(0,true);
        dialog.show();
    }

    public void destroy() {
        if (childEventListener != null)
            userSavedEventRef.removeEventListener(childEventListener);
    }
}
