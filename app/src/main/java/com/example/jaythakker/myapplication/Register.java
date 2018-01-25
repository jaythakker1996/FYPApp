package com.example.jaythakker.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Register extends AppCompatActivity {

    int mYear=0,mMonth=0,mDay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final RequestQueue queue=VolleyQueue.getInstance(this.getApplicationContext()).getRequestQueue();;

        final Button reg = (Button) findViewById(R.id.register);

        final EditText na = (EditText) findViewById(R.id.name);
        final EditText pass = (EditText) findViewById(R.id.password);
        final EditText rpass = (EditText) findViewById(R.id.rpassword);
        final EditText em=(EditText) findViewById(R.id.emailid);
        final EditText num =(EditText) findViewById(R.id.number);
        final EditText add=(EditText) findViewById(R.id.address);
        final EditText date=(EditText) findViewById(R.id.date);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = na.getText().toString();
                final String password = pass.getText().toString();
                final String rpassword=rpass.getText().toString();
                final String email=em.getText().toString();
                final String dob=date.getText().toString();

                final String numbers =num.getText().toString();
                final String address=add.getText().toString();
                int fnumber=0;
                try{

                    fnumber=Integer.parseInt(numbers);
                }
                catch(NumberFormatException e){
                    // handle your exception
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setMessage("Number prob"+e)
                            .create()
                            .show();

                }
                JSONObject obj=new JSONObject();
                try {
                    obj.put("email",email);
                    obj.put("password",password);
                    obj.put("name",name);
                    obj.put("number",fnumber);
                    obj.put("address",address);
                    obj.put("dob",(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(dob))));
                    obj.put("roles","USER");
                    obj.put("active",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                try {
                    builder.setMessage("date"+(new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(dob))))
                            .create()
                            .show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String url ="http://192.168.43.220:8080/login/";

                Response.Listener list=new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        String text=(response.toString());
                        boolean access= false;
                        try {
                            access = response.getBoolean("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(access)
                        {
                            Intent registerIntent = new Intent(Register.this,MainActivity.class);
                            Register.this.startActivity(registerIntent);
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                            builder.setMessage("Failed")
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setMessage("error"+error)
                                .create()
                                .show();
                    }
                };

                JSONRequest req=new JSONRequest(Request.Method.POST,url,obj,list,err);

                queue.add(req);

            }
        });
    }
}
