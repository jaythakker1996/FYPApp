package com.example.jaythakker.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class FilterSearch extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, com.google.android.gms.location.LocationListener {

    private static final String TAG = "Logging App Activity";
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    double latitude;
    double longitude;

    private LocationHelper locationHelper;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        final EditText noOfPersonsEntered = (EditText) findViewById(R.id.noOfPersons);
        final EditText budgetEntered = (EditText) findViewById(R.id.budget);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        final Spinner cuisineSelected = (Spinner) findViewById(R.id.cuisine);
        final Button search = (Button) findViewById(R.id.search);
        final ImageButton getLoc = (ImageButton) findViewById(R.id.location);
        final RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        locationHelper = new LocationHelper(this);
        locationHelper.checkpermission();

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
                final String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                String cuisine = cuisineSelected.getSelectedItem().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                builder.setMessage(noOfPersons + " " + budget + " " + time + " " + cuisine)
                        .create()
                        .show();

                String url ="http://192.168.1.101:8080/search/";
                Response.Listener list=new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                        builder.setMessage(response.toString())
                                .create()
                                .show();
                        if(response != null) {
                            try {

                                JSONArray jsonRests = response.getJSONArray("Results");
                                ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

                                for (int i = 0; i < jsonRests.length(); i++) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(FilterSearch.this);
                                    builder1.setMessage(jsonRests.getJSONObject(i).getString("name"))
                                            .create()
                                            .show();
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
                            builder2.setMessage("Can't connect to the server at this moment! Please try again.")
                                    .create()
                                    .show();
                        }

                    }
                };

                Response.ErrorListener err=new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                        builder.setMessage("Error : "+error)
                                .create()
                                .show();
                    }
                };

                try {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("time", time);
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
            }
        });

        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLastLocation = locationHelper.getLocation();
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                    builder.setMessage(latitude + " : " + longitude)
                            .create()
                            .show();
                    getAddress();

                } else {

                    showToast("Couldn't get the location. Make sure location is enabled on the device");
                }
            }
        });

        if (locationHelper.checkPlayServices()) {
            locationHelper.buildGoogleApiClient();
        }

    }

    protected void onStart(){
        super.onStart();
        if(!locationHelper.isPermissionGranted()){
            AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
            builder.setMessage("Permission not granted")
                    .create()
                    .show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
            builder.setMessage("Permission granted")
                    .create()
                    .show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showToast("Stopping...");
    }

    public void getAddress()
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
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

                AlertDialog.Builder builder = new AlertDialog.Builder(FilterSearch.this);
                builder.setMessage(currentLocation)
                        .create()
                        .show();
            }

        }
        else
            showToast("Something went wrong");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }

    /**
     * Google api callback methods
     */

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        showToast("Connection Failed!");
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        showToast("Connected...");
        mLocationRequest = locationHelper.getLocationRequest();
        mGoogleApiClient = locationHelper.getGoogleApiCLient();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
        showToast("Suspended");

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
        showToast("Connection Update...");
    }
}
