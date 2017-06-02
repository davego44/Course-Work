package com.davego44.familysync.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.davego44.familysync.helper.Utilities;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

/**
 * Base activity for all activities that holds user account data and handles authentication
 */
public class BaseActivity extends AppCompatActivity {
    protected String baseEncodedEmail;
    protected DatabaseReference baseFirebaseRef;
    protected FirebaseAuth baseFirebaseAuth;
    protected FirebaseAuth.AuthStateListener baseAuthListener;

    /**
     * Initializes the Firebase references and grabs logged in user data
     * @param savedInstanceState Generic activity data
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseFirebaseRef = FirebaseDatabase.getInstance().getReference();
        baseFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = baseFirebaseAuth.getCurrentUser();
        if (user != null)
            baseEncodedEmail = Utilities.encodeEmail(user.getEmail());

        if (!((this instanceof SignInActivity) || (this instanceof CreateAccountActivity))) {
            baseAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = baseFirebaseAuth.getCurrentUser();
                    if (user == null)
                        moveToLoginScreen();
                }
            };
            baseFirebaseAuth.addAuthStateListener(baseAuthListener);
        }
    }

    /**
     * Move the user to the login screen if authentication fails
     */
    private void moveToLoginScreen() {
        Intent intent = new Intent(BaseActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Method that runs when the activity is finished
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!((this instanceof SignInActivity) || (this instanceof CreateAccountActivity))) {
            baseFirebaseAuth.removeAuthStateListener(baseAuthListener);
        }
    }
}
