package com.example.foodjournal;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;


public class SingleJournalActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private DocumentReference docRef;
    FirebaseUser currentUser;
    private StorageReference mStorageRef;


    boolean update = false;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        // set header title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Journal");

        // links elements to views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        LinearLayout layoutImageSet = findViewById(R.id.layoutImageSet);
        Button mButtonChooseImage = findViewById(R.id.button_choose_image);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // set NumberPicker values [list]
        String[] categoriesList = { //shall this go here, don't think so
                "Brunch/Breakfast",
                "Lunch",
                "Dinner",
                "Snack"
        };

        //get extra intent from Main Activity to determine default values for picker
        Intent intent = getIntent();
        int category = intent.getIntExtra("category", -1);
        String[] categories;

        if (category == -1) {
            //if the category of the journal was not specified [-1], show all
            categories = categoriesList;
        } else {
            //if the category was specified, set default
            categories = new String[1];
            categories[0] = categoriesList[category - 1];
        }

        numberPickerPriority.setDisplayedValues(categories);
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(categories.length);


        //check if update or create new document
        String path = intent.getStringExtra("path");

        //if value different from empty get reference to document
        if (path != null) {
            setTitle("Update Journal");
            layoutImageSet.setVisibility(View.GONE);
            update = true;
            docRef = mStore.document(path);
            getDocumentData(docRef);
        }


        mButtonChooseImage.setOnClickListener(v -> openFileChooser());

    }


    // inflate menu [new_journal_menu]
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_journal_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // if someone clicks on save_journal item in menu, call saveJournal()
    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_journal) {
            saveJournal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }

    // retrieve values from document if "update" is true
    private void getDocumentData(DocumentReference docRef) {

        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("title");
                        String description = documentSnapshot.getString("description");

                        //set EditText elements to values in document
                        editTextTitle.setText(name, TextView.BufferType.EDITABLE);
                        editTextDescription.setText(description, TextView.BufferType.EDITABLE);

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

    private void uploadFile(String title, String description, int priority, String date) {

        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                + "." + getFileExtension(mImageUri));

        // progress bar with delay
        // create new notebook
        StorageTask mUploadTask = fileReference.putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // progress bar with delay
                    Handler handler = new Handler();
                    handler.postDelayed(() -> mProgressBar.setProgress(0), 500);


                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imgUrl = uri.toString();
                        if (uri.toString() == null) {
                            imgUrl = "it is null";

                        }

                        // create new notebook
                        CollectionReference notebookRef = mStore
                                .collection("Journal").document(currentUser.getUid()).collection("Journals");

                        notebookRef.add(new Journal(imgUrl, title, description, priority, date));
                    });

                    Toast.makeText(getApplicationContext(), "Journal added", Toast.LENGTH_SHORT).show();
                    finish();

                })
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                });

    }


    // create or update journal
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveJournal() {
        // show message
        Toast.makeText(this, "Uploading file...", Toast.LENGTH_LONG).show();

        // get input values
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String date = java.time.LocalDate.now().toString();
        int priority = numberPickerPriority.getValue();

        // check if image file was selected
        if (mImageUri == null && !update) {
            Toast.makeText(this, "Please select image file", Toast.LENGTH_SHORT).show();
            return;
        }

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
        } else {
            // create new journal
            // call upload file method
            uploadFile(title, description, priority, date);
        }
    }
}