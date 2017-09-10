package com.example.jaythakker.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);

        final Button login = (Button) findViewById(R.id.login);
        final Button signup = (Button) findViewById(R.id.signup);

        final RequestQueue queue=VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();;

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(MainActivity.this,Register.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user = username.getText().toString();
                final String pass = password.getText().toString();
                String url ="http://192.168.1.103:8080/login/"+user+"and"+pass;
                Response.Listener list=new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String text=(response.toString());
                        boolean access= false;
                        try {
                            access = response.getBoolean("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(access)
                        {
                            Intent registerIntent = new Intent(MainActivity.this,Register.class);
                            MainActivity.this.startActivity(registerIntent);
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Incorrect username and password")
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("error"+error)
                                .create()
                                .show();
                    }
                };
                JSONRequest req=new JSONRequest(Request.Method.GET,url,null,list,err);

// Access the RequestQueue through your singleton class.
                queue.add(req);

            }
        });

    }
}
