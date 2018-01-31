package com.example.jaythakker.myapplication;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jay Thakker on 01-02-2018.
 */
//https://smaspe.github.io/2013/06/06/volley-part2.html
//https://github.com/danielsz/android-oauth2-client/blob/master/src/org/sdf/danielsz/OAuthUtils.java
//https://github.com/danielsz/oauth2-client/tree/master/src/org/sdf/danielsz

public final class TokenRequest extends StringRequest {
    public TokenRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    /*@Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        //params.put("grant_type", "client_credentials");
        //params.put("grant_type", "password");
        //params.put("username", ServerValues.USERNAME);
        //params.put("password", ServerValues.PASSWORD);
        return params;
    }*/



    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        String auth = "Basic "
                + Base64.encodeToString((ServerValues.CONSUMER_KEY + ":" + ServerValues.CONSUMER_SECRET).getBytes(),
                Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
    }
}
