package edu.lehigh.cse216.grw224.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.Timestamp;

public class Database {

	/**
	 * The connection to the database.	When there is no connection, it should
	 * be null.  Otherwise, there is a valid open connection
	 */
	private Connection mConnection;

	/**
	 * A prepared statement for getting all data in the message database
	 */
	private PreparedStatement mSelectAll;

	/**
	 * A prepared statement for getting one row from the message database
	 */
	private PreparedStatement mSelectOne;

	/**
	 * A prepared statement for deleting a row from the message database
	 */
	private PreparedStatement mDeleteOne;

	/**
	 * A prepared statement for inserting into the message database
	 */
	private PreparedStatement mInsertOne;

	/**
	 * A prepared statement for updating a single row in the message database
	 */
	private PreparedStatement mUpdateOne;

	/**
	 * A prepared statement for creating the table in our message database
	 */
	private PreparedStatement mCreateTable;

	/**
	 * A prepared statement for dropping the table in our message database
	 */
	private PreparedStatement mDropTable;

	/**
	 * A prepared statement for updating the table in our message database to account for secure voting
	 */
	private PreparedStatement getMessageId;

	/**
	 * A prepared statement for getting all data in the comment database
	 */
	private PreparedStatement mSelectAll2;

	/**
	 * A prepared statement for getting one row from the comment database
	 */
	private PreparedStatement mSelectOne2;

	/**
	 * A prepared statement for deleting a row from the comment database
	 */
	private PreparedStatement mDeleteOne2;

	/**
	 * A prepared statement for inserting into the comment database
	 */
	private PreparedStatement mInsertOne2;

	/**
	 * A prepared statement for updating a single row in the comment database
	 */
	private PreparedStatement mUpdateOne2;

	/**
	 * A prepared statement for creating the table in our comment database
	 */
	private PreparedStatement mCreateTable2;

	/**
	 * A prepared statement for dropping the table in our comment database
	 */
    private PreparedStatement mDropTable2;
    
    /**
	 * A prepared statement for dropping the table in our comment database
	 */
	private PreparedStatement mSelectMessage2;

	/**
	 * A prepared statement for getting all data in the user database
	 */
	private PreparedStatement mSelectAll3;

	/**
	 * A prepared statement for getting one row from the user database
	 */
	private PreparedStatement mSelectOne3;

	/**
	 * A prepared statement for deleting a row from the user database
	 */
	private PreparedStatement mDeleteOne3;

	/**
	 * A prepared statement for inserting into the user database
	 */
	private PreparedStatement mInsertOne3;

	/**
	 * A prepared statement for updating a single row in the user database
	 */
	private PreparedStatement mUpdateOne3;

	/**
	 * A prepared statement for creating the table in our user database
	 */
	private PreparedStatement mCreateTable3;

	/**
	 * A prepared statement for dropping the table in our user database
	 */
    private PreparedStatement mDropTable3;
    
    /**
	 * A prepared statement for dropping the table in our user database
	 */
	private PreparedStatement getId;

	/**
	 * A prepared statement for getting all data in the likes/dislikes database
	 */
	private PreparedStatement mSelectAll4;

	/**
	 * A prepared statement for getting one row from the likes/dislikes database
	 */
	private PreparedStatement mSelectOne4;

	/**
	 * A prepared statement for deleting a row from the likes/dislikes database
	 */
	private PreparedStatement mDeleteOne4;

	/**
	 * A prepared statement for inserting into the likes/dislikes database
	 */
	private PreparedStatement mInsertOne4;

	/**
	 * A prepared statement for updating a single row in the likes/dislikes database
	 */
	private PreparedStatement mUpdateOne4;

	/**
	 * A prepared statement for creating the table in our likes/dislikes database
	 */
	private PreparedStatement mCreateTable4;

	/**
	 * A prepared statement for dropping the table in our likes/dislikes database
	 */
	private PreparedStatement mDropTable4;

	/**
	 * A prepared statement for getting all data in the user database
	 */
	private PreparedStatement mSelectAll5;

	/**
	 * A prepared statement for getting one row from the user database
	 */
	private PreparedStatement mSelectOne5;

	/**
	 * A prepared statement for deleting a row from the user database
	 */
	private PreparedStatement mDeleteOne5;

	/**
	 * A prepared statement for inserting into the user database
	 */
	private PreparedStatement mInsertOne5;

	/**
	 * A prepared statement for updating a single row in the user database
	 */
	private PreparedStatement mUpdateOne5;

	/**
	 * A prepared statement for creating the table in our user database
	 */
	private PreparedStatement mCreateTable5;

	/**
	 * A prepared statement for dropping the table in our user database
	 */
	private PreparedStatement mDropTable5;

	/**
	 * A prepared statement for selecting the earliestDate of a Drive access
	 */
	private PreparedStatement mEarliestDate;

	/**
	 * A prepared statement for returning the id of a liketable row
	 */
	private PreparedStatement getLikeId;

	/**
	 * MessageRow is like a struct in C: we use it to hold data, and we allow 
	 * direct access to its fields.  In the context of this Database, MessageRow 
	 * represents the data we'd see in a row.
	 * We make MessageRow a static class of Database because we don't really want
	 * to encourage users to think of MessageRow as being anything other than an
	 * abstract representation of a row of the database.  MessageRow and the 
	 * Database are tightly coupled: if one changes, the other should too.
	 */
	public static class MessageRow {
		/**
		 * The ID of this row of the database
		 */
		int id;

		/**
		 * The message stored in this row
		 */
		String message;

		/**
		 * The number of likes stored in this row
		 */
		int likes;

		/**
		 * The number of dislikes stored in this row
		 */
		int dislikes;

		/**
		 * The ID of the user who posted this message
		 */
		int userId;

		/**
		 * Construct a MessageRow object by providing values for its fields
		 */
		public MessageRow(int id, String message, int likes, int dislikes, int userId) {
			this.id = id;
			this.message = message;
			this.likes = likes;
			this.dislikes = dislikes;
			this.userId = userId;
		}

	}

	/**
	 * CommentRow is the row data for comments
	 */
	public static class CommentRow {

		/**
		 * The ID of this row of the database
		 */
		int id;

		/**
		 * The comment stored in this row
		 */
		String comment;

		/**
		 * The ID of the message the comment is commenting on
		 */
		int messageId;

		/**
		 * The ID of the user who posted this comment
		 */
		int userId;

		/**
		 * Construct a CommentRow object by providing values for its fields
		 */
		public CommentRow(int id, String comment, int messageId, int userId) {
			this.id = id;
			this.comment = comment;
			this.messageId = messageId;
			this.userId = userId;
		}

	}

	/**
	 * UserRow is the row data for users
	 */
	public static class UserRow {

		/**
		 * The ID of this row of the database
		 */
		int id;

		/**
		 * The user's first name
		 */
		String firstName;

		/**
		 * The user's last name
		 */
		String lastName;

		/**
		 * The email of the user
		 */
		String email;

		/**
		 * The userId of the user (note: different than the userId used by the other tables)
		 */
		String userId;

		/**
		 * Construct a UserRow object by providing values for its fields
		 */
		public UserRow(int id, String firstName, String lastName, String email, String userId) {
			this.id = id;
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.userId = userId;
		}

	}

	/**
	 * LikesRow is the row data for likes/dislikes of a given user for a given message
	 */
	public static class LikesRow {

		/**
		 * The ID of this row of the database
		 */
		int id;

		/**
		 * If the message has been liked
		 * 0 = false, 1 = true
		 * Should not be true if disliked is true
		 */
		int liked;

		/**
		 * If the message has been disliked
		 * 0 = false, 1 = true
		 * Should not be true if liked is true
		 */
		int disliked;

		/**
		 * ID of the user who this liked/disliked row is refering to
		 */
		int userId;

		/**
		 * ID of the message who this liked/disliked row is refering to
		 */
		int messageId;

		/**
		 * Construct a LikesRow object by providing values for its fields
		 */
		public LikesRow(int id, int liked, int disliked, int messageId, int userId) {
			this.id = id;
			this.liked = liked;
			this.disliked = disliked;
			this.userId = userId;
			this.messageId = messageId;
		}

	}

	/**
	 * DriveRow is the row data for users
	 */
	public static class DriveRow {

		/**
		 * The ID of this row of the database
		 */
		int id;

		/**
		 * User posting file
		 */
		int userId;

		/**
		 * Message file belongs to
		 */
		int messageId;

		/**
		 * Name of file
		 */
		String fileName;

		/**
		 * Date file was created
		 */
		Timestamp accessDate;

		/**
		 * Construct a DriveRow object by providing values for its fields
		 */
		public DriveRow(int id, int userId, int messageId, String fileName,Timestamp accessDate) {
			this.id = id;
			this.userId = userId;
			this.messageId = messageId;
			this.fileName = fileName;
			this.accessDate = accessDate;
		}
	
	}

	/**
	 * The Database constructor is private: we only create Database objects 
	 * through the getDatabase() method.
	 */
	private Database() {

	}

	/**
	 * Get a fully-configured connection to the database
	 * 
	 * @param ip   The IP address of the database server
	 * @param port The port on the database server to which connection requests
	 *			   should be sent
	 * @param user The user ID to use when connecting
	 * @param pass The password to use when connecting
	 * 
	 * @return A Database object, or null if we cannot connect properly
	 */
	static Database getDatabase(String db_url){
		// Create an un-configured Database object
		Database db = new Database();

		// Give the Database object a connection, fail if we cannot get one
		try {
		Class.forName("org.postgresql.Driver");
		URI dbUri = new URI(db_url);
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()+"?sslmode=require";
		Connection conn = DriverManager.getConnection(dbUrl, username, password);
		if (conn == null) {
			System.err.println("Error: DriverManager.getConnection() returned a null object");
			return null;
		}
		db.mConnection = conn;
		} catch (SQLException e) {
			System.err.println("Error: DriverManager.getConnection() threw a SQLException");
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Unable to find postgresql driver");
			return null;
		} catch (URISyntaxException s) {
			System.out.println("URI Syntax Error");
			return null;
		}

		// Attempt to create all of our prepared statements.  If any of these 
		// fail, the whole getDatabase() call should fail
		try {
			// NB: we can easily get ourselves in trouble here by typing the
			//	   SQL incorrectly.  We really should have things like "tblData"
			//	   as constants, and then build the strings for the statements
			//	   from those constants.
			// Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table 
			// creation/deletion, so multiple executions will cause an exception

			// Statements for message table (tblData)
			db.mCreateTable = db.mConnection.prepareStatement("CREATE TABLE tblData (id SERIAL PRIMARY KEY, message VARCHAR(500) NOT NULL, likes INTEGER NOT NULL,dislikes INTEGER NOT NULL, userId INTEGER, FOREIGN KEY (userId) REFERENCES userTable(id))"); 
			db.mDropTable = db.mConnection.prepareStatement("DROP TABLE tblData");

			// Standard CRUD operations
			db.mDeleteOne = db.mConnection.prepareStatement("DELETE FROM tblData WHERE id = ?");
			db.mInsertOne = db.mConnection.prepareStatement("INSERT INTO tblData VALUES (default, ?, 0, 0, ?)");
			db.mSelectAll = db.mConnection.prepareStatement("SELECT * FROM tblData");
			db.mSelectOne = db.mConnection.prepareStatement("SELECT * from tblData WHERE id=?");
			db.mUpdateOne = db.mConnection.prepareStatement("UPDATE tblData SET message = ?, likes = likes + ?, dislikes = dislikes + ? WHERE id = ?");
			
			//backend implemented
            db.getMessageId = db.mConnection.prepareStatement("SELECT id from tblData WHERE message=?");

			// Statements for comment table (commentTable)
			db.mCreateTable2 = db.mConnection.prepareStatement("CREATE TABLE commentTable (id SERIAL PRIMARY KEY, comment VARCHAR(500) NOT NULL, messageId INTEGER NOT NULL, userId INTEGER NOT NULL, FOREIGN KEY (messageID) REFERENCES tblData(id) ON DELETE CASCADE, FOREIGN KEY (userId) REFERENCES userTable(id) ON DELETE CASCADE)"); 
			db.mDropTable2 = db.mConnection.prepareStatement("DROP TABLE commentTable");

			// Standard CRUD operations
			db.mDeleteOne2 = db.mConnection.prepareStatement("DELETE FROM commentTable WHERE id = ?");
			db.mInsertOne2 = db.mConnection.prepareStatement("INSERT INTO commentTable VALUES (default, ?, ?, ?)");
			db.mSelectAll2 = db.mConnection.prepareStatement("SELECT * FROM commentTable");
			db.mSelectOne2 = db.mConnection.prepareStatement("SELECT * from commentTable WHERE id=?");
			db.mUpdateOne2 = db.mConnection.prepareStatement("UPDATE commentTable SET comment = ? where id=?");
            
			//backend implemented operation
            db.mSelectMessage2= db.mConnection.prepareStatement("SELECT * from commentTable WHERE messageId=?");

			// Statements for user table (userTable)
			db.mCreateTable3 = db.mConnection.prepareStatement("CREATE TABLE userTable (id SERIAL PRIMARY KEY, firstName VARCHAR(500) NOT NULL, lastName VARCHAR(500) NOT NULL, email VARCHAR(500) NOT NULL, userId VARCHAR(500) NOT NULL)"); 
			db.mDropTable3 = db.mConnection.prepareStatement("DROP TABLE userTable");

			// Standard CRUD operations
			db.mDeleteOne3 = db.mConnection.prepareStatement("DELETE FROM userTable WHERE id = ?");
			db.mInsertOne3 = db.mConnection.prepareStatement("INSERT INTO userTable VALUES (default, ?, ?, ?, ?)");
			db.mSelectAll3 = db.mConnection.prepareStatement("SELECT * FROM userTable");
			db.mSelectOne3 = db.mConnection.prepareStatement("SELECT * from userTable WHERE id=?");
            db.mUpdateOne3 = db.mConnection.prepareStatement("UPDATE userTable SET email = ? where id=?");
            db.getId = db.mConnection.prepareStatement("SELECT id from userTable WHERE userId=?");

			// Statements for like/dislike table (likeTable)
			db.mCreateTable4 = db.mConnection.prepareStatement("CREATE TABLE likeTable (id SERIAL PRIMARY KEY, liked INTEGER NOT NULL, disliked INTEGER NOT NULL, messageId INTEGER NOT NULL, userId INTEGER NOT NULL, FOREIGN KEY (messageID) REFERENCES tblData(id) ON DELETE CASCADE, FOREIGN KEY (userId) REFERENCES userTable(id) ON DELETE CASCADE)");
			db.mDropTable4 = db.mConnection.prepareStatement("DROP TABLE likeTable");

			// Standard CRUD operations
			db.mDeleteOne4 = db.mConnection.prepareStatement("DELETE FROM likeTable WHERE id = ?");
			db.mInsertOne4 = db.mConnection.prepareStatement("INSERT INTO likeTable VALUES (default, -1, -1, ?, ?)");
			db.mSelectAll4 = db.mConnection.prepareStatement("SELECT * FROM likeTable");
			db.mSelectOne4 = db.mConnection.prepareStatement("SELECT * from likeTable WHERE id=?");
			db.mUpdateOne4 = db.mConnection.prepareStatement("UPDATE likeTable SET liked = ?, disliked = ? where id=?");
			db.getLikeId = db.mConnection.prepareStatement("SELECT id from likeTable WHERE userId=? AND messageId=?");

			// Statements for like/dislike table (likeTable)
			db.mCreateTable5 = db.mConnection.prepareStatement(
					"CREATE TABLE driveTable (id SERIAL PRIMARY KEY, userId INTEGER NOT NULL, messageId INTEGER NOT NULL, fileName VARCHAR(500) NOT NULL, accessDate TIMESTAMP NOT NULL, FOREIGN KEY (messageID) REFERENCES tblData(id) ON DELETE CASCADE, FOREIGN KEY (userId) REFERENCES userTable(id) ON DELETE CASCADE)");
			db.mDropTable5 = db.mConnection.prepareStatement("DROP TABLE driveTable");

			// Standard CRUD operations
			db.mDeleteOne5 = db.mConnection.prepareStatement("DELETE FROM driveTable WHERE id = ?");
			db.mInsertOne5 = db.mConnection.prepareStatement("INSERT INTO driveTable VALUES (default, ?, ?, ?, ?)");
			db.mSelectAll5 = db.mConnection.prepareStatement("SELECT * FROM driveTable");
			db.mSelectOne5 = db.mConnection.prepareStatement("SELECT * from driveTable WHERE id=?");
			db.mUpdateOne5 = db.mConnection.prepareStatement("UPDATE driveTable SET accessDate = ?, where id=?");
			db.mEarliestDate = db.mConnection.prepareStatement("select * from driveTable order by accessDate fetch first 1 row only");
		} 
		catch (SQLException e) {
			System.err.println("Error creating prepared statement");
			e.printStackTrace();
			db.disconnect();
			return null;
        }
		return db;
	}

	/**
	 * Close the current connection to the database, if one exists.
	 * NB: The connection will always be null after this call, even if an 
	 *	   error occurred during the closing operation.
	 * @return True if the connection was cleanly closed, false otherwise
	 */
	boolean disconnect() {
		if (mConnection == null) {
			System.err.println("Unable to close connection: Connection was null");
			return false;
		}
		try {
			mConnection.close();
		}
		catch (SQLException e) {
			System.err.println("Error: Connection.close() threw a SQLException");
			e.printStackTrace();
			mConnection = null;
			return false;
		}
		mConnection = null;
		return true;
	}

	/**
	 * Insert a row into the database
	 * 
	 * @param subject The subject for this new row
	 * @param message The message body for this new row
	 * 
	 * @return The number of rows that were inserted
	 */
	int insertRow(String message, int userId) {
		int count = 0;
		try {
			mInsertOne.setString(1, message);
			mInsertOne.setInt(2,userId);
			count += mInsertOne.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Query the database for a list of all subjects and their IDs
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<MessageRow> selectAll() {
		ArrayList<MessageRow> res = new ArrayList<MessageRow>();
		try {
			ResultSet rs = mSelectAll.executeQuery();
			while (rs.next()) {
				res.add(new MessageRow(rs.getInt("id"), rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("userId")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	MessageRow selectOne(int id) {
		MessageRow res = null;
		try {
			mSelectOne.setInt(1, id);
			ResultSet rs = mSelectOne.executeQuery();
			if (rs.next()) {
				res = new MessageRow(rs.getInt("id"), rs.getString("message"), rs.getInt("likes"), rs.getInt("dislikes"), rs.getInt("userId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Delete a row by ID
	 * 
	 * @param id The id of the row to delete
	 * 
	 * @return The number of rows that were deleted.  -1 indicates an error.
	 */
	int deleteRow(int id) {
		int res = -1;
		try {
			mDeleteOne.setInt(1, id);
			res = mDeleteOne.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the message for a row in the database
	 * 
	 * @param id The id of the row to update
	 * @param message The new message contents
	 * @param dVotes The total change in votes
	 * 
	 * @return The number of rows that were updated. -1 indicates an error.
	 */
	int updateOne(int id, String message) {
		int res = -1;
		int[] votes={0,0};//up,down
		try {
			mUpdateOne.setString(1, message);
			mUpdateOne.setInt(2, votes[0]);//# of upvotes to add
			mUpdateOne.setInt(3, votes[1]);//# of downvotes to add
			mUpdateOne.setInt(4, id);
			res = mUpdateOne.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the message for a row in the database
	 * 
	 * @param id The id of the row to update
	 * @param message The new message contents
	 * @param dVotes The total change in votes
	 * 
	 * @return The number of rows that were updated. -1 indicates an error.
	 */
	int updateOne(int id, String message,int like,int dislike) {
		int res = -1;
		int[] votes={0,0};//up,down
		try {
			mUpdateOne.setString(1, message);
			mUpdateOne.setInt(2, like);//# of upvotes to add
			mUpdateOne.setInt(3, dislike);//# of downvotes to add
			mUpdateOne.setInt(4, id);
			res = mUpdateOne.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Create tblData.	If it already exists, this will print an error
	 */
	void createTable() {
		try {
			mCreateTable.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove tblData from the database.  If it does not exist, this will print
	 * an error.
	 */
	void dropTable() {
		try {
			mDropTable.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Commands for Comment Table

	/**
	 * Create commentTable.	If it already exists, this will print an error
	 */
	void createTable2() {
		try {
			mCreateTable2.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove commentTable from the database.  If it does not exist, this will print
	 * an error.
	 */
	void dropTable2() {
		try {
			mDropTable2.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert a row into the database
	 *
	 * @param message The message body for this new row
	 * 
	 * @return The number of rows that were inserted
	 */
	int insertRow2(String message, int messageId, int userId) {
		int count = 0;
		try {
			mInsertOne2.setString(1, message);
			mInsertOne2.setInt(2,messageId);
			mInsertOne2.setInt(3,userId);
			count += mInsertOne2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Query the database for a list of all comments and their IDs
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<CommentRow> selectAll2() {
		ArrayList<CommentRow> res = new ArrayList<CommentRow>();
		try {
			ResultSet rs = mSelectAll2.executeQuery();
			while (rs.next()) {
				res.add(new CommentRow(rs.getInt("id"), rs.getString("comment"), rs.getInt("messageId"), rs.getInt("userId")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    /**
	 * Query the database for a list of all comments to given ID
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<CommentRow> selectAll2(int messageId) {
		ArrayList<CommentRow> res = new ArrayList<CommentRow>();
		try {
            mSelectMessage2.setInt(1, messageId);
			ResultSet rs = mSelectMessage2.executeQuery();
			while (rs.next()) {
				res.add(new CommentRow(rs.getInt("id"), rs.getString("comment"), rs.getInt("messageId"), rs.getInt("userId")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	CommentRow selectOne2(int id) {
		CommentRow res = null;
		try {
			mSelectOne2.setInt(1, id);
			ResultSet rs = mSelectOne2.executeQuery();
			if (rs.next()) {
				res = new CommentRow(rs.getInt("id"), rs.getString("comment"), rs.getInt("messageId"), rs.getInt("userId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Delete a row by ID from commentTable
	 * 
	 * @param id The id of the row to delete
	 * 
	 * @return The number of rows that were deleted.  -1 indicates an error.
	 */
	int deleteRow2(int id) {
		int res = -1;
		try {
			mDeleteOne2.setInt(1, id);
			res = mDeleteOne2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the message for a row in commentTable
	 * 
	 * @param id The id of the row to update
	 * @param message The new message contents
	 * @param dVotes The total change in votes
	 * 
	 * @return The number of rows that were updated.  -1 indicates an error.
	 */
	int updateOne2(int id, String message) {
		int res = -1;
		int[] votes={0,0};//up,down
		try {
			mUpdateOne2.setString(1, message);
			mUpdateOne2.setInt(2, id);

			res = mUpdateOne2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	// Commands for User Table

	/**
	 * Create userTable. If it already exists, this will print an error
	 */
	void createTable3() {
		try {
			mCreateTable3.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove userTable from the database. If it does not exist, this will print
	 * an error.
	 */
	void dropTable3() {
		try {
			mDropTable3.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert a row into the database
	 *
	 * @param message The message body for this new row
	 * 
	 * @return The number of rows that were inserted
	 */
	int insertRow3(String firstName, String lastName, String email, String userId) {
		int count = 0;
		try {
			mInsertOne3.setString(1, firstName);
			mInsertOne3.setString(2, lastName);
			mInsertOne3.setString(3, email);
			mInsertOne3.setString(4, userId);
			count += mInsertOne3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Query the database for a list of all comments and their IDs
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<UserRow> selectAll3() {
		ArrayList<UserRow> res = new ArrayList<UserRow>();
		try {
			ResultSet rs = mSelectAll3.executeQuery();
			while (rs.next()) {
				res.add(new UserRow(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"), rs.getString("userId")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	UserRow selectOne3(int id) {
		UserRow res = null;
		try {
			mSelectOne3.setInt(1, id);
			ResultSet rs = mSelectOne3.executeQuery();
			if (rs.next()) {
				res = new UserRow(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"), rs.getString("userId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Delete a row by ID from commentTable
	 * 
	 * @param id The id of the row to delete
	 * 
	 * @return The number of rows that were deleted.  -1 indicates an error.
	 */
	int deleteRow3(int id) {
		int res = -1;
		try {
			mDeleteOne3.setInt(1, id);
			res = mDeleteOne3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the message for a row in commentTable
	 * 
	 * @param id The id of the row to update
	 * @param message The new message contents
	 * @param dVotes The total change in votes
	 * 
	 * @return The number of rows that were updated.  -1 indicates an error.
	 */
	int updateOne3(int id, String message) {
		int res = -1;
		int[] votes={0,0};//up,down
		try {
			mUpdateOne3.setString(1, message);
			mUpdateOne3.setInt(2, id);
			res = mUpdateOne3.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	// Commands for Like/Dislike Table

	/**
	 * Create likeTable.	If it already exists, this will print an error
	 */
	void createTable4() {
		try {
			mCreateTable4.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove likeTable from the database.  If it does not exist, this will print
	 * an error.
	 */
	void dropTable4() {
		try {
			mDropTable4.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert a row into the database
	 *
	 * @param message The message body for this new row
	 * 
	 * @return The number of rows that were inserted
	 */
	int insertRow4(int messageId, int id) {
		int count = 0;
		try {
			mInsertOne4.setInt(1, messageId);
			mInsertOne4.setInt(2, id);
			count += mInsertOne4.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Query the database for a list of all likes/dislikes for all messages by all users
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<LikesRow> selectAll4() {
		ArrayList<LikesRow> res = new ArrayList<LikesRow>();
		try {
			ResultSet rs = mSelectAll4.executeQuery();
			while (rs.next()) {
				res.add(new LikesRow(rs.getInt("id"), rs.getInt("liked"), rs.getInt("disliked"), rs.getInt("messageId"), rs.getInt("userId")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	LikesRow selectOne4(int id) {
		LikesRow res = null;
		try {
			mSelectOne4.setInt(1, id);
			ResultSet rs = mSelectOne4.executeQuery();
			if (rs.next()) {
				res = new LikesRow(rs.getInt("id"), rs.getInt("liked"), rs.getInt("disliked"), rs.getInt("messageId"), rs.getInt("userId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Delete a row by ID from likeTable
	 * 
	 * @param id The id of the row to delete
	 * 
	 * @return The number of rows that were deleted.  -1 indicates an error.
	 */
	int deleteRow4(int id) {
		int res = -1;
		try {
			mDeleteOne4.setInt(1, id);
			res = mDeleteOne4.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the likes/dislikes for a row in likeTable
	 * 
	 * @param id The id of the row to update
	 * @param liked Whether the message is being liked (1) or disliked (0)
	 * 
	 * @return The number of rows that were updated.  -1 indicates an error.
	 */
	int updateOne4(int id, int liked) {
		int res = -1;
		try {
			if (liked == 0) {		
				mUpdateOne4.setInt(1, 0);
				mUpdateOne4.setInt(2, 1);
				mUpdateOne4.setInt(3, id);
				System.out.print("THIS IS THE USER'S FIRST LIKE/DIS");
			}
			else {	
				mUpdateOne4.setInt(1, 1);
				mUpdateOne4.setInt(2, 0);
				mUpdateOne4.setInt(3, id);
				System.out.print("THIS IS THE USER'S FIRST LIKE/DIS");
			}
			res = mUpdateOne4.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Insert a row into the database
	 * 
	 * @param subject The subject for this new row
	 * @param message The message body for this new row
	 * 
	 * @return The number of rows that were inserted
	 */
	int insertRow5(int userId, int messageId, String fileName) {
		int count = 0;
		try {
			mInsertOne5.setInt(1, userId);
			mInsertOne5.setInt(2, messageId);
			mInsertOne5.setString(3, fileName);
			LocalDateTime time =  LocalDateTime.now(ZoneId.of( "GMT-4" ));
			mInsertOne5.setObject(4,java.sql.Timestamp.valueOf(time));
			count += mInsertOne5.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * Query the database for a list of all subjects and their IDs
	 * 
	 * @return All rows, as an ArrayList
	 */
	ArrayList<DriveRow> selectAll5() {
		ArrayList<DriveRow> res = new ArrayList<DriveRow>();
		try {
			ResultSet rs = mSelectAll5.executeQuery();
			while (rs.next()) {
				res.add(new DriveRow(rs.getInt("id"), rs.getInt("userId"), rs.getInt("messageId"), rs.getString("fileName"), rs.getTimestamp("accessDate")));
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	DriveRow selectOne5(int id) {
		DriveRow res = null;
		try {
			mSelectOne5.setInt(1, id);
			ResultSet rs = mSelectOne5.executeQuery();
			if (rs.next()) {
				res = new DriveRow(rs.getInt("id"), rs.getInt("userId"), rs.getInt("messageId"), rs.getString("fileName"), rs.getTimestamp("accessDate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Get all data for a specific row, by ID
	 * 
	 * @param id The id of the row being requested
	 * 
	 * @return The data for the requested row, or null if the ID was invalid
	 */
	DriveRow earliestDate() {
		DriveRow res = null;
		try {
			System.out.println("howdy");
			ResultSet rs = mEarliestDate.executeQuery();
			if (rs.next()) {
				res = new DriveRow(rs.getInt("id"), rs.getInt("userId"), rs.getInt("messageId"), rs.getString("fileName"), rs.getTimestamp("accessDate"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Delete a row by ID
	 * 
	 * @param id The id of the row to delete
	 * 
	 * @return The number of rows that were deleted.  -1 indicates an error.
	 */
	int deleteRow5(int id) {
		int res = -1;
		try {
			mDeleteOne5.setInt(1, id);
			res = mDeleteOne5.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Update the message for a row in the database
	 * 
	 * @param id The id of the row to update
	 * @param fileName fileName of a message
	 * 
	 * 
	 * @return The number of rows that were updated.  -1 indicates an error.
	 */
	int updateOne5(int id) {
		int res = -1;
		try {
			mUpdateOne5.setInt(1, id);
			LocalDateTime time =  LocalDateTime.now(ZoneId.of( "GMT-4" ));
			mInsertOne5.setObject(2,java.sql.Timestamp.valueOf(time));
			res = mUpdateOne5.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Create tblData.	If it already exists, this will print an error
	 */
	void createTable5() {
		try {
			mCreateTable5.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove tblData from the database.  If it does not exist, this will print
	 * an error.
	 */
	void dropTable5() {
		try {
			mDropTable5.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	int getLikeId(int userId, int messageId) {
		int res = -1;
		try {
			getLikeId.setInt(1, userId);
			getLikeId.setInt(2, messageId);
            ResultSet rs = getLikeId.executeQuery();
            if (rs.next()) {
                res = rs.getInt("id");
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
		
    }
    
	/**
 	* 
 	* @param messageId message we are changing to reflect current likes
 	* @param liked like or dislike
 	* @param likeId row id in like table
 	* @return
 	*/
	int secureVoting(int messageId, int liked, int likeId) {
		int res = -1;
		int up = 0;
		int down = 0;
		try {
			Database.LikesRow likeRow = selectOne4(likeId);
			//dislike followed by like
			if(likeRow.disliked==0 && liked==1){
				System.out.print("THIS IS SHOULD BE DISLIKE FOLLOWED BY LIKE");
				up = 1;
				down =-1;
				updateOne4(likeId,liked);
			}
			//dislike followed by dislike
			if(likeRow.disliked==0 && liked==0){
				System.out.print("THIS IS SHOULD BE DISLIKE FOLLOWED BY DISLIKE");
				up = 0;
				down =-1;
				deleteRow4(likeId);
			}
			//like followed by like
			if(likeRow.liked==1 && liked==1){
				System.out.print("THIS IS SHOULD BE LIKE FOLLOWED BY LIKE");
				up = -1;
				down = 0;
				deleteRow4(likeId);
			}
			//like followed by dislike
			if(likeRow.liked==1 && liked==0){
				System.out.print("THIS IS SHOULD BE DISLIKE FOLLOWED BY LIKE");
				up = -1;
				down =1;
				updateOne4(likeId,liked);
			}
			
			// if like is currently at 1 no matter what we do the like count will go down we now have to differentiate between the new click being either like or dislike
			String actualMessage = selectOne(messageId).message;
			mUpdateOne.setString(1, actualMessage);
			mUpdateOne.setInt(2,up);
			mUpdateOne.setInt(3, down);
			mUpdateOne.setInt(4, messageId);
			System.out.print("I HAVE JUST UPDATE THE MESSAGETBL W/ UP:  "+up+"	down:	"+down);
			res = mUpdateOne.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
    }
    
    int get_Id(String userId) {
        int res = -1;
        try {
            getId.setString(1, userId);
            ResultSet rs = getId.executeQuery();
            if (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
	}
	
	int get_Message_Id(String message) {
        int res = -1;
        try {
            getMessageId.setString(1, message);
            ResultSet rs = getMessageId.executeQuery();
            if (rs.next()) {
                res = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
	
}