package edu.lehigh.cse216.grw224;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentEditActivity extends AppCompatActivity {

    int userId;
    String sessionId;
    String queryParam;

    /*
    onCreate is where you initialize your activity
    @param savedInstanceState  if the activity is being re-initialized after previously
    being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
    Note: Otherwise it is null. This value may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_edit);
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        final EditText et = (EditText) findViewById(R.id.messageText);
        Intent mIntent = getIntent();
        userId = mIntent.getIntExtra("userId",0);
        sessionId = mIntent.getStringExtra("sessionId");
        queryParam = mIntent.getStringExtra("queryParam");
        //Obtaining the comment so that it is already entered in the text box and you don't have to start from scratch
        String commentData = mIntent.getStringExtra("commentData");
        et.append(commentData);
        Button bOk = (Button) findViewById(R.id.buttonOk);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {
                    Intent i = new Intent();
                    i.putExtra("userId",userId);
                    i.putExtra("sessionId",sessionId);
                    i.putExtra("queryParam",queryParam);
                    final JSONObject messageData = new JSONObject();
                    try {
                        messageData.put("mMessage", et.getText().toString());
                    }
                    catch(final JSONException e){
                        Log.d("kpb222", "Error adding mId/mContent JSON file: " + e.getMessage());
                    }
                    //TODO: alter url as necessary
                    String url = "https://lilchengs.herokuapp.com/messages" + queryParam;
                    final JsonObjectRequest requesting = new JsonObjectRequest(Request.Method.POST, url, messageData,
                            new Response.Listener<JSONObject>() {
                                /*
                                @param response represents status message that backend will
                                return if the status is ok then we proceed
                                 */
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String backendResponse = response.getString("mStatus");
                                        if (backendResponse.equals("ok")){
                                            Intent i = new Intent();
                                            i.putExtra("userId",userId);
                                            i.putExtra("sessionId",sessionId);
                                            i.putExtra("result", et.getText().toString());
                                            setResult(Activity.RESULT_OK, i);
                                            finish();
                                        }
                                    }
                                    catch (final JSONException e) {
                                        Log.d("kpb222", "Error parsing JSON file for POST request: " + e.getMessage());
                                        return;
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("kpb222", "Failure: Volley error");
                        }
                    });
                    queue.add(requesting);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        // The Cancel button returns to the caller without sending any data
        Button bCancel = (Button) findViewById(R.id.buttonCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

}