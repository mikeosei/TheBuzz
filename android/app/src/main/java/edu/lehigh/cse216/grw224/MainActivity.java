package edu.lehigh.cse216.grw224;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * mData holds the data we get from Volley
     */
    ArrayList<Datum> mData = new ArrayList<>();

    /**
     * likeCounter and dislikeCounter will hold the data for the likes
     * and dislikes on messages
     */
    int likeCounter = 0;
    int dislikeCounter = 0;

    /*
    messageDisliked and messageLiked will be referenced when we need to see if the user
    has already liked/disliked a given message
     */
    int messageDisliked = -1;
    int messageLiked = -1;

    ItemListAdapter adapter;
    RecyclerView rv;
    LinearLayoutManager manager;

    int userId;
    String sessionId;


    /*
    onCreate is where you initialize your activity
    @param savedInstanceState  if the activity is being re-initialized after previously
    being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
    Note: Otherwise it is null. This value may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getIntExtra("userId",0);
        sessionId = getIntent().getStringExtra("sessionId");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Instantiate the RequestQueue.
        RequestQueue queue = VolleySingleton.getRequestQueue(this);
        String url = "https://lilchengs.herokuapp.com/messages";

        adapter = new ItemListAdapter(this, mData);
        manager = new LinearLayoutManager(this);
        rv = (RecyclerView) findViewById(R.id.datum_list_view);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kpb222", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*
    refresh recyclerview
     */
    public void refresh(){
        RequestQueue queue = VolleySingleton.getRequestQueue(this);
        String url = "https://lilchengs.herokuapp.com/messages";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kpb222", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*
    populateListFromVolley will put all the message information onto the screen for the user
     */
    private void populateListFromVolley(String response){
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        try {
            JSONObject ob = new JSONObject(response);
            JSONArray json =  ob.getJSONArray("mData");
            mData.clear();
            for (int i = 0; i < json.length(); ++i) {
                int id = json.getJSONObject(i).getInt("id");
                String content = json.getJSONObject(i).getString("message");
                int likes = json.getJSONObject(i).getInt("likes");
                int dislikes = json.getJSONObject(i).getInt("dislikes");
                mData.add(new Datum(id, content, likes, dislikes));
            }
            Log.d("kpb222", mData.toString());
        } catch (final JSONException e) {
            Log.d("kpb222", "Error parsing JSON file: " + e.getMessage());
            return;
        }

        /*ItemListAdapter adapter = new ItemListAdapter(this, mData);
        RecyclerView rv = (RecyclerView) findViewById(R.id.datum_list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);*/
        adapter.notifyDataSetChanged();
        /*
        creates new click listener
        setClickListener() is a method of ItemListAdapter that specifically connects
        the following request to the button in ItemlistAdapter
        */
        adapter.setClickListener(new ItemListAdapter.ClickListener() {
            @Override
            public void onClick(Datum d) {
                /*
                This GET request is to check and see if the user has already disliked the given message.
                If not already disliked, the PUT request for the dislike will go through like normal
                 */
                //TODO: change url as needed
                String checkUrl = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/" + LoginActivity.getUserId();
                StringRequest checkRequest = new StringRequest(Request.Method.GET, checkUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Here, we care about the value of disliked. 1 signifies the
                                //message has already disliked. 0 means it has not been disliked
                                //by the current user
                                try {
                                    JSONObject ob = new JSONObject(response);
                                    JSONArray json =  ob.getJSONArray("mData");
                                    for (int i = 0; i < json.length(); ++i) {
                                        int id = json.getJSONObject(i).getInt("mId");
                                        int liked = json.getJSONObject(i).getInt("mLiked");
                                        int disliked = json.getJSONObject(i).getInt("mDisliked");
                                        messageDisliked = disliked;
                                        int mId = json.getJSONObject(i).getInt("mMId");
                                        int uId = json.getJSONObject(i).getInt("mUId");
                                    }
                                } catch (final JSONException e) {
                                    Log.d("kpb222", "Error parsing JSON file: " + e.getMessage());
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("kpb222", "Unable to get request for dislike");
                    }
                });
                queue.add(checkRequest);
                //allow for PUT request to dislike message only if it hasn't been disliked already
                if (messageDisliked != 1) {
                    String url = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/dislike";
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("kpb222",response);
                                    dislikeCounter++;
                                    refresh();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("kpb222", "That didn't work!");
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
                //Reset value of messageDisliked back to -1
                messageDisliked = -1;
            }
        });
        adapter.setLikeClickListener(new ItemListAdapter.ClickListener() {
            @Override
            public void onClick(Datum d) {
                /*
                This GET request is to check and see if the user has already liked the given message.
                If not already liked, the PUT request for the like will go through like normal
                 */
                //TODO: change url as needed
                String checkUrl = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/" + LoginActivity.getUserId();
                StringRequest checkRequest = new StringRequest(Request.Method.GET, checkUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Here, we care about the value of liked. 1 signifies the
                                //message has already been liked. 0 means it has not been liked
                                //by the current user
                                try {
                                    JSONObject ob = new JSONObject(response);
                                    JSONArray json =  ob.getJSONArray("mData");
                                    for (int i = 0; i < json.length(); ++i) {
                                        int id = json.getJSONObject(i).getInt("mId");
                                        int liked = json.getJSONObject(i).getInt("mLiked");
                                        messageLiked = liked;
                                        int disliked = json.getJSONObject(i).getInt("mDisliked");
                                        int mId = json.getJSONObject(i).getInt("mMId");
                                        int uId = json.getJSONObject(i).getInt("mUId");
                                    }
                                } catch (final JSONException e) {
                                    Log.d("kpb222", "Error parsing JSON file: " + e.getMessage());
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("kpb222", "Unable to get request for like");
                    }
                });
                queue.add(checkRequest);
                //allow for PUT request to like message only if it hasn't been liked already
                if (messageLiked != 1) {
                    String url = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/like";
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("kpb222",response);
                                    likeCounter++;
                                    refresh();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("kpb222", "That didn't work!");
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
                //Reset value of messageLiked back to -1
                messageLiked = -1;
            }
        });
        adapter.setCommentClickListener(new ItemListAdapter.ClickListener() {
            @Override
            public void onClick(Datum d) {
                Intent commentIntent = new Intent(getBaseContext(), CommentViewActivity.class);
                commentIntent.putExtra("MESSAGE_ID",d.mId);
                commentIntent.putExtra("userId",userId);
                commentIntent.putExtra("sessionId",sessionId);
                startActivity(commentIntent);
                setContentView(R.layout.activity_comment_view);
            }
        });
    }

    /*
    onCreateOptionsMenu will load the toolbar to the top of the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    onOptionsItemSelected will give various commands for whatever button is
    clicked on the toolbar
     */
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
            i.putExtra("userId",userId);
            i.putExtra("sessionId",sessionId);
            startActivityForResult(i, 789); // 789 is the number that will come back to us
            return true;
        }
        //have to start LoginActivity class to access the GoogleSignInClient
        //so you can logout the user
        else if (id == R.id.action_logout) {
            LoginActivity.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        //takes you to the logged in user profile
        else if (id == R.id.action_profile) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra("userId",userId);
            profileIntent.putExtra("sessionId",sessionId);
            startActivity(profileIntent);
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
                //Toast.makeText(MainActivity.this, data.getStringExtra("result"), Toast.LENGTH_LONG).show();
                refresh();
            }
        }
    }

}