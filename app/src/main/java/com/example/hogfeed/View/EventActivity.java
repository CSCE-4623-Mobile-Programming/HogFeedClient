package com.example.hogfeed.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogfeed.Model.ApiClient;
import com.example.hogfeed.Model.ApiInterface;
import com.example.hogfeed.Model.Event;
import com.example.hogfeed.R;
import com.example.hogfeed.View.ui.login.LoginActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity
{
    //Components
    Button buttonImage;
    ImageView image;
    TextView tvLongitude;
    TextView tvLatitude;
    EditText etTitle;
    EditText etDescription;
    EditText etLocation;
    EditText etQuantity;

    //Variables to get/save info
    String title;
    String description;
    String location;
    String LAT;
    String LNG;
    int quantity;
    String imageFilePath = "filename";
    String time;
    double lng;
    double lat;


    //For Permissions
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PERMISSION = 101;

    //To use retrofit
    ApiInterface apiInterface;


    //Initializing Components
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        buttonImage = (Button) findViewById(R.id.buttonImage);
        image = (ImageView) findViewById(R.id.image);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etQuantity = (EditText) findViewById(R.id.etQuantity);

        //Asks User If They Can Save -- for images
        askStoragePermission();

        //User's Current Location received from MapsView
        Intent receive = getIntent();
        lat = receive.getDoubleExtra("latitude", 0);
        lng = receive.getDoubleExtra("longitude", 0);

        //Display Current Location Fields
        tvLatitude.setText("Latitude: " + lat);
        tvLongitude.setText("Longitude: " + lng);

        //Take an image onclick listener
        buttonImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //opens camera to take image
                openCameraIntent();

            }
        });

        //Enables Retrofit Use for REST
        apiInterface = ApiClient.getClient().create(ApiInterface.class);


    }

    //Asks User to Write to Storage -- for images
    private void askStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

    }

    //Captures an image
    private void openCameraIntent()
    {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (pictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File imageFile = null;

            try {
                imageFile = makeImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (imageFile != null)
            {
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toastMessage("Permission granted");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAPTURE_IMAGE)
        {
            if (resultCode == RESULT_OK)
            {
                image.setImageURI(Uri.parse(imageFilePath));
                //this.saveImage();
            }

            else if (resultCode == RESULT_CANCELED)
            {
                toastMessage("Operation cancelled");
            }
        }
    }

    //Saves image -- need to implement
    public void saveImage() {
        //dbHelper.insertData(imageFilePath, longitude, latitude, time);
        //toastMessage("Image and content saved to database");

    }


    //Makes File for image
    private File makeImageFile() throws IOException
    {
        time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageName = "IMG_" + time + "_";
        File storage = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName, ".jpg", storage);
        imageFilePath = image.getAbsolutePath();

        System.out.println("File Path: " + imageFilePath);

        return image;
    }

    //Post Event
    public void buttonSubmit(View v)
    {
        //Gathers Data from form
        //Title
        if(etTitle.getText().toString().isEmpty())
        {
            title = "title";
        }
        else
        {
            title = etTitle.getText().toString();
        }

        //Description
        if(etDescription.getText().toString().isEmpty())
        {
            description = "description";
        }
        else
        {
            description = etDescription.getText().toString();
        }

        //Location
        if(etLocation.getText().toString().isEmpty())
        {
            location = "location";
        }
        else
        {
            location = etLocation.getText().toString();
        }

        //Longitude
        LNG = String.valueOf(lng);

        //Latitude
        LAT = String.valueOf(lat);

        //Quantity
        if(etQuantity.getText().toString().isEmpty())
        {
            quantity = 0;
        }
        else
        {
            quantity = Integer.parseInt(etQuantity.getText().toString());
        }

        //Sends data to server
        postEvent(LAT, location, LNG, quantity, title, imageFilePath, description);

        //Closes form
        finish();

    }

    //Toast Messages to display texts to user
    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    //REST CALLS
    //GET
    private void getAllEvents()
    {
        Call<List<Event>> call = apiInterface.getEvents();
        call.enqueue(new Callback<List<Event>>()
        {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response)
            {
                Log.e("EventActivity", "onResponse: " + response.body());

            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t)
            {
                Log.i("EventActivity", "onFailure: " + t.getLocalizedMessage());

            }
        });
    }

    //GET
    private void getOneEvent(int id)
    {
        Call<Event> eventCall = apiInterface.getEvent(id);
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
                Log.e("EventActivity", "onFailure: " + t.getLocalizedMessage() );
            }
        });
    }

    //POST
    private void postEvent(String latitude, String location, String longitude, int quantity, String title, String pictureid, String description)
    {
        Event event = new Event(latitude, location, longitude, quantity, title, pictureid, description);

        Call<Event> eventPostCall = apiInterface.postEvent(event);
        eventPostCall.enqueue(new Callback<Event>()
        {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response)
            {
                Log.e("EventActivity", "onResponse: " + response.body());
                toastMessage("Event successfully posted");
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t)
            {
                Log.i("EventActivity", "onFailure: " + t.getLocalizedMessage());
                toastMessage("Event posting was unsuccessful");
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

}
