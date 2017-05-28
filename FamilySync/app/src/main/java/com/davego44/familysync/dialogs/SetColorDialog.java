package com.davego44.familysync.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davego44.familysync.firebase.GroupUser;
import com.davego44.familysync.R;
import com.davego44.familysync.helper.Utilities;
import com.google.firebase.database.DatabaseReference;


/**
 * A class that is in charge of displaying a dialog that finds a user
 *
 * This find user dialog displays a screen to allow the user to type in
 * a user's email they want to add to their group. If it exists on Firebase,
 * it adds it, but if not then displays an error message.
 *
 * @author David
 * @version 1.0
 */
public class SetColorDialog {
    private Dialog dialog;
    private LinearLayout exampleColor;
    private DatabaseReference myUserGroupRef;
    private String email;
    private String color;
    private String name;

    /**
     * Constructor that sets up the internal dialog, Firebase references, and buttons for the dialog
     *
     * @param context For the dialog
     */
    public SetColorDialog(Context context, DatabaseReference userGroup, String email, String name) {
        dialog = new Dialog(context);
        dialog.setTitle("Select Color");
        dialog.setContentView(R.layout.dialog_set_color);

        myUserGroupRef = userGroup;
        this.email = email;
        this.name = name;
        color = "";

        setupUI();
    }

    private void setupUI() {
        TextView userNameTextView = (TextView) dialog.findViewById(R.id.userNameTextView);
        TextView userEmailTextView = (TextView) dialog.findViewById(R.id.userEmailTextView);
        Button cancelColorSelectButton = (Button) dialog.findViewById(R.id.cancelColorSelectButton);
        Button selectButton = (Button) dialog.findViewById(R.id.selectButton);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(dialog.getContext(),
                R.array.colors_array, android.R.layout.simple_spinner_item);

        exampleColor = (LinearLayout) dialog.findViewById(R.id.exampleColor);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        userNameTextView.setText("Member Name:  \n" + name);
        userEmailTextView.setText("Member Email: \n" + email);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = (String)parent.getItemAtPosition(position);
                exampleColor.setBackgroundResource(getColorRes(color));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancelColorSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.equals("") && !color.equals("")) {
                    GroupUser groupUser = new GroupUser(Utilities.encodeEmail(email), name);
                    groupUser.setColorRes(getColorRes(color));
                    myUserGroupRef.push().setValue(groupUser);
                    Toast.makeText(dialog.getContext(), "Successfully Added", Toast.LENGTH_SHORT).show();
                    cancel();
                }
            }
        });
    }

    private int getColorRes(String s){
        int color = 0;

        switch (s) {
            case "Purple":
                color = R.color.color1;
                break;
            case "Orange":
                color = R.color.color2;
                break;
            case "Green":
                color = R.color.color4;
                break;
            case "Brown":
                color = R.color.color3;
                break;
            default:
                color = R.color.color4;
        }

        return color;
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
    public void cancel() {
        dialog.cancel();
    }

}
