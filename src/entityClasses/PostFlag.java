package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: PostFlag Class </p>
 * 
 * <p> Description: This PostFlag class represents a flagged post in the system.
 * It tracks which staff member flagged a post, the reason, and the status of the flag. </p>
 * 
 * @author Kyle Porche
 * 
 */ 
public class PostFlag {
    
    private int flagID;
    private int postID;
    private String flaggedBy;        // Username of staff who flagged it
    private String reason;           // Why it was flagged
    private LocalDateTime timestamp;
    private String status;           // PENDING, REVIEWED, APPROVED, RESOLVED
    private String notes;            // Additional notes from staff

    /*****
     * <p> Method: PostFlag() </p>
     * 
     * <p> Description: Default constructor. </p>
     */
    public PostFlag() {
    }

    /*****
     * <p> Method: PostFlag(int postID, String flaggedBy, String reason) </p>
     * 
     * <p> Description: Constructor to create a new post flag. </p>
     * 
     * @param postID the ID of the post being flagged
     * @param flaggedBy the username of the staff member flagging
     * @param reason why the post is being flagged
     */
    public PostFlag(int postID, String flaggedBy, String reason) {
        this.postID = postID;
        this.flaggedBy = flaggedBy;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.notes = "";
    }

    public int getFlagID() { return flagID; }
    public int getPostID() { return postID; }
    public String getFlaggedBy() { return flaggedBy; }
    public String getReason() { return reason; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void setFlagID(int flagID) { this.flagID = flagID; }
    public void setPostID(int postID) { this.postID = postID; }
    public void setFlaggedBy(String flaggedBy) { this.flaggedBy = flaggedBy; }
    public void setReason(String reason) { this.reason = reason; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("PostFlag [ID: %d, Post: %d, Flagged by: %s, Reason: %s, Status: %s]",
                flagID, postID, flaggedBy, reason, status);
    }
}
