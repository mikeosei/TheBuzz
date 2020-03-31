package edu.lehigh.cse216.grw224;

class CommentsDatum {

    /**
     * An integer index for this piece of data
     */
    int mId;

    /**
     * An integer index for the user iD
     */
    int uId;

    /**
     * A string for the comment
     */
    String mComment;

    /**
     * Construct a CommentsDatum by setting its index and text
     */
    CommentsDatum(int mid, int uid, String comment) {
        mId = mid;
        uId = uid;
        mComment = comment;
    }

}