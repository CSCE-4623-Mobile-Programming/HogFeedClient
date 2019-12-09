package com.example.hogfeed.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hogfeed.Model.ApiClient;
import com.example.hogfeed.Model.Event;
import com.example.hogfeed.Model.gpsHelper;
import com.example.hogfeed.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.hogfeed.Model.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MapsView extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    //Google
    GoogleMap mMap;
    GoogleSignInClient mGoogleSignInClient;
    private MarkerOptions userLocation;

    //Components
    Button signOut;
    TextView display;

    //Variables
    double lat;
    double lng;
    List<Event> events;

    gpsHelper helper;

    HashMap<Integer, LatLng> latLngList;
    private boolean initialLocationFlag;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    //To use retrofit
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_view);

        //Initialize
        signOut = findViewById(R.id.buttonSignOut);
        display = findViewById(R.id.tvDisplay);
        events = new ArrayList<Event>();
        latLngList = new HashMap<Integer, LatLng>();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        helper = new gpsHelper(this);

        //Asks for location permissions
        askLocationPermission();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Checks to see if user is signed in
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        //Gets Gmail SignIn info
        if(acct != null)
        {
            String userName = acct.getDisplayName();
            String userEmail = acct.getEmail();
            String userId = acct.getId();

            //Eliminates @------.com from email name
            userEmail = userEmail.substring(0, userEmail.indexOf("@"));

            //Displays users username
            display.setText("User: " + userEmail);
        }

        //onClick listener for sign out button
        signOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.buttonSignOut:
                        signOut();
                        toastMessage("Successfully Signed Out");
                        finish();
                        break;
                }

            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getAllEvents();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        getAllEvents();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //Instantiate Google Map Api Fragment
        mMap = googleMap;

        //Imports Custom Map Style
        try
        {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            MapsView.this, R.raw.google_json));

            if (!success)
            {
                Log.e("Map", "Style parsing failed.");
            }
        }

        catch (Resources.NotFoundException e) {
            Log.e("Map", "Can't find style.", e);
        }

        initialLocationFlag = false;

        //Gets events from server
        getAllEvents();

        // Enable on click listener for marker
        mMap.setOnMarkerClickListener(this);

        //Centers Map on User's current location
        centerMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        //Checks what marker was clicked
        Integer id = (Integer) marker.getTag();

        //Goes to new page with that marker
        if(id != null)
        {
            //Go to new activity
            Intent i = new Intent(this, EventView.class);
            i.putExtra("id", id);
            startActivity(i);
        }

        return false;
    }

    //Gets Users current location and goes to it on map
    public void centerMap()
    {
        //Helper to get current location
        gpsHelper helper = new gpsHelper(this);

        //get current latitude & longitude
        lat = helper.getLatitude();
        lng = helper.getLongitude();

        //Move camera to position of taken photo
        LatLng currentPos = new LatLng(lat,lng);

        //Instantiate user location marker with curr user location
        userLocation = new MarkerOptions().position(currentPos).title("My Location");

        //Set marker icon
        //userLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.userlocation));
        userLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        //Add/Update Marker
        mMap.addMarker(userLocation);

        //Move view to current postion
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        currentPos,
                        18f
                )
        );
    }

    //Gets events from server
    public void getAllEvents()
    {
        Call<List<Event>> call = apiInterface.getEvents();
        call.enqueue(new Callback<List<Event>>()
        {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response)
            {
                //If Get method is successful
                if(response.isSuccessful())
                {
                    //Saves Json from server to list
                    events = response.body();

                    //Goes through all events on server
                    for(Event e: events)
                    {
                        //Gets id, lats, and longs from server
                        int id = e.getId();
                        double latit = Double.parseDouble(e.getLatitude());
                        double longi = Double.parseDouble(e.getLongitude());

                        //Stores lats and longs as LatLng
                        LatLng ll = new LatLng(latit, longi);

                        //List that stores lats lngs
                        latLngList.put(id, ll);

                    }

                    //Clears map of deleted markers
                    mMap.clear();

                    //adds current location marker
                    lat = helper.getLatitude();
                    lng = helper.getLongitude();
                    LatLng currentPos = new LatLng(lat,lng);
                    userLocation = new MarkerOptions().position(currentPos).title("My Location");
                    userLocation.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(userLocation);

                    //Iterator for locations list
                    Iterator it = latLngList.entrySet().iterator();

                    //Goes through list with iterator
                    while(it.hasNext())
                    {
                        Map.Entry mapElement = (Map.Entry) it.next();

                        //Gets LatLng from Hashmap
                        LatLng latLng = (LatLng) mapElement.getValue();

                        //Gets key from map
                        String name = mapElement.getKey().toString();

                        //Adds markers
                        Marker mk = mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                        mk.setTag(Integer.parseInt(name));
                        mk.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.event));
                    }

                    //clear hashmap
                    latLngList.clear();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t)
            {
                Log.i("MapsView", "onFailure: " + t.getLocalizedMessage());
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
                Log.e("MapsView", "onResponse: " + response.body() );
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t)
            {
                Log.e("MapsView", "onFailure: " + t.getLocalizedMessage() );
            }
        });
    }

    //onClick Listener to go to form to create event
    public void createEvent(View v)
    {
        Intent i = new Intent(this, EventActivity.class);
        i.putExtra("latitude", lat);
        i.putExtra("longitude", lng);
        startActivity(i);
    }

    //onClick Listener to center map on users location -- aka refresh page
    public void findMe(View v)
    {
        centerMap();
        getAllEvents();
    }


    //Method to signout from Google
    private void signOut()
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        // ...
                    }
                });
    }

    //Toast Message
    private void toastMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //Asks user for location permission
    public void askLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    // This method adds permissions to the system
    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission)
    {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
        {
            permissionsList.add(permission);

            // Check for Rationale Option
            if(!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    // All Permissions Granted
                    //stopSelf();
                    //startActivity(new Intent(this, this.getClass()));

                }

                else
                {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
