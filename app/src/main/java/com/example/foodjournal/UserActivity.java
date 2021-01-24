package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView logOut;
    Button buttonJournals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.profile_signOut);
        buttonJournals = findViewById(R.id.btn_journals);

        // send to myJournals page if clicked
        buttonJournals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(UserActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MyJournalsActivity.class));
            }

        });
    }

    // log out user
    public void logOut(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}