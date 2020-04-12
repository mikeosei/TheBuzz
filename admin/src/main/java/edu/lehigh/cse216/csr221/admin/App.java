package edu.lehigh.cse216.csr221.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import com.google.api.services.drive.Drive.Files;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * App is our basic admin app. For now, it is a demonstration of the six key
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {
    private static final String APPLICATION_NAME = "The Buzz";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final static Collection<String> SCOPES = new ArrayList<String>();;
    // Directory to store user credentials for this application.
    // private static final java.io.File CREDENTIALS_FOLDER = new
    // java.io.File(System.getProperty("user.home"), "credentials");

    // private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

    //
    // Global instance of the scopes required by this quickstart. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    //
    // private static final List<String> SCOPES =
    // Collections.singletonList(DriveScopes.DRIVE);

    // Total number of tables
    private static final int NUM_TABLES = 5;

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        // Commands for tblData
        System.out.println("  [T] Create table");
        System.out.println("  [D] Drop table");
        System.out.println("  [1] Query for a specific row of the table");
        System.out.println("  [*] Query for all rows of the table");
        System.out.println("  [-] Delete a row of the table");
        System.out.println("  [+] Insert a new row of the table");
        System.out.println("  [~] Update a row of tblData");
        // Commands for tblData2
        /*
         * System.out.println("  [Y] Create tblData2");
         * System.out.println("  [F] Drop tblData2");
         * System.out.println("  [2] Query for a specific row of tblData2");
         * System.out.println("  [#] Query for all rows of tblData2");
         * System.out.println("  [_] Delete a row of tblData");
         * System.out.println("  [=] Insert a new row of tblData");
         * System.out.println("  [`] Update a row of tblData");
         */
        // General Commands
        System.out.println("  [S] Select table");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TD1*-+~Sq?";

        // We repeat until a valid single-character option is selected
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided. May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided. On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    static void DriveQuickstart() {
        try {
            SCOPES.add("https://www.googleapis.com/auth/drive");

            // 2: Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            // 3: Read client_secret.json file & create Credential object.
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream("client_secret.json"))
                    .createScoped(SCOPES);

            // 5: Create Google Drive Service.
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME).build();

            // Print the names and IDs for up to 10 files.
            FileList result = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)")
                    .execute();
            List<File> files = result.getFiles();
            System.out.println("hello world");
            if (files == null || files.isEmpty()) {
                System.out.println("No files found.");
            } else {
                System.out.println("Files:");
                for (File file : files) {
                    System.out.printf("%s (%s)\n", file.getName(), file.getId());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main routine runs a loop that gets a request from the user and processes
     * it
     * 
     * @param argv Command-line options. Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        // String ip = env.get("POSTGRES_IP");
        // String port = env.get("POSTGRES_PORT");
        // String user = env.get("POSTGRES_USER");
        // String pass = env.get("POSTGRES_PASS");
        // String database = env.get("POSTGRES_DATABASE");
        String db_url = "postgres://ecxfhpxnovpejh:0a0657538f39c41357b1de598e83f75940333ef42441c9eb41268f0a5d08dc2a@ec2-34-235-108-68.compute-1.amazonaws.com:5432/d70uqvt93jbpoq";

        // Current table (0 = messsageTable, 1 = CommentRow)
        int currentTable = 0;
        
        Database db = Database.getDatabase(db_url); // pass the DB url
        // Start our basic command-line interpreter:
        if (db == null)
            return;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call
            // help
            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            }
            // select table
            else if (action == 'S') {
                boolean valid = false;
                while (!valid) {
                    currentTable = getInt(in, "Enter the ID of the desired table");
                    if (currentTable >= 0 && currentTable < NUM_TABLES) {
                        valid = true;
                    } else {
                        System.out.println("Invalid table");
                    }
                }
                System.out.println("Switched to table " + currentTable);
            }
            // Commands for tblData
            else if (action == 'T') {
                if (currentTable == 0) {
                    db.createTable();
                } else if (currentTable == 1) {
                    db.createTable2();
                } else if (currentTable == 2) {
                    db.createTable3();
                } else if (currentTable == 3) {
                    db.createTable4();
                } else if (currentTable == 4) {
                    db.createTable5();
                }
                // drop table
            } else if (action == 'D') {
                if (currentTable == 0) {
                    db.dropTable();
                } else if (currentTable == 1) {
                    db.dropTable2();
                } else if (currentTable == 2) {
                    db.dropTable3();
                } else if (currentTable == 3) {
                    db.dropTable4();
                } else if (currentTable == 4) {
                    db.dropTable5();
                }
                // Query specific row
            } else if (action == '1') {
                if (currentTable == 0) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.MessageRow res = db.selectOne(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.message);
                    }
                } else if (currentTable == 1) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.CommentRow res = db.selectOne2(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.comment);
                    }
                } else if (currentTable == 2) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.UserRow res = db.selectOne3(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.firstName + " " + res.lastName + " Email: " + res.email
                                + " User Id: " + res.userId);
                    }
                } else if (currentTable == 3) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.LikesRow res = db.selectOne4(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> Liked: " + res.liked + " Disliked: " + res.disliked + " Message ID: "
                                + res.messageId + " UserId: " + res.userId);
                    }
                } else if (currentTable == 4) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.DriveRow res = db.selectOne5(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> userId: " + res.userId+ " messageId: " + res.messageId + " fileName: " + res.fileName + " fileSize: " + res.fileSize + " accessDate: " + res.accessDate);
                    }
                }

            } else if (action == '*') {
                if (currentTable == 0) {
                    ArrayList<Database.MessageRow> res = db.selectAll();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.MessageRow rd : res) {
                        System.out
                                .println("  [ID # " + rd.id + "]" + "  Message: " + rd.message + " User: " + rd.userId);
                    }
                } else if (currentTable == 1) {
                    ArrayList<Database.CommentRow> res = db.selectAll2();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.CommentRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Comment: " + rd.comment + "  MessageId: "
                                + rd.messageId + " User: " + rd.userId);
                    }
                } else if (currentTable == 2) {
                    ArrayList<Database.UserRow> res = db.selectAll3();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.UserRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Name: " + rd.firstName + " " + rd.lastName
                                + " email: " + rd.email + " User Id: " + rd.userId);
                    }
                } else if (currentTable == 3) {
                    ArrayList<Database.LikesRow> res = db.selectAll4();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.LikesRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Liked: " + rd.liked + " Disliked: "
                                + rd.disliked + " Message Id: " + rd.messageId + " User Id: " + rd.userId);
                    }
                
                } else if (currentTable ==4){
                    ArrayList<Database.DriveRow> res = db.selectAll5();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.DriveRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  userId: " + rd.userId+ " messageId: " + rd.messageId + " fileName: " + rd.fileName + " fileSize: " + rd.fileSize + " accessDate: " + rd.accessDate);
                    }
                }
            } else if (action == '-') {
                if (currentTable == 0) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                } else if (currentTable == 1) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow2(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                } else if (currentTable == 2) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow3(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                } else if (currentTable == 3) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow4(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                } else if (currentTable == 4) {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow5(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                }
            } else if (action == '+') {
                if (currentTable == 0) {
                    String message = getString(in, "Enter the message");
                    int userId = getInt(in, "Enter the user id");
                    if (message.equals(""))
                        continue;
                    int res = db.insertRow(message, userId);
                    System.out.println(res + " rows added");
                } else if (currentTable == 1) {
                    String message = getString(in, "Enter the Comment");
                    int messageId = getInt(in, "Enter the messageId");
                    int userId = getInt(in, "Enter the user id");
                    if (message.equals(""))
                        continue;
                    int res = db.insertRow2(message, messageId, userId);
                    System.out.println(res + " rows added");
                } else if (currentTable == 2) {
                    String firstName = getString(in, "Enter the First Name");
                    String lastName = getString(in, "Enter the Last Name");
                    String email = getString(in, "Enter the email");
                    String userId = getString(in, "Enter the user Id");
                    // if (message.equals(""))
                    // continue;
                    int res = db.insertRow3(firstName, lastName, email, userId);
                    System.out.println(res + " rows added");
                } else if (currentTable == 3) {
                    int messageId = getInt(in, "Enter the Message ID");
                    int userId = getInt(in, "Enter the User ID");
                    // if (message.equals(""))
                    // continue;
                    int res = db.insertRow4(messageId, userId);
                    System.out.println(res + " rows added");
                } else if (currentTable == 4) {
                    int messageId = getInt(in, "Enter the Message ID");
                    int userId = getInt(in, "Enter the User ID");
                    String fileName = getString(in, "Enter the file name");
                    int fileSize = getInt(in, "Enter the file size");
                    String accessDate = getString(in, "Enter the access date");

                    int res = db.insertRow5(userId, messageId, fileName, fileSize, accessDate);
                    System.out.println(res + " rows added");
                }
            } 

        }
        // Always remember to disconnect from the database when the program
        // exits
        db.disconnect();
    }
}