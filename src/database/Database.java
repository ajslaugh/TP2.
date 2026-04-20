
package database;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.collections.FXCollections;
import entityClasses.User;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.collections.ObservableList;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01		2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;
	// ***MODIFIED*** 
	// for one-time password
	private String currentOneTimePassword;
	private boolean currentChangePassword;
	// ***MODIFIED***
	// store reason for last failed operation (UI display message)
	private String lastErrorMessage = "";

	//For posts
	private String thisPost;
	private String thisUser;
	private String thisRole;
	private String thisThread;
	
	//For Reply
	private String thisReply;
	private String thisReplier;
	private String thisReplierRole;
	private String thisReplyThread;
	private int thisReplyNumber;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the four database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable =
			    "CREATE TABLE IF NOT EXISTS userDB ("
			  + "id INT AUTO_INCREMENT PRIMARY KEY, "
			  + "userName VARCHAR(255) UNIQUE, "
			  + "password VARCHAR(255), "
			  + "firstName VARCHAR(255), "
			  + "middleName VARCHAR(255), "
			  + "lastName VARCHAR(255), "
			  + "preferredFirstName VARCHAR(255), "
			  + "emailAddress VARCHAR(255), "
			  + "adminRole BOOL DEFAULT FALSE, "
			  + "newRole1 BOOL DEFAULT FALSE, "
			  + "newRole2 BOOL DEFAULT FALSE, "
			  + "oneTimePassword VARCHAR(255), " // ***MODIFIED***
			  + "changePassword BOOL DEFAULT FALSE," // ***MODIFIED***
			  + "numUnread INT DEFAULT 0"//***MODIFIED***
			  + ")";
		statement.execute(userTable);
		
		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
		        + "code VARCHAR(10) PRIMARY KEY, "
		        + "emailAddress VARCHAR(255), "
		        + "role VARCHAR(10), "
		        + "expiresAt BIGINT)"; // ***MODIFIED*** added deadline column

	    statement.execute(invitationCodesTable);

		//modified by jerry
	    //Alex 
	    //Create the post table
	    String postTable="CREATE TABLE IF NOT EXISTS userPosts(" 
		+ "number INT AUTO_INCREMENT PRIMARY KEY,"
		+ "post VARCHAR(255),"
		+ "username VARCHAR(255), "
		+ "role VARCHAR(255), "
		+ "deleted INT DEFAULT 0,"
		+ "numReplies INT DEFAULT 0,"
		+ "authorUnread INT DEFAULT 0,"
		+ "endorsed INT DEFAULT 0," //ADDED ENDORSEMENT COLUMN
		+ "thread VARCHAR(255) DEFAULT 'General')";
		
	    statement.execute(postTable);
	//Alex
	//Create the reply table 
	String replyTable="CREATE TABLE IF NOT EXISTS userReplies(" 
			+ "id INT AUTO_INCREMENT PRIMARY KEY,"
			+ "replyTo INT,"
    		+ "post VARCHAR(255),"
    		+ "username VARCHAR(255), "
    		+ "role VARCHAR(255), "
    		+ "thread VARCHAR(255) DEFAULT 'General')";
	
    statement.execute(replyTable);
	
	// Brenn thread table
    String threadTable = "CREATE TABLE IF NOT EXISTS threadTypes("
    	    + "id INT AUTO_INCREMENT PRIMARY KEY,"
    	    + "thread_name VARCHAR(255) UNIQUE NOT NULL,"
    	    + "thread_description VARCHAR(500) DEFAULT '',"
    	    + "status VARCHAR(20) DEFAULT 'active'"
    	    + ")";
    	statement.execute(threadTable);

	statement.execute("INSERT INTO threadTypes (thread_name, thread_description, status) "
        + "SELECT 'General', 'Default discussion thread', 'active' WHERE NOT EXISTS ("
        + "SELECT 1 FROM threadTypes WHERE thread_name = 'General'"
        +")"
        );

	//MODIFIED by KYLE 
	// Create the PostFlags table 
		String postFlagsTable = "CREATE TABLE IF NOT EXISTS PostFlags ("
			+ "  flagID INT AUTO_INCREMENT PRIMARY KEY," 
			+ "  postID INT NOT NULL,"
			+ "  flaggedBy VARCHAR(255) NOT NULL,"
			+ "  reason VARCHAR(1000) NOT NULL,"
			+ "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
			+ "  status VARCHAR(50) DEFAULT 'PENDING',"
			+ "  notes VARCHAR(1000),"
			+ "  FOREIGN KEY (postID) REFERENCES userPosts(number),"
			+ "  FOREIGN KEY (flaggedBy) REFERENCES userDB(userName)"
			+ ")";
		
		try {
			statement.execute(postFlagsTable);
		} catch (SQLException e) {
			System.out.println("PostFlags table may already exist: " + e.getMessage());
		}
		
	// Create Feedback Table
		String feedbackTable = "CREATE TABLE IF NOT EXISTS Feedback ("
				+ "  feedbackID INT AUTO_INCREMENT PRIMARY KEY,"
				+ "  fromUser VARCHAR(255) NOT NULL,"
				+ "  toUser VARCHAR(255) NOT NULL,"
				+ "  content VARCHAR(2000) NOT NULL,"
				+ "  targetPostID INT,"
				+ "  targetReplyID INT,"
				+ "  isPrivate BOOL DEFAULT TRUE,"
				+ "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
				+ "  isRead BOOL DEFAULT FALSE,"
				+ "  feedbackType VARCHAR(50),"
				+ "  FOREIGN KEY (fromUser) REFERENCES userDB(userName),"
				+ "  FOREIGN KEY (toUser) REFERENCES userDB(userName),"
				+ "  FOREIGN KEY (targetPostID) REFERENCES userPosts(number),"
				+ "  FOREIGN KEY (targetReplyID) REFERENCES userReplies(id)"
				+ ")";
			statement.execute(feedbackTable);

			statement.execute("INSERT INTO threadTypes (thread_name) "
		        + "SELECT 'General' WHERE NOT EXISTS ("
		        + "SELECT 1 FROM threadTypes WHERE thread_name = 'General'"
		        +")"
		        );

			// MODIFIED TP3 Brenna -- creates postGrades table for grading system
			String gradingTable = "CREATE TABLE IF NOT EXISTS postGrades ("
			    + "gradeID INT AUTO_INCREMENT PRIMARY KEY, "
			    + "postID INT NOT NULL, "
			    + "studentUsername VARCHAR(255), "
			    + "points INT DEFAULT 0, "
			    + "gradedBy VARCHAR(255), "
			    + "FOREIGN KEY (postID) REFERENCES userPosts(number)"
			    + ")";
			try {
			    statement.execute(gradingTable);
			} catch (SQLException e) {
			    System.out.println("postGrades table may already exist: " + e.getMessage());
			}
		
	}

/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}
	/*******
	 * <p> Method: getNumberOfPosts()</p>
	 * 
	 * <p> Description: Returns an integer of number of posts currently in userPostsdatabase. </p>
	 * 
	 * @return the number of user post records in the database.
	 * 
	 */
	//Alex get number of posts
	public int getNumberOfPosts() {
		String query = "SELECT COUNT(*) AS count FROM userPosts";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if(resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch(SQLException e) {
			return 0;
		}
		return 0;
	}
	
	/*******
	 * <p> Method: addPost(Post) </p>
	 * 
	 * <p> Description: Add a post to the userPosts database.  </p>
	 *  
	 * @return 
	 * 
	 */
	//Alex add Post
   public int addPost(Post post) throws SQLException{
	   String addPost = "INSERT INTO userPosts (post, username, role, thread)"
			   + "VALUES (?, ?, ?, ?)";
	   try (PreparedStatement pstmt = connection.prepareStatement(addPost)){
		   thisPost = post.getContent();
		   pstmt.setString(1, thisPost);
		   
		   thisUser = post.getAuthor();
		   pstmt.setString(2, thisUser);
		   
		   thisRole = post.getRole();
		   pstmt.setString(3,  thisRole);
		   
		   thisThread = post.getThread();
		   pstmt.setString(4,  thisThread);
		   
		   pstmt.executeUpdate();
		   
		   ResultSet rs = pstmt.getGeneratedKeys();
		    if (rs.next()) {
		        return rs.getInt(1); //post_id
		    }
		    else return 0;
	   }
	   
	  
	   
	   }
   
   /*******
	 * <p> Method: addReply(reply) </p>
	 * 
	 * <p> Description: Add a reply to the userReplies database.  </p>
	 *  
	 * @return 
	 * 
	 */
	   //Alex create reply
	   public void addReply(Reply reply) throws SQLException{
		   String addReply = "INSERT INTO userReplies (replyTo, post, username, role, thread)"
				   + "VALUES (?, ?, ?, ?, ?)";
		   try (PreparedStatement pstmt = connection.prepareStatement(addReply)){
			   thisReplyNumber = reply.getogPost();
			   pstmt.setInt(1, thisReplyNumber);
			   
			   thisReply = reply.getContent();
			   pstmt.setString(2, thisReply);
			   
			   thisReplier = reply.getAuthor();
			   pstmt.setString(3, thisReplier);
			   
			   thisReplierRole = reply.getRole();
			   pstmt.setString(4,  thisReplierRole);
			   
			   thisReplyThread = reply.getThread();
			   pstmt.setString(5,  thisReplyThread);
			   
			   pstmt.executeUpdate();
		   }	   
	   
   }

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2, "
				+ "oneTimePassword, changePassword) " // ***MODIFIED***
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			currentOneTimePassword = null;
			pstmt.setString(11, currentOneTimePassword);

			currentChangePassword = false;
			pstmt.setBoolean(12, currentChangePassword);

			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  MODIFIED by KYLE PORCHE
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> usernames = new ArrayList<>();
	    String query = "SELECT userName FROM userDB ORDER BY userName";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return usernames;
	}
	/*******
*  <p> Method: List getStudentList() </p>
*  
*  <P> Description: Generate an List of Strings, one for each student role in the database,
*  starting with "<Select Student>" at the start of the list. </p>
*  MODIFIED by DACIA BAIL
*  @return a list of students found in the database.
*/
	public List<String> getStudentList() {
	    List<String> usernames = new ArrayList<>();
	   
	    String query = "SELECT userName FROM userDB WHERE NEWROLE1 = TRUE ORDER BY userName";

	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return usernames;
	}
	
	//HW2 Brenn 
	   //get all current thread types
	   public List<String> getAllThreadTypes() {
		    List<String> threads = new ArrayList<>();
		    String query = "SELECT thread_name FROM threadTypes";
		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {
		        while (rs.next()) {
		            threads.add(rs.getString("thread_name"));
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return threads;
		}
	   
	   //HW2 Brenn
	   //Staff adds a new thread type
	   public boolean addThreadType(String threadName) {
		    String query = "INSERT INTO threadTypes (thread_name) VALUES (?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, threadName);
		        pstmt.executeUpdate();
		        return true;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
	   
	   //HW2 Brenn
	   //Staff removes a thread type
	   public boolean removeThreadType(String threadName) {
		    String query = "DELETE FROM threadTypes WHERE thread_name = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, threadName);
		        pstmt.executeUpdate();
		        return true;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}

	 //HW2 Brenn
		//get posts by thread
		public ObservableList<Post> getPostsByThread(String thread) {
		    ObservableList<Post> postDisplay = FXCollections.observableArrayList();
		    Post newPost;
		    String query = "SELECT post, username, role, thread, number FROM userPosts WHERE thread = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, thread);
		        ResultSet rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	if(rs.getString("thread").equals(thread)) {
		        		newPost = new Post(rs.getString("username"), rs.getString("role"), rs.getString("post"), rs.getString("thread"));
		        		newPost.setID(rs.getInt("number"));
		        		postDisplay.add(newPost);
		        	}
		        }}
				catch(SQLException e) {
					e.printStackTrace();
					return postDisplay;
				}
		    return postDisplay;
		}
		
		/***************************************
		 * Thread Management Methods (Staff)
		 ***************************************/
		
		/*****
		 * Retrieves all threads from the database including their name,
		 * description, and status.
		 * 
		 * @return list of all thread objects in the system
		 */
		public List<entityClasses.ThreadType> getAllThreadsDetailed() {
		    List<entityClasses.ThreadType> threads = new ArrayList<>();
		    String query = "SELECT id, thread_name, thread_description, status FROM threadTypes ORDER BY thread_name";

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {

		        while (rs.next()) {
		            entityClasses.ThreadType thread = new entityClasses.ThreadType(
		                rs.getString("thread_name"),
		                rs.getString("thread_description"),
		                rs.getString("status")
		            );
		            thread.setId(rs.getInt("id"));
		            threads.add(thread);
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return threads;
		}   
		
		/*****
		 * Retrieves all active thread names from the database.
		 * 
		 * @return a list of thread names that are currently active
		 */
		public List<String> getActiveThreadTypes() {
			List<String> threads = new ArrayList<>();
			String query = "SELECT thread_name FROM threadTypes WHERE status = 'active' ORDER BY thread_name";
			try (PreparedStatement pstmt = connection.prepareStatement(query);
					ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					threads.add(rs.getString("thread_name"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return threads;
		}
		   
		
		/*****
		 * Wrapper method for creating a new thread as a staff member.
		 * Sets the default status to "active".
		 * 
		 * @param threadName thread name
		 * @param description thread description
		 * @return true if the thread was successfully created, false otherwise
		 */		
		public boolean addThreadType(String threadName, String description) {
			return addThreadTypeStaff(threadName, description, "active");
		}
		
		/*****
		 * Creates a new thread in the database with the specified attributes.
		 * This method performs the actual database insertion.
		 * 
		 * @param threadName thread name
		 * @param description thread description
		 * @param status thread status (active vs. archived)
		 * @return true if the thread was successfully inserted, false otherwise
		 */
		private boolean addThreadTypeStaff(String threadName, String description, String status) {
		    if (threadName == null || threadName.trim().isEmpty()) return false;

		    String query = "INSERT INTO threadTypes (thread_name, thread_description, status) VALUES (?, ?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, threadName.trim());
		        pstmt.setString(2, description == null ? "" : description.trim());
		        pstmt.setString(3, status == null ? "active" : status);
		        pstmt.executeUpdate();
		        return true;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		/*****
		 * Updates an existing thread's name, description, and status.
		 * 
		 * @param id thread id
		 * @param name the updated thread name
		 * @param description the updated thread description
		 * @param status the updated status (active vs. archived)
		 * @return true if the update was successful, false otherwise
		 */
		public boolean updateThreadType(int id, String name, String description, String status) {
		    if (name == null || name.trim().isEmpty()) return false;
		    if (status == null || status.trim().isEmpty()) status = "active";

		    String query = "UPDATE threadTypes SET thread_name = ?, thread_description = ?, status = ? WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, name.trim());
		        pstmt.setString(2, description == null ? "" : description.trim());
		        pstmt.setString(3, status.trim().toLowerCase());
		        pstmt.setInt(4, id);
		        return pstmt.executeUpdate() > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		/*****
		 * Deletes a thread from the database using its ID.
		 * The "General" thread cannot be deleted.
		 * 
		 * @param id thread id
		 * @return true if the thread was successfully deleted, false otherwise
		 */
		public boolean removeThreadTypeById(int id) {
		    String query = "DELETE FROM threadTypes WHERE id = ? AND thread_name <> 'General'";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, id);
		        return pstmt.executeUpdate() > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		/*****
		 * Archives a thread by updating its status to "archived".
		 * Archived threads are read-only and cannot accept new posts or replies.
		 * 
		 * @param id thread id
		 * @return true if the thread was successfully archived, false otherwise
		 */
		public boolean archiveThreadType(int id) {
		    String query = "UPDATE threadTypes SET status = 'archived' WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, id);
		        return pstmt.executeUpdate() > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		/*****
		 * Checks if a thread is currently active.
		 * 
		 * @param threadName name of the thread to check
		 * @return true if the thread is active, false if it is archived or not found
		 */
		public boolean isThreadActive(String threadName) {
		    String query = "SELECT status FROM threadTypes WHERE thread_name = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, threadName);
		        ResultSet rs = pstmt.executeQuery();
		        if (rs.next()) {
		            return "active".equalsIgnoreCase(rs.getString("status"));
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return false;
		}
		
		
		//Alex
		//get all unread posts
		public ObservableList<Post> getUnreadPosts() {
		    ObservableList<Post> postDisplay = FXCollections.observableArrayList();
		    Post newPost;
		    String query = "SELECT post, username, role, thread, number FROM userPosts WHERE authorUnread > ? AND username = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, 0);
		        pstmt.setString(2, currentUsername);
		        ResultSet rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	newPost = new Post(rs.getString("username"), rs.getString("role"), rs.getString("post"), rs.getString("thread"));
		        	newPost.setID(rs.getInt("number"));
		       		postDisplay.add(newPost);
		        }
		     }
				catch(SQLException e) {
					e.printStackTrace();
					return postDisplay;
				}
		    return postDisplay;
		}
		
		//Alex
		//Get users own posts
		public ObservableList<Post> getOwnPosts() {
		    ObservableList<Post> postDisplay = FXCollections.observableArrayList();
		    Post newPost;
		    String query = "SELECT post, username, role, thread, number FROM userPosts WHERE username = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1,currentUsername);
		        ResultSet rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	newPost = new Post(rs.getString("username"), rs.getString("role"), rs.getString("post"), rs.getString("thread"));
		        	newPost.setID(rs.getInt("number"));
		        	postDisplay.add(newPost);
		       	}
		       }
				catch(SQLException e) {
					e.printStackTrace();
					return postDisplay;
				}
		    return postDisplay;
		}


		// Brenn
		//search posts by keyword
		public ObservableList<Post> searchPostsByKeyword(String keyword) {
		    ObservableList<Post> results = FXCollections.observableArrayList();
		    // % on both sides means "keyword can appear anywhere in the post"
		    String query = "SELECT post, username, role, thread, number FROM userPosts " +
		                   "WHERE LOWER(post) LIKE LOWER(?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, "%" + keyword + "%");
		        ResultSet rs = pstmt.executeQuery();
		        while (rs.next()) {
		            Post p = new Post(rs.getString("username"), rs.getString("role"),
		                              rs.getString("post"), rs.getString("thread"));
		            p.setID(rs.getInt("number"));
		            results.add(p);
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return results;
		}	
	// Returns full user info (List<String[]>)
	// MODIFIED by KYLE PORCHE
	
	public List<String[]> getAllUsers() {
	    List<String[]> users = new ArrayList<>();
	    String query = "SELECT userName, firstName, middleName, lastName, emailAddress, adminRole, newRole1, newRole2 FROM userDB ORDER BY userName";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String[] user = new String[8];
	            user[0] = rs.getString("userName");
	            user[1] = rs.getString("firstName");
	            user[2] = rs.getString("middleName");
	            user[3] = rs.getString("lastName");
	            user[4] = rs.getString("emailAddress");
	            user[5] = String.valueOf(rs.getBoolean("adminRole"));
	            user[6] = String.valueOf(rs.getBoolean("newRole1"));
	            user[7] = String.valueOf(rs.getBoolean("newRole2"));
	            users.add(user);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return users;
	}
	


	

	/*******
	 * <p> Method: isOwnPost(int postnum) </p>
	 * 
	 * <p> Description: Return a boolean to show if the post is the students own post. </p>
	 *  
	 * @return isownPost boolean 
	 * 
	 */
	//Alex
	//Checks to see if post is own post
	public boolean isOwnPost(Post post) {
		String author = post.getAuthor();
		if(currentUsername.equals(author)) {
			return true;
		}
		else {
			return false;
		}
		
				
	}
	
	//Alex
	//Updates the number of replies a post has
	public boolean updateReplyNumber(Post post) {
		int postID = post.getID();
		String query = "UPDATE userPosts SET numReplies = numReplies + 1 WHERE number = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, postID);
			return (pstmt.executeUpdate() > 0);
	}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	//Alex
	//Updates the number of unread replies the author has on a single post
	public boolean updateAuthorUnread(Post post) {
		int postID = post.getID();
		String query = "UPDATE userPosts SET authorUnread = authorUnread + 1 WHERE number = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, postID);
			return (pstmt.executeUpdate() > 0);
	}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	//Alex
	//Decrements the number of unread replies after author views the post.
	public boolean decrementAuthorUnread(Post post) {
		int postID = post.getID();
		String query = "UPDATE userPosts SET authorUnread = ? WHERE number = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, 0);
			pstmt.setInt(2, postID);
			return (pstmt.executeUpdate() > 0);
	}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Alex
	//Increments users total unread replies by 1
	public boolean updateUserUnread(Post post) {
		String theUser = post.getAuthor();
		String query = "UPDATE userDB SET numUnread = numUnread + 1 WHERE username = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, theUser);
			return (pstmt.executeUpdate() > 0);
	}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Alex
	//Decrements number of unread replies on a single post by the author by how many unread replies that post had
	public boolean DecrementUserUnread(Post post, int num) {
		String theUser = post.getAuthor();
		int dec = getUserUnread(theUser) - num;
		String query = "UPDATE userDB SET numUnread = ? WHERE username = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, dec);
			pstmt.setString(2, theUser);
			return (pstmt.executeUpdate() > 0);
	}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//Alex
	//Gets number of unread replies a user has total.
	public int getUserUnread(String theUser) {
		int unread;
		String query = "SELECT numUnread FROM userDB WHERE username = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
				pstmt.setString(1, theUser);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
				unread = rs.getInt("numUnread");
				return unread;
				}
				else {return 0;}
		}
		catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	//Alex
	//Get number of unread posts the author has
	public int getAuthorUnread(Post post) {
		int postID = post.getID();
		int unread;
		String query = "SELECT authorUnread FROM userPosts WHERE number = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
				pstmt.setInt(1, postID);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()) {
				unread = rs.getInt("authorUnread");
				return unread;
				}
				
				return 0;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	

	public int getNumReplies(Post post) {
		int postID = post.getID();
		int replies;
		String query = "SELECT numReplies FROM userPosts WHERE number = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
				pstmt.setInt(1, postID);
				ResultSet rs = pstmt.executeQuery();
				replies = rs.getInt("numReplies");
				return replies;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/*******
	 * <p> Method: getRepliesFromPost(int) </p>
	 * 
	 * <p> Description: Return a boolean to show if the post is the students own post. </p>
	 *  
	 * @return List<String[]> replies 
	 * 
	 */
	//Alex Get replylist
	public List<String[]>getRepliesFromPost(int ogPost){
		List<String[]> replies = new ArrayList<>();
		String query = "SELECT replyTo, post, username, role, thread FROM userReplies";
		try(PreparedStatement pstmt = connection.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery()){
			
			while(rs.next()) {
				int num = rs.getInt("replyTo");
				if(num == ogPost) {
					String[] reply = new String[4];
					reply[0] = rs.getString("username");
		            reply[1] = rs.getString("post");
		            reply[2] = rs.getString("role");
		            reply[3] = rs.getString("thread");
		            replies.add(reply);
				}
			}}
			catch (SQLException e) {
				e.printStackTrace();
				return new ArrayList<>();
				}
			
			return replies;
	}
	
	/*******
	 * <p> Method:  displayPostsHelper() </p>
	 * 
	 * <p> Description: Return an observable list to display posts. </p>
	 *  
	 * @return ObservableList<String> postDisplay
	 * 
	 */

	//CHANGES MADE BY JERRY TO ALLOW FOR POST ENDORSMENT
public ObservableList<Post> displayPostHelper(){
	ObservableList<Post> postDisplay = FXCollections.observableArrayList();
	Post newPost;
	String query = "SELECT post, username, role, thread, number, endorsed FROM userPosts";
	
	try(PreparedStatement pstmt = connection.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery()){
		
		while(rs.next()) {
			newPost = new Post(rs.getString("username"), rs.getString("role"), rs.getString("post"), rs.getString("thread"));
			newPost.setID(rs.getInt("number"));
			//ADDING ENDORSEMENT VALUE FROM DATABASE
		    newPost.setEndorsed(rs.getInt("endorsed"));
			postDisplay.add(newPost);
		}}
		catch(SQLException e) {
			e.printStackTrace();
			return postDisplay;
		}
		return postDisplay;
}

	

	/*******
	 * <p> Method: boolean deleteUser(String userName) </p>
	 *
	 * <p> Description: Deletes the user record that matches the given userName.</p>
	 *
	 * @param userName the username to delete
	 *
	 * @return true if a user was deleted, false otherwise
	 */
	
	public boolean deleteUser(String userName) {

	    // Basic safety checks
	    if (userName == null) return false;
	    if (userName.trim().equals("")) return false;
	    if (userName.equals("<Select a User>")) return false;

	    String query = "DELETE FROM userDB WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);

	        int rowsAffected = pstmt.executeUpdate();   // number of rows deleted
	        return rowsAffected > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	

	


/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
		
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code

	    // ***MODIFIED***
	    // setting deadline for 1 minute (testing)
	    long expiresAt = System.currentTimeMillis() + 60L * 1000L;

	    String query = "INSERT INTO InvitationCodes (code, emailAddress, role, expiresAt) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.setLong(4, expiresAt);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}


	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE expiresAt > ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setLong(1, System.currentTimeMillis());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("count");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}


	/*******
	 * <p> Method: List<String> thisgetInviteList() </p>
	 * 
	 * <p> Description: Displays GUI of dropdown list of pending invitations. Displays role, generated code and emil address. </p>
	 *  
	 * @return the list of invitations in the table.
	 * 
	 */
	//hw2
	
	public List<String> thisgetInviteList() {
		List<String> invitations = new ArrayList<>(); 
		String query = "SELECT code, emailAddress, role FROM InvitationCodes";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) { 
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			invitations.add(String.format("Code: %s | Email: %s | Role: %s\n", rs.getString("code"), rs.getString("emailAddress"),rs.getString("role")));}} 
    catch (SQLException e) { e.printStackTrace();
    }
    return invitations;
    }
	
	// get all the invitations in the database
	public List<String[]> getAllInvitations() {
	    List<String[]> invitations = new ArrayList<>();

	    String query = "SELECT code, emailAddress, role, expiresAt " +
	                   "FROM InvitationCodes WHERE expiresAt > ? " +
	                   "ORDER BY expiresAt ASC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setLong(1, System.currentTimeMillis());

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                String[] invitation = new String[4];
	                invitation[0] = rs.getString("code");
	                invitation[1] = rs.getString("emailAddress");
	                invitation[2] = rs.getString("role");
	                invitation[3] = String.valueOf(rs.getLong("expiresAt")); // milliseconds
	                invitations.add(invitation);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return invitations;
	}


	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ? AND expiresAt > ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setString(1, emailAddress);
	    	pstmt.setLong(2, System.currentTimeMillis());
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT role FROM InvitationCodes WHERE code = ? AND expiresAt > ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setLong(2, System.currentTimeMillis());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getString("role");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}


	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode(String code) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ? AND expiresAt > ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setLong(2, System.currentTimeMillis());
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getString("emailAddress");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	// ***MODIFIED***
	// returns true if code exists but is expired
	public boolean isInvitationExpired(String code) {
	    String query = "SELECT expiresAt FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            long expiresAt = rs.getLong("expiresAt");
	            return expiresAt <= System.currentTimeMillis();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // if not found, it's invalid (not expired)
	}

	/*******
	 * <p> Method: boolean deleteInvitationByCode(String code) </p> <--- ADDED CODE BY DACIA/KYLE
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	//select code of user that we want to rescind invitation from
	public boolean deleteInvitationByCode(String code) {
	    String query = "DELETE FROM InvitationCodes WHERE code = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        return false;
	    }
	}

	//STAFF ENDORSE POST METHOD CREATED BY JERRY
public boolean endorsePost(int postID) {
    String query = "UPDATE userPosts SET endorsed = 1 WHERE number = ?";
    
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setInt(1, postID);
        return pstmt.executeUpdate() > 0;
    }
    catch(SQLException e) {
        e.printStackTrace();
        return false;
    }
}
	//HW2 Delete post
	/*******
	 * <p> Method: deletePost(post) </p>
	 * 
	 * <p> Description: Return boolean to show if post was deleted or not.  </p>
	 *  
	 * @return boolean deleted  
	 * 
	 */
	//alex
	//Delets content and username from a post, keeping it live 
	public boolean deletePost(Post post) {
		String query = "UPDATE userPosts SET post = ?, username = ?, deleted = ? WHERE number = ?";
		int postID = post.getID();
		String deletedPost = "This post has been deleted";
		String deletedUser = "DELETED";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, deletedPost);
			pstmt.setString(2, deletedUser);
			pstmt.setInt(3,  1);
	        pstmt.setInt(4, postID);
	        return (pstmt.executeUpdate() > 0);
	        
	    } catch (SQLException e) {
	    	e.printStackTrace();
	        return false;
	    }
		
	}
	
	//Alex
	//Return a boolean to show if a post has been successfully deleted
	public boolean isDeleted(Post post) {
		String query = "SELECT deleted FROM userPosts WHERE number = ?";
		int postID = post.getID();
		int deletedPost;
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1,postID);
			ResultSet rs = pstmt.executeQuery();
			deletedPost = rs.getInt("deleted");
			if(deletedPost == 0) {
				return false;
			}
			else {
				return true;
			}
		}
		catch(SQLException e) {
			return false;
		}
	}
	//HW2 delete Reply
	/*******
	 * <p> Method: deleteReply(int) </p>
	 * 
	 * <p> Description: Return a boolean to show if reply was deleted. </p>
	 *  
	 * @return boolean deleted  
	 * 
	 */
	//needed for role 2
	public boolean deleteReply(int id) {
		String query = "DELETE FROM userReplies WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        return false;
	    }
		
	}
	/*******
	 * <p> Method: boolean resendInvitation(String code) </p> 
	 * 
	 * <p> Description: Resends the same invitation.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return boolean.
	 * 
	 */

	public boolean resendInvitation(String code) {
	    // In a real system, this would re-send the email
	    return true;
	}
	
	// returns true if code exists at all (expired or not)
	public boolean invitationCodeExists(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getInt("count") > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
	        if (!rs.next()) {
	            return false;
	        }
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
	    	currentOneTimePassword = rs.getString(12);
	    	currentChangePassword = rs.getBoolean(13);

			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	

	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user 
	 * 
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	// wrapper to preserve the original method params
	public boolean updateUserRole(String username, String role, String value) {
	    // currentUsername = acting admin
	    return updateUserRole(currentUsername, username, role, value);
	}

	// ***MODIFIED***
	// real update role method
	public boolean updateUserRole(String actingAdminUsername, String username, String role, String value) {

		lastErrorMessage = ""; // reset every call

		if (role.compareTo("Admin") == 0) {

			// if target role removed = admin
	        if (value.compareTo("false") == 0) {
	            // admin CANNOT remove their own role
	            if (username.equals(actingAdminUsername)) {
	                lastErrorMessage = "You cannot remove your own Admin role.";
	                return false;
	            }

	            // must always have at least one admin
	            int adminCount = getNumberOfAdmins();
	            boolean targetIsAdminCurrently = false;

	            // check if target is currently an admin
	            String check = "SELECT adminRole FROM userDB WHERE userName = ?";
	            try (PreparedStatement ps = connection.prepareStatement(check)) {
	                ps.setString(1, username);
	                ResultSet rs = ps.executeQuery();
	                if (rs.next()) targetIsAdminCurrently = rs.getBoolean("adminRole");
	            } catch (SQLException e) {
	                e.printStackTrace();
	                return false;
	            }

	            // block ability to remove a SOLE admin role
	            if (targetIsAdminCurrently && adminCount <= 1) {
	                lastErrorMessage = "You cannot remove the Admin role. There must be at least one Admin in the system.";
	                return false;
	            }
	        }
	        // otherwise continue removing role 
			String query = "UPDATE userDB SET adminRole = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
	            // only update admin role if you're the admin logged in
	            if (username.equals(currentUsername)) {
	                currentAdminRole = value.compareTo("true") == 0;
	            }
	            return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) { // ***MODIFIED***
			String query = "UPDATE userDB SET newRole1 = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole1 = true;
				else
					currentNewRole1 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) { // ***MODIFIED***
			String query = "UPDATE userDB SET newRole2 = ? WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentNewRole2 = true;
				else
					currentNewRole2 = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};
	
	// ***MODIFIED***
	// getter for last error message
	public String getLastErrorMessage() {
	    return lastErrorMessage;
	}

	
	// ***MODIFIED***
	// helper method for admin count
	public int getNumberOfAdmins() {
	    String query = "SELECT COUNT(*) AS count FROM userDB WHERE adminRole = TRUE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getInt("count");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}


	// ***MODIFIED***
	public String getCurrentOneTimePassword() { return currentOneTimePassword; }
	// ***MODIFIED***
	public boolean getCurrentChangePassword() { return currentChangePassword; }
	
	// ***MODIFIED***
	// update password method
	public void updatePassword(String username, String password) {
	    String query = "UPDATE userDB SET password = ? WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = password;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	// ***MODIFIED***
	// set a one-time password for a user and require password change upon login
	public boolean setOneTimePassword(String username, String tempPassword) {
	    String query = "UPDATE userDB SET oneTimePassword = ?, changePassword = TRUE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, tempPassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	// ***MODIFIED***
	// clear one-time password after use
	public boolean clearOneTimePassword(String username) {
	    String query = "UPDATE userDB SET oneTimePassword = NULL, changePassword = FALSE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	


	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
/*******
	 * <p> Method: flagPost(int postID, String flaggedBy, String reason) </p>
	 * 
	 * <p> Description: Flags a post as inappropriate for staff review. </p>
	 */
	public boolean flagPost(int postID, String flaggedBy, String reason) {
		try {
			String sql = "INSERT INTO PostFlags (postID, flaggedBy, reason, timestamp, status) " +
						 "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'PENDING')";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, postID);
			pstmt.setString(2, flaggedBy);
			pstmt.setString(3, reason);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (SQLException e) {
			System.err.println("Error flagging post: " + e.getMessage());
			return false;
		}
	}

	/*******
	 * <p> Method: getAllFlaggedPosts() </p>
	 * 
	 * <p> Description: Retrieves all flagged posts for staff review. </p>
	 */
	public List<String[]> getAllFlaggedPosts() {
		List<String[]> flaggedPosts = new ArrayList<>();
		try {
			String sql = "SELECT f.flagID, f.postID, f.flaggedBy, f.reason, f.status, p.post " +
						 "FROM PostFlags f " +
						 "JOIN userPosts p ON f.postID = p.number " +
						 "ORDER BY f.timestamp DESC";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String[] flag = new String[6];
				flag[0] = String.valueOf(rs.getInt("flagID"));
				flag[1] = String.valueOf(rs.getInt("postID"));
				flag[2] = rs.getString("flaggedBy");
				flag[3] = rs.getString("reason");
				flag[4] = rs.getString("status");
				flag[5] = rs.getString("post");
				flaggedPosts.add(flag);
			}
		} catch (SQLException e) {
			System.err.println("Error retrieving flagged posts: " + e.getMessage());
		}
		return flaggedPosts;
	}

	/*******
	 * <p> Method: updateFlagStatus(int flagID, String newStatus) </p>
	 * 
	 * <p> Description: Updates the status of a flag (PENDING, APPROVED, RESOLVED). </p>
	 */
	public boolean updateFlagStatus(int flagID, String newStatus) {
		try {
			String sql = "UPDATE PostFlags SET status = ? WHERE flagID = ?";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, newStatus);
			pstmt.setInt(2, flagID);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (SQLException e) {
			System.err.println("Error updating flag status: " + e.getMessage());
			return false;
		}
	}
	
	/*******
	 * <p> Method: submitFeedback() </p>
	 * 
	 * <p> Description: Submits private feedback from one user to another. </p>
	 */
	public boolean submitFeedback(String fromUser, String toUser, String content, int targetPostID, String feedbackType) {
		try {
			String sql = "INSERT INTO Feedback (fromUser, toUser, content, targetPostID, isPrivate, timestamp, feedbackType) " +
						 "VALUES (?, ?, ?, ?, TRUE, CURRENT_TIMESTAMP, ?)";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, fromUser);
			pstmt.setString(2, toUser);
			pstmt.setString(3, content);
			pstmt.setInt(4, targetPostID);
			pstmt.setString(5, feedbackType);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (SQLException e) {
			System.err.println("Error submitting feedback: " + e.getMessage());
			return false;
		}
	}

	/*******
	 * <p> Method: getFeedbackForUser() </p>
	 * 
	 * <p> Description: Retrieves all private feedback directed to a user. </p>
	 */
	public List<String[]> getFeedbackForUser(String toUser) {
		List<String[]> feedbackList = new ArrayList<>();
		try {
			String sql = "SELECT feedbackID, fromUser, content, feedbackType, timestamp, isRead " +
						 "FROM Feedback " +
						 "WHERE toUser = ? AND isPrivate = TRUE " +
						 "ORDER BY timestamp DESC";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, toUser);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String[] feedback = new String[6];
				feedback[0] = String.valueOf(rs.getInt("feedbackID"));
				feedback[1] = rs.getString("fromUser");
				feedback[2] = rs.getString("content");
				feedback[3] = rs.getString("feedbackType");
				feedback[4] = rs.getString("timestamp");
				feedback[5] = String.valueOf(rs.getBoolean("isRead"));
				feedbackList.add(feedback);
			}
			pstmt.close();
		} catch (SQLException e) {
			System.err.println("Error retrieving feedback: " + e.getMessage());
		}
		return feedbackList;
	}

	/*******
	 * <p> Method: getFeedbackSentByUser() </p>
	 * 
	 * <p> Description: Retrieves all feedback sent by a user. </p>
	 */
	public List<String[]> getFeedbackSentByUser(String fromUser) {
		List<String[]> feedbackList = new ArrayList<>();
		try {
			String sql = "SELECT feedbackID, toUser, content, feedbackType, timestamp, isRead " +
						 "FROM Feedback " +
						 "WHERE fromUser = ? " +
						 "ORDER BY timestamp DESC";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, fromUser);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				String[] feedback = new String[6];
				feedback[0] = String.valueOf(rs.getInt("feedbackID"));
				feedback[1] = rs.getString("toUser");
				feedback[2] = rs.getString("content");
				feedback[3] = rs.getString("feedbackType");
				feedback[4] = rs.getString("timestamp");
				feedback[5] = String.valueOf(rs.getBoolean("isRead"));
				feedbackList.add(feedback);
			}
			pstmt.close();
		} catch (SQLException e) {
			System.err.println("Error retrieving sent feedback: " + e.getMessage());
		}
		return feedbackList;
	}

	/*******
	 * <p> Method: markFeedbackAsRead() </p>
	 * 
	 * <p> Description: Marks a feedback item as read. </p>
	 */
	public boolean markFeedbackAsRead(int feedbackID) {
		try {
			String sql = "UPDATE Feedback SET isRead = TRUE WHERE feedbackID = ?";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setInt(1, feedbackID);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (SQLException e) {
			System.err.println("Error marking feedback as read: " + e.getMessage());
			return false;
		}
	}

	/*******
	 * <p> Method: getUnreadFeedbackCount() </p>
	 * 
	 * <p> Description: Returns count of unread feedback for a user. </p>
	 */
	public int getUnreadFeedbackCount(String toUser) {
		try {
			String sql = "SELECT COUNT(*) AS count FROM Feedback WHERE toUser = ? AND isRead = FALSE";
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, toUser);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				int count = rs.getInt("count");
				pstmt.close();
				return count;
			}
			pstmt.close();
		} catch (SQLException e) {
			System.err.println("Error getting unread feedback count: " + e.getMessage());
		}
		return 0;
	}

	//GRADING METHODS - BRENNA
	/*******
	 * <p> Method: setPostGrade(int postID, String studentUsername, int points, String gradedBy) </p>
	 *
	 * <p> Description: Saves or updates a point value for a specific post.
	 * If a grade already exists for this postID it is updated (MERGE).
	 * If no grade exists yet, a new row is inserted. </p>
	 *
	 * @param postID          the unique ID of the post being graded
	 * @param studentUsername the username of the student who wrote the post
	 * @param points          the number of points assigned (must be >= 0)
	 * @param gradedBy        the username of the staff member assigning the grade
	 *
	 * @return true if the save succeeded, false if a database error occurred
	 *
	 * <p> Tested by: GradingDisplayTest.testSetPostGrade() </p>
	 */
	public boolean setPostGrade(int postID, String studentUsername, int points, String gradedBy) {
	    // H2 supports MERGE which acts as INSERT-or-UPDATE in one statement.
	    // KEY(postID) means: if a row with this postID already exists, UPDATE it;
	    // otherwise INSERT a new row.
	    String query = "MERGE INTO postGrades (postID, studentUsername, points, gradedBy) "
	        + "KEY(postID) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, postID);
	        pstmt.setString(2, studentUsername);
	        pstmt.setInt(3, points);
	        pstmt.setString(4, gradedBy);
	        pstmt.executeUpdate();
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/*******
	 * <p> Method: getPostsByStudent(String username) </p>
	 *
	 * <p> Description: Retrieves all posts written by a specific student,
	 * joined with any existing grade for each post. If a post has not been
	 * graded yet, the points column will be 0. This is used to populate
	 * the grading table on the GradingDisplay page. </p>
	 *
	 * @param username the username of the student whose posts to load
	 *
	 * @return a List of String arrays, one per post.
	 *         Each array contains: [postID, postContent, thread, points]
	 *
	 * <p> Tested by: GradingDisplayTest.testGetPostsByStudent() </p>
	 */
	public List<String[]> getPostsByStudent(String username) {
	    List<String[]> posts = new ArrayList<>();

	    // LEFT JOIN means we get every post even if it has no grade yet.
	    // COALESCE(g.points, 0) means: use the grade if it exists, else default to 0.
	    String query = "SELECT p.number, p.post, p.thread, COALESCE(g.points, 0) AS points "
	        + "FROM userPosts p "
	        + "LEFT JOIN postGrades g ON p.number = g.postID "
	        + "WHERE p.username = ? "
	        + "ORDER BY p.number ASC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            // Store the four values we need for each row in the grading table
	            String[] row = new String[4];
	            row[0] = String.valueOf(rs.getInt("number"));    // postID
	            row[1] = rs.getString("post");                   // post content
	            row[2] = rs.getString("thread");                 // thread name
	            row[3] = String.valueOf(rs.getInt("points"));    // current grade
	            posts.add(row);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return posts;
	}

	/*******
	 * <p> Method: getTotalPointsByStudent(String username) </p>
	 *
	 * <p> Description: Sums all graded points for a given student across
	 * all their posts. This is displayed as the "Total Points" on the
	 * grading page so staff do not have to add manually. </p>
	 *
	 * @param username the username of the student
	 *
	 * @return the total points awarded to this student (0 if none graded yet)
	 *
	 * <p> Tested by: GradingDisplayTest.testGetTotalPointsByStudent() </p>
	 */
	public int getTotalPointsByStudent(String username) {
	    // SUM all points in postGrades where the student matches.
	    // COALESCE handles the case where no rows exist yet (SUM of nothing = null).
	    String query = "SELECT COALESCE(SUM(g.points), 0) AS total "
	        + "FROM postGrades g "
	        + "WHERE g.studentUsername = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("total");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}

/*******
	 * <p> Method: getGradeSummaryAllStudents() </p>
	 *
	 * <p> Description: Retrieves a grade summary for every student in the
	 * system. For each student, returns their username, total number of
	 * posts, total points awarded across all graded posts, and the average
	 * points per post. Students with no posts are excluded.
	 * Used to populate the Grade Summary dialog on the staff home page. </p>
	 *
	 * @return a List of String arrays, one per student.
	 *         Each array contains:
	 *         [0] studentUsername
	 *         [1] totalPosts    (total posts written by this student)
	 *         [2] totalPoints   (sum of all graded points, 0 if none graded)
	 *         [3] average       (totalPoints / totalPosts, formatted to 1 decimal)
	 *
	 */
	public List<String[]> getGradeSummaryAllStudents() {
	    List<String[]> summary = new ArrayList<>();

	    String query = "SELECT p.username, "
	        + "COUNT(p.number) AS totalPosts, "
	        + "COALESCE(SUM(g.points), 0) AS totalPoints "
	        + "FROM userPosts p "
	        + "LEFT JOIN postGrades g ON p.number = g.postID "
	        + "JOIN userDB u ON p.username = u.userName "
	        + "WHERE u.newRole1 = TRUE "
	        + "GROUP BY p.username "
	        + "ORDER BY totalPoints DESC";  
	 
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String[] row = new String[4];
	            row[0] = rs.getString("username");          // student name
	 
	            int totalPosts  = rs.getInt("totalPosts");
	            int totalPoints = rs.getInt("totalPoints");
	 
	            row[1] = String.valueOf(totalPosts);         // total posts
	            row[2] = String.valueOf(totalPoints);        // total points
	 
	            // Calculate average 
	            if (totalPosts > 0) {
	                double avg = (double) totalPoints / totalPosts;
	                row[3] = String.format("%.1f", avg);     
	            } else {
	                row[3] = "0.0";
	            }
	 
	            summary.add(row);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return summary;
	}
	

	
}
