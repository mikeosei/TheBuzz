package edu.lehigh.cse216.grw224.backend;

import java.util.*;
// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
//import java.util.HashTable;
import java.util.Map;

// Import GoogleIdToken libraries

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
/**/
import java.util.Random;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {
    static HashMap<String, String> table = new HashMap<String, String>();
    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    /**
     * Get an integer environment varible if it exists, and otherwise return the
     * default value.
     * 
     * @envar The name of the environment variable to get.
     * @defaultVal The integer value to use as the default if envar isn't found
     * 
     * @returns The best answer we could come up with for a value for envar
     */
    static int getIntFromEnv(String envar, int defaultVal) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get(envar) != null) {
            return Integer.parseInt(processBuilder.environment().get(envar));
        }
        return defaultVal;
    }

    public static boolean lehighEmailCheck(String email) {
        int n = 11;
        String lastNchars = email.substring(email.length() - n);
        if (email.equals("@lehigh.edu")) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));

        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null)
            return;

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        //
        // NB: it must be final, so that it can be accessed from our lambdas
        //
        // NB: Gson is thread-safe. See
        // https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse
        final Gson gson = new Gson();

        // dataStore holds all of the data that has been provided via HTTP
        // requests
        //
        // NB: every time we shut down the server, we will lose all data, and
        // every time we start the server, we'll have an empty dataStore,
        // with IDs starting over from 0.
        final Database dataStore = Database.getDatabase(db_url);

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        } else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        // Set up a route for serving the main page
        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET route that returns all message titles and Ids. All we do is get
        // the data, embed it in a StructuredResponse, turn it into JSON, and
        // return it. If there's no data, we return "[]", so there's no need
        // for error handling.
        Spark.get("/messages", (request, response) -> {

            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);

            // try is used to retrieve userId and sessionId of current client
            // if the user is not logged in then an exception will be raised
            try {
                // retrieve userId of current client
                String userId = request.headers("userId");
                // retrieve sessionId of current client
                String sessionId = request.headers("sessionId");

                // checks to see if session of a given client exists in hashtable
                // if statement - takes the userId of the client and checks the hashtable
                // to see if the specific client is has an existing sessionId.
                // else- indicates that sessionId is for a different client instead of current
                if (table.get(userId).equals(sessionId)) {
                    return gson.toJson(new StructuredResponse("ok", null, dataStore.readAll()));
                } else {
                    response.redirect("/index.html");
                    return gson.toJson(new StructuredResponse("error", "sessionId is invalid", null));
                }

            } catch (Exception e) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error",
                        "user cannot access current route without being logged in", null));
            }

        });

        // GET route that returns everything for a single row in the DataStore.
        // The ":id" suffix in the first parameter to get() becomes
        // request.params("id"), so that we can get the requested row ID. If
        // ":id" isn't a number, Spark will reply with a status 500 Internal
        // Server Error. Otherwise, we have an integer, and the only possible
        // error is that it doesn't correspond to a row with data.
        Spark.get("/messages/:id", (request, response) -> {
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            DataRow data = dataStore.readOne(idx);

            // try is used to retrieve userId and sessionId of current client
            // if the user is not logged in then an exception will be raised
            try {
                // retrieve userId of current client
                String userId = request.headers("userId");
                // retrieve sessionId of current client
                String sessionId = request.headers("sessionId");

                // checks to see if session of a given client exists in hashtable
                // if statement - takes the userId of the client and checks the hashtable
                // to see if the specific client is has an existing sessionId.
                // else- indicates that sessionId is for a different client instead of current
                if (table.get(userId).equals(sessionId)) {
                    if (data == null) {
                        return gson.toJson(new StructuredResponse("error", idx + " not found", null));
                    } else {
                        return gson.toJson(new StructuredResponse("ok", null, data));
                    }
                } else {
                    response.redirect("/index.html");
                    return gson.toJson(new StructuredResponse("error", "sessionId is invalid", null));
                }

            } catch (Exception e) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error",
                        "user cannot access current route without being logged in", null));
            }

        });

        // POST route for adding a new element to the DataStore. This will read
        // JSON from the body of the request, turn it into a SimpleRequest
        // object, extract the title and message, insert them, and return the
        // ID of the newly created row.
        Spark.post("/messages", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            // describes the error.
            response.status(200);
            response.type("application/json");
            // NB: createEntry checks for null title and message
            int newId = dataStore.createEntry(req.mTitle, req.mMessage);
            if (newId == -1) {
                return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", "" + newId, null));
            }
        });

        // PUT route for updating a row in the DataStore. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            DataRow result = dataStore.updateOne(idx, req.mMessage);
            if (result == null) {
                return gson.toJson(new StructuredResponse("error", "unable to update row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, result));
            }
        });

        // DELETE route for removing a row from the DataStore
        Spark.delete("/messages/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: we won't concern ourselves too much with the quality of the
            // message sent on a successful delete
            boolean result = dataStore.deleteOne(idx);
            if (!result) {
                return gson.toJson(new StructuredResponse("error", "unable to delete row " + idx, null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, null));
            }
        });

        Spark.post("/login", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            // describes the error.
            response.status(200);
            response.type("application/json");
            


            String CLIENT_ID = "585478383264-j9obqp66iqsied7br8n9c1a17b8l6ptd.apps.googleusercontent.com";
/*
            String transport = "https://accounts.google.com/o/oauth2/auth?redirect_u" +
            "ri=https%3A%2F%2Fserene-gorge-86582.herokuapp.com&response_type=code&client_id=" +
            CLIENT_ID+"&scope=https%3A%2F%2Fmail.google.com%2F&approval_prompt=force";
*/
            
            Random rand = new Random();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    // Or, if multiple clients access the backend:
                    // .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();

            // (Receive idTokenString by HTTPS POST)
            String idTokenString = request.headers("access_token");
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");

                // Use or store profile information
                // possible syntax for adding to database: boolean result =
                // dataStore.storeEmail(email);

                if (lehighEmailCheck(email)) {
                    if (!table.containsKey(userId)) {
                        // generate random number between 1 and 10000 for sessionId
                        String sessionId = new Integer(rand.nextInt(10000)).toString();
                        table.put(userId, sessionId);
                        // send userId and sessionId for current client to front end
                        response.header("userId", userId);
                        response.header("sessionId", sessionId.toString());
                        return gson.toJson(new StructuredResponse("ok",
                                "Session id has been create for userID : " + userId, null));
                    }
                    return gson.toJson(new StructuredResponse("ok",
                            "Session id for userId " + userId + " is " + table.get(userId), null));

                } else {
                    return gson.toJson(new StructuredResponse("error", email + " :is not a Lehigh email", null));
                }

                //return gson.toJson(new StructuredResponse("ok", "Integrity of id token is verified for" + email, null));
            } else {
                return gson.toJson(new StructuredResponse("error", "Invalid ID token for email" , null));
            }

        });

        

    }
}