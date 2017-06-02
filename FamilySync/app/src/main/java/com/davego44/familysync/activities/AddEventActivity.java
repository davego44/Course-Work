package com.davego44.familysync.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.davego44.familysync.helper.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends BaseActivity {

    //Time for event
    private Calendar selectedDateCal;

    //Firebase references
    private DatabaseReference myEventsRef, userSavedEventRef;

    //UI Elements
    private EditText nameEditText, locationEditText, notesEditText;
    private TextView dateTextView, timeTextView;
    private Spinner spinner;
    private Button saveButton, deleteButton;

    //Adapter for spinner
    private SavedEventAdapter adapter;

    //Listeners
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private ChildEventListener childEventListener;

    /**
     * This method initializes and sets up the UI Elements, create Firebase references, and sets
     * the time
     * @param savedInstanceState Generic data for activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        selectedDateCal = (Calendar) Calendar.getInstance().clone();
        myEventsRef = baseFirebaseRef.child(Constants.Firebase.EVENTS_USERS).
                child(baseEncodedEmail);
        userSavedEventRef= baseFirebaseRef.child(Constants.Firebase.USERS).child(baseEncodedEmail).
                child(Constants.Firebase.SAVED_EVENTS);
        initializeUIElements();
        spinnerSetup();
        setupTimeDialogListener();
        setupSavedEventListener();
        setupButtonListeners();
        Intent intent = getIntent();
        long dateLong = intent.getLongExtra(Constants.Intent.DATE, 0);
        if (dateLong != 0) {
            Date date = new Date();
            date.setTime(dateLong);
            selectedDateCal.setTime(date);
            selectedDateCal.set(Calendar.HOUR_OF_DAY, 17);
            selectedDateCal.set(Calendar.MINUTE, 0);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL);
            timeTextView.setText(getTimeString());
            dateTextView.setText(dateFormatter.format(date));
        }
    }

    /**
     * Initializes the UI Elements
     */
    private void initializeUIElements() {
        dateTextView = (TextView) findViewById(R.id.addEventDateTextView);
        nameEditText = (EditText) findViewById(R.id.addEventNameEditText);
        locationEditText = (EditText) findViewById(R.id.addEventLocationEditText);
        notesEditText = (EditText) findViewById(R.id.addEventNotesEditText);
        timeTextView = (TextView) findViewById(R.id.addEventTimeTextView);
        spinner = (Spinner) findViewById(R.id.addEventLoadSpinner);
        saveButton = (Button) findViewById(R.id.addEventSaveButton);
        deleteButton = (Button) findViewById(R.id.addEventDeleteButton);
    }

    /**
     * Sets up the spinner and sets listener
     */
    private void spinnerSetup() {
        adapter = new SavedEventAdapter(this, new ArrayList<Event>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.add(new Event(Constants.Spinner.DEFAULT_NAME,"","",0,""));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                if (event.getName().equals(Constants.Spinner.DEFAULT_NAME))
                    return;
                nameEditText.setText(event.getName());
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

    /**
     * Sets up the listener for the time dialog
     */
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

    /**
     * From the time it generates the appropriate string
     * @return String representation of the time
     */
    private String getTimeString() {
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
     * Sets up the Firebase listeners to connect the spinner's adapter with the database
     */
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

    /**
     * Sets up all the listeners for the buttons namely save and delete, as well as sets up listener
     * to create the time dialog
     */
    private void setupButtonListeners() {
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            TimePickerDialog timeDialog = new TimePickerDialog(AddEventActivity.this,
                    timeSetListener, 0, 0, false);
            timeDialog.show();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String title = nameEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String notes = notesEditText.getText().toString();
            if (title.equals("")) {
                nameEditText.setError("Please enter an event title");
            } else if (title.length() < 3) {
                nameEditText.setError("Event title must be longer than 2 characters");
            } else if (title.length() > 19) {
                nameEditText.setError("Event title must be below 20 characters");
            } else {
                final Event event = new Event(title, notes, location,
                        selectedDateCal.getTime().getTime(), baseEncodedEmail);
                userSavedEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 9) {
                            Toast.makeText(AddEventActivity.this, "No more than 10 events can be " +
                                    "saved in this scope", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            if (event.getName().equals(dataSnap.getValue(Event.class).getName())) {
                                Toast.makeText(AddEventActivity.this, "An event with this name " +
                                        "has already been saved", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        userSavedEventRef.push().setValue(event);
                        Toast.makeText(AddEventActivity.this, "Successfully Saved",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final Event Event = (Event) spinner.getSelectedItem();
            if (Event.getName().equals(Constants.Spinner.DEFAULT_NAME))
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
                public void onCancelled(DatabaseError databaseError) { }
            });
            }
        });
    }

    /**
     * Creates the menu at the top
     * @param menu Menu for the activity
     * @return Whether or not it was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_add_event, menu);
        return true;
    }

    /**
     * Sets action for ADD EVENT menu item
     * @param item Item that has been triggered
     * @return Whether or not it was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_add_event:
                String title = nameEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String notes = notesEditText.getText().toString();
                if (title.equals("")) {
                    nameEditText.setError("Please enter an event title");
                } else if (title.length() < 3) {
                    nameEditText.setError("Event title must be longer than 2 characters");
                } else if (title.length() > 19) {
                    nameEditText.setError("Event title must be below 20 characters");
                } else {
                    Event event = new Event(title, notes, location,
                            selectedDateCal.getTime().getTime(), baseEncodedEmail);
                    myEventsRef.push().setValue(event);
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the destroying of the activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (childEventListener != null)
            userSavedEventRef.removeEventListener(childEventListener);
    }
}
