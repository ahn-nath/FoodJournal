package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.profile_signOut);
    }

    //log out
    public void logOut(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}