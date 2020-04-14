package edu.lehigh.cse216.grw224.backend;

/**
 * Slightly different than SimpleRequest.java. Used for file uploads
 */
public class UploadSimpleRequest {

    /**
     * File name including extension such as "File.jpeg"
     */
    public String uploadName;

    /**
     * The upload data for files. This string will be base 64 encoded for images
     */
    public String uploadData;

}