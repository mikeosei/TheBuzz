package edu.lehigh.cse216.grw224;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    /**
     * mGoogleSignInClient holds the data for user log in
     */
    GoogleSignInClient mGoogleSignInClient;

    /**
     * Placeholder for handSignInResult method
     */
    private static final String TAG = "";

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
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });
        }
        else {
            setContentView(R.layout.login);
            // Request user data to log in to the app
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("585478383264-j9obqp66iqsied7br8n9c1a17b8l6ptd.apps.googleusercontent.com")
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
    handleSignInResult attempts to sign in the user
     */
    // TODO: May have to pass a currently signed-in user to backend
    // TODO: by sending the ID token for validation from server
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //token passed to backend
            String token = account.getIdToken();
            //unique user id
            String accountID = account.getId();
            // TODO: send request to backend with id token and account id

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    /*
    updateUI redirects user to Main Activity page upon successful login
    @param account  the account of the user that has logged in
     */
    public void updateUI(GoogleSignInAccount account) {
        startActivity(new Intent(this, MainActivity.class));
        setContentView(R.layout.activity_main);
    }

}