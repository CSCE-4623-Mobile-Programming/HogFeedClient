package com.example.hogfeed.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hogfeed.R;


import java.util.HashMap;


public class MapsActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void addEvent(View v)
    {
        Intent i = new Intent(this, EventActivity.class);
        startActivity(i);
    }

    public void home(View v)
    {
        Intent i = new Intent(this, MapsView.class);
        startActivity(i);
    }




}
