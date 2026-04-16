package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Feedback Class </p>
 * 
 * <p> Description: This Feedback class represents private feedback in the system.
 * It tracks feedback from staff to students or other staff. </p>
 * 
 * @author Kyle Porche
 * 
 */
public class Feedback {
    
    private int feedbackID;
    private String fromUser;           // Staff member sending feedback
    private String toUser;             // Student or staff receiving feedback
    private String content;            // Feedback message
    private int relatedPostID;         // Post ID (optional, null if general feedback)
    private LocalDateTime timestamp;
    private boolean isRead;
    private String feedbackType;       // COACHING, CONCERN, GENERAL, PRAISE
    
    /**
     * Default constructor
     */
    public Feedback() {
    }
    /**
     * Constructor for creating new feedback
     */
    public Feedback(String fromUser, String toUser, String content, int relatedPostID, String feedbackType) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.content = content;
        this.relatedPostID = relatedPostID;
        this.feedbackType = feedbackType;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
    
    public int getFeedbackID() { return feedbackID; }
    public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }
    
    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }
    
    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getRelatedPostID() { return relatedPostID; }
    public void setRelatedPostID(int relatedPostID) { this.relatedPostID = relatedPostID; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public String getFeedbackType() { return feedbackType; }
    public void setFeedbackType(String feedbackType) { this.feedbackType = feedbackType; }
    
    @Override
    public String toString() {
        return String.format("From: %s | To: %s | Type: %s | %s | Read: %s",
                fromUser, toUser, feedbackType, timestamp, isRead);
    }
}
