package edu.lehigh.cse216.grw224;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.lehigh.cse216.grw224.MainActivity;

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private RequestQueue queue;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mId;
        TextView mContent;
        TextView mNumOfLikes;
        Button mLikes;
        Button mDislikes;
        //FloatingActionButton mAddButton;




        ViewHolder(View itemView) {
            super(itemView);
            this.mId = (TextView) itemView.findViewById(R.id.listItemId);
            this.mContent = (TextView) itemView.findViewById(R.id.listItemContent);
            this.mNumOfLikes = (TextView) itemView.findViewById(R.id.listNumberOfLikes);
            this.mLikes = itemView.findViewById(R.id.buttonLikes);
            this.mDislikes = itemView.findViewById(R.id.buttonDislikes);
            //this.mAddButton =  itemView.findViewById(R.id.buttonDislikes);
            //Log.d("mfs409", "testung bbbbbbbbbb ");
        }
    }

    private ArrayList<Datum> mData;
    private LayoutInflater mLayoutInflater;

    ItemListAdapter(Context context, ArrayList<Datum> data, RequestQueue queueIn) {
        queue = queueIn;
        mData = data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Datum d = mData.get(position);
        holder.mId.setText(Integer.toString(d.mId));
        holder.mContent.setText(d.mContent);
        holder.mLikes.setText(Integer.toString(d.mLikes));
        holder.mDislikes.setText(Integer.toString(d.mDislikes));



        // Attach a click listener to the view we are configuring
        final View.OnClickListener likeListener = new View.OnClickListener(){
            int likeCounter = 0;

            @Override
            public void onClick(View likeListener) {
                String url = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/like";
                //Log.d("mfs409", "testung bbbbbbbbbb ");
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                likeCounter++;
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("grw224", "That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        };
        holder.mLikes.setOnClickListener(likeListener);
        //holder.mId.setOnClickListener(listener);

        //////////////////////
        final View.OnClickListener dislikeListener = new View.OnClickListener(){
            int dislikeCounter = 0;

            @Override
            public void onClick(View view) {


                String url = "https://lilchengs.herokuapp.com/messages/" + d.mId + "/dislike";
                //Log.d("mfs409", "testung bbbbbbbbbb ");
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dislikeCounter++;
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("grw224", "That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        };
        holder.mDislikes.setOnClickListener(dislikeListener);
        //////////////////////



        final View.OnClickListener newEntryListener = new View.OnClickListener(){



            public void onClick(View view) {
                String url = "https://lilchengs.herokuapp.com/messages/";
                //Log.d("mfs409", "testung bbbbbbbbbb ");
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                               //// likeCounter++;
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("grw224", "That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        };
        //holder.mAddButton.setOnClickListener(newEntryListener);



    }















    interface ClickListener{
        void onClick(Datum d);
    }
    private ClickListener mClickListener;
    ClickListener getClickListener() {return mClickListener;}
    void setClickListener(ClickListener c) { mClickListener = c;}


}