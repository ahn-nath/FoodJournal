package com.example.foodjournal;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "General";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    FirebaseUser currentUser;
    private String userId;
    Button btnRegister;
    Button btnLogin;
    EditText userEmail;
    EditText userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
        userEmail = findViewById(R.id.et_emailLogin);
        userPassword = findViewById(R.id.et_passwordLogin);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    //if there's an user, redirect to UserActivity
    public void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }


    // register or log in
    @SuppressLint("NonConstantResourceId")
    public void userAuth(View view) {
        final  String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        //Validate
        if (email.isEmpty()) {
            userEmail.setError("Please enter email address");
            userEmail.requestFocus();
        }
        else if (password.isEmpty()) {
            userPassword.setError("Please enter password");
            userPassword.requestFocus();
        }

        //if not empty, proceed to register or log in
        else {
        switch (view.getId()) {

            // register new user [button clicked]
            case R.id.btn_register:

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                //Create user and add custom fields to database
                                userId = mAuth.getCurrentUser().getUid();
                                // get username part from email address
                                int index = email.indexOf('@');
                                String username = email.substring(0, index);

                                // Create Journal collection for user
                                DocumentReference docReference = mStore.collection("Journal").document(userId);
                                Map<String, Object> userJournal = new HashMap<>();
                                userJournal.put("email", email);
                                userJournal.put("username", username);

                                docReference.set(userJournal)
                                        .addOnFailureListener(e -> {
                                            // trace error
                                             Log.d("ERROR CREATING JOURNAL", task.getException().getMessage());
                                        });

                                // Users collection
                                DocumentReference documentReference = mStore.collection("Users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                user.put("email", email);
                                user.put("username", username);

                                documentReference.set(user)

                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getApplicationContext(), "User registered.",
                                                    Toast.LENGTH_LONG).show();
                                            updateUI(currentUser); //send to UserActivity

                                        })

                                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "There's was a problem while trying to register" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show());

                                // problem while registering
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Failed to register. " + task.getException(),
                                        Toast.LENGTH_SHORT).show();

                                updateUI(null);
                            }
                        });
                break;
            // log in user  [button clicked]
            case R.id.btn_login:

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "User logged in",
                                        Toast.LENGTH_LONG).show();

                                updateUI(currentUser); //send to UserActivity

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();

                                updateUI(null);
                            }
                        });
                break;
            }

        }
    }
}