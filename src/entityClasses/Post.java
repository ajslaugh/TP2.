package entityClasses;

/*******
 * <p> Title: Post Class </p>
 * 
 * <p> Description: This Post class represents a postin the system.  It contains the posts
 *  content as well as the thread, the author, and the role they hold. </p>
 * 
 * 
 * @author Alexia Slaughter
 * 
 * 
 */ 
//HW2 post class
public class Post {
	
    /**
	 * The username or identifier of the post's author.
	 * 
	 * <p>Rationale: This attribute is required to associate each post
	 * with the user who created it, as defined in student user stories.</p>
	 */
    private String author;

	 /**
     * The main textual content of the post.
     * 
     * <p>Rationale: Stores the user generated message, which is the core
     * functionality of the post system.</p>
     *
	 */
    private String content;

	/**
     * The role of the user who created the post.
     * 
     * <p>Rationale: Supports role based behavior and future staff related
     * features such as moderation and permissions.</p>
     */
    private String role;

	/**
     * The thread  to which this post belongs.
     * 
     * <p>Rationale: Enables grouping of posts and supports discussion based
     * organization required by user stories.</p> 
     */
    private String thread;

	/**
     * The unique identifier associated with a post.
     * 
     * <p>Rationale: Enables a user to search for a specific post 
     * they are looking to delete or reply to or view. 
     */
    private int postID;
    
    //JERRY ADDING ENDORSEMENT FIELD
	private int endorsed = 0;
    
    /*****
     * <p> Method: Post() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Post() {
    	
    }
    
    /*****
     * <p> Method: Post(String author, String role, String Content, String thread) </p>
     * 
     * <p> Description: This constructor is used to establish user post objects. </p>
     * 
     * @param author specifies the author of the post
     * 
     * @param role specifies the role of the author
     * 
     * @param content specifies content of the post
     * 
     * @param thread specifies the thread post is in 
     * 
     */   

    // Create Post
    public Post(String author, String role, String content, String thread) {
        this.author = author;
        this.role = role;
        this.content = content;
        this.thread = thread;
       /* this.adminRole = r1;
        this.role1 = r2;
        this.role2 = r3; */
    }

	/*****
     * <p> Method: int getId() </p>
     * 
     * <p> Description: This getter returns the unique ID of the post. </p>
     * 
     * @return an int of the post's ID
	 *
     */
    public int getID() {return postID;}    

    /*****
     * <p> Method: String getContent() </p>
     * 
     * <p> Description: This getter returns the content. </p>
     * 
     * @return a String of the content
	 *
     */
    // Update Post
    public String getContent() { return content; }
    

    /*****
     * <p> Method: String getAuthor() </p>
     * 
     * <p> Description: This getter returns the author. </p>
     * 
     * @return a String of the author name
	 *
     */
    
    public String getAuthor() { return author;}
    

    /*****
     * <p> Method: String getRole() </p>
     * 
     * <p> Description: This getter returns the role. </p>
     * 
     * @return a String of the role.
	 *
     */
    
    public String getRole() { return role;}
    

    /*****
     * <p> Method: String getThread() </p>
     * 
     * <p> Description: This getter returns the thread. </p>
     * 
     * @return a String of the thread. 
	 *
     */
    
    public String getThread() { return thread;} 

    /**
     * Updates the content of the post.
     * 
     * <p>This method supports the student user story that allows users
     * to modify their posts. Validation may be applied to ensure the
     * content meets system requirements.</p>
     * 
     * @param s the new content of the post
     */

	//JERRY SETTING ENDORSEMENT VALUE
	public void setEndorsed(int e) { endorsed = e; }
    public void setContent(String s) { content = s; }

	/**
     * Updates the author of the post.
     * 
     * <p>Rationale: Allows reassignment or correction of post ownership,
     * which may be necessary for administrative operations.</p>
     * 
     * @param s the new author name
     */
    public void setAuthor(String s) { author = s; }
	
	/**
     * Updates the role associated with the post.
     * 
     * <p>Rationale: Supports role-based behavior such as distinguishing
     * between students and staff for future system features.</p>
     * 
     * @param s the new role value
     */
    public void setRole(String s) { role = s; }

	/**
     * Updates the thread to which this post belongs.
     * 
     * <p>Rationale: Enables reorganizing posts within different discussion
     * threads as required by user interactions.</p>
     * 
     * @param s the new thread identifier
     */
    public void setThread(String s) { thread = s; }

	/**
	* Updates the unique identifier of the post.
	* 
	* <p>Rationale: Allows the system to assign or modify the post ID,
	* which may be necessary during data initialization, migration,
	* or administrative corrections. The ID should remain unique
	* within the system.</p>
	* 
	* @param s the new post ID value
	*/
    public void setID(int s) {postID = s;}
//JERRY ADDED CHANGES TO toString
	@Override 
public String toString() {
	//DISPLAY ENDORSED POSTS
	if (endorsed == 1) {
		return "[ENDORSED] " + String.format("%s %s to %s thread: %s", role, author, thread, content);
	}

	return String.format("%s %s to %s thread: %s", role, author, thread, content);
}
}
