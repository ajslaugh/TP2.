package entityClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*******
 * <p> Title: Request Class </p>
 * 
 * <p> Description: This Request class represents a new request being made.  It contains the requests
 *  content as well as the staff member name who made it and the url of the original request if applicable. </p>
 * 
 * 
 * @author Alexia Slaughter
 * 
 * 
 */ 

public class Request {
	
    /**
	 * The username of the staff member..
	 * 
	 * <p>Rationale: This attribute is required to associate each request
	 * with the staff member who created it.</p>
	 */
    private String staffMember;

	 /**
     * The main textual content of the request.
     * 
     * <p>Rationale: Stores the user generated message, which is the core
     * functionality of the request system.</p>
     *
	 */
    private String content;


	/**
     * The url of the original post if applicable. 
     * 
     * <p>Rationale: Stores the url of the original post if making an adjustment to a request previously closed.</p> 
     */
    private int url;
    

	/**
     * The id of the request. 
     * 
     * <p>Rationale: Stores the id of the post to track for admin notes during request closing.</p> 
     */
    private int id;
    
    /*****
     * <p> timeStamp of the request </p>
     * 
     * <p> Rationale: Stores time stamp of request so admins know how urgently they should get to it.   </p>
     */
    private String timeStamp;
    
    private int index;

	

    
    /*****
     * <p> Method: Request() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public Request() {
    	
    }
    
    /*****
     * <p> Method: Post(String staffMember, String content) </p>
     * 
     * <p> Description: This constructor is used to establish the request object. </p>
     * 
     * @param staffMember specifies the staff member who created the request.
     * 
     * @param content specifies content of the request
     */   

    // Create Request
    public Request(String staffMember, String content) {
    	this.staffMember = staffMember;
    	this.content = content;
    	this.url = -1;
    	
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' h:mm a");
        this.timeStamp = now.format(formatter);
   }
    
    /*****
     * <p> Method: Post(String staffMember, String content, String url) </p>
     * 
     * <p> Description: This constructor is used to establish the request object that has been previously closed. </p>
     * 
     * @param staffMember specifies the staff member who created the request.
     * 
     * @param content specifies content of the request
     * 
     * @param url specifies url of the original request
     */   
    
    public Request(String staffMember, String content, int url, int id) {
    	this.staffMember = staffMember;
    	this.content = content;
    	this.url = url;
    	this.id = id;
    	
    	 LocalDateTime now = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' h:mm a");
         this.timeStamp = now.format(formatter);
    }
    
    public Request(String staffMember, String content, int url) {
    	this.staffMember = staffMember;
    	this.content = content;
    	this.url = url;
    	
    	 LocalDateTime now = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' h:mm a");
         this.timeStamp = now.format(formatter);
    }
    

	
    /*****
     * <p> Method: String getContent() </p>
     * 
     * <p> Description: This getter returns the content of the request. </p>
     * 
     * @return a String of the content
	 *
     */
    // get request content
    public String getContent() { return content; }
    

    /*****
     * <p> Method: String getUrl() </p>
     * 
     * <p> Description: This getter returns the url of the original post. </p>
     * 
     * @return a String of the original post url
	 *
     */
    
    public int geturl() { return url;}
    
    /*****
     * <p> Method: String getStaffMember() </p>
     * 
     * <p> Description: This getter returns the staff member who created the post. </p>
     * 
     * @return a String of the staff member username
	 *
     */
    
    public String getStaffMember() { return staffMember;}
    
    /*****
     * <p> Method: String getId() </p>
     * 
     * <p> Description: This getter returns the id of a request for admin responses. </p>
     * 
     * @return an int representing the id
	 *
     */
    
    
    public String getTimeStamp() { return timeStamp;}
    
    /*****
     * <p> Method: String getId() </p>
     * 
     * <p> Description: This getter returns the id of a request for admin responses. </p>
     * 
     * @return an int representing the id
	 *
     */
        
    
    public int getId() { return id;}
    
    /*****
     * <p> Method: String getIndex() </p>
     * 
     * <p> Description: This getter returns the index. </p>
     * 
     * @return an int representing the index
	 *
     */
    
    public int getIndex() { return index;}
    

    /*****
     * <p> Method: void setContent(string s) </p>
     * 
     * <p> Description: This sets the content of the request. . </p>
     * 
	 *@param s is the new content of the request
     */
   
    public void setContent(String s) { content = s; }

	/**
     * Updates the staff member username of the request
     * 
     * 
     * @param s the new staff member name
     */
    public void setStaffMember(String s) { staffMember = s; }
    
    /**
     * Updates the time stamp of a request
     * 
     * 
     * @param s the new timestamp
     */
    
    public void setTimeStamp(String s) { timeStamp = s;}
    
    /**
     *Updates the original request index of the request
     * 
     * 
     * @param s the new request index
     */
    
    public void setIndex(int s) {index = s;}
    
	/**
     *Updates the original request url of the request
     * 
     * 
     * @param s the new request url
     */
    public void setUrl(int s) { url = s; }
    
    /**
     *Updates the id of the request
     * 
     * 
     * @param s the new requestid
     */
    
    public void setId(int s) { id = s;}
   
    
    
    /**
     * Overrides the To String for easy access
     */
    @Override
    public String toString() {
    	if(url >= 0) {
    		return String.format("REQUEST RESUBMITTED %s BY %s: \n %s", timeStamp, staffMember, content);
    	}
    	return String.format("REQUEST SUBMITTED %s BY %s: \n %s", timeStamp, staffMember, content);
   
    }
    
	
	
}
