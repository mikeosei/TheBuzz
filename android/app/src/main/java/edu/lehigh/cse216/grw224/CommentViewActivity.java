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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CommentViewActivity extends AppCompatActivity{

    ArrayList<CommentsDatum> mDataComments = new ArrayList<>();

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
        String url = "https://lilchengs.herokuapp.com/comments";
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

}