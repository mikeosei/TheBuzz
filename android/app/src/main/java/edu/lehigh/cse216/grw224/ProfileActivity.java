package edu.lehigh.cse216.grw224;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    //These variables will hold the data of the user profile
    String id = "";
    String firstName = "";
    String lastName = "";
    String email = "";

    int userId;
    String sessionId;

    /*
    onCreate is where you initialize your activity
    @param savedInstanceState  if the activity is being re-initialized after previously
    being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
    Note: Otherwise it is null. This value may be null
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RequestQueue queue = VolleySingleton.getRequestQueue(this);
        //Obtains id of the user's profile that will be viewed
        Intent mIntent = getIntent();
        userId = mIntent.getIntExtra("userId", 0);
        sessionId = mIntent.getStringExtra("sessionId");
        //TODO: Revise url as needed
        String url = "https://lilchengs.herokuapp.com/profile";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kpb222", "Couldn't get profile info");
                populateListFromVolley("");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*
    populateListFromVolley parses the string response with
    profile information and puts information into list view
     */
    private void populateListFromVolley(String response){
        try {
            JSONObject ob = new JSONObject(response);
            JSONArray json =  ob.getJSONArray("mData");
            for (int i = 0; i < json.length(); ++i) {
                id = json.getJSONObject(i).getString("id");
                firstName = json.getJSONObject(i).getString("firstName");
                lastName = json.getJSONObject(i).getString("lastName");
                email = json.getJSONObject(i).getString("email");
            }
            TextView uId = (TextView) findViewById(R.id.profileid);
            uId.append(id);
            TextView fName = (TextView) findViewById(R.id.firstname);
            fName.append(firstName);
            TextView lName = (TextView) findViewById(R.id.lastname);
            lName.append(lastName);
            TextView eml = (TextView) findViewById(R.id.email);
            eml.append(email);
        }
        catch (final JSONException e) {
            Log.d("kpb222", "Error parsing JSON file: " + e.getMessage());
            return;
        }
    }

    /*
    onCreateOptionsMenu adds the various options to the toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    /*
    onOptionsItemSelected gives click functionality to the options on the toolbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Intent homeIntent = new Intent(this, ProfileActivity.class);
            homeIntent.putExtra("userId",userId);
            homeIntent.putExtra("sessionId",sessionId);
            startActivity(homeIntent);
            return true;
        }
        //have to start LoginActivity class to access the GoogleSignInClient
        //so you can logout the user
        else if (id == R.id.action_logout) {
            LoginActivity.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
