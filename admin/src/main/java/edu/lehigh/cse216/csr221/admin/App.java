package edu.lehigh.cse216.csr221.admin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    // Total number of tables
    private static final int NUM_TABLES = 4;

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
        /*System.out.println("  [Y] Create tblData2");
        System.out.println("  [F] Drop tblData2");
        System.out.println("  [2] Query for a specific row of tblData2");
        System.out.println("  [#] Query for all rows of tblData2");
        System.out.println("  [_] Delete a row of tblData");
        System.out.println("  [=] Insert a new row of tblData");
        System.out.println("  [`] Update a row of tblData");
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
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
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
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
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

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
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
        // Get a fully-configured connection to the database, or exit 
        // immediately
       // Database db = Database.getDatabase(ip, port, user, pass);
        
        Database db = Database.getDatabase(db_url); //pass the DB url
        // Start our basic command-line interpreter:
        if (db == null)
            return;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            //     function call
            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } 
            else if (action == 'S')
            {
                boolean valid = false;
                while (!valid)
                {
                    currentTable = getInt(in, "Enter the ID of the desired table");
                    if (currentTable >= 0 && currentTable < NUM_TABLES)
                    {
                        valid = true;
                    }
                    else
                    {
                        System.out.println("Invalid table");
                    }
                }
                System.out.println("Switched to table " + currentTable);
            }
            // Commands for tblData
            else if (action == 'T') {
                if (currentTable == 0)
                {
                    db.createTable();
                }
                else if (currentTable == 1)
                {
                    db.createTable2();
                }
                else if (currentTable == 2)
                {
                    db.createTable3();
                }
                else if (currentTable == 3)
                {
                    db.createTable4();
                }
            } else if (action == 'D') {
                if (currentTable == 0)
                {
                    db.dropTable();
                }
                else if (currentTable == 1)
                {
                    db.dropTable2();
                }
                else if (currentTable == 2)
                {
                    db.dropTable3();
                }
                else if (currentTable == 3)
                {
                    db.dropTable4();
                }
            } else if (action == '1') {
                if (currentTable == 0)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.MessageRow res = db.selectOne(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.message);
                    }
                }
                else if (currentTable == 1)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.CommentRow res = db.selectOne2(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.comment);
                    }
                }
                else if (currentTable == 2)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.UserRow res = db.selectOne3(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> " + res.firstName + " " + res.lastName + " Email: " + res.email + " User Id: " + res.userId);
                    }
                }
                else if (currentTable == 3)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    Database.LikesRow res = db.selectOne4(id);
                    if (res != null) {
                        System.out.println("  [" + res.id + "] ");
                        System.out.println("  --> Liked: " + res.liked + " Disliked: " + res.disliked + " Message ID: " + res.messageId + " UserId: " + res.userId);
                    }
                }
            } else if (action == '*') {
                if (currentTable == 0)
                {
                    ArrayList<Database.MessageRow> res = db.selectAll();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.MessageRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Message: " + rd.message + " User: " + rd.userId);
                    }
                }
                else if (currentTable == 1)
                {
                    ArrayList<Database.CommentRow> res = db.selectAll2();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.CommentRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Comment: " + rd.comment + "  MessageId: " + rd.messageId + " User: " + rd.userId);
                    }
                }
                else if (currentTable == 2)
                {
                    ArrayList<Database.UserRow> res = db.selectAll3();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.UserRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Name: " + rd.firstName + " " + rd.lastName + " email: " + rd.email + " User Id: " + rd.userId);
                    }
                }
                else if (currentTable == 3)
                {
                    ArrayList<Database.LikesRow> res = db.selectAll4();
                    if (res == null)
                        continue;
                    System.out.println("  Current Database Contents");
                    System.out.println("  -------------------------");
                    for (Database.LikesRow rd : res) {
                        System.out.println("  [ID # " + rd.id + "]" + "  Liked: " + rd.liked + " Disliked: " + rd.disliked + " Message Id: " + rd.messageId + " User Id: " + rd.userId);
                    }
                }
            } else if (action == '-') {
                if (currentTable == 0)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                }
                else if (currentTable == 1)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow2(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                }
                else if (currentTable == 2)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow3(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                }
                else if (currentTable == 3)
                {
                    int id = getInt(in, "Enter the row ID");
                    if (id == -1)
                        continue;
                    int res = db.deleteRow4(id);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows deleted");
                }
            } else if (action == '+') {
                if (currentTable == 0)
                {
                    String message = getString(in, "Enter the message");
                    int userId = getInt(in, "Enter the user id");
                    if (message.equals(""))
                        continue;
                    int res = db.insertRow(message,userId);
                    System.out.println(res + " rows added");
                    }
                else if (currentTable == 1)
                {
                    String message = getString(in, "Enter the Comment");
                    int messageId = getInt(in, "Enter the messageId");
                    int userId = getInt(in, "Enter the user id");
                    if (message.equals(""))
                        continue;
                    int res = db.insertRow2(message,messageId,userId);
                    System.out.println(res + " rows added");
                }
                else if (currentTable == 2)
                {
                    String firstName = getString(in, "Enter the First Name");
                    String lastName = getString(in, "Enter the Last Name");
                    String email = getString(in, "Enter the email");
                    String userId = getString(in, "Enter the user Id");
                    //if (message.equals(""))
                        //continue;
                    int res = db.insertRow3(firstName,lastName,email,userId);
                    System.out.println(res + " rows added");
                }
                else if (currentTable == 3)
                {
                    int messageId = getInt(in, "Enter the Message ID");
                    int userId = getInt(in, "Enter the User ID");
                    //if (message.equals(""))
                        //continue;
                    int res = db.insertRow4(messageId, userId);
                    System.out.println(res + " rows added");
                }
            } else if (action == '~') {
                if (currentTable == 0)
                {
                    int id = getInt(in, "Enter the row ID :> ");
                    if (id == -1)
                        continue;
                    String newMessage = getString(in, "Enter the new message");
                    int res = db.updateOne(id, newMessage);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows updated");
                }
                else if (currentTable == 1)
                {
                    int id = getInt(in, "Enter the row ID :> ");
                    if (id == -1)
                        continue;
                    String newMessage = getString(in, "Enter the new comment");
                    int res = db.updateOne2(id, newMessage);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows updated");
                }
                else if (currentTable == 1)
                {
                    int id = getInt(in, "Enter the row ID :> ");
                    if (id == -1)
                        continue;
                    String newemail = getString(in, "Enter the new email");
                    int res = db.updateOne3(id, newemail);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows updated");
                }
                else if (currentTable == 3)
                {
                    int id = getInt(in, "Enter the row ID :> ");
                    if (id == -1)
                        continue;
                    int liked = getInt(in, "Enter if is is disliked or liked (0 or 1 respectively)");
                    int res = db.updateOne4(id, liked);
                    if (res == -1)
                        continue;
                    System.out.println("  " + res + " rows updated");
                }
            }

        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }
}