package com.example.foodjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyJournalsActivity extends AppCompatActivity {
    private JournalAdapter adapter;
    private int category;
    LinearLayout emptyLayout;
    TextView emptyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_journals);

        // Firebase
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        CollectionReference notebookRef = mStore.collection("Journal");

        // link views
        emptyLayout = findViewById(R.id.emptyLayout);
        emptyText = findViewById(R.id.info);

        // set header title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Journals");


        //get extra intent from Main Activity to determine what filter to use with query [category]
        Intent intent = getIntent();
        category = intent.getIntExtra("category", -1);
        String id = intent.getStringExtra("userId");

        Query queryReference;
        if (category == -1) {
            //if the category of the journal was not specified [-1], show all journals by user
            queryReference = notebookRef.document(id)
                    .collection("Journals");
        } else {
            //if the category was specified, get it
            queryReference = notebookRef.document(id)
                    .collection("Journals").whereEqualTo("priority", category);
        }


        // add listener to button for adding a new journal
        FloatingActionButton buttonAddJournal = findViewById(R.id.button_add_journal);
        buttonAddJournal.setOnClickListener(v -> {
            // send to single journal with category
            Intent intent1 = new Intent(getApplicationContext(), SingleJournalActivity.class);
            intent1.putExtra("category", category);
            startActivity(intent1);
        });

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
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getApplicationContext(), "Journal deleted", Toast.LENGTH_LONG).show();
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        //get document reference after clicking on element to update
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String path = documentSnapshot.getReference().getPath();

            Toast.makeText(getApplicationContext(),
                    "Update Journal", Toast.LENGTH_SHORT).show();

            // send doc reference to SingleJournalActivity to update document
            Intent intent = new Intent(getApplicationContext(), SingleJournalActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("category", category);
            startActivity(intent);
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