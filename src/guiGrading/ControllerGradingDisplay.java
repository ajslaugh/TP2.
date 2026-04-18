package guiGrading;

import database.Database;
import javafx.scene.control.Alert;
import java.util.List;

/*******
 * <p> Title: ControllerGradingDisplay Class </p>
 *
 * <p> Description: The Controller for the Grading Display page. Follows the
 * exact same pattern as ControllerRole2Home and ControllerPostDisplay:
 * all methods are protected and static, the class is never instantiated,
 * and it acts as the bridge between ViewGradingDisplay and the Database. </p>
 *
 * <p> This controller handles three responsibilities:
 *   1. Loading all posts written by a selected student
 *   2. Saving a point value for a specific post
 *   3. Calculating and returning a student's total points </p>
 *
 * <p> Input validation is enforced here before any database call is made,
 * consistent with the CRUD epic requirement that invalid inputs throw
 * descriptive error messages rather than silently failing. </p>
 *
 * @author Brenna
 *
 * @version 1.00    TP3 Initial version
 */
public class ControllerGradingDisplay {

    // ----------------------------------------------------------------
    // Reference to the shared database — same pattern used in every
    // other controller in this project.
    // ----------------------------------------------------------------
    protected static Database theDatabase = applicationMain.FoundationsMain.database;

    /*******
     * <p> Method: ControllerGradingDisplay() </p>
     *
     * <p> Description: Default constructor. Not used — all methods are static,
     * consistent with every other controller in the project. </p>
     */
    public ControllerGradingDisplay() {
    }

    // ----------------------------------------------------------------
    // LOAD
    // ----------------------------------------------------------------

    /*******
     * <p> Method: loadPostsForStudent(String username) </p>
     *
     * <p> Description: Retrieves all posts written by the given student
     * from the database, including any points already assigned to each post.
     * This is called when the staff member selects a student on the grading page
     * to populate the TableView with that student's posts. </p>
     *
     * <p> Each returned String array contains four elements:
     *   [0] postID (String form of the integer)
     *   [1] post content (the text of the post)
     *   [2] thread name
     *   [3] current points (0 if not yet graded) </p>
     *
     * @param username the username of the student whose posts to load;
     *                 must be non-null and non-empty
     *
     * @return a List of String arrays representing each post, or an empty
     *         list if the username is invalid or no posts exist
     *
     * <p> Tested by: GradingDisplayTest.testLoadPostsForStudent() </p>
     */
    protected static List<String[]> loadPostsForStudent(String username) {

        // Input validation: username must not be null or blank.
        // This prevents a meaningless DB query and gives the View
        // a clear signal that nothing was loaded.
        if (username == null || username.trim().isEmpty()) {
            showError("Cannot load posts", "No student username was provided.");
            return new java.util.ArrayList<>();
        }

        // Delegate to the database — it handles the SQL JOIN with postGrades
        // so we get both the post content and any existing grade in one call.
        return theDatabase.getPostsByStudent(username);
    }

    // ----------------------------------------------------------------
    // SAVE / UPDATE
    // ----------------------------------------------------------------

    /*******
     * <p> Method: assignGrade(int postID, String studentUsername,
     *                          int points, String gradedBy) </p>
     *
     * <p> Description: Validates the inputs and saves a point value for a
     * specific post to the database. This is called when the staff member
     * clicks "Save Grades" on the grading page.
     *
     * Validation rules (per the CRUD epic):
     *   - postID must be greater than 0
     *   - points must be >= 0 (negative grades are not allowed)
     *   - studentUsername must be non-null and non-empty
     *   - gradedBy must be non-null and non-empty
     *
     * If validation fails, an error alert is shown and false is returned.
     * The database is not called unless all inputs pass validation. </p>
     *
     * @param postID          the unique integer ID of the post being graded
     * @param studentUsername the username of the student who wrote the post
     * @param points          the number of points to assign (must be >= 0)
     * @param gradedBy        the username of the staff member saving the grade
     *
     * @return true if the grade was saved successfully, false otherwise
     *
     * <p> Tested by: GradingDisplayTest.testAssignGrade() </p>
     */
    protected static boolean assignGrade(int postID, String studentUsername,
                                         int points, String gradedBy) {

        // Validate postID: must be a positive integer because auto-increment
        // IDs in H2 always start at 1, so 0 or negative means something
        // went wrong upstream.
        if (postID <= 0) {
            showError("Invalid Post ID",
                "ERROR: Cannot grade — postID must be greater than 0. Received: " + postID);
            return false;
        }

        // Validate points: negative grades are not meaningful in this system.
        if (points < 0) {
            showError("Invalid Points Value",
                "ERROR: Cannot grade — points must be 0 or greater. Received: " + points);
            return false;
        }

        // Validate studentUsername: must not be null or blank.
        if (studentUsername == null || studentUsername.trim().isEmpty()) {
            showError("Missing Student",
                "ERROR: Cannot grade — no student username was provided.");
            return false;
        }

        // Validate gradedBy: must not be null or blank.
        if (gradedBy == null || gradedBy.trim().isEmpty()) {
            showError("Missing Staff Member",
                "ERROR: Cannot grade — no staff username was provided.");
            return false;
        }

        // All inputs are valid — save to the database.
        // Database.setPostGrade uses MERGE so it handles both
        // first-time grading and updating an existing grade.
        return theDatabase.setPostGrade(postID, studentUsername, points, gradedBy);
    }

    // ----------------------------------------------------------------
    // CALCULATE TOTAL
    // ----------------------------------------------------------------

    /*******
     * <p> Method: calculateTotal(String username) </p>
     *
     * <p> Description: Returns the sum of all points awarded to a student
     * across all their graded posts. This is displayed on the grading page
     * as "Total Points" so the staff member does not have to add manually.
     *
     * If the username is invalid or the student has no graded posts yet,
     * 0 is returned. </p>
     *
     * @param username the username of the student
     *
     * @return the total points for this student, or 0 if none
     *
     * <p> Tested by: GradingDisplayTest.testCalculateTotal() </p>
     */
    protected static int calculateTotal(String username) {

        // Return 0 immediately for invalid input — no need to hit the database.
        if (username == null || username.trim().isEmpty()) {
            return 0;
        }

        return theDatabase.getTotalPointsByStudent(username);
    }

    // ----------------------------------------------------------------
    // NAVIGATION
    // ----------------------------------------------------------------

    /*******
     * <p> Method: performBack() </p>
     *
     * <p> Description: Returns the staff member to the Staff Home Page (Role2).
     * Follows the same navigation pattern used by every other controller
     * in the project (e.g., ControllerPostDisplay.backToHomePage). </p>
     *
     * <p> Tested by: Manual test — see Manual Tests PDF </p>
     */
    protected static void performBack() {
        // Navigate back to the staff home page using the shared stage and user
        // that ViewGradingDisplay stored when the page was opened.
        guiRole2.ViewRole2Home.displayRole2Home(
            ViewGradingDisplay.theStage,
            ViewGradingDisplay.theUser
        );
    }

    /*******
     * <p> Method: performQuit() </p>
     *
     * <p> Description: Terminates the application.
     * Consistent with performQuit() in all other controllers. </p>
     *
     * <p> Tested by: Manual test — see Manual Tests PDF </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    /*******
     * <p> Method: showError(String header, String message) </p>
     *
     * <p> Description: Private helper that shows a JavaFX ERROR alert.
     * Centralizes error display so all validation failures look consistent,
     * matching the style used in ControllerPostDisplay and ControllerRole2Home. </p>
     *
     * @param header  the bold header line of the alert dialog
     * @param message the detailed message explaining what went wrong
     */
    private static void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
