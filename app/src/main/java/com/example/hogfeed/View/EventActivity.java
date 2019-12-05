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

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    Button buttonImage;
    ImageView image;
    TextView tvLongitude;
    TextView tvLatitude;

    EditText etTitle;
    EditText etDescription;
    EditText etLocation;
    EditText etQuantity;

    String title;
    String description;
    String location;
    String LAT;
    String LNG;
    int quantity;

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PERMISSION = 101;

    String imageFilePath = "";
    String time;

    Geocoder geocoder;
    String longitude;
    String latitude;
    double lng;
    double lat;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        askStoragePermission();


        geocoder = new Geocoder(this, Locale.getDefault());

        buttonImage = (Button) findViewById(R.id.buttonImage);
        image = (ImageView) findViewById(R.id.image);

        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);

        //title names
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etQuantity = (EditText) findViewById(R.id.etQuantity);


        //information received
        Intent receive = getIntent();
        lat = receive.getDoubleExtra("latitude", 0);
        lng = receive.getDoubleExtra("longitude", 0);

        //Display lat/lng field
        tvLatitude.setText("Latitude: " + lat);
        tvLongitude.setText("Longitude: " + lng);


        //getLocation();
        //Take an image onclick listener
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //askLocationPermission();
                System.out.println("working");
                openCameraIntent();

            }


        });


        apiInterface = ApiClient.getClient().create(ApiInterface.class);


    }

    private void askStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

    }


    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;

            try {
                imageFile = makeImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            if (imageFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);

            }


            System.out.println("image activity");
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

        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                image.setImageURI(Uri.parse(imageFilePath));
                //this.getLocation();
                this.saveImage();
            } else if (resultCode == RESULT_CANCELED) {
                toastMessage("Operation cancelled");
            }
        }
    }

    public void saveImage() {
        //dbHelper.insertData(imageFilePath, longitude, latitude, time);
        //toastMessage("Image and content saved to database");

    }

    /*
    public void getLocation()
    {

        try
        {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lon = location.getLongitude();
            lat = location.getLatitude();

            longitude = Double.toString(lon);
            latitude = Double.toString(lat);

            //toastMessage(longitude);

            tvLongitude.setText("Longitude: " + longitude);
            tvLatitude.setText("Latitude: " + latitude);
        }

        catch(SecurityException e)
        {
            e.printStackTrace();
            toastMessage("Error with location");
        }


    }

    */


    private File makeImageFile() throws IOException {
        time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageName = "IMG_" + time + "_";
        File storage = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName, ".jpg", storage);
        imageFilePath = image.getAbsolutePath();

        System.out.println("File Path: " + imageFilePath);


        return image;
    }

    public void buttonSubmit(View v) {

        title = etTitle.getText().toString();
        description = etDescription.getText().toString();
        location = etLocation.getText().toString();
        LNG = String.valueOf(lng);
        LAT = String.valueOf(lat);
        quantity = Integer.parseInt(etQuantity.getText().toString());



        //postEvent(LAT, location, LNG, quantity, title, "pictureid", description);
        //getOneEvent(5);
        //getAllEvents();
        finish();



    }

    public boolean inputValid(String latitude, String location, String longitude, int quantity, String title, String pictureid, String description)
    {
        //Location field has to be filled out
        if (location.length() == 0)
        {
            toastMessage("Enter a location");
            return false;
        }

        //Quantity can't be less than or equal to 0
        if(quantity <= 0)
        {
            toastMessage("Enter a valid quantity");
            return false;
        }

        //Title field has to be filled out
        if(title.length() == 0)
        {
            toastMessage("Enter a title");
            return false;
        }

        //Picture id has to be given
        if(pictureid.length() == 0)
        {
            toastMessage("Take an image");
            return false;
        }

        //Description has to be filled out
        if(description.length() == 0)
        {
            toastMessage("Enter a description");
            return false;
        }

        return true;

    }

    private void toastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*private class SaveEventTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            this.savingEventAlert.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Event event = (new Event()).
                    setId(new UUID(0,0)).
                    setTitle(title).
                    setDescription(description).
                    setLocation(location).
                    setLongitude(LNG).
                    setLatitude(LAT).
                    setPictureId("pictureid").
                    setQuantity(2);

            ApiResponse<Event> apiResponse = (
                    (event.getId().equals(new UUID(0, 0)))
                            ? (new EventService()).createEvent(event)
                            : (new EventService()).updateEvent(event)
            );

            if (apiResponse.isValidResponse()) {
                eventTransition.setLatitude(apiResponse.getData().getLatitude());
                eventTransition.setLocation(apiResponse.getData().getLocation());
                eventTransition.setLongitude(apiResponse.getData().getLongitude());
                eventTransition.setQuantity(apiResponse.getData().getQuantity());
                eventTransition.setTitle(apiResponse.getData().getTitle());
                eventTransition.setPictureId(apiResponse.getData().getPictureId());
                eventTransition.setDescription(apiResponse.getData().getDescription());
            }

            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            String message;

            savingEventAlert.dismiss();

            if (successfulSave) {
                message = "Event was successfully posted";
            } else {
                message = "Event posting failed";
            }

            new AlertDialog.Builder(EventActivity.this).
                    setMessage(message).
                    setPositiveButton(
                            "Dismiss",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            }
                    ).
                    create().
                    show();
        }

        private AlertDialog savingEventAlert;

        private SaveEventTask() {
            this.savingEventAlert = new AlertDialog.Builder(EventActivity.this).
                    setMessage("Posting event..").
                    create();
        }

    }*/

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

        private void getOneEvent(int id)
        {
            Call<Event> eventCall = apiInterface.getEvent(id);
            eventCall.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    Log.e("EventActivity", "onResponse: " + response.body() );
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
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



}
