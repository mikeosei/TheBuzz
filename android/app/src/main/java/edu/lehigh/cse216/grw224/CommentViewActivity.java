package edu.lehigh.cse216.grw224;

import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CommentViewActivity extends AppCompatActivity{

    ArrayList<CommentsDatum> mDataComments = new ArrayList<>();
    int messageId;
    /*
    onCreate is where you initialize your activity
    @param savedInstanceState  if the activity is being re-initialized after previously
    being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
    Note: Otherwise it is null. This value may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        //TODO: change url as necessary
        messageId = getIntent().getIntExtra("MESSAGE_ID",0);
        String url = "https://lilchengs.herokuapp.com/messages/" + messageId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("kpb222", response);
                        populateCommentListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("kpb222", "Failure: Volley error retrieving comment list");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*
    populateCommentListFromVolley will put all the comments onto the screen for the user
     */
    private void populateCommentListFromVolley(String response){
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        try {
            JSONObject Json = new JSONObject(response);
            JSONArray json =  Json.getJSONArray("mData");
            mDataComments.clear();
            for(int i = 0; i < json.length(); i++) {
                int mId = json.getJSONObject(i).getInt("mId");
                int uId = json.getJSONObject(i).getInt("uId");
                String comment = json.getJSONObject(i).getString("comment");
                mDataComments.add(new CommentsDatum(mId, uId, comment));
            }
        }
        catch (final JSONException e) {
            Log.d("kpb222", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        Log.d("kpb222", "Successfully parsed JSON file.");
        LinearLayoutManager llm = new LinearLayoutManager(this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.comment_datum_list_view);
        rv.setLayoutManager(llm);
        CommentListAdapter adapter = new CommentListAdapter(this, mDataComments, queue);
        rv.setAdapter(adapter);

        /*
        click listener that would return user to main activity
         */
        adapter.setGoBackClickListener(new CommentListAdapter.ClickListener() {
            @Override
            public void onClick(CommentsDatum d) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(i, 789);
            }
        });

        /*
        This adapter is a way of ensuring that a user can only edit their own comments
        If a user is interacting with a comment that isn't theirs, they will be able
        to see that user's profile instead
         */
        adapter.setCommentClickListener(new CommentListAdapter.ClickListener() {
            @Override
            public void onClick(CommentsDatum d) {
                //If the user is trying to edit their own comment, this if statement is followed
                if(d.uId == LoginActivity.getUserId()){
                    Intent i = new Intent(getApplicationContext(), CommentEditActivity.class);
                }
                //If the comment is not the user's own, they will view the user's profile that made the comment
                else{
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("userId", d.uId);
                }
            }
        });

    }

    /*
    onCreateOptionsMenu will load the toolbar to the top of the screen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
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
            Intent i = new Intent(getApplicationContext(), CommentActivity.class);
            i.putExtra("MESSAGE_ID", messageId);
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
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        //takes you to the home page
        else if (id == R.id.action_home) {
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}