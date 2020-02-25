package edu.lehigh.cse216.grw224;

public class Datum {
    /**
     * An integer index for this piece of data
     */
    int mId;

    /**
     * The string contents that comprise this piece of data
     */
    String mContent;

    /**
     * An integer to track the number of likes
     */
    int mLikes;

    /**
     * An integer to track the number of dislikes
     */
    int mDislikes;

    /**
     * Construct a Datum by setting its index and text
     *
     * @param id The index of this piece of data
     * @param content The string contents for this piece of data
     * @param likes An integer to track the number of likes
     * @param dislikes An integer to track the number of dislikes
     */
    Datum(int id, String content, int likes, int dislikes) {
        this.mId = id;
        this.mContent = content;
        this.mLikes = likes;
        this.mDislikes= dislikes;


    }
}