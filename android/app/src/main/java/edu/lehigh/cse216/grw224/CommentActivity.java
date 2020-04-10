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

public class CommentActivity extends AppCompatActivity{

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
        setContentView(R.layout.activity_comment);
        messageId = getIntent().getIntExtra("MESSAGE_ID",0);
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        // The OK button gets the text from this editText box and return it to the calling activity
        final EditText edit = (EditText) findViewById(R.id.messageText);
        Button bOk = (Button) findViewById(R.id.buttonOk);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                final JSONObject messageData = new JSONObject();
                try {
                    messageData.put("mMessage", edit.getText().toString());
                }
                catch(final JSONException e){
                    Log.d("kpb222", "Error adding mId/mContent JSON file: " + e.getMessage());
                }
                //TODO: edit url as necessary by getting message id for comment
                //TODO: need to figure out how to attach the comment to the correct message
                String url = "https://lilchengs.herokuapp.com/comment/" + messageId;
                final JsonObjectRequest requesting = new JsonObjectRequest(Request.Method.POST, url, messageData,
                        new Response.Listener<JSONObject>() {
                            /*
                            @param response represents status message that backend will return
                            if the status is ok then we proceed
                             */
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String backendResponse = response.getString("mStatus");
                                    if (backendResponse.equals("ok")){
                                        Intent i = new Intent();
                                        i.putExtra("result", edit.getText().toString());
                                        setResult(Activity.RESULT_OK, i);
                                        finish();
                                    }
                                } catch (final JSONException e) {
                                    Log.d("kpb222", "Error parsing JSON file for POST request: " + e.getMessage());
                                    return;
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("kpb222", "Failure: Volley error adding comment");
                    }
                });
                queue.add(requesting);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        // The Cancel button returns to the main activity page without sending any data
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