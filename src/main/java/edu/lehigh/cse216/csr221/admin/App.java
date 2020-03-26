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

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        // Commands for tblData
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific row of tblData");
        System.out.println("  [*] Query for all rows of tblData");
        System.out.println("  [-] Delete a row of tblData");
        System.out.println("  [+] Insert a new row of tblData");
        System.out.println("  [~] Update a row of tblData");
        // Commands for tblData2
        System.out.println("  [Y] Create tblData2");
        System.out.println("  [F] Drop tblData2");
        System.out.println("  [2] Query for a specific row of tblData2");
        System.out.println("  [#] Query for all rows of tblData2");
        System.out.println("  [_] Delete a row of tblData");
        System.out.println("  [=] Insert a new row of tblData");
        System.out.println("  [`] Update a row of tblData");
        // General Commands
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
        String actions = "TD1*-+~YF2#_=`q?";

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
            // Commands for tblData
            else if (action == 'T') {
                db.createTable();
            } else if (action == 'D') {
                db.dropTable();
            } else if (action == '1') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.RowData res = db.selectOne(id);
                if (res != null) {
                    System.out.println("  [" + res.id + "] ");
                    System.out.println("  --> " + res.message);
                }
            } else if (action == '*') {
                ArrayList<Database.RowData> res = db.selectAll();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.RowData rd : res) {
                   System.out.println("  [ID # " + rd.id + "]" + "  Message: " + rd.message);
                }
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.deleteRow(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String message = getString(in, "Enter the message");
                if (message.equals(""))
                    continue;
                int res = db.insertRow(message);
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new message");
                int res = db.updateOne(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
            // Commands for tblData2
            else if (action == 'Y') {
                db.createTable2();
            } else if (action == 'F') {
                db.dropTable2();
            } else if (action == '2') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                Database.RowData2 res = db.selectOne2(id);
                if (res != null) {
                    System.out.println("  [" + res.id + "] ");
                    System.out.println("  --> " + res.comment);
                }
            } else if (action == '#') {
                ArrayList<Database.RowData2> res = db.selectAll2();
                if (res == null)
                    continue;
                System.out.println("  Current Database Contents");
                System.out.println("  -------------------------");
                for (Database.RowData2 rd : res) {
                   System.out.println("  [ID # " + rd.id + "]" + "  Comment: " + rd.comment + "  MessageId: " + rd.messageId);
                }
            } else if (action == '_') {
                int id = getInt(in, "Enter the row ID");
                if (id == -1)
                    continue;
                int res = db.deleteRow2(id);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows deleted");
            } else if (action == '=') {
                String message = getString(in, "Enter the Comment");
                int messageId = getInt(in, "Enter the messageId");
                if (message.equals(""))
                    continue;
                int res = db.insertRow2(message,messageId);
                System.out.println(res + " rows added");
            } else if (action == '`') {
                int id = getInt(in, "Enter the row ID :> ");
                if (id == -1)
                    continue;
                String newMessage = getString(in, "Enter the new comment");
                int res = db.updateOne2(id, newMessage);
                if (res == -1)
                    continue;
                System.out.println("  " + res + " rows updated");
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }
}