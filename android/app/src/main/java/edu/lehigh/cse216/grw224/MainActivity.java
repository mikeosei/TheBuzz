package edu.lehigh.cse216.grw224;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.parser.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    /**
     * mData holds the data we get from Volley
     */
    ArrayList<Datum> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Instantiate the RequestQueue.


        RequestQueue queue = VolleySingleton.getRequestQueue(this);
        String url = "https://lilchengs.herokuapp.com/messages";
        Log.d("mfs409", "testung bbbbbbbbbb ");
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                      //  final ArrayList<String> myList = new ArrayList<>();
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("grw224", "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }


    public void refresh(){
        RequestQueue queue = VolleySingleton.getRequestQueue(this);

        String url = "https://lilchengs.herokuapp.com/messages";
        Log.d("mfs409", "testung bbbbbbbbbb ");
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  final ArrayList<String> myList = new ArrayList<>();
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("grw224", "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }




    private void populateListFromVolley(String response){
        RequestQueue queue = VolleySingleton.getRequestQueue(this);
        try {
            Log.d("mbo", "RESPONSE " + response);
            JSONObject ob = new JSONObject(response);
            Log.d("mbo", ob.toString());
            JSONArray json=  ob.getJSONArray("mData");


            for (int i = 0; i < json.length(); ++i) {
                int id = json.getJSONObject(i).getInt("mId");
                String content = json.getJSONObject(i).getString("mContent");
                int likes = json.getJSONObject(i).getInt("mLikes");
                int dislikes = json.getJSONObject(i).getInt("mDislikes");

                mData.add(new Datum(id, content, likes, dislikes));
            }
        } catch (final JSONException e) {
            Log.d("mfs409", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        Log.d("mfs409", "Successfully parsed JSON file.");
        RecyclerView rv = (RecyclerView) findViewById(R.id.datum_list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ItemListAdapter adapter = new ItemListAdapter(this, mData, queue);
        rv.setAdapter(adapter);



        adapter.setClickListener(new ItemListAdapter.ClickListener() {
            @Override
            public void onClick(Datum d) {
                /*Toast.makeText(MainActivity.this, d.mIndex + " --> " + d.mText, Toast.LENGTH_LONG).show();

D/mfs409: Error parsing JSON file: Value {"mStatus":"ok","mData":[{"mId":15,"mContent":"Message","mLikes":0,"mDislikes":0},{"mId":18,"mContent":"mess","mLikes":0,"mDislikes":0},{"mId":20,"mContent":"kjg","mLikes":0,"mDislikes":0}]} of type org.json.JSONObject cannot be converted to JSONArray

                 */


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Bundle extras = getIntent().getExtras();
            String newId;
            String newMessage;
            JSONObject ob= new JSONObject();
            Intent i = new Intent(getApplicationContext(), SecondActivity.class);
            //i.putExtra("label_contents", "New Message");
/*
            newId= extras.getString("result1");
            newMessage= extras.getString("result2");
        try{
            ob.accumulate("mData",newId);
            ob.accumulate("mContent",newMessage);

            ob.accumulate("mLikes",0);
            ob.accumulate("mDislikes",0);

            RequestQueue queue = VolleySingleton.getRequestQueue(this);
            String url = "https://lilchengs.herokuapp.com/messages";
            Log.d("mfs409", "testung bbbbbbbbbb ");
// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        refresh();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("grw224", "That didn't work!");
                }
            });

// Add the request to the RequestQueue.
            queue.add(stringRequest);


        } catch (final JSONException e) {
            Log.d("mfs409", "Error parsing JSON file: " + e.getMessage());
            return false;
        }

        */


            startActivityForResult(i, 789); // 789 is the number that will come back to us
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        if (requestCode == 789) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the "extra" string of data
                Toast.makeText(MainActivity.this, data.getStringExtra("result"), Toast.LENGTH_LONG).show();
            }
        }
    }
}