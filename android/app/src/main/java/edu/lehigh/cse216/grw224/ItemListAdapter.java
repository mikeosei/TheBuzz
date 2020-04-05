package edu.lehigh.cse216.grw224;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.RequestQueue;
import java.util.ArrayList;

class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private RequestQueue queue;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mId;
        TextView mContent;
        Button mLikes;
        Button mDislikes;
        Button mComment;

        ViewHolder(View itemView) {
            super(itemView);
            this.mId = (TextView) itemView.findViewById(R.id.listItemId);
            this.mContent = (TextView) itemView.findViewById(R.id.listItemContent);
            this.mLikes = itemView.findViewById(R.id.buttonLikes);
            this.mDislikes = itemView.findViewById(R.id.buttonDislikes);
            this.mComment = itemView.findViewById(R.id.buttonComment);
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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Datum d = mData.get(position);
        holder.mId.setText(Integer.toString(d.mId));
        holder.mContent.setText(d.mContent);
        holder.mLikes.setText(Integer.toString(d.mLikes));
        holder.mDislikes.setText(Integer.toString(d.mDislikes));
        holder.mComment.setText("Comment View");

        // Attach a click listener to the view we are configuring for COMMENT
        final View.OnClickListener commentListener = new View.OnClickListener() {
            @Override
            public void onClick(View commentListener) {
                mCommentListener.onClick(d);
            }
        };
        //binds button to like listener previously created
        holder.mComment.setOnClickListener(commentListener);
        // Attach a click listener to the view we are configuring
        final View.OnClickListener likeListener = new View.OnClickListener(){

            @Override
            public void onClick(View likeListener) {
                mLikeListener.onClick(d);
            }
        };
        //binds button to like listener previously created

        holder.mLikes.setOnClickListener(likeListener);

        /*
        creates new onclicklistener for view (dislikebutton) and when it is clicked the Main activity's  onclicklistener is triggered
         */
        final View.OnClickListener dislikeListener = new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mDislikeListener.onClick(d);
            }
        };

        //binds button to dislikeLike listener previously created
        holder.mDislikes.setOnClickListener(dislikeListener);
    }


    /*
    creation of a clicklistener interface which uses a method called onClick
    */
    interface ClickListener{
        void onClick(Datum d);
    }

    /*
    private ClickListener is an instance of the instance
    creation of mDislikeListener, a instance of ClickListener that will be potentially linked to the interfaces onClick method
    SEE final View.OnClickListener
     */
    private ClickListener mDislikeListener;
    ClickListener getClickListener() {return mDislikeListener;}
    void setClickListener(ClickListener c) { mDislikeListener = c;}

    /*
    private ClickListener is an instance of the instance
    creation of mLikeListener, a instance of ClickListener that will be potentially linked to the interfaces onClick method
    SEE final View.OnClickListener
     */
    private ClickListener mLikeListener;
    ClickListener getLikeClickListener() {return mLikeListener;}
    void setLikeClickListener(ClickListener c) { mLikeListener = c;}

    /*
    private ClickListener is an instance of the instance
    creation of mCommentListener, a instance of ClickListener that will be potentially linked to the interfaces onClick method
    SEE final View.OnClickListener
     */
    private ClickListener mCommentListener;
    ClickListener setCommentClickListener() {return mCommentListener;}
    void setCommentClickListener(ClickListener c) { mCommentListener = c;}

}