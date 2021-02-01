package com.example.foodjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    // choose journal category
    public void mealCategory(View view) {
        switch (view.getId()) {

            case R.id.meal_brunch:
                //category: 1

                break;

            case R.id.meal_lunch:
                //category: 2

                break;

            case R.id.meal_dinner:
                //category: 3

                break;

            case R.id.meal_snacks:
                //category: 4

                break;

            case R.id.meal_all:
                //category: -1

                break;

        }
        }
}