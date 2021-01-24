package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SingleJournalActivity extends AppCompatActivity {
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    boolean update = false;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        // set header title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Journal");

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(4);


        //check if update or create new document
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        //if value different from empty get reference to document
        if (path != null) {
            setTitle("Update Journal");
            update = true;
            docRef = mStore.document(path);
            getDocumentData(docRef);
        }

    }
    // inflate menu [new_journal_menu]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_journal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // if someone clicks on save_journal item in menu, call saveJournal()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_journal:
                saveJournal();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // retrieve values from document if "update" is true
    private void getDocumentData(DocumentReference docRef) {

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("title");
                            String description = documentSnapshot.getString("description");

                            //set EditText elements to values in document
                            editTextTitle .setText(name, TextView.BufferType.EDITABLE);
                            editTextDescription.setText(description, TextView.BufferType.EDITABLE);

                        }
                    }
                });
    }


    // create or update journal
    private void saveJournal() {
        // get input values
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        // verify values are not empty/null
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        // update the product
        if (update) {
            //Update product
            docRef.update("title", title, "description", description, "priority", priority);

            Toast.makeText(this, "Journal updated", Toast.LENGTH_SHORT).show();
            finish();
        }

        else {
            // create new journal
            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("Journal");
            notebookRef.add(new Journal(title, description, priority));

            Toast.makeText(this, "Journal added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}