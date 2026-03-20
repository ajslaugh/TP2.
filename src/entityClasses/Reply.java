package entityClasses;
/*******
 * <p> Title: Reply Class </p>
 * 
 * <p> Description: The reply class represents a reply post in the system.  It contains the user's
 *  details such as author, content, role, thread and integer post replied to.  </p>
 * 
 *
 * 
 * @author Alex Slaughter
 * 
 * 
 */ 
//HW2 reply class
public class Reply {
	
	
    private String author = "";
    private String content = "";
    private String role = "";
    private String thread = "General";
    private int replyTo;
    
    /*****
     * <p> Method: Reply() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
   
    public Reply() {
    	
    }

    /*****
     * <p> Method: Reply(String author, String role, String Content, String thread, int ogPost) </p>
     * 
     * <p> Description: This constructor is used to establish user reply objects. </p>
     * 
     * @param author specifies the author of the user
     * 
     * @param role specifies the role of the author
     * 
     * @param content specifies content of the reply
     * 
     * @param thread specifies the thread reply is posted in 
     * 
     * @param og post specifies what the original post this reply goes to. 
     * 
     */   
 
    // Constructor to initialize a new Post with author, role, content, and thread.
    public Reply(String author, String role, String content, String thread, int ogPost) {
        this.author = author;
        this.role = role;
        this.content = content;
        this.thread = thread;
        this.replyTo = ogPost;
       
    }
    
    


    /*****
     * <p> Method: String getContent() </p>
     * 
     * <p> Description: This getter returns the content. </p>
     * 
     * @return a String of the content
	 *
     */
    public String getContent() { return content; }
    
    /*****
     * <p> Method: String getAuthor() </p>
     * 
     * <p> Description: This getter returns the author name. </p>
     * 
     * @return a String of the author
	 *
     */
    
    public String getAuthor() { return author;}
    /*****
     * <p> Method: String getRole() </p>
     * 
     * <p> Description: This getter returns the role of the author. </p>
     * 
     * @return a String of the role
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
    
    /*****
     * <p> Method: int getogPost() </p>
     * 
     * <p> Description: This getter returns the integer of where the og post is located. </p>
     * 
     * @return a int of the ogPost.
	 *
     */
    
    public int getogPost() {return replyTo;}

   //Update Reply
    public void setContent(String s) { content = s; }
    public void setAuthor(String s) { author = s; }
    public void setRole(String s) { role = s; }
    public void setThread(String s) { thread = s; }
    public void setogPost(int s) { replyTo = s;}
 
    
   
  

 
}
