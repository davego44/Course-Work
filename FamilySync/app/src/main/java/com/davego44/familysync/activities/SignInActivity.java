package com.davego44.familysync.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.davego44.familysync.R;
import com.davego44.familysync.helper.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends BaseActivity {

    private EditText signInEmailEditText;
    private EditText signInPasswordEditText;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        createInterfaceElements();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = baseFirebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(SignInActivity.this, CalendarViewerActivity.class);
                    startActivity(intent);
                }
            }
        };
        baseFirebaseAuth.addAuthStateListener(authListener);
        Intent intent = getIntent();
        String emailText = intent.getStringExtra(Constants.Intent.EMAIL);
        String passwordText = intent.getStringExtra(Constants.Intent.PASSWORD);
        if (emailText != null & passwordText != null) {
            signInEmailEditText.setText(emailText);
            signInPasswordEditText.setText(passwordText);
        }
    }

    private void createInterfaceElements()
    {
        signInEmailEditText = (EditText) findViewById(R.id.signInEmailEditText);
        signInPasswordEditText = (EditText) findViewById(R.id.signInPasswordEditText);
    }

    public void onSignInPressed(View view) {
        String email = signInEmailEditText.getText().toString();
        String password = signInPasswordEditText.getText().toString();
        if (email.equals("")) {
            signInEmailEditText.setError(Constants.Errors.INVALID_EMAIL);
            return;
        }
        if (password.equals("")) {
            signInPasswordEditText.setError(Constants.Errors.INVALID_PASSWORD);
            return;
        }
        Toast.makeText(SignInActivity.this, "Signing in...", Toast.LENGTH_LONG).show();
        baseFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignInActivity.this, "Success", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onCreateAccountPressed(View view) {
        String email = signInEmailEditText.getText().toString();
        String password = signInPasswordEditText.getText().toString();
        Intent intent = new Intent(SignInActivity.this, CreateAccountActivity.class);
        intent.putExtra(Constants.Intent.EMAIL, email);
        intent.putExtra(Constants.Intent.PASSWORD, password);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        baseFirebaseAuth.removeAuthStateListener(authListener);
    }
}
