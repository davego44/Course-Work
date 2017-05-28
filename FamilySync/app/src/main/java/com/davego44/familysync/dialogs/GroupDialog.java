package com.davego44.familysync.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A class that is in charge of displaying a dialog that allows group management
 *
 * This add event dialog displays a screen that allows the user to see who
 * is in their group and allows them to add more or clear the list.
 *
 * @author David
 * @version 1.0
 */
public class GroupDialog {
    private Dialog dialog;

    private DatabaseReference userRef;
    private DatabaseReference myUserGroupRef;

    private EditText userEmailEditText;
    private String userToCheck;

    /**
     * Constructor that sets up the internal dialog, Firebase references, and buttons for the dialog
     *
     * @param context For the dialog
     * @param userRef Firebase database reference set to the users using the app
     * @param myUserGroupRef Firebase database reference set to the group emails under the user
     */
    public GroupDialog(Context context, DatabaseReference userRef, DatabaseReference myUserGroupRef) {
        dialog = new Dialog(context);
        dialog.setTitle("Family Management");
        dialog.setContentView(R.layout.dialog_manage_group);

        this.userRef = userRef;
        this.myUserGroupRef = myUserGroupRef;

        setupUI();
    }

    //creates UI elements and sets the button listeners
    private void setupUI() {

        Button addMemberButton = (Button) dialog.findViewById(R.id.addMemberButton);
        Button removeMemberButton = (Button) dialog.findViewById(R.id.removeMemberButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        userEmailEditText = (EditText) dialog.findViewById(R.id.userEmailEditText);

        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEmailEditText.getText().toString();
                if (user.equals("")) {
                    userEmailEditText.setError("Please enter a user to add");
                } else {
                    check(user);
                }
            }
        });

        removeMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userEmailEditText.getText().toString();
                if (user.equals("")) {
                    userEmailEditText.setError("Please enter a user to add");
                } else {
                    checkIfExistsInMembers(user);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }

    private void check(final String user) {
        myUserGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 4) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Utilities.encodeEmail(user).equals(snapshot.getValue(GroupUser.class).getEmail())) {
                            Toast.makeText(dialog.getContext(), user + " is already a member", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    checkIfExists(user);
                } else
                    Toast.makeText(dialog.getContext(), "Adding more than 4 members is outside the scope", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfExists(String user) {
        userToCheck = user;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String theUser = snapshot.getKey();
                    if (Utilities.encodeEmail(userToCheck).equals(theUser)) {
                        userName = (String) snapshot.child("name").getValue();
                        if (userName == null)
                            return;
                        Toast.makeText(dialog.getContext(), "Found " + userToCheck, Toast.LENGTH_SHORT).show();
                        SetColorDialog setColorDialog = new SetColorDialog(dialog.getContext(), myUserGroupRef, userToCheck, userName);
                        setColorDialog.show();
                        return;
                    }
                }
                Toast.makeText(dialog.getContext(), "Cannot find user: " + userToCheck, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfExistsInMembers(String user) {
        userToCheck = user;

        myUserGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupUser groupUser;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupUser = snapshot.getValue(GroupUser.class);
                    if (Utilities.encodeEmail(userToCheck).equals(groupUser.getEmail())) {
                        snapshot.getRef().removeValue();
                        Toast.makeText(dialog.getContext(), "Removed: " + userToCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(dialog.getContext(), "Cannot find user: " + userToCheck, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Method that shows the internal dialog
     */
    public void show() {
        dialog.show();
    }

    /**
     * Method that cancels the internal dialog
     */
    private void cancel() {
        dialog.cancel();
    }
}
