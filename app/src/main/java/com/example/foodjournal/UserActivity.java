package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userId;
    TextView profileUsername;
    TextView profileEmail;
    TextView logOut;
    Button buttonJournals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        profileEmail = findViewById(R.id.profileEmail);
        profileUsername = findViewById(R.id.profileUsername);
        logOut = findViewById(R.id.profile_signOut);
        buttonJournals = findViewById(R.id.btn_journals);

        // set header title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("User Profile");

        // set user information
        userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = mStore.collection("Users").document(userId);
        getUserData(documentReference);

        // send to myJournals page if clicked
        buttonJournals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(UserActivity.this, "All Journals", Toast.LENGTH_SHORT).show();
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


    private void getUserData(DocumentReference docRef) {
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            String username = documentSnapshot.getString("username");

                            // set values to TextView elements
                            profileEmail.setText(email);
                            profileUsername.setText(username);
                        }
                    }
                });
    }
}