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
	
	/*
	 * These are the private attributes for this entity object
	 */
	
    
  
    private String author;
    private String content;
    private String role;
    private String thread;
    private int postID;
    
    
    
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
    
    public int getID() {return postID;}    
    
    public String toString() {
    	return String.format("%s %s to %s thread: %s", role, author, thread, content);
    	
    }


    

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

   
    public void setContent(String s) { content = s; }
    public void setAuthor(String s) { author = s; }
    public void setRole(String s) { role = s; }
    public void setThread(String s) { thread = s; }
    public void setID(int s) {postID = s;}
    
    
    

 
}
