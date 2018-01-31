package com.example.jaythakker.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.*;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        final Button login = (Button) findViewById(R.id.login);
        final TextView signup = (TextView) findViewById(R.id.signup);

        final RequestQueue queue=VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();;

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

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
                String url ="http://192.168.0.104:8080/oauth/token?grant_type=password&username="+user+"&password="+pass;
                //String url ="http://localhost:8080/oauth/token";
                //ServerValues.USERNAME=user;
                //ServerValues.PASSWORD=pass;
                Response.Listener list=new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String text=response;
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgress.dismiss();
                        boolean access= false;
                        try {
                            if(object.has("access_token")) {
                                access = true;
                                ServerValues.ACCESS_TOKEN=object.optString("access_token");
                            }
                            else
                                access=false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(access)
                        {
                            Intent registerIntent = new Intent(MainActivity.this,FilterSearch.class);
                            MainActivity.this.startActivity(registerIntent);
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Incorrect username or password !")
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Could not connect to server ! Please try again after a while.")
                                .create()
                                .show();
                    }
                };

                if (user.matches(emailPattern) && user.length() > 0 && pass.length() > 0)
                {
                    TokenRequest req=new TokenRequest(Request.Method.GET,url,list,err);
                    //JSONRequest req=new JSONRequest(Request.Method.GET,url,null,list,err);
                    // Access the RequestQueue through your singleton class.
                    queue.add(req);
                    mProgress.show();
                }
                else
                    Toast.makeText(getApplicationContext(),"Invalid email address or password !",Toast.LENGTH_SHORT).show();
            }
        });

    }
}