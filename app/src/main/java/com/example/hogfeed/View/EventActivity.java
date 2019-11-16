package com.example.hogfeed.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hogfeed.R;


//This activity will be the screen that you use to
//add a new event.
//Which includes actions like:
//entering event name, location, time
//amount of food, pictures, and a 'add new event' button

public class EventActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
    }
}
