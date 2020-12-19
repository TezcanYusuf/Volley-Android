package com.example.volleyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends AppCompatActivity {
    private static JSONArray jsonarray2;
    JSONArray jsonarray;
    ListView dogListview;
    Button button;
    TextView nameText;
    ImageView detailimageView;
    ImageView backgroundBlur;
    WebView webView;

    static RequestQueue queue;
    static ArrayList<String> genre = new ArrayList<>();
    static String base_url = "https://api.themoviedb.org/3/movie/";
    static String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c9af1cc1e4e20238d5ea76cdfabdd62f&language=en-US&page=1";
    String[] data_keyList = new String[]{"id","poster_path","title","backdrop_path"};

    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> posterList = new ArrayList<String>();
    ArrayList<String> backDropList = new ArrayList<String>();

    ArrayList<String> trailerList = new ArrayList<>();
    private int lastPosition = 0;
    private View lastview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        dogListview = (ListView) findViewById(R.id.genreList);
        nameText = findViewById(R.id.textView2);
        detailimageView = findViewById(R.id.detailImageView);
        backgroundBlur = findViewById(R.id.backdropImageView);
        webView = findViewById(R.id.trailerView);
        queue = Volley.newRequestQueue(this);
        button = findViewById(R.id.button);
        ParseString();



        dogListview.setSelection(0);
        dogListview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Animation animation_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                Animation animation_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);

                if (lastPosition != 0) {
                    lastview.setAnimation(animation_out);
                }
                view.setAnimation(animation_in);

                lastPosition = position;
                lastview = view;

                String info = titleList.get(position);
                nameText.setText(info);

                if (trailerList.size() == 0)
                    parseIframe(idList.get(0));
                else
                    parseIframe(idList.get(position));

                Log.e("film_id", idList.get(position));
                String picture = "http://image.tmdb.org/t/p/w440_and_h660_face" + posterList.get(position);

                Glide.with(MainActivity.this)
                        .load(picture)
                        .placeholder(R.drawable.deneme)
                        .into(detailimageView);

                Glide.with(MainActivity.this)
                        .load(picture)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 5)))
                        .into(backgroundBlur);
                Log.e("trailerlistsize :", trailerList.size() + "");


                trailerList.clear();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setTrailer(String url)
    {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            String iframe = "https://www.youtube.com/embed/" + url + "?autoplay=1&fs=0&iv_load_policy=3&showinfo=0&rel=0&cc_load_policy=0&start=0&end=0&vq=hd1080";
            Log.e("youtube-iframe :",iframe);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadUrl(iframe);
        }
        catch (Exception e)
        {
            Log.e("loggg::","hataaa");
        }
    }

    public void ParseString()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("Responserror:",response);
                        for (int i = 0; i < data_keyList.length; i++) {
                            parseData(response,data_keyList[i]);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void parseData(String val,String data_key)
    {
        try
        {
            JSONObject obj = new JSONObject(val);
            jsonarray = new JSONArray(obj.get("results").toString());
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonarray.get(i).toString());
                switch (data_key)
                {
                    case "title":
                        titleList.add(jsonObject.get(data_key).toString());break;
                    case "poster_path":
                        posterList.add(jsonObject.get(data_key).toString());break;
                    case "backdrop_path":
                        backDropList.add(jsonObject.get(data_key).toString());break;
                    case "id":
                        idList.add(jsonObject.get(data_key).toString());break;

                }
            }
            if (posterList.size() > 0)
                createAdapter(posterList,titleList);
//            textView.setText(jsonArray.optString(4).toString());
        }
        catch (Throwable t)
        {
            Log.e("My App", "Could not parse malformed JSON: \"" + val + "\"");
        }
    }

    public void createAdapter(ArrayList<String> picturedataList, ArrayList<String> namedataList)
    {

        ArrayList<Product> productList = new ArrayList<Product>();

        for (int i = 0; i < picturedataList.size(); i++) {
            productList.add(new Product(picturedataList.get(i),namedataList.get(i)));

        }
        ProductAdapter personAdapter = new ProductAdapter(this, productList);

        dogListview.setAdapter(personAdapter);
        Log.e("doglistviewsize",picturedataList.size()+"");
    }

    public  void parseIframe(String id) {
        String result_url = base_url + id + "/videos?api_key=c9af1cc1e4e20238d5ea76cdfabdd62f&language=en-US";
        Log.e("baseurl",result_url);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, result_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject obj = new JSONObject(response);
                            jsonarray2 = new JSONArray(obj.get("results").toString());
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < jsonarray2.length(); i++)
                        {
                            try {
                                JSONObject obj2 = new JSONObject(jsonarray2.get(i).toString());
//                                if (obj2.get("site").toString().equals("YouTube"))
                                Log.e("siteee",obj2.get("site")+"");
                                    trailerList.add(obj2.get("key").toString());
                                    setTrailer(obj2.get("key").toString());
                                    break;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest2);

    }

}
