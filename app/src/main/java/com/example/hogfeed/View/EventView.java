package com.example.hogfeed.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogfeed.Model.ApiClient;
import com.example.hogfeed.Model.ApiInterface;
import com.example.hogfeed.Model.Event;
import com.example.hogfeed.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventView extends AppCompatActivity
{
    //variables
    int id;

    //Components
    TextView listTitle;
    TextView listDescription;
    TextView listQuantity;
    TextView listLocation;
    TextView listLongitude;
    TextView listLatitude;
    ImageView listImage;
    Button buttonNo;
    Button buttonYes;
    String filepath;

    //To use retrofit
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        //Instantiate
        listTitle = (TextView) findViewById(R.id.listTitle);
        listDescription = (TextView) findViewById(R.id.listDescription);
        listQuantity = (TextView) findViewById(R.id.listQuantity);
        listLocation = (TextView) findViewById(R.id.listLocation);
        listLongitude = (TextView) findViewById(R.id.listLongitude);
        listLatitude = (TextView) findViewById(R.id.listLatitude);
        listImage = (ImageView) findViewById(R.id.listImage);
        buttonNo = (Button) findViewById(R.id.buttonNo);
        buttonYes = (Button) findViewById(R.id.buttonYes);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        filepath = "Android/data/com.example.hogfeed/files/Pictures/";

        //Gets id from previous marker click
        Intent receive = getIntent();
        id = receive.getIntExtra("id", 0);

        //Sets other titles
        getOneEvent(id);

        //OnClickForButtons
        //Button No
        buttonNo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Event is kept
                toastMessage("Thank you for letting us know");
                finish();
            }
        });

        //Button Yes
        buttonYes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Event gets deleted
                deleteOneEvent(id);
                toastMessage("Event has been deleted");
                finish();
            }
        });

    }

    //GET
    //Gets information from a specific marker
    private void getOneEvent(int id)
    {
        Call<Event> eventCall = apiInterface.getEvent(id);
        eventCall.enqueue(new Callback<Event>()
        {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response)
            {
                Log.e("EventView", "onResponse: " + response.body() );

                if(response.isSuccessful())
                {
                    Event data = response.body();

                    //gets data from server
                    String title = data.getTitle();
                    String description = data.getDescription();
                    int quantity = data.getQuantity();
                    String location = data.getLocation();
                    String longitude = data.getLongitude();
                    String latitude = data.getLatitude();
                    String pictureId = data.getPictureid();
                    System.out.println(pictureId);

                    //sets data to text fields
                    listTitle.setText("Title: " + title);
                    listDescription.setText("Description: " + description);
                    listQuantity.setText("Quantity: " + quantity);
                    listLocation.setText("Location: " + location);
                    listLongitude.setText("Longitude: " + longitude);
                    listLatitude.setText("Latitude: " + latitude);

                    //sets image
                    try
                    {
                        //gets image file path from server
                        filepath = pictureId;

                        //converts filepath into an image
                        Bitmap picture = BitmapFactory.decodeFile(filepath);

                        //Generate matrix to rotate 90 degree photo
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);

                        //Generate final rotated picture
                        Bitmap finalPicture = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), picture.getHeight(), matrix, true);

                        //Outputs final image
                        listImage.setImageBitmap(finalPicture);
                    }

                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t)
            {
                Log.e("EventView", "onFailure: " + t.getLocalizedMessage() );
            }
        });
    }

    //DELETE
    private void deleteOneEvent(int id)
    {
        Call<Event> eventCall = apiInterface.deleteEvent(id);
        eventCall.enqueue(new Callback<Event>()
        {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response)
            {
                Log.e("EventActivity", "onResponse: " + response.body() );
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t)
            {
                //Log.e("EventActivity", "onFailure: " + t.getLocalizedMessage() );
            }
        });
    }

    //Toast Messages to display texts to user
    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
