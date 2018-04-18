//TODO Update Cuisines

package com.example.jaythakker.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Build;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.res.Resources;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class FilterSearch extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback, com.google.android.gms.location.LocationListener, OnMapReadyCallback{

    private static final String TAG = "Logging App Activity";
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private ProgressDialog mProgress;
    private TextView dispLoc;

    double latitude;
    double longitude;
    String address;
    private Activity activity;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

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
        final ImageButton getLoc = (ImageButton) findViewById(R.id.imageButton);
        final RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        final Context context = this;
        final ImageButton recommendations = (ImageButton) findViewById(R.id.recommendations);
        final EditText timeToSpare = (EditText)findViewById(R.id.editText8);
        dispLoc = (TextView) findViewById(R.id.location2);

        locationHelper = new LocationHelper(this);
        locationHelper.checkpermission();
        activity = this;

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
                double noOfPersons = 0.0, budget = 0.0;
                double time = 0.0;
                try {
                    time = Double.parseDouble(timeToSpare.getText().toString());
                    noOfPersons = Double.parseDouble(noOfPersonsEntered.getText().toString());
                    budget = Double.parseDouble(budgetEntered.getText().toString());
                }
                catch(NumberFormatException ex){
                    ex.printStackTrace();
                }

                String cuisine = cuisineSelected.getSelectedItem().toString();

                String url ="http://192.168.1.104:8080/auth/search/";
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

                                if(jsonRests.getJSONObject(0).getString("name") == "NULL"){
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(FilterSearch.this);
                                    builder2.setMessage("No results to display at the moment! Modify search parameters and try again.")
                                            .create()
                                            .show();
                                }
                                else {
                                    for (int i = 0; i < jsonRests.length(); i++) {

                                        if(!jsonRests.getJSONObject(i).getString("name").equals("NULL")) {
                                            restaurants.add(i, new Restaurant(jsonRests.getJSONObject(i).getString("restId"),
                                                    jsonRests.getJSONObject(i).getString("name"),
                                                    jsonRests.getJSONObject(i).getString("area"),
                                                    jsonRests.getJSONObject(i).getDouble("latitude"),
                                                    jsonRests.getJSONObject(i).getDouble("longitude"),
                                                    jsonRests.getJSONObject(i).getString("cuisine"),
                                                    jsonRests.getJSONObject(i).getDouble("est_cost_per_person")));
                                        }
                                    }

                                    if(restaurants.size() > 0) {
                                        Intent registerIntent = new Intent(FilterSearch.this, Results.class);
                                        registerIntent.putParcelableArrayListExtra("Restaurants", restaurants);
                                        FilterSearch.this.startActivity(registerIntent);
                                    }
                                    else{
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(FilterSearch.this);
                                        builder2.setMessage("No results to display at the moment! Modify search parameters and try again.")
                                                .create()
                                                .show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(FilterSearch.this);
                            builder2.setMessage("No results to display at the moment! Modify search parameters and try again.")
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
                        builder.setMessage("Could not connect to server ! Please try again after a while.")
                                .create()
                                .show();
                    }
                };

                JSONObject jsonObj = new JSONObject();

                ServerValues.LATITUDE = latitude;
                ServerValues.LONGITUDE = longitude;
                ServerValues.CUISINE = cuisine;
                ServerValues.UCOST = budget;
                ServerValues.UTIME = time;
                ServerValues.UNUMBER = noOfPersons;

                try {
                    jsonObj.put("noOfPersons", noOfPersons);
                    jsonObj.put("budget", budget);
                    jsonObj.put("cuisine", cuisine);
                    jsonObj.put("latitude", latitude);
                    jsonObj.put("longitude", longitude);
                    jsonObj.put("time", time);
                }
                catch(JSONException ex){
                    ex.printStackTrace();
                }
                JSONRequest req = new JSONRequest(Request.Method.POST,url,jsonObj,list,err);
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

                    mCameraPosition = new CameraPosition.Builder().target(myLaLn)
                            .zoom(15)
                            .bearing(45)
                            .build();

                    CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(mCameraPosition);

                    if(mMap != null) {
                        mMap.animateCamera(camUpd3);
                        mMap.clear();
                        MarkerOptions markerOpts = new MarkerOptions().position(myLaLn).title("my Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                        mMap.addMarker(markerOpts);
                    }

                    address = getAddress();
                    if(address != null)
                        dispLoc.setText(address.substring(0, 30) + "...");
                    else
                        showToast("Check your internet connection and try again !");

                } else {
                    showToast("Couldn't get the location. Make sure location is enabled on the device.");
                }
            }
        });

        dispLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(activity);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        if (locationHelper.checkPlayServices()) {
            locationHelper.buildGoogleApiClient();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                dispLoc.setText((place.getName().toString() + place.getAddress().toString()).substring(0, 33) + "...");
                LatLng queriedLocation = place.getLatLng();
                longitude = queriedLocation.longitude;
                latitude = queriedLocation.latitude;
                Log.i(TAG, "Place: " + place.getName());

                mCameraPosition = new CameraPosition.Builder().target(queriedLocation)
                        .zoom(15)
                        .bearing(45)
                        .build();
                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(mCameraPosition);

                if(mMap != null && queriedLocation != null) {
                    mMap.animateCamera(camUpd3);
                    mMap.clear();
                    MarkerOptions markerOpts = new MarkerOptions().position(queriedLocation).title("my Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    mMap.addMarker(markerOpts);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                showToast("Check your internet connection and try again !");
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
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

    @Override
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