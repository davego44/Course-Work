package com.davego44.familysync.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.davego44.familysync.helper.Constants;
import com.davego44.familysync.R;
import com.davego44.familysync.firebase.User;
import com.davego44.familysync.helper.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class CreateAccountActivity extends BaseActivity {

    //UI element variable declaration
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        createInterfaceElements();
        Intent intent = getIntent();
        String email = intent.getStringExtra(Constants.Intent.EMAIL);
        String password = intent.getStringExtra(Constants.Intent.PASSWORD);
        if (email != null & password != null) {
            emailEditText.setText(email);
            passwordEditText.setText(password);
        }
    }

    private void createInterfaceElements() {
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

    public void onCreateAccountPressed(View view) {
        final String name = nameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if (name.equals("")) {
            nameEditText.setError(Constants.Errors.INVALID_NAME);
            return;
        }
        if (email.equals("")) {
            emailEditText.setError(Constants.Errors.INVALID_EMAIL);
            return;
        }
        if (password.equals("")) {
            passwordEditText.setError(Constants.Errors.INVALID_PASSWORD);
            return;
        }
        Toast.makeText(CreateAccountActivity.this, "Creating...", Toast.LENGTH_SHORT).show();
        baseFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String encodedEmail = Utilities.encodeEmail(email);

                    User user = new User(name, encodedEmail);
                    baseFirebaseRef.child(Constants.Firebase.USERS).child(encodedEmail).setValue(user);

                    Toast.makeText(CreateAccountActivity.this, "Success", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateAccountActivity.this, CalendarViewerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorText = "ERROR";
                    if (task.getException().getMessage() != null) {
                        errorText = task.getException().getMessage();
                    }
                    Toast.makeText(CreateAccountActivity.this, errorText, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onBackPressed(View view) {
        Intent intent = new Intent(CreateAccountActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
