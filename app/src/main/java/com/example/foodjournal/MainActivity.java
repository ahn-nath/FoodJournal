package com.example.foodjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    //if there's an user, redirect to UserActivity
    public void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }


    // register or log in
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
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    //Create user and add custom fields to database

                                    //Get reference to the collection in database and add values
                                    userId = mAuth.getCurrentUser().getUid();
                                    // get username part from email address
                                    int index = email.indexOf('@');
                                    String username = email.substring(0, index);


                                    DocumentReference documentReference = mStore.collection("Users").document(userId);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("username", username);

                                    documentReference.set(user)

                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "User registered.",
                                                            Toast.LENGTH_LONG).show();
                                                    updateUI(currentUser); //send to UserActivity

                                                }})

                                            .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "There's was a problem while trying to register" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // problem while registering
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Failed to register. " + task.getException(),
                                            Toast.LENGTH_SHORT).show();

                                    updateUI(null);
                                }
                            }
                        });
                break;
            // log in user  [button clicked]
            case R.id.btn_login:

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

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
                            }
                        });
                break;
            }

        }
    }
}