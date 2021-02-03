package com.example.foodjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    //Top menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), UserActivity.class));

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    // choose journal category
    public void mealCategory(View view) {
        int category = -1;
        Intent intent;

        // get category
        switch (view.getId()) {

            case R.id.meal_brunch:
                //category: 1
                category = 1;


                break;

            case R.id.meal_lunch:
                //category: 2
                category = 2;

                break;

            case R.id.meal_dinner:
                //category: 3
                category = 3;


                break;

            case R.id.meal_snacks:
                //category: 4
                category = 4;

                break;


        }
        // send to activity with filter
        intent = new Intent(getApplicationContext(), MyJournalsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
        }
}