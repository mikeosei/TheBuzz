package edu.lehigh.cse216.grw224;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Base64;

public class SecondActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 123;
    int userId;
    String sessionId;
    String queryParam;
    File photoFile;
    int imageId;
    String imagePath;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    boolean finished = false;

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
        queryParam = getIntent().getStringExtra("queryParam");
        photoFile = null;
        final RequestQueue queue = VolleySingleton.getRequestQueue(this);
        // The OK button gets the text from the input box and returns it to the calling activity
        final EditText et = (EditText) findViewById(R.id.editText);
        Button bOk = (Button) findViewById(R.id.buttonOk);
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("") ) {
                    String encodedImage = "";
                    if (photoFile != null)
                    {
                        Bitmap bm = BitmapFactory.decodeFile(photoFile.getPath());
                        Log.e("grw224",photoFile.getPath());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        final JSONObject imageData = new JSONObject();
                        try {
                            imageData.put("mImage", encodedImage);
                        }catch(final JSONException e){
                            Log.d("mfs409", "Error adding mImage to JSON file: " + e.getMessage());
                        }
                        // Change to url for post image route
                        String imageUrl = "https://lilchengs.herokuapp.com/images" + queryParam;

                        final JsonObjectRequest requesting = new JsonObjectRequest(Request.Method.POST,imageUrl, imageData,
                                new Response.Listener<JSONObject>() {
                                    /*
                                    @param response represents status message that backend will return if the status is ok then we proceed
                                     */
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String backendResponse = response.getString("mStatus");
                                            String backendResponseReturn = response.getString("mData");
                                            JSONObject ob = new JSONObject(backendResponseReturn);
                                            JSONArray json =  ob.getJSONArray("mData");
                                            if (backendResponse.equals("ok")){
                                                for (int i = 0; i < json.length(); ++i) {
                                                    imagePath = json.getJSONObject(i).getString("imagePath");
                                                    imageId = json.getJSONObject(i).getInt("imageId");
                                                }
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
                    }
                    Intent i = new Intent();
                    i.putExtra("userId",userId);
                    i.putExtra("sessionId",sessionId);
                    i.putExtra("queryParam",queryParam);
                    final JSONObject messageData = new JSONObject();
                    try {
                        messageData.put("mMessage", et.getText().toString());
                        messageData.put("mImageId", imageId);
                        messageData.put("mImagePath",imagePath);
                    }catch(final JSONException e){
                        Log.d("mfs409", "Error adding mId/mContent JSON file: " + e.getMessage());
                    }

                    String url = "https://lilchengs.herokuapp.com/messages" + queryParam;

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
                                            i.putExtra("queryParam",queryParam);
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

        // The Add Image button adds an image to the post
        Button bTakeImage = (Button) findViewById(R.id.buttonImageTake);
        bTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
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

        Button bGetImage = (Button) findViewById(R.id.buttonImageGet);
        bGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
    }


    // Creates path for image and stores it
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Take Picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("grw224", "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.lehigh.cse216.grw224",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    photoFile = new File(selectedImage.getPath());
                    break;
            }

    }
}
