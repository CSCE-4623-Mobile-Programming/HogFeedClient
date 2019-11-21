package com.example.hogfeed.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogfeed.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    Button buttonImage;
    ImageView image;
    TextView tvLongitude;
    TextView tvLatitude;

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PERMISSION = 101;

    String imageFilePath = "";
    String time;

    Geocoder geocoder;
    String longitude;
    String latitude;
    double lng;
    double lat;

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


        //information received
        Intent receive = getIntent();
        lat = receive.getDoubleExtra("latitude", 0);
        lng = receive.getDoubleExtra("longitude", 0);

        //Display lat/lng field
        tvLatitude.setText("Latitude: " + lat);
        tvLongitude.setText("Longitude: " + lng);


        //getLocation();
        //Take an image onclick listener
        buttonImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //askLocationPermission();
                System.out.println("working");
                openCameraIntent();

            }


        });


    }

    private void askStoragePermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }

    }



    private void openCameraIntent()
    {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(pictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File imageFile = null;

            try
            {
                imageFile = makeImageFile();
            }

            catch(IOException e)
            {
                e.printStackTrace();
                return;
            }

            if(imageFile != null)
            {
                Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);

            }



            System.out.println("image activity");
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                toastMessage("Permission granted");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAPTURE_IMAGE)
        {
            if(resultCode == RESULT_OK)
            {
                image.setImageURI(Uri.parse(imageFilePath));
                //this.getLocation();
                this.saveImage();
            }

            else if (resultCode == RESULT_CANCELED)
            {
                toastMessage("Operation cancelled");
            }
        }
    }

    public void saveImage()
    {
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

    public void buttonSubmit(View v)
    {
        finish();

    }

    private void toastMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
