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

class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private ArrayList<CommentsDatum> mDataComments;
    private LayoutInflater mLayoutInflater;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mId;
        TextView uId;
        TextView comment;
        Button commentViewOrEdit;
        Button goBack;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.mId = (TextView) itemView.findViewById(R.id.messageIndex);
            this.uId = (TextView) itemView.findViewById(R.id.userIndex);
            this.comment = (TextView) itemView.findViewById(R.id.commentContent);
            this.commentViewOrEdit = itemView.findViewById(R.id.profileOrEditComment);
            this.goBack = itemView.findViewById(R.id.goBack);
        }

    }

    CommentListAdapter(Context context, ArrayList<CommentsDatum> data, RequestQueue queueIn) {
        mDataComments = data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemCount() {
        return mDataComments.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.comment_view_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommentsDatum d = mDataComments.get(position);
        holder.mId.setText("MessageID: " + d.mId);
        holder.uId.setText("UserID: " + d.uId);
        holder.comment.setText(d.mComment);
        holder.commentViewOrEdit.setText("View Profile or Edit Comment");
        holder.goBack.setText("Go Back");

        // Attach a click listener to the view we are configuring for COMMENT
        final View.OnClickListener commentViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View commentViewListener) {
                mCommentListener.onClick(d);
            }
        };
        //binds button to like listener previously created
        holder.commentViewOrEdit.setOnClickListener(commentViewListener);

        // Attach a click listener to the view we are configuring
        final View.OnClickListener goBackClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View goBackListener) {
                mGoBackListener.onClick(d);
            }
        };
        //binds button to like listener previously created
        holder.goBack.setOnClickListener(goBackClickListener);

    }

    interface ClickListener {
        void onClick(CommentsDatum d);
    }

    private ClickListener mCommentListener;

    ClickListener setCommentClickListener() {
        return mCommentListener;
    }
    void setCommentClickListener(ClickListener c) {
        mCommentListener = c;
    }

    private ClickListener mGoBackListener;

    ClickListener setGoBackClickListener() {
        return mGoBackListener;
    }
    void setGoBackClickListener(ClickListener c) {
        mGoBackListener = c;
    }

}