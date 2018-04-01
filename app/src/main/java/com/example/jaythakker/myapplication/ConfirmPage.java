//TODO - Make restaurant confirmation entry in the database on submit button.
//TODO - Complete getting values of estimates from the server.
//TODO - Add images if required.

package com.example.jaythakker.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfirmPage extends AppCompatActivity {

    private Activity activity = null;
    private ProgressDialog mProgress;
    final RequestQueue queue = VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);
        Restaurant restaurant = (Restaurant) getIntent().getParcelableExtra("Restaurant");

        final Button confirmBooking = (Button) findViewById(R.id.confirmBooking);
        final TextView name = (TextView) findViewById(R.id.name);
        final TextView cuisine = (TextView) findViewById(R.id.cuisine);
        final TextView area = (TextView) findViewById(R.id.area);
        final TextView estCostPerPerson = (TextView) findViewById(R.id.costPerPerson);
        final TextView rtt = (TextView) findViewById(R.id.rtt);
        final TextView noOfPeople = (TextView) findViewById(R.id.noOfPeople);
        final String url = "";

        name.setText(restaurant.getName());
        area.setText(restaurant.getArea());
        cuisine.setText(restaurant.getCuisine());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        activity = this;

        Response.Listener list=new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                mProgress.dismiss();
                try {
                    if (response != null) {
                        JSONObject jsonObj = response;
                        estCostPerPerson.setText("Estimated Cost Per Person : " + jsonObj.getString("estCostPerPerson"));
                        rtt.setText("Estimated Round Trip Time : " + jsonObj.getString("estRoundTripTime"));

                    } else {
                        android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(ConfirmPage.this);
                        builder2.setMessage("Could not update estimates. Go back and try again !")
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener err=new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgress.dismiss();
                error.printStackTrace();
                // TODO Auto-generated method stub
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ConfirmPage.this);
                builder.setMessage("Could not get estimates. Go back and try again !")
                        .create()
                        .show();
            }
        };

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("restId", restaurant.getRestId());
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        JSONRequest req = new JSONRequest(Request.Method.POST,url,jsonObj,list,err);
        // Access the RequestQueue through your singleton class.
        queue.add(req);
        mProgress.show();

        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.rating_dialog, null);

                final RatingBar ratingBar = alertLayout.findViewById(R.id.ratingBar);
                final TextView ratingText = alertLayout.findViewById(R.id.rateText);
                ratingText.setText("");
                final TextView cancel = alertLayout.findViewById(R.id.cancel);
                final TextView submit = alertLayout.findViewById(R.id.submit);
                ratingBar.setNumStars(5);
                ratingBar.setRating(5);
                ratingBar.setStepSize(1);

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                        if(rating==0){
                            ratingText.setText("Terrible !");
                        }else if(rating==1){
                            ratingText.setText("Terrible !");
                        }else if(rating==2){
                            ratingText.setText("Bad !");
                        }else if(rating==3){
                            ratingText.setText("OK !");
                        }else if(rating==4){
                            ratingText.setText("Good !");
                        }else if(rating==5){
                            ratingText.setText("Excellent !");
                        }
                    }
                });


                final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("");

                // this is set the view from XML inside AlertDialog
                alert.setView(alertLayout);
                // disallow cancel of AlertDialog on click of back button and outside touch
                alert.setCancelable(false);

                final AlertDialog dialog = alert.create();
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        dialog.dismiss();
                        Intent registerIntent = new Intent(ConfirmPage.this, FilterSearch.class);
                        ConfirmPage.this.startActivity(registerIntent);
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        mProgress.show();
                        //Database code
                        mProgress.dismiss();
                        Intent registerIntent = new Intent(ConfirmPage.this, FilterSearch.class);
                        ConfirmPage.this.startActivity(registerIntent);
                    }
                });

            }


        });

    }
}