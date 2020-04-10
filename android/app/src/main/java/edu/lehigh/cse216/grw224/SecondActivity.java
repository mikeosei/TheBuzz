package edu.lehigh.cse216.grw224;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_second);
        userId = getIntent().getIntExtra("userId",0);
        sessionId = getIntent().getStringExtra("sessionId");
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        // The OK button gets the text from the input box and returns it to the calling activity
        final EditText et = (EditText) findViewById(R.id.editText);
        Button bOk = (Button) findViewById(R.id.buttonOk);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("") ) {
                    Intent i = new Intent();
                    i.putExtra("userId",userId);
                    i.putExtra("sessionId",sessionId);
                    final JSONObject messageData = new JSONObject();
                    try {
                        messageData.put("mMessage", et.getText().toString());
                    }catch(final JSONException e){
                        Log.d("mfs409", "Error adding mId/mContent JSON file: " + e.getMessage());
                    }

                    String url = "https://lilchengs.herokuapp.com/messages";

                    // Request a string response from the provided URL.
                    /*
                    POST Request
                    @param Request.Method.POST type of http request
                    @param messageData data that we are sending the backend
                    */
                    final JsonObjectRequest requesting = new JsonObjectRequest(Request.Method.POST,url, messageData,
                            new Response.Listener<JSONObject>() {
                        /*
                        @param response represents status message that backend will return if the status is ok then we proceed
                         */
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String backendResponse = response.getString("mStatus");
                                        //if the backend response is ok, then we use an Intent object to allow you to communicate with the Main Activity
                                        //so you can refresh the view
                                        if (backendResponse.equals("ok")){
                                            Intent i = new Intent();
                                            i.putExtra("userId",userId);
                                            i.putExtra("sessionId",sessionId);
                                            i.putExtra("result", et.getText().toString());
                                            setResult(Activity.RESULT_OK, i);
                                            finish();
                                        }
                                    } catch (final JSONException e) {
                                        Log.d("mfs409", "Error parsing JSON file for POST request: " + e.getMessage());
                                        return;
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("grw224", "That didn't work!");
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
