package com.example.TmdbApp;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private static JSONArray jsonarray2;
    JSONArray jsonarray;
    ListView moviesLisview;
    ImageButton playButton;
    TextView nameText;
    ImageView detailimageView;
    GifImageView gifImageView;
    ImageView backgroundBlur;
    String playerUrl;
    TextView storyView;
    FrameLayout frameLayout;
    Player player;
    FragmentManager fragmentManager;
    Animation animation_in;
    Animation animation_out;
    Animation animation_moveLeft;
    ConstraintLayout.LayoutParams lp;
    ConstraintLayout leftMenu;
    ValueAnimator anim;
    Button reqPopularButton;
    TextView rateTextview;
    ImageView rateImage;

    static RequestQueue queue;
    ProductAdapter personAdapter;
    static ArrayList<String> genre = new ArrayList<>();
    static String base_url = "https://api.themoviedb.org/3/movie/";
    static String url = "https://api.themoviedb.org/3/movie/upcoming?api_key=c9af1cc1e4e20238d5ea76cdfabdd62f&language=en-US&page=1";
    static String searchUrl = "https://api.themoviedb.org/3/search/movie?api_key=c9af1cc1e4e20238d5ea76cdfabdd62f&language=en-US&query=avengers&page=1&include_adult=false";
    String[] data_keyList = new String[]{"id","poster_path","title","backdrop_path","vote_average","overview"};

    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> posterList = new ArrayList<String>();
    ArrayList<String> backDropList = new ArrayList<String>();
    ArrayList<String> voteList = new ArrayList<String>();
    ArrayList<String> overviewList = new ArrayList<String>();

    ArrayList<String> trailerList = new ArrayList<>();
    private int lastPosition = 0;
    private View lastview;
    FragmentTransaction fragmentTransaction;
    EditText searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        moviesLisview = (ListView) findViewById(R.id.moviesLisview);
        nameText = findViewById(R.id.textView2);
        detailimageView = findViewById(R.id.detailImageView);
        backgroundBlur = findViewById(R.id.backdropImageView);
        queue = Volley.newRequestQueue(this);
        frameLayout = findViewById(R.id.frame_layout);
        playButton = findViewById(R.id.button);
        gifImageView = findViewById(R.id.gifImageView);
        leftMenu = findViewById(R.id.leftMenu);
        searchBar = findViewById(R.id.editText);
        reqPopularButton = findViewById(R.id.req3);
        storyView = findViewById(R.id.storyView);
        rateTextview = findViewById(R.id.rateText);
        rateImage = findViewById(R.id.ratingStar);
        lp = (ConstraintLayout.LayoutParams) leftMenu.getLayoutParams();


        moviesLisview.requestFocus();
        ParseString();

        moviesLisview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playButton.requestFocus();
                fillDetail(view,position);
            }
        });



        moviesLisview.setSelection(0);
        moviesLisview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillDetail(view,position);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("lastposition",lastPosition+"");
                parseIframe(idList.get(lastPosition),v);
                playButton.setVisibility(v.GONE);
                frameLayout.requestFocus();
            }
        });

        playButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    playButton.setImageResource(R.drawable.playred2);
                else
                {
                    playButton.setImageResource(R.drawable.blackyoutube);
//                    moviesLisview.setSelection(0);selection position olmalı
                }
            }
        });

    }

    public void  fillDetail(View view, int position)
    {
        animation_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        animation_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);

        if (lastPosition != 0) {
            lastview.setAnimation(animation_out);
        }
        view.setAnimation(animation_in);

        lastPosition = position;
        lastview = view;

        String info = titleList.get(position);
        nameText.setText(info);

        Log.e("film_id", idList.get(position));
        String picture = "http://image.tmdb.org/t/p/w440_and_h660_face" + posterList.get(position);

        gifImageView.setVisibility(View.VISIBLE);

        Glide.with(MainActivity.this)
                .load(picture)
                .placeholder(R.drawable.deneme)
                .into(detailimageView);

        Glide.with(MainActivity.this)
                .load(picture)

                .apply(RequestOptions.bitmapTransform(new BlurTransformation(70, 5)))
                .listener(new RequestListener<Drawable>()
                {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                    {
                        Log.e("glideexxc",e.getMessage());
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                    {
                        gifImageView.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(backgroundBlur);

        storyView.setText(overviewList.get(position));
        String score = voteList.get(position);
        Log.e("vote",voteList.get(position)+"");
        if(!score.equals("0"))
        {
            rateTextview.setText(voteList.get(position));
            rateTextview.setVisibility(View.VISIBLE);
            rateImage.setVisibility(View.VISIBLE);
        }
        else
        {
            rateTextview.setVisibility(View.INVISIBLE);
            rateImage.setVisibility(View.INVISIBLE);
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
                    case "vote_average":
                        voteList.add(jsonObject.get(data_key).toString());break;
                    case "overview":
                        overviewList.add(jsonObject.get(data_key).toString());break;

                }
            }
            if (posterList.size() > 0)
                createAdapter(posterList,titleList,voteList);
        }
        catch (Throwable t)
        {
            Log.e("My App", "Could not parse malformed JSON: \"" + val + "\"");
        }
    }

    public void createAdapter(ArrayList<String> picturedataList, ArrayList<String> namedataList ,ArrayList<String> votesList)
    {
        ArrayList<Product> productList = new ArrayList<Product>();
        for (int i = 0; i < picturedataList.size(); i++) {
            productList.add(new Product(picturedataList.get(i),namedataList.get(i),votesList.get(i)));
        }
        personAdapter = new ProductAdapter(this, productList);
        moviesLisview.setAdapter(personAdapter);
    }

    public  void parseIframe(String id, final View v) {
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
                                Log.e("siteee",obj2.get("site")+"");
                                    trailerList.add(obj2.get("key").toString());
                                    break;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String iframe = trailerList.get(0);
                        goPlayer(v, iframe);
                        trailerList.clear();


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

    public void goPlayer(View view,String player_url)
    {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        player = new Player();
        Bundle bundle = new Bundle();
        bundle.putString("url", player_url);
        frameLayout.requestFocus();
        player.setArguments(bundle);
        fragmentTransaction.add(R.id.frame_layout,player,"playertag").commit();
    }



    @Override
    public void onBackPressed() {
        Log.e("buttonvisible",playButton.getVisibility()+"");
        if (playButton.getVisibility() != View.GONE)
            super.onBackPressed();
        else
        {
            Player player = (Player) getFragment("playertag");
            Log.e("player","deneme");
            playButton.setVisibility(View.VISIBLE);
            player.stopPlayer();
            removeFragment("playertag");
            moviesLisview.requestFocus();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (playButton.isFocused())
                        moviesLisview.requestFocus();
                    else if (moviesLisview.isFocused())
                    {
                        Log.e("deneme","denemeeeee");
//                        reqPopularButton.requestFocus();
                        ValueAnimator anim = ValueAnimator.ofInt(leftMenu.getMeasuredWidth(), 300);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = leftMenu.getLayoutParams();
                                layoutParams.width = val;
                                leftMenu.setLayoutParams(layoutParams);
                            }
                        });
                        anim.setDuration(1000);
                        anim.start();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (leftMenu.getWidth() == 300)
                    {
                        leftMenu.setLayoutParams(new ConstraintLayout.LayoutParams(70,leftMenu.getHeight()));
                        ValueAnimator anim2 = ValueAnimator.ofInt(leftMenu.getMeasuredWidth(), 70);
                        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = leftMenu.getLayoutParams();
                                layoutParams.width = val;
                                leftMenu.setLayoutParams(layoutParams);
                            }
                        });
                        anim2.setDuration(1000);
                        anim2.start();
                        leftMenu.setLayoutParams(new ConstraintLayout.LayoutParams(300,leftMenu.getHeight()));
                        if (playButton.isFocused() == false)
                            moviesLisview.requestFocus();

                    }
                    else
                        playButton.requestFocus();
                    break;
//                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    if (searchBar.isFocused())
//                        reqPopularButton.requestFocus();
                case KeyEvent.KEYCODE_BACK:
                    onBackPressed();break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Fragment getFragment(String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    public void removeFragment(String tag){
        Fragment fragment = getFragment(tag);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null){
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
        else {
            Log.d("msg", "Fragment yok "+ tag);
        }
    }




}
