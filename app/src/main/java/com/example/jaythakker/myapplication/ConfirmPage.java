package com.example.jaythakker.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import java.util.Arrays;

public class ConfirmPage extends AppCompatActivity {

    private Activity activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);

        final Button confirmBooking = (Button) findViewById(R.id.confirmBooking);
        activity = this;

        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                ViewDialog alert = new ViewDialog();
                alert.showDialog(activity, "Error de conexión al servidor");
            }
        });


    }

    public void ShowDialog(){

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar rating = new RatingBar(this);
        rating.setMax(5);

        popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("Rate Your Experience !");
        popDialog.setView(rating);


        popDialog.create();
        popDialog.show();
    }
}
