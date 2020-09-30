package com.example.volleyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static TextView textView;
    public static String data;
    ListView dogListview;
    static RequestQueue queue;
    static ArrayList<String> resultList = new ArrayList<>();
    static ArrayList<String> genre = new ArrayList<>();
    static String url = "https://dog.ceo/api/breeds/list/all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dogListview = (ListView) findViewById(R.id.genreList);
        queue = Volley.newRequestQueue(this);
        ParseString();

        dogListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDog = ((TextView) view).getText().toString();
                selectedDog = selectedDog.replace(" ","/");
                Toast.makeText(getApplicationContext(),selectedDog, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Details.class);
                intent.putExtra("DataName",selectedDog.trim().replace(" ",""));
                startActivity(intent);
            }
        });
    }

    public void ParseString()
    {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        parseResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    public void parseResponse(String val)
    {
        try
        {
            JSONObject obj = new JSONObject(val);
            JSONObject jsonarray = new JSONObject(obj.get("message").toString());
            Iterator x = jsonarray.keys();
            JSONArray jsonArray = new JSONArray();
            while (x.hasNext())
            {
                String key = (String) x.next();
                jsonArray.put(jsonarray.get(key));
                for (int i = 0; i<jsonArray.length();i++)
                {
                    JSONArray array = new JSONArray(jsonArray.get(i).toString());
                    if (array.length()>0)
                    {
                        for (int j = 0; j < array.length(); j++) {
                            genre.add(key + " " + array.get(j));
                        }
                    }
                    jsonArray = new JSONArray(new ArrayList<String>());
                }
            }
            createAdapter(genre);
            textView.setText(jsonArray.optString(4).toString());
        }
        catch (Throwable t)
        {
            Log.e("My App", "Could not parse malformed JSON: \"" + val + "\"");
        }
    }

    public void createAdapter(ArrayList<String> genreList)
    {
        ArrayAdapter<String> veriAdaptoru=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, genreList);
        dogListview.setAdapter(veriAdaptoru);


    }
}
