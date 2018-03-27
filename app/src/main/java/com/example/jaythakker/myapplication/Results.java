//TODO - Implement pictures if required.

package com.example.jaythakker.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Results extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView restaurantView;

    ArrayList<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        restaurantList = getIntent().getParcelableArrayListExtra("Restaurants");
        restaurantView = (ListView) findViewById(R.id.restaurant_list);

        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getApplicationContext(), R.layout.row, restaurantList);
        restaurantView.setAdapter(restaurantAdapter);

        restaurantView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Restaurant r = restaurantList.get(position);
                String item = r.getName();
                Intent intent = new Intent(Results.this, ConfirmPage.class);
                // using putExtra(String key, Parcelable value) method
                intent.putExtra("Restaurant", r);
                startActivity(intent);
            }
        });
    }
}
