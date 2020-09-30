package com.example.volleyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Details extends AppCompatActivity {
    static RequestQueue queue;
    static TextView textView;
    String resultText = "";
    String dataName;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        queue = Volley.newRequestQueue(this);
        img= (ImageView) findViewById(R.id.imageView);
        Intent i = getIntent();

        dataName = i.getStringExtra("DataName");
        String url = "https://dog.ceo/api/breed/" + dataName + "/images/random";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        resultText = response;
                        parseImg();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    public void parseImg() {
        try {
            JSONObject jsonObject = new JSONObject(resultText);
            String pictureUrl = jsonObject.get("message").toString();
            Log.e("denemne",pictureUrl);
            Glide.with(this)
                    .load(pictureUrl)
                    .into(img);

        }catch (JSONException err){
            Log.d("Error", err.toString());
        }
    }

}
