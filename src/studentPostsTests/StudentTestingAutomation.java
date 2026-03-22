package studentPostsTests;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.collections.ObservableList;

/*******
 * <p> Title: StudentTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests for the
 * TP2 Student Discussion System. A sequence of test cases will examine the program's
 * logic and database behavior. The console output will inform us which tests have
 * passed or failed. These tests are designed with the Student User Story requirements
 * in mind. </p>
 * 
 * <p> TP2 Student Requirements: </p>
 * <ul>
 *   <li>1: A student can post statements and questions and receive replies.</li>
 *   <li>2: A student can see a list of posts others have made.</li>
 *   <li>3: A student can see their own posts, the number of replies, and unread replies.</li>
 *   <li>4: A student can search for posts matching specified keywords.</li>
 *   <li>5: A student can post to different threads.</li>
 *   <li>6: A student can delete one of their posts while replies remain.</li>
 *   <li>7: If no thread is specified during a search, all threads are searched.</li>
 *   <li>8: If no thread is specified when creating a post, the thread defaults to General.
 *       This requirement will be tested manually.</li>
 *   <li>9: A delete confirmation message appears before a post is deleted.
 *       This requirement will be tested manually.</li>
 *   <li>10: When replies are viewed for a deleted post, a message is shown
 *       indicating that the original post has been deleted. This requirement will be tested manually.</li>
 * </ul>
 * 
 * <ul>
 *   <li>***Success*** -> behavior matched expectations</li>
 *   <li>***Failure*** -> behavior did not match expectations</li>
 * </ul>
 * 
 * @author Gabriella Romero
 */
public class StudentTestingAutomation {

	/******* Counter for the number of tests passed. */
	static int numPassed = 0;

	/******* Counter for the number of tests failed. */
	static int numFailed = 0;

	/******* Shared database object used during testing. */
	static Database db;

	/*******
	 * <p> Method: main(String[] args) </p>
	 * 
	 * <p> Description: This mainline displays a header to the console, performs a
	 * sequence of test cases, and then displays a footer with a summary of the results. </p>
	 * 
	 * @param args N/A
	 * 
	 * @throws SQLException if a database operation fails during testing
	 */
	public static void main(String[] args) throws SQLException {
		System.out.println("____________________________________________________________________________");
		System.out.println("\nStudent Discussion System Testing Automation");

		initializeTestEnvironment();

		testCreatePost(1);
		testCreateReplyAndUnreadCount(2);
		testSearchPostsByKeyword(3);
		testSearchAcrossAllThreads(4);
		testGetPostsByThread(5);
		testGetOwnPosts(6);
		testGetUnreadPosts(7);
		testDeletePostKeepsReplies(8);

		db.closeConnection();

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/*******
	 * <p> Method: initializeTestEnvironment() </p>
	 * 
	 * <p> Description: This method creates the database object and connects it to the
	 * database for test case simulations. </p>
	 * 
	 * @throws SQLException if a database operation fails during testing
	 */
	private static void initializeTestEnvironment() throws SQLException {
		db = new Database();
		db.connectToDatabase();
	}

	/*******
	 * <p> Test 1: Create a student post. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>1: A student can post statements and questions and receive replies.</li>
	 *   <li>5: A student can post to different threads.</li>
	 * </ul>
	 *  
	 * <p> Description: This test creates a new post and inserts it into the database.
	 * The list of posts is retrieved and checked for matching content to verify the 
	 * post exists. </p>
	 * 
	 * <p> Expected result: The post should be found in the database and match
	 * the expected values. </p>
	 * 
	 * @param testCase test case 1
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testCreatePost(int testCase) throws SQLException {
		displayTestHeader(testCase, "Create a student post");

		// create a new post and store it in the database
		Post p = new Post("student1", "Student", "i need help with assignment 2", "General");
		db.addPost(p);

		// retrieve all posts and check for the new one
		ObservableList<Post> posts = db.displayPostHelper();
		Post found = findPost(posts, "student1", "i need help with assignment 2");

		// verify the post exists and matches the thread
		boolean passed = found != null &&
				found.getThread().equals("General");

		displayResult(passed,
				"The student post was successfully stored and retrieved.",
				"The student post could not be found.");
	}

	/*******
	 * <p> Test 2: Create a reply and update unread count. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>1: A student can post statements and questions and receive replies.</li>
	 *   <li>3: A student can see their own posts, the number of replies, and unread replies.</li>
	 * </ul>
	 * 
	 * <p> Description: This test creates a post, stores it, adds a reply, and updates
	 * the reply and unread counts. It verifies that one reply exists and
	 * that the unread count is correct. </p>
	 * 
	 * <p> Expected result: The post should have one reply and an unread count of one. </p>
	 * 
	 * @param testCase test case 2
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testCreateReplyAndUnreadCount(int testCase) throws SQLException {
		displayTestHeader(testCase, "Create a reply and update unread count");

		// create a post for the original author
		Post p = new Post("studentOwner", "Student", "when are office hours?", "General");
		db.addPost(p);

		// retrieve the stored post to get its ID
		Post stored = findPost(db.displayPostHelper(), "studentOwner", "when are office hours?");

		// create a reply linked to the original post
		Reply r = new Reply("studentResponder", "Student",
				"4-5pm", "General", stored.getID());

		// store the reply and update counts
		db.addReply(r);
		db.updateReplyNumber(stored);
		db.updateAuthorUnread(stored);
		db.updateUserUnread(stored);

		// retrieve replies and unread count
		List<String[]> replies = db.getRepliesFromPost(stored.getID());
		int unread = db.getAuthorUnread(stored);

		// verify reply count and unread count
		boolean passed = replies.size() == 1 && unread == 1;

		displayResult(passed,
				"The reply was stored and unread count updated correctly.",
				"The reply or unread count did not update correctly.");
	}

	/*******
	 * <p> Test 3: Search posts by keyword. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>2: A student can see a list of posts others have made.</li>
	 *   <li>4: A student can search for posts matching specified keywords.</li>
	 * </ul>
	 * 
	 * <p> Description: This test creates two posts and performs a keyword search for one 
	 * of the posts. It verifies that only the matching post with the keyword is 
	 * returned. </p>
	 * 
	 * <p> Expected result: The matching post should be returned and the non-matching
	 * post should not appear in the results. </p>
	 * 
	 * @param testCase test case 3
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testSearchPostsByKeyword(int testCase) throws SQLException {
		displayTestHeader(testCase, "Search posts by keyword");

		// insert two posts, only one contains the keyword
		db.addPost(new Post("student2", "Student", "exam study guide", "General"));
		db.addPost(new Post("student3", "Student", "assignment 3", "Homework"));

		// perform keyword search
		ObservableList<Post> results = db.searchPostsByKeyword("study");

		// verify only the matching post is returned
		boolean passed = containsPost(results, "exam study guide") &&
				!containsPost(results, "assignment 3");

		displayResult(passed,
				"Keyword search returned correct results.",
				"Keyword search returned incorrect results.");
	}

	/*******
	 * <p> Test 4: Search across all threads. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>4: A student can search for posts matching specified keywords.</li>
	 *   <li>7: If no thread is specified during a search, all threads are searched.</li>
	 * </ul> 
	 * 
	 * <p> Description: This test creates posts in different threads and performs
	 * a keyword search without specifying a thread. It verifies that all threads
	 * are searched when a thread is not specified. </p>
	 * 
	 * <p> Expected result: Posts from different threads that match the keyword
	 * should be included in the results. </p>
	 * 
	 * @param testCase test case 4
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testSearchAcrossAllThreads(int testCase) throws SQLException {
		displayTestHeader(testCase, "Search all threads");

		// insert posts into different threads
		db.addPost(new Post("student4", "Student", "project question", "General"));
		db.addPost(new Post("student5", "Student", "project 2 help", "Homework"));

		// search without specifying a thread
		ObservableList<Post> results = db.searchPostsByKeyword("project");

		// verify results include posts from all threads
		boolean passed = results.size() >= 2;

		displayResult(passed,
				"Search included all threads.",
				"Search did not include all threads.");
	}

	/*******
	 * <p> Test 5: Retrieve posts by thread. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>5: A student can post to different threads.</li>
	 * </ul> 
	 * 
	 * <p> Description: This test creates a post in a specific thread and retrieves
	 * posts using that thread filter. It verifies that all returned posts belong to the
	 * selected thread. </p>
	 * 
	 * <p> Expected result: All returned posts should match the specified thread. </p>
	 * 
	 * @param testCase test case 5
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testGetPostsByThread(int testCase) throws SQLException {
		displayTestHeader(testCase, "Retrieve posts by thread");

		// insert a post into a specific thread
		db.addPost(new Post("student6", "Student", "exam review date", "Exams"));

		// retrieve posts filtered by thread
		ObservableList<Post> posts = db.getPostsByThread("Exams");

		// verify all returned posts belong to the selected thread
		boolean passed = !posts.isEmpty() && threadMatch(posts, "Exams");

		displayResult(passed,
				"Thread filtering worked correctly.",
				"Thread filtering failed.");
	}

	/*******
	 * <p> Test 6: Retrieve a student's own posts. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>3: A student can see their own posts, the number of replies, and unread replies.</li>
	 * </ul> 
	 * 
	 * <p> Description: This test inserts posts from two users into the database and retrieves
	 * the current user’s posts. It verifies that only the correct posts are
	 * returned. </p>
	 * 
	 * <p> Expected result: Only posts created by the current user should be returned. </p>
	 * 
	 * @param testCase test case 6
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testGetOwnPosts(int testCase) throws SQLException {
		displayTestHeader(testCase, "Retrieve own posts");

		// create and register a test user
		User u = buildUser("ownerStudent");
		db.register(u);
		db.getUserAccountDetails("ownerStudent");

		// insert posts from two different users
		db.addPost(new Post("ownerStudent", "Student", "my post", "General"));
		db.addPost(new Post("other", "Student", "other post", "General"));

		// retrieve posts for the current user
		ObservableList<Post> posts = db.getOwnPosts();

		// verify only the current user's posts are returned
		boolean passed = authorMatch(posts, "ownerStudent");

		displayResult(passed,
				"Only the student's posts were returned.",
				"Own posts retrieval failed.");
	}

	/*******
	 * <p> Test 7: Retrieve unread posts. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>3: A student can see their own posts, the number of replies, and unread replies.</li>
	 * </ul> 
	 * 
	 * <p> Description: This test creates a post for a user, adds a reply, and updates
	 * unread counts. It then retrieves unread posts to verify that the post appears in
	 * the unread list. </p>
	 * 
	 * <p> Expected result: The post should appear in the unread posts list. </p>
	 * 
	 * @param testCase test case 7
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testGetUnreadPosts(int testCase) throws SQLException {
		displayTestHeader(testCase, "Retrieve unread posts");

		// create and register a test user
		User u = buildUser("unreadUser");
		db.register(u);
		db.getUserAccountDetails("unreadUser");

		// create a post for the user
		db.addPost(new Post("unreadUser", "Student", "help me", "General"));
		Post stored = findPost(db.displayPostHelper(), "unreadUser", "help me");

		// add a reply and update unread counts
		db.addReply(new Reply("helper", "Student", "i can help", "General", stored.getID()));
		db.updateReplyNumber(stored);
		db.updateAuthorUnread(stored);
		db.updateUserUnread(stored);

		// retrieve unread posts
		ObservableList<Post> unread = db.getUnreadPosts();

		// verify the post appears in unread list
		boolean passed = !unread.isEmpty();
		
		displayResult(passed,
				"Unread posts retrieval succeeded.",
				"Unread posts retrieval failed.");
	}

	/*******
	 * <p> Test 8: Delete a post while keeping replies. </p>
	 * 
	 * <p> Requirement(s) covered:</p>
	 * <ul>
	 *   <li>6: A student can delete one of their posts while replies remain.</li>
	 * </ul> 
	 * 
	 * <p> Description: This test creates a post, adds a reply, and deletes the post.
	 * It then verifies that the post is deleted while the reply still exists. </p>
	 * 
	 * <p> Expected result: The post should be deleted and its replies should remain accessible. </p>
	 * 
	 * @param testCase test case 8
	 * 
	 * @throws SQLException if a database operation fails during the test
	 */
	private static void testDeletePostKeepsReplies(int testCase) throws SQLException {
		displayTestHeader(testCase, "Delete post but keep replies");

		// create a post to be deleted
		db.addPost(new Post("deleteUser", "Student", "to be deleted", "General"));
		Post stored = findPost(db.displayPostHelper(), "deleteUser", "to be deleted");

		// add a reply to the post
		db.addReply(new Reply("replyUser", "Student",
				"this reply should still be visible", "General", stored.getID()));

		// delete the post
		boolean deleted = db.deletePost(stored);

		// retrieve replies after deletion
		List<String[]> replies = db.getRepliesFromPost(stored.getID());

		// verify post is deleted but reply remains
		boolean passed = deleted && replies.size() == 1;

		displayResult(passed,
				"Post deleted and replies preserved.",
				"Delete behavior failed.");
	}

	// **Helper Methods**
	
	/*******
	 * <p> Method: displayTestHeader(int testCase, String title) </p>
	 * 
	 * <p> Description: Displays the header information for an individual test case. </p>
	 * 
	 * @param testCase the number of the test case
	 * @param title the title of the test case
	 */
	private static void displayTestHeader(int testCase, String title) {
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTest case: " + testCase);
		System.out.println("Title: " + title);
		System.out.println("______________");
	}

	/*******
	 * <p> Method: displayResult(boolean passed, String success, String failure) </p>
	 * 
	 * <p> Description: Displays the result of a test case and updates the pass/fail counters. </p>
	 * 
	 * @param passed true if the test passed, otherwise false
	 * @param success displays when the test passes
	 * @param failure displays when the test fails
	 */
	private static void displayResult(boolean passed, String success, String failure) {
		System.out.println();

		if (passed) {
			System.out.println("***Success*** " + success);
			numPassed++;
		} else {
			System.out.println("***Failure*** " + failure);
			numFailed++;
		}
	}

	/*******
	 * <p> Method: findPost(ObservableList<Post> posts, String author, String content) </p>
	 * 
	 * <p> Description: Searches a list of posts for a specific post based on author and content. </p>
	 * 
	 * @param posts the list of posts to search
	 * @param author author's name
	 * @param content post content
	 * 
	 * @return the matching post if found, otherwise null
	 */
	private static Post findPost(ObservableList<Post> posts, String author, String content) {
		for (Post p : posts) {
			if (p.getAuthor().equals(author) && p.getContent().equals(content)) {
				return p;
			}
		}
		return null;
	}

	/*******
	 * <p> Method: containsPost(ObservableList<Post> posts, String content) </p>
	 * 
	 * <p> Description: Checks whether a list of posts contains a post with the given content. </p>
	 * 
	 * @param posts the list of posts to search
	 * @param content post content
	 * 
	 * @return true if a matching post is found, otherwise false
	 */
	private static boolean containsPost(ObservableList<Post> posts, String content) {
		for (Post p : posts) {
			if (p.getContent().equals(content)) return true;
		}
		return false;
	}

	/*******
	 * <p> Method: threadMatch(ObservableList<Post> posts, String thread) </p>
	 * 
	 * <p> Description: Checks that all posts in the list belong to the specified thread. </p>
	 * 
	 * @param posts the list of posts to check
	 * @param thread thread name
	 * 
	 * @return true if every post matches the thread, otherwise false
	 */
	private static boolean threadMatch(ObservableList<Post> posts, String thread) {
		for (Post p : posts) {
			if (!p.getThread().equals(thread)) return false;
		}
		return true;
	}

	/*******
	 * <p> Method: authorMatch(ObservableList<Post> posts, String author) </p>
	 * 
	 * <p> Description: Checks that all posts in the list were created by the specified author. </p>
	 * 
	 * @param posts the list of posts to check
	 * @param author expected author name
	 * 
	 * @return true if every post matches the author, otherwise false
	 */
	private static boolean authorMatch(ObservableList<Post> posts, String author) {
		for (Post p : posts) {
			if (!p.getAuthor().equals(author)) return false;
		}
		return true;
	}

	/*******
	 * <p> Method: buildUser(String username) </p>
	 * 
	 * <p> Description: Creates a test user with default values for use in automated tests. </p>
	 * 
	 * @param username the username assigned to the test user
	 * 
	 * @return a User object with default values assigned
	 */
	private static User buildUser(String username) {
		User u = new User();
		u.setUserName(username);
		u.setPassword("pass");
		u.setFirstName("first");
		u.setMiddleName("middle");
		u.setLastName("last");
		u.setPreferredFirstName("first");
		u.setEmailAddress(username + "@test.com");
		u.setRole1User(true);
		return u;
	}
}
