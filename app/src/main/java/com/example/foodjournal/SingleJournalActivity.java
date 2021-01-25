package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class SingleJournalActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference docRef;


    boolean update = false;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        // set header title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Journal");

        // links elements to views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

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


        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

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

    // to pick image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // set ImageView to picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if user picked an image
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            //get data and set imageView to image
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    // get extension file [image]
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
            notebookRef.add(new Journal(null, title, description, priority));

            Toast.makeText(this, "Journal added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}