package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyJournalsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Journal");
    private Query queryReference;
    private JournalAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journals);

        // add listener to button for adding a new journal
        FloatingActionButton buttonAddJournal = findViewById(R.id.button_add_journal);
        buttonAddJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SingleJournalActivity.class));
            }
        });


        //get extra intent from Main Activity to determine what products to show
        Intent intent = getIntent();
        int category = intent.getIntExtra("category", -1);
        if (category == -1) {
            //if the category of the journal was not specified [-1], show all
            queryReference = notebookRef;
        } else {
            //if the category was specified, get it
            queryReference = notebookRef.whereEqualTo("priority", category);
        }

        // RecyclerView with all journals from database
        setUpRecyclerView(queryReference);
    }


    private void setUpRecyclerView(Query query) {
        FirestoreRecyclerOptions<Journal> options = new FirestoreRecyclerOptions.Builder<Journal>()
                .setQuery(query, Journal.class)
                .build();

        adapter = new JournalAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // delete onSwiped
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getApplicationContext(), "director:" + direction, Toast.LENGTH_LONG).show();
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        //get document reference after clicking on element
        adapter.setOnItemClickListener(new JournalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Journal journal = documentSnapshot.toObject(Journal.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();


                Toast.makeText(getApplicationContext(),
                        "Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();

                // send doc reference to SingleJournalActivity to update document
                Intent intent = new Intent(getApplicationContext(), SingleJournalActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

    // final setup
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}