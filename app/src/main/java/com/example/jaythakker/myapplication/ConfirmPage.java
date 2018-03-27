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

public class ConfirmPage extends AppCompatActivity {

    private Activity activity = null;
    private ProgressDialog mProgress;

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
        name.setText(restaurant.getName());
        area.setText(restaurant.getArea());
        cuisine.setText(restaurant.getCuisine());

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        activity = this;

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