package com.example.jaythakker.myapplication;
//(add restaurant markers, enable location searching)
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.content.res.Resources;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class FilterSearch extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, com.google.android.gms.location.LocationListener, OnMapReadyCallback{

    private static final String TAG = "Logging App Activity";
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private ProgressDialog mProgress;

    double latitude;
    double longitude;
    String address;

    private LocationHelper locationHelper;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_filter_search);

        final EditText noOfPersonsEntered = (EditText) findViewById(R.id.noOfPersons);
        final EditText budgetEntered = (EditText) findViewById(R.id.budget);
        final Spinner cuisineSelected = (Spinner) findViewById(R.id.cuisine);
        final Button search = (Button) findViewById(R.id.search);
        final TextView dispLoc = (TextView) findViewById(R.id.location);
        final ImageButton getLoc = (ImageButton) findViewById(R.id.imageButton);
        final RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        final Context context = this;
        final ImageButton history = (ImageButton) findViewById(R.id.history);
        final ImageButton recommendations = (ImageButton) findViewById(R.id.recommendations);

        locationHelper = new LocationHelper(this);
        locationHelper.checkpermission();

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int noOfPersons = 0, budget = 0;
                try {
                    noOfPersons = Integer.parseInt(noOfPersonsEntered.getText().toString());
                    budget = Integer.parseInt(budgetEntered.getText().toString());

                }
                catch(NumberFormatException ex){
                    ex.printStackTrace();
                }

                String cuisine = cuisineSelected.getSelectedItem().toString();

                /*AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                builder.setMessage(noOfPersons + " " + budget + " " + " " + cuisine)
                        .create()
                        .show();
                */

                String url ="http://192.168.43.113:8080/auth/search/";
                Response.Listener list=new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        mProgress.dismiss();

                        /*AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                        builder.setMessage(response.toString())
                                .create()
                                .show();*/

                        if(response != null) {
                            try {

                                JSONArray jsonRests = response.getJSONArray("Results");
                                ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

                                for (int i = 0; i < jsonRests.length(); i++) {
                                    /*AlertDialog.Builder builder1 = new AlertDialog.Builder(FilterSearch.this);
                                    builder1.setMessage(jsonRests.getJSONObject(i).getString("name"))
                                            .create()
                                            .show();*/
                                    restaurants.add(i, new Restaurant(jsonRests.getJSONObject(i).getString("restId"),
                                            jsonRests.getJSONObject(i).getString("name"),
                                            jsonRests.getJSONObject(i).getString("area"),
                                            jsonRests.getJSONObject(i).getDouble("latitude"),
                                            jsonRests.getJSONObject(i).getDouble("longitude"),
                                            jsonRests.getJSONObject(i).getString("cuisine"),
                                            jsonRests.getJSONObject(i).getDouble("est_cost_per_person")));
                                }

                                Intent registerIntent = new Intent(FilterSearch.this, Results.class);
                                registerIntent.putParcelableArrayListExtra("Restaurants", restaurants);
                                FilterSearch.this.startActivity(registerIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(FilterSearch.this);
                            builder2.setMessage("Can't connect to the server at this moment! Please try again later.")
                                    .create()
                                    .show();
                        }
                    }
                };

                Response.ErrorListener err=new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgress.dismiss();
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                        builder.setMessage("Can't connect to the server at this moment! Please try again later.")
                                .create()
                                .show();
                    }
                };

                try {

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("noOfPersons", noOfPersons);
                    jsonObj.put("budget", budget);
                    jsonObj.put("cuisine", cuisine);
                    jsonObj.put("latitude", latitude);
                    jsonObj.put("longitude", longitude);

                }
                catch(JSONException ex){
                    ex.printStackTrace();
                }
                JSONRequest req = new JSONRequest(Request.Method.GET,url,null,list,err);
                // Access the RequestQueue through your singleton class.
                queue.add(req);
                mProgress.show();
            }
        });

        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLastLocation = locationHelper.getLocation();
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    LatLng myLaLn = new LatLng(latitude, longitude);

                    CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                            .zoom(15)
                            .bearing(45)
                            .build();

                    CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

                    if(mMap != null) {
                        mMap.animateCamera(camUpd3);
                        mMap.clear();
                        MarkerOptions markerOpts = new MarkerOptions().position(myLaLn).title("my Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        mMap.addMarker(markerOpts);
                    }

                    address = getAddress();
                    if(address != null)
                        dispLoc.setText(address.substring(0, 28) + "...");
                    else
                        showToast("Something went wrong ! Try again later.");

                } else {
                    showToast("Couldn't get the location. Make sure location is enabled on the device.");
                }
            }
        });

        dispLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(FilterSearch.this);
                builder1.setMessage(address)
                        .create()
                        .show();

            }
        });

        recommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (locationHelper.checkPlayServices()) {
            locationHelper.buildGoogleApiClient();
        }

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastLocation);
            super.onSaveInstanceState(outState);
        }
    }

    protected void onStart(){
        super.onStart();
        if(!locationHelper.isPermissionGranted()){
            showToast("Location permissions not granted !");
        }
        else{
            showToast("Location permissions granted !");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String getAddress()
    {
        Address locationAddress;
        locationAddress = locationHelper.getAddress(latitude,longitude);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+=", "+address1;
                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+=", "+city;
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=", "+postalCode;
                }
                if (!TextUtils.isEmpty(state))
                    currentLocation+=", "+state;
                if (!TextUtils.isEmpty(country))
                    currentLocation+=", "+country;

                return currentLocation;
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }

    //Google api callback methods

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        showToast("Connection to Google API Failed!");
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        showToast("Connected to Google API");
        mLocationRequest = locationHelper.getLocationRequest();
        mGoogleApiClient = locationHelper.getGoogleApiCLient();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
        showToast("Connection to Google API Suspended");
    }

    // Permission check functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));
    }
}
