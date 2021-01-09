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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "General";
    private FirebaseAuth mAuth;
    Button btnRegister;
    Button btnLogin;
    EditText userEmail;
    EditText userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
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


    public void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            startActivity(new Intent(this, UserActivity.class));
            finish();
        }
    }

    public void userAuth(View view) {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        //Validate
        if (email.isEmpty()) {
            userEmail.setError("Please enter email address");
            userEmail.requestFocus();
        } else if (password.isEmpty()) {
            userPassword.setError("Please enter password");
            userPassword.requestFocus();
        } else {
        switch (view.getId()) {

            case R.id.btn_register:
                //if not empty, submit
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "User registered.",
                                            Toast.LENGTH_LONG).show();
                                    updateUI(user); //send to new activity
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
                break;

            case R.id.btn_login:
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "User logged in",
                                            Toast.LENGTH_LONG).show();
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                    updateUI(null);
                                }

                                // ...
                            }
                        });
                break;
            }

        }
    }
}