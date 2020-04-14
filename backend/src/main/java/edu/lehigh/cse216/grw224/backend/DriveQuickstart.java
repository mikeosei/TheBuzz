package edu.lehigh.cse216.grw224.backend;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.client.http.FileContent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;

public class DriveQuickstart {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static NetHttpTransport HTTP_TRANSPORT;
    private static Drive service;

    public static boolean setup() {
        // Build a new authorized API client service.
        try {
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return true;
        }
        catch (Exception e) {
            System.out.println("An error occurred: " + e);
            return false;
        }
    }

    /**
     * Getter for NetHttpTransport object
     */
    public static NetHttpTransport getNetHttpTransport() {
        return HTTP_TRANSPORT;
    }

    /**
     * Getter for Drive object
     */
    public static Drive getDrive() {
        return service;
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleCredential credential = GoogleCredential.fromStream(in)
            .createScoped(SCOPES);
        
        return credential;
    }

    /**
    * Uploads file into Drive
    *
    * @param service Static variable that was created for the DriveQuickstart class
    * @param title Name of the file
    * @param description Description of file being uploaded
    * @param parentId Optional parent folder's ID
    * @param mimeType MIME type of the file being uploaded
    * @return File that was uploaded
    */
    public static String insertFile(Drive service, String title, java.io.File file, String mimeType) {
        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setMimeType(mimeType);

        // File's content.
        java.io.File fileContent = file;
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        try {
            File newFile = service.files().insert(body, mediaContent).execute();
            return newFile.getId();
        }
        catch (Exception e) {
            System.out.println("An error occurred: " + e);
            e.printStackTrace();
            return null;
        }
    }

    /**
    * Downloads file from Drive
    *
    * @param fileId of the file to be downloaded from Drive
    * @return Byte array stream of the data for the file
    */
    public static ByteArrayOutputStream getFile(Drive service, String fileId) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            service.files().get(fileId).executeMediaAndDownloadTo(stream);
            return stream;
        }
        catch(Exception e) {
            System.out.println("Error obtaining file");
            return null;
        }
    }

    /**
    * Deletes file from Drive
    *
    * @param fileId of the file to be deleted from Drive
    * @return True if succesfully deleted from Drive, false if not
    */
    public static boolean deleteFile(Drive service, String fileId){
        try{
            service.files().delete(fileId).execute();
            return true;
        }
        catch(Exception e){
            System.out.println("Error deleting file");
            return false;
        }
    }

}