package entityClasses;

/*******
 * <p> Title: ThreadType Class </p>
 * 
 * <p> Description: The ThreadType class represents a discussion thread in the system.
 * It contains details such as the thread name, description, status (active vs. archived),
 * and the associated ID. Staff members use this class to manage and organize
 * discussion topics. </p>
 * 
 * @author Gabriella Romero
 * 
 */

public class ThreadType {
    
    private int id;
    private String name;
    private String description;
    private String status; // used to determine whether the thread is open for posting or archived


    /*****
     * <p> Method: ThreadType() </p>
     * 
     * <p> Description: This default constructor initializes an empty ThreadType object.
     * It may be used when creating a thread object before setting its values. </p>
     */
    public ThreadType() {}

    /*****
     * <p> Method: ThreadType(String name, String description, String status) </p>
     * 
     * <p> Description: This constructor creates a ThreadType object with a name,
     * description, and status. </p>
     * 
     * @param name the name of the thread
     * @param description the description of the thread
     * @param status whether the thread is active or archived
     */
    public ThreadType(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /*****
     * <p> Method: int getId() </p>
     * 
     * <p> Description: This getter returns the unique ID associated with the thread. </p>
     * 
     * @return an integer representing the thread ID
     */
    public int getId() { return id; }

    /*****
     * <p> Method: void setId(int id) </p>
     * 
     * <p> Description: This setter assigns a unique ID to the thread. </p>
     * 
     * @param id specifies the thread ID
     */
    public void setId(int id) { this.id = id; }

    /*****
     * <p> Method: String getName() </p>
     * 
     * <p> Description: This getter returns the name of the thread. </p>
     * 
     * @return a String representing the thread name
     */
    public String getName() { return name; }

    /*****
     * <p> Method: void setName(String name) </p>
     * 
     * <p> Description: This setter updates the name of the thread. </p>
     * 
     * @param name specifies the new thread name
     */
    public void setName(String name) { this.name = name; }

    /*****
     * <p> Method: String getDescription() </p>
     * 
     * <p> Description: This getter returns the description of the thread. </p>
     * 
     * @return a String representing the thread description
     */
    public String getDescription() { return description; }

    /*****
     * <p> Method: void setDescription(String description) </p>
     * 
     * <p> Description: This setter updates the thread description. </p>
     * 
     * @param description specifies the new thread description
     */
    public void setDescription(String description) { this.description = description; }

    /*****
     * <p> Method: String getStatus() </p>
     * 
     * <p> Description: This getter returns the current status of the thread. </p>
     * 
     * @return a String representing the thread status (active vs. archived)
     */
    public String getStatus() { return status; }

    /*****
     * <p> Method: void setStatus(String status) </p>
     * 
     * <p> Description: This setter updates the thread status. </p>
     * 
     * @param status specifies whether the thread is active or archived
     */
    public void setStatus(String status) { this.status = status; }

    /*****
     * <p> Method: String toString() </p>
     * 
     * <p> Description: This method returns a formatted string representation of the
     * thread, including its name, description, and status. </p>
     * 
     * @return a String representing the thread details
     */
    @Override
    public String toString() {
        return name + " | " + description + " | " + status;
    }
}
