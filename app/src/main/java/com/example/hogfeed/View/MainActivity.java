package com.example.hogfeed.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hogfeed.R;


//The main activity will include all the actions for the home screen
//this includes: a recycler view of a list of events, tap and view details
// of the events, and a button to add new events

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
