package edu.lehigh.cse216.grw224;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        RequestQueue queue;

        // Get the parameter from the calling activity, and put it in the TextView
        Intent input = getIntent();
        String label_contents = input.getStringExtra("label_contents");
        TextView tv = (TextView) findViewById(R.id.specialMessage);
        tv.setText(label_contents);

        // The OK button gets the text from the input box and returns it to the calling activity
        final EditText et = (EditText) findViewById(R.id.editText);
        final EditText et2 = (EditText) findViewById(R.id.editText2);


        Button bOk = (Button) findViewById(R.id.buttonOk);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {
                    Intent i = new Intent();
                   // i.putExtra("result1", et.getText().toString());
                   // i.putExtra("result2",et2.getText().toString());

try {
    JSONObject ob = new JSONObject();
    ob.accumulate("mData", et.getText().toString());
    ob.accumulate("mContent", et2.getText().toString());

    ob.accumulate("mLikes", 0);
    ob.accumulate("mDislikes", 0);
    String url = "https://lilchengs.herokuapp.com/messages";
    Log.d("mfs409", "testung bbbbbbbbbb ");
// Request a string response from the provided URL.

    final JsonObjectRequest requesting = new JsonObjectRequest(url, ob,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    


                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("grw224", "That didn't work!");
        }
    });



}catch (final JSONException e) {
                        Log.d("mfs409", "Error parsing JSON file: " + e.getMessage());
                        return;
                    }

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
