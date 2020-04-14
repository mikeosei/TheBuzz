package edu.lehigh.cse216.grw224.backend;

// Import the Spark package, so that we can make use of the "get" function to 
// create an HTTP GET route
import com.google.api.services.sqladmin.SQLAdmin;
import spark.Spark;

// Import Google's JSON library
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

// Import GoogleIdToken libraries
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.util.Random;
import java.util.*;
import org.apache.commons.io.FileUtils;

// Import OAuth 2 libraries for phase 3
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sqladmin.SQLAdminScopes;

// Import classes for memcachier
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;
import java.lang.InterruptedException;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * For now, our app creates an HTTP server that can only get and add data.
 */
public class App {

    // static HashMap<String, String> table = new HashMap<String, String>();
    
    // This is what we will now use instead of the HashMap
    private static MemcachedClient mc;

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    // Variable for the DriveQuickstart.java class
    private static DriveQuickstart drive = new DriveQuickstart();

    public App() throws IOException {
    }

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

    // Check if the user email trying to access our app is of the lehigh.edu domain
    static boolean lehighEmailCheck(String email) {
        int n = 11;
        String lastNchars = email.substring(email.length() - n);
        if (lastNchars.equals("@lehigh.edu")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set up CORS headers for the OPTIONS verb, and for every response that the
     * server sends. This only needs to be called once.
     * 
     * @param origin  The server that is allowed to send requests to this server
     * @param methods The allowed HTTP verbs from the above origin
     * @param headers The headers that can be sent with a request from the above
     *                origin
     */
    private static void enableCORS(String origin, String methods, String headers) {
        // Create an OPTIONS route that reports the allowed CORS headers and methods
        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 'before' is a decorator, which will run before any
        // get/post/put/delete. In our case, it will put three extra CORS
        // headers into the response
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    public static void main(String[] args) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String db_url = env.get("DATABASE_URL");
        System.out.println(db_url);

        // Get the port on which to listen for requests
        Spark.port(getIntFromEnv("PORT", 4567));

        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(db_url);
        if (db == null) {
            return;
        }

        /**
        * Implementing memcachier
        * https://blog.memcachier.com/2018/06/11/switch-to-xmemcached/
         */
        List<InetSocketAddress> servers = AddrUtil.getAddresses(env.get("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(env.get("MEMCACHIER_USERNAME"), env.get("MEMCACHIER_PASSWORD"));
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);
        // Configure SASL auth for each server
        for(InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }
        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);
        try {
            mc = builder.build();
            try {
                mc.set("foo", 0, "bar");
                String val = mc.get("foo");
                System.out.println(val);
            }
            catch (TimeoutException te) {
                System.err.println("Timeout during set or get: " + te.getMessage());
            }
            catch (InterruptedException ie) {
                System.err.println("Interrupt during set or get: " + ie.getMessage());
            }
            catch (MemcachedException me) {
                System.err.println("Memcached error during get or set: " + me.getMessage());
            }
        }
        catch (IOException ioe) {
            System.err.println("Couldn't create a connection to Memcached server: " + ioe.getMessage());
        }

        // gson provides us with a way to turn JSON into objects, and objects
        // into JSON.
        // NB: it must be final, so that it can be accessed from our lambdas
        // NB: Gson is thread-safe
        final Gson gson = new Gson();

        // database holds all of the data that has been provided via HTTP
        // requests
        // NB: every time we shut down the server, we will lose all data, and
        // every time we start the server, we'll have an empty database,
        // with IDs starting over from 0.
        final Database database = Database.getDatabase(db_url);

        // client_ids used for login route
        final String CLIENT_ID_WEB = "721614654195-cofg349cqbl2q6kajjojhiuvpqc51gp4.apps.googleusercontent.com";
        final String CLIENT_ID_ANDROID1 = "721614654195-ul6l58hkacnsvage9okikdr9r0pklmh0.apps.googleusercontent.com";

        // Set up the location for serving static files. If the STATIC_LOCATION
        // environment variable is set, we will serve from it. Otherwise, serve
        // from "/web"
        String static_location_override = System.getenv("STATIC_LOCATION");
        if (static_location_override == null) {
            Spark.staticFileLocation("/web");
        }
        else {
            Spark.staticFiles.externalLocation(static_location_override);
        }

        String cors_enabled = env.get("CORS_ENABLED");
        if (cors_enabled.equals("True")) {
            final String acceptCrossOriginRequestsFrom = "*";
            final String acceptedCrossOriginRoutes = "GET,PUT,POST,DELETE,OPTIONS";
            final String supportedRequestHeaders = "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin";
            enableCORS(acceptCrossOriginRequestsFrom, acceptedCrossOriginRoutes, supportedRequestHeaders);
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
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // checks to see if session of a given client exists in hashtable
            // if - takes the userId of the client and checks the hashtable
            // to see if the specific client has an existing sessionId.
            // else - indicates that sessionId is for a different client instead of current
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                return gson.toJson(new StructuredResponse("ok", null, database.selectAll()));
            }
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // GET route that returns everything for a single row in the Database.
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
            Database.MessageRow data = database.selectOne(idx);
            if (data == null) {
                return gson.toJson(new StructuredResponse("error", idx + " not found", null));
            } else {
                return gson.toJson(new StructuredResponse("ok", null, data));
            }
        });

        // POST route for adding a new element to the Database. This will read
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

            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int id = database.get_Id(userId);

            // checks to see if session of a given client exists in hashtable
            // if - takes the userId of the client and checks the hashtable
            // to see if the specific client has an existing sessionId.
            // else - indicates that sessionId is for a different client instead of current
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error",
                        "Could not POST userId & sessionId are null: Client has not logged in", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                int row = database.insertRow(req.mMessage, id);
                if (row == 0) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("ok", "Database has inserted " + row + " row(s)", null));
                }
            } 
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + ". Session may have expired", null));
            }

        });

        // PUT route for updating a row in the Database. This is almost
        // exactly the same as POST
        Spark.put("/messages/:id", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // checks to see if session of a given client exists in hashtable
            // if - takes the userId of the client and checks the hashtable
            // to see if the specific client has an existing sessionId.
            // else - indicates that sessionId is for a different client instead of current
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                int result = database.updateOne(idx, req.mMessage);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "Could not PUT unable to update for id  " + idx, null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("ok", "Changes have been made ", result));
                }
            } 
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // PUT route for increasing the likes for a row in the Database
        Spark.put("/messages/:id/like", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int secure;
            // if - checks to see if session of a given client exists in hashtable
            // else if - like a message that the user has not currently liked or dislike
            // else - update message like to reflect status
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                // get database id of userId
                int uId = database.get_Id(userId);
                Database.MessageRow data = database.selectOne(idx);
                int messageId = database.get_Message_Id(data.message);
                int likeId = database.getLikeId(uId,messageId);
                Database.LikesRow likesRow = database.selectOne4(likeId);
                if (likesRow == null) {
                    // updates like in the message table
                    int insertResult = database.updateOne(messageId, data.message, 1, 0);
                    // inserts a row in like table with like=0,dislike=0
                    database.insertRow4(messageId, uId);
                    //likeId of new like row
                    likeId = database.getLikeId(uId,messageId);
                    // updates the like table newly created default row to reflect the like we just made
                    int updateResult = database.updateOne4(likeId, 1);
                    if (insertResult == -1) {
                        return gson.toJson(new StructuredResponse("error",
                                "An error occured with an INSERTION into the messageTable", null));
                    }
                    if (updateResult == -1) {
                        return gson.toJson(new StructuredResponse("error",
                                "A error occured with an UPDATE to the likeTable", null));
                    }
                    return gson.toJson(new StructuredResponse("ok", "like has been updated in both messageTable and likeTable", null));
                } 
                else {
                    secure = database.secureVoting(messageId, 1, likeId);
                }
                if (secure == -1) {
                    return gson.toJson(new StructuredResponse("error", "Could not PUT unable to update row " + idx, null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("ok", "Successful PUT changes have been made     ", secure));
                }

            } 
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // PUT route for increasing the dislikes for a row in the Database
        Spark.put("/messages/:id/dislike", (request, response) -> {
            // If we can't get an ID or can't parse the JSON, Spark will send
            // a status 500
            int idx = Integer.parseInt(request.params("id"));
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int secure;
            // if - checks to see if session of a given client exists in hashtable
            // else if - like a message that the user has not currently liked or dislike &
            // update message's like to reflect status
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                // get database id of userId
                int uId = database.get_Id(userId);
                Database.MessageRow data = database.selectOne(idx);
                int messageId = database.get_Message_Id(data.message);
                int likeId = database.getLikeId(uId,messageId);
                Database.LikesRow likesRow = database.selectOne4(likeId);
                if (likesRow == null) {
                    // updates dislike in the message table
                    int insertResult = database.updateOne(messageId, data.message, 0, 1);
                    // inserts a row in like table with like like=0,dislike=0
                    database.insertRow4(messageId, uId);
                    //likeId of new like row
                    likeId = database.getLikeId(uId,messageId);
                    // updates the like table newly created default row to reflect the dislike
                    int updateResult = database.updateOne4(likeId, 0);
                    if (insertResult == -1) {
                        return gson.toJson(new StructuredResponse("error", "An error occured with an inserion into the messageTable", null));
                    }
                    if (updateResult == -1) {
                        return gson.toJson(new StructuredResponse("error", "A error occured with an update to the likeTable", null));
                    }
                    return gson.toJson(new StructuredResponse("ok", "like has been updated in both messageTable and likeTable", null));
                } 
                else {
                    secure = database.secureVoting(messageId, 0, likeId);
                }
                if (secure == -1) {
                    return gson.toJson(new StructuredResponse("error", "Could not PUT unable to update row " + idx, null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("ok", "Successful PUT changes have been made     ", secure));
                }
            } 
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // DELETE route for removing a row from the Database
        Spark.delete("/messages/:id", (request, response) -> {
            // If we can't get an ID, Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // if - checks to see if session of a given client exists in hashtable
            // else if - deletion of message
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                // NB: we won't concern ourselves too much with the quality of the
                // message sent on a successful delete
                // int id = database.get_Id(userId);
                int result = database.deleteRow(idx);
                if (result == -1) {
                    return gson.toJson(new StructuredResponse("error", "Could not DELETE unable to delete row " + idx, null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("ok", "Row deleted for id     ", idx));
                }

            } 
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // login route
        Spark.post("/login", (request, response) -> {
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // ensure status 200 OK, with a MIME type of JSON
            // NB: even on error, we return 200, but with a JSON object that
            // describes the error.
            response.status(200);
            response.type("application/json");
            // random number representing sessionID
            Random rand = new Random();
            // Specify the CLIENT_ID of the app that accesses the backend:
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).setAudience(Arrays.asList(CLIENT_ID_WEB, CLIENT_ID_ANDROID1)).build();
            // (Receive idTokenString by HTTPS POST)
            String idTokenString = request.queryParams("access_token");
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
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                // if - verifies lehigh email
                // else - not lehigh email
                if (lehighEmailCheck(email)) {
                    // generate random number between 1 and 10000 for sessionId
                    String sessionId = new Integer(rand.nextInt(10000)).toString();
                    mc.set(userId, 0, sessionId);

                    // send userId and sessionId for current client to front end
                    response.body(userId);
                    response.body(sessionId);
                    database.insertRow3(name, familyName, email, userId);
                    return gson.toJson(new StructuredResponse("ok", "Session id has been create for userID : " + userId, null));
                } 
                else {
                    return gson.toJson(new StructuredResponse("error", email + " :is not a Lehigh email", null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Invalid token:" + idTokenString, null));
            }
        }); 

        // route to create a comment
        Spark.post("/comment/:id", (request, response) -> {
            // If we can't get an ID,
            // Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: if gson.Json fails, Spark will reply with status 500 Internal Server
            // Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int id = database.get_Id(userId);
            // if - checks to see if session of a given client exists in hashtable
            // else if - create comment
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                int row = database.insertRow2(req.mComment, idx, id);
                if (row == 0) {
                    return gson.toJson(new StructuredResponse("error", "error performing insertion", null));
                }
                else {
                    return gson.toJson(new StructuredResponse("ok", "Database has inserted  " + row + "   row(s)", null));
                }
            }
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // route to get all comment for a specific messageID
        Spark.get("/comment/:id", (request, response) -> {
            // If we can't get an ID,Spark will send a status 500
            int idx = Integer.parseInt(request.params("id"));
            // ensure status 200 OK, with a MIMEtype of JSON
            response.status(200);
            response.type("application/json");
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // if - checks to see if session of a given client exists in hashtable
            // else if - retrieve all comment
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                // Database.CommentRow comments
                return gson.toJson(new StructuredResponse("ok", null, database.selectAll2(idx)));
            }
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // route to edit comment
        Spark.put("/comment/:id/:cid", (request, response) -> {
            // If we can't get an ID, Spark will send a status500
            int idx = Integer.parseInt(request.params("id"));
            int cidx = Integer.parseInt(request.params("cid"));
            // ensure status 200OK, with a MIME type of JSON
            response.status(200);
            response.type("application/json");
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // retrieve userId & sessionId of // current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // if - checks to see if session of a given client exists in hashtable
            // else if - edit comment
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                // Database.CommentRow comments
                return gson.toJson(new StructuredResponse("ok", null, database.updateOne2(cidx, req.mComment)));
            }
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // route to get profile information
        Spark.get("/profile", (request, response) -> {
            // If we can't get an ID,Spark will send a status 500
            // ensure status 200 OK, with a MIMEtype of JSON
            response.status(200);
            response.type("application/json");
            // NB: if gson.Json fails, Spark will reply with status 500 Internal
            // Server Error
            SimpleRequest req = gson.fromJson(request.body(), SimpleRequest.class);
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            // if - checks to see if session of a given client exists in hashtable
            // else if - retreive profile information
            // else - session expire
            if (mc.get(userId) == null) {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            } 
            else if (mc.get(userId).equals(sessionId)) {
                int id = database.get_Id(userId);
                return gson.toJson(new StructuredResponse("ok", null, database.selectOne3(id)));
            }
            else {
                response.redirect("/index.html");
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid:   " + sessionId + "   Session may have expired", null));
            }
        });

        // route to post images
        Spark.post("/images/:id", (request, response) -> {
            // This is a slightly modified class from SimpleRequest that we will use for files
            UploadSimpleRequest req = gson.fromJson(request.body(), UploadSimpleRequest.class);
            // If we can't get an ID,Spark will send a status 500
            // ensure status 200 OK, with a MIMEtype of JSON
            response.status(200);
            response.type("application/json");
            int idx = Integer.parseInt(request.params("id"));
            // retrieve userId & sessionId of current client
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int id = database.get_Id(userId);
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null: client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                byte[] imageBytes;
                try {
                    imageBytes = Base64.getDecoder().decode(req.uploadData);
                    java.io.File fileToInsert = new java.io.File(req.uploadName);
                    FileUtils.writeByteArrayToFile(fileToInsert, imageBytes);
                    String newFileId = DriveQuickstart.insertFile(DriveQuickstart.getDrive(), req.uploadName, fileToInsert, "image/png");
                    int result1 = db.insertRow5(id, idx, newFileId);
                    if (result1 == -1) {
                        return gson.toJson(new StructuredResponse("error", "unable to update row after file " + idx, null));
                    }
                    return gson.toJson(new StructuredResponse("ok", "" + newFileId, null));
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return gson.toJson(new StructuredResponse("error", "error performing image file insertion", null));
                }
            } 
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

        // If a message has a file attached, this route will obtain it from Drive and then will return the encoded string for frontend
        Spark.get("/images/:id/:fId", (request,response) -> {
            // If we can't get an ID,Spark will send a status 500
            // ensure status 200 OK, with a MIMEtype of JSON
            response.status(200);
            response.type("application/json");
            String idx = request.params("id");
            String fIdx = request.params("fId");
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null: client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                try {
                    ByteArrayOutputStream targetStream = DriveQuickstart.getFile(DriveQuickstart.getDrive(), fIdx);
                    byte[] tempBytes = targetStream.toByteArray();
                    String encodedString = Base64.getEncoder().encodeToString(tempBytes);
                    App.UploadData newRequest = new App.UploadData(fIdx, encodedString);
                    return gson.toJson(new StructuredResponse("ok", null, newRequest));
                }
                catch(Exception e) {
                    return gson.toJson(new StructuredResponse("ok", "error obtaining file of interest", null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

        // route to delete a file
        Spark.delete("/images/:id/:fId", (request,response) -> {
            int idx = Integer.parseInt(request.params("id"));
            String fileId = request.params("fId");
            // If we can't get an ID,Spark will send a status 500
            // ensure status 200 OK, with a MIMEtype of JSON
            response.status(200);
            response.type("application/json");
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null: client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                try {
                    boolean result1 = DriveQuickstart.deleteFile(DriveQuickstart.getDrive(), fileId);
                    if (result1 == false) {
                        return gson.toJson(new StructuredResponse("error", "error deleting file at id " + fileId, null));
                    }
                    else {
                        int output = db.deleteRow5(idx);
                        if (output == -1) {
                            return gson.toJson(new StructuredResponse("error", "unable to update row after file " + idx, null));
                        }
                    return gson.toJson(new StructuredResponse("ok", "image file deleted" , null));
                    }
                }
                catch(Exception e) {
                    return gson.toJson(new StructuredResponse("error", "error deleting file at id " + idx, null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

        // route to post links/urls
        Spark.post("/links/:id", (request, response) -> {
            UploadSimpleRequest req = gson.fromJson(request.body(), UploadSimpleRequest.class);
            response.status(200);
            response.type("application/json");
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            int id = database.get_Id(userId);
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null: client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                try {
                    String textToInsert = req.uploadData;
                    byte[] imageBytes = textToInsert.getBytes();
                    java.io.File fileToInsert =  new java.io.File(req.uploadName);
                    FileUtils.writeByteArrayToFile(fileToInsert, imageBytes);
                    String newFileId = DriveQuickstart.insertFile(DriveQuickstart.getDrive(), req.uploadName, fileToInsert, "text/plain");
                    int idx = Integer.parseInt(request.params("id"));
                    int result1 = db.insertRow5(id, idx, newFileId);
                    if (result1 == -1) {
                        return gson.toJson(new StructuredResponse("error", "unable to update row after text file " + idx, null));
                    }
                return gson.toJson(new StructuredResponse("ok", "" + newFileId, null));
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return gson.toJson(new StructuredResponse("error", "error performing text file insertion", null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

        // If a message has a link attached, this route will obtain it from Drive and then will return the encoded string for frontend
        Spark.get("/links/:id/:fId", (request,response) -> {
            String idx = request.params("fId");
            response.status(200);
            response.type("application/json");
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null:   client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                try{
                    ByteArrayOutputStream targetStream = DriveQuickstart.getFile(DriveQuickstart.getDrive(), idx);
                    byte[] tempBytes = targetStream.toByteArray();
                    String dataString = new String(tempBytes);
                    App.UploadData newRequest = new App.UploadData(idx, dataString);
                    return gson.toJson(new StructuredResponse("ok", null, newRequest));
                }
                catch(Exception e) {
                    return gson.toJson(new StructuredResponse("ok", "error obtaining file of interest", null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

        // route to delete a link
        Spark.delete("/links/:id/:fId", (request,response) -> {
            int idx = Integer.parseInt(request.params("id"));
            String fileId = request.params("fId");
            response.status(200);
            response.type("application/json");
            String userId = request.queryParams("userId");
            String sessionId = request.queryParams("sessionId");
            if (mc.get(userId) == null) {
                return gson.toJson(new StructuredResponse("error", "userId & sessionId are null: client has not login", null));
            }
            else if (mc.get(userId).equals(sessionId)) {
                try {
                    boolean result1 = DriveQuickstart.deleteFile(DriveQuickstart.getDrive(), fileId);
                    if (result1 == false) {
                        return gson.toJson(new StructuredResponse("error", "error deleting text file at id "+ fileId, null));
                    }
                    else {
                        int output = db.deleteRow5(idx);
                        if (output == -1) {
                            return gson.toJson(new StructuredResponse("error", "unable to update row after file " + idx, null));
                        }
                        return gson.toJson(new StructuredResponse("ok", "text file deleted" , null));
                    }
                }
                catch(Exception e) {
                    return gson.toJson(new StructuredResponse("error", "error deleting file at id "+ idx, null));
                }
            }
            else {
                return gson.toJson(new StructuredResponse("error", "Could not POST sessionId is invalid: " + sessionId + " Session may have expired", null));
            }
        });

    } //End of main method

    /**
     * Static class for upload file/URL structures
     */
    public static class UploadData {

        /**
         * File name including extension such as "File.pdf"
         */

        public String uploadName;

        /**
         * Upload data as base 64 string
         */

        public String uploadData;

        public UploadData(String nameOfUpload, String dataOfUpload){
            uploadName = nameOfUpload;
            uploadData = dataOfUpload;
        }

    }

}