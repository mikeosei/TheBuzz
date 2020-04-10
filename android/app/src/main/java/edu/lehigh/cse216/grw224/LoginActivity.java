package edu.lehigh.cse216.grw224;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import android.support.v7.app.AppCompatViewInflater;

public class LoginActivity extends AppCompatActivity {

    /**
     * mGoogleSignInClient holds the data for user log in
     */
    static GoogleSignInClient mGoogleSignInClient;

    /**
     * userID is the ID of the logged in user
     */
     public static int userId;

    /**
     * sessionId of the logged in user
     */
    String sessionId = "";

    /**
     * Request code for starting a new activity
     */
    private static final int RC_SIGN_IN = 9001;

    /*
    onCreate is where you initialize your activity
    @param savedInstanceState  if the activity is being re-initialized after previously
    being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
    Note: Otherwise it is null. This value may be null
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Request user data to log in to the app
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("721614654195-cofg349cqbl2q6kajjojhiuvpqc51gp4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Add click functionality to login button
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    signIn();
                }
            }
        });
    }

    /*
    signIn starts the intent to prompt the user to select
    a Google account to sign in with
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /*
    onActivityResult gets a GoogleSignInAccount object
    for the user
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /*
    handleSignInResult signs in the user
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //token passed to backend
            String token = account.getIdToken();
            RequestQueue queue = VolleySingleton.getRequestQueue(this);
            Intent i = new Intent();
            //String url = "https://lilchengs.herokuapp.com/login?access_token=" + token;
            String url = "https://lilchengs.herokuapp.com/login";
            final JSONObject loginData = new JSONObject();
            try {
                loginData.put("access_token", token);
            }catch(final JSONException e){
                Log.d("mfs409", "Error adding mId/mContent JSON file: " + e.getMessage());
            }
            JsonObjectRequest requesting = new JsonObjectRequest(Request.Method.POST, url, loginData,
                    new Response.Listener<JSONObject>() {
                        /*
                        @param response represents status message that backend will return if the status is ok then we proceed
                         */
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String backendResponse = response.getString("mData");
                                JSONObject ob = new JSONObject(backendResponse);
                                JSONArray json =  ob.getJSONArray("mData");
                                for (int i = 0; i < json.length(); ++i) {
                                    sessionId = json.getJSONObject(i).getString("sessionId");
                                    userId = json.getJSONObject(i).getInt("userId");
                                    Log.e("kpb222", "" + sessionId + " " + userId);
                                    updateUI();
                                }
                            } catch (final JSONException e) {
                                Log.d("kpb222", "Error parsing JSON file for POST request: " + e.getMessage());
                                return;
                            }
                        }
                    },
                    new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            Log.d("kpb222", "stack trace below");
                            error.printStackTrace();
                            //TODO: You shouldn't go to the main activity page upon a failed login,
                            //TODO:but I do just so I could see other functionality. Should delete when login works correctly
                            updateUI();
                        }
                    }
                });
            queue.add(requesting);
            setResult(Activity.RESULT_OK, i);
            updateUI();
        }
        catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w("kpb222", "signInResult:failed code= " + e.getStatusCode());
        }
    }


    /*
    updateUI redirects user to Main Activity page upon successful login
    @param account  the account of the user that has logged in
     */
    public void updateUI() {
        Intent updateIntent = new Intent(this, MainActivity.class);
        updateIntent.putExtra("userId", userId);
        updateIntent.putExtra("sessionId",sessionId);
        startActivity(updateIntent);
        setContentView(R.layout.activity_main);
    }

    /*
    getUserId allows other classes to access the logged in user id for requests
     */
    public static int getUserId() {
        return userId;
    }

    /*
    signOut is called to sign the user out
     */
    public static void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //App will automatically go back to login screen
                    }
                });
    }

}