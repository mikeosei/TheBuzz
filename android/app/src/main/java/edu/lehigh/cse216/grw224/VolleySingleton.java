package edu.lehigh.cse216.grw224;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    //private static VolleySingleton  = null;
    private static RequestQueue instance;

    /**
     * Constructor for a VolleySingleton
     */
    private VolleySingleton()
    {

    }

    /**
     * Gets a RequestQueue, ensuring that only one is created during the entire time the app is running
     * @param context the context required to call newRequestQueue
     * @return the RequestQueue
     */
    public static RequestQueue getRequestQueue(Context context)

    {
        if (instance == null)
        {
            instance = Volley.newRequestQueue(context);
        }
        return instance;
    }
}
