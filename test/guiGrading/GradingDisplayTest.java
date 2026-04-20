package guiGrading;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import database.Database;
import java.sql.SQLException;
import java.util.List;

/*******
 * <p> Title: GradingDisplayTest </p>
 *
 * <p> Description: JUnit 5 test class for the Staff Grading Package implemented
 * in TP3. Tests cover the database methods and controller validation logic added
 * for the grading system. GUI interactions are covered in Manual Tests.pdf since
 * they cannot be driven by JUnit without a full JavaFX test harness.
 *
 * Methods tested:
 *   - Database.setPostGrade()
 *   - Database.getPostsByStudent()
 *   - Database.getTotalPointsByStudent()
 *   - Database.getGradeSummaryAllStudents()
 *   - ControllerGradingDisplay.assignGrade()
 *   - ControllerGradingDisplay.submitFeedback()
 *   - ControllerGradingDisplay.calculateTotal()
 * </p>
 *
 * @author Brenna
 * @version 1.00 TP3 Initial version
 */
public class GradingDisplayTest {

    private Database testDatabase;

    /*******
     * <p> Method: setUp() </p>
     *
     * <p> Description: Runs before every test. Creates a fresh in-memory H2
     * database and seeds it with one staff user, one student user, and one
     * post so every test starts from a clean known state. </p>
     *
     * <p> Validated by: all tests in this class </p>
     */
    @BeforeEach
    public void setUp() throws SQLException {
        testDatabase = new Database();
        testDatabase.connectToDatabase();
        testDatabase.dropAllObjects();    
        testDatabase.createTablesPublic();

        // Register a staff user (role2 = true)
        entityClasses.User staffUser = new entityClasses.User(
            "testStaff", "pass123", "Test", "", "Staff", "Test",
            "staff@test.com", false, false, true);
        testDatabase.register(staffUser);

        // Register a student user (role1 = true)
        entityClasses.User studentUser = new entityClasses.User(
            "testStudent", "pass123", "Test", "", "Student", "Test",
            "student@test.com", false, true, false);
        testDatabase.register(studentUser);

        // Log in as student so posts are linked to testStudent
        testDatabase.getUserAccountDetails("testStudent");

        // Create one post by the student
        entityClasses.Post post = new entityClasses.Post(
            "testStudent", "STUDENT", "This is a test post", "General");
        testDatabase.addPost(post);
    }

    // ================================================================
    // setPostGrade() tests
    // ================================================================

    /*******
     * <p> Method: testSetPostGrade_ValidInputs </p>
     * <p> Description: Validates that setPostGrade() returns true for valid inputs. </p>
     * <p> Validates: Database.setPostGrade() </p>
     */
    @Test
    public void testSetPostGrade_ValidInputs() {
        boolean result = testDatabase.setPostGrade(1, "testStudent", 8, "testStaff");
        assertTrue(result, "setPostGrade should return true for valid inputs");
    }

    /*******
     * <p> Method: testSetPostGrade_UpdateExistingGrade </p>
     * <p> Description: Validates that grading the same post twice updates the
     * existing grade rather than inserting a duplicate row. </p>
     * <p> Validates: Database.setPostGrade() — MERGE/update behavior </p>
     */
    @Test
    public void testSetPostGrade_UpdateExistingGrade() {
        testDatabase.setPostGrade(1, "testStudent", 5, "testStaff");
        testDatabase.setPostGrade(1, "testStudent", 10, "testStaff");

        // Total should be 10 not 15 — second call overwrites first
        int total = testDatabase.getTotalPointsByStudent("testStudent");
        assertEquals(10, total, "Grading the same post twice should update, not duplicate");
    }

    /*******
     * <p> Method: testSetPostGrade_ZeroPoints </p>
     * <p> Description: Validates that setPostGrade() accepts 0 as a valid grade.
     * Zero is a legitimate grade for a poor quality post. </p>
     * <p> Validates: Database.setPostGrade() — zero boundary </p>
     */
    @Test
    public void testSetPostGrade_ZeroPoints() {
        boolean result = testDatabase.setPostGrade(1, "testStudent", 0, "testStaff");
        assertTrue(result, "setPostGrade should accept 0 as a valid point value");
    }

    // ================================================================
    // getPostsByStudent() tests
    // ================================================================

    /*******
     * <p> Method: testGetPostsByStudent_ReturnsCorrectPosts </p>
     * <p> Description: Validates that getPostsByStudent() returns exactly the
     * posts written by the given student with matching content. </p>
     * <p> Validates: Database.getPostsByStudent() </p>
     */
    @Test
    public void testGetPostsByStudent_ReturnsCorrectPosts() {
        List<String[]> posts = testDatabase.getPostsByStudent("testStudent");

        assertEquals(1, posts.size(), "Should return exactly 1 post for testStudent");
        assertEquals("This is a test post", posts.get(0)[1],
            "Post content should match what was inserted in setUp");
    }

    /*******
     * <p> Method: testGetPostsByStudent_DefaultPointsIsZero </p>
     * <p> Description: Validates that ungraded posts show 0 points rather than
     * null. Enforced by COALESCE(g.points, 0) in the SQL query. </p>
     * <p> Validates: Database.getPostsByStudent() — COALESCE default </p>
     */
    @Test
    public void testGetPostsByStudent_DefaultPointsIsZero() {
        List<String[]> posts = testDatabase.getPostsByStudent("testStudent");
        assertEquals("0", posts.get(0)[3], "Ungraded post should show 0 points");
    }

    /*******
     * <p> Method: testGetPostsByStudent_ReturnsUpdatedPoints </p>
     * <p> Description: Validates that after saving a grade, getPostsByStudent()
     * reflects the updated points value — tests the full round trip. </p>
     * <p> Validates: Database.getPostsByStudent() — grade round-trip </p>
     */
    @Test
    public void testGetPostsByStudent_ReturnsUpdatedPoints() {
        testDatabase.setPostGrade(1, "testStudent", 7, "testStaff");

        List<String[]> posts = testDatabase.getPostsByStudent("testStudent");
        assertEquals("7", posts.get(0)[3],
            "getPostsByStudent should reflect the saved grade of 7");
    }

    /*******
     * <p> Method: testGetPostsByStudent_EmptyForUnknownStudent </p>
     * <p> Description: Validates that getPostsByStudent() returns an empty list
     * for a username with no posts, preventing a grading page crash. </p>
     * <p> Validates: Database.getPostsByStudent() — empty result </p>
     */
    @Test
    public void testGetPostsByStudent_EmptyForUnknownStudent() {
        List<String[]> posts = testDatabase.getPostsByStudent("unknownUser");
        assertTrue(posts.isEmpty(), "Should return empty list for a user with no posts");
    }

    // ================================================================
    // getTotalPointsByStudent() tests
    // ================================================================

    /*******
     * <p> Method: testGetTotalPointsByStudent_CorrectSum </p>
     * <p> Description: Validates that getTotalPointsByStudent() returns the
     * correct sum after a grade has been saved. </p>
     * <p> Validates: Database.getTotalPointsByStudent() </p>
     */
    @Test
    public void testGetTotalPointsByStudent_CorrectSum() {
        testDatabase.setPostGrade(1, "testStudent", 9, "testStaff");

        int total = testDatabase.getTotalPointsByStudent("testStudent");
        assertEquals(9, total, "Total should be 9 after grading one post with 9 points");
    }

    /*******
     * <p> Method: testGetTotalPointsByStudent_ZeroWhenNoGrades </p>
     * <p> Description: Validates that getTotalPointsByStudent() returns 0 when
     * no grades exist. COALESCE(SUM(g.points), 0) prevents a null return. </p>
     * <p> Validates: Database.getTotalPointsByStudent() — zero default </p>
     */
    @Test
    public void testGetTotalPointsByStudent_ZeroWhenNoGrades() {
        int total = testDatabase.getTotalPointsByStudent("testStudent");
        assertEquals(0, total, "Total should be 0 when no grades have been saved");
    }

    /*******
     * <p> Method: testGetTotalPointsByStudent_ZeroForUnknownStudent </p>
     * <p> Description: Validates that getTotalPointsByStudent() returns 0 for
     * a username that does not exist in the database. </p>
     * <p> Validates: Database.getTotalPointsByStudent() — unknown user </p>
     */
    @Test
    public void testGetTotalPointsByStudent_ZeroForUnknownStudent() {
        int total = testDatabase.getTotalPointsByStudent("doesNotExist");
        assertEquals(0, total, "Total should be 0 for a username that does not exist");
    }

    // ================================================================
    // getGradeSummaryAllStudents() tests
    // ================================================================

    /*******
     * <p> Method: testGetGradeSummaryAllStudents_ContainsStudent </p>
     * <p> Description: Validates that getGradeSummaryAllStudents() returns at
     * least one row and includes a row for testStudent. </p>
     * <p> Validates: Database.getGradeSummaryAllStudents() </p>
     */
    @Test
    public void testGetGradeSummaryAllStudents_ContainsStudent() {
        List<String[]> summary = testDatabase.getGradeSummaryAllStudents();

        assertFalse(summary.isEmpty(), "Summary should contain at least one student");

        boolean found = summary.stream()
            .anyMatch(row -> row[0].equals("testStudent"));
        assertTrue(found, "Summary should contain a row for testStudent");
    }

    /*******
     * <p> Method: testGetGradeSummaryAllStudents_CorrectTotalPosts </p>
     * <p> Description: Validates that the totalPosts column correctly reflects
     * the number of posts the student has made. setUp() creates one post. </p>
     * <p> Validates: Database.getGradeSummaryAllStudents() — post count </p>
     */
    @Test
    public void testGetGradeSummaryAllStudents_CorrectTotalPosts() {
        List<String[]> summary = testDatabase.getGradeSummaryAllStudents();

        String[] studentRow = summary.stream()
            .filter(row -> row[0].equals("testStudent"))
            .findFirst()
            .orElse(null);

        assertNotNull(studentRow, "testStudent should appear in the summary");
        assertEquals("1", studentRow[1], "testStudent has 1 post so totalPosts should be 1");
    }

    /*******
     * <p> Method: testGetGradeSummaryAllStudents_CorrectAverage </p>
     * <p> Description: Validates that the average per post is calculated correctly.
     * 8 points across 1 post should give an average of "8.0". </p>
     * <p> Validates: Database.getGradeSummaryAllStudents() — average calculation </p>
     */
    @Test
    public void testGetGradeSummaryAllStudents_CorrectAverage() {
        testDatabase.setPostGrade(1, "testStudent", 8, "testStaff");

        List<String[]> summary = testDatabase.getGradeSummaryAllStudents();

        String[] studentRow = summary.stream()
            .filter(row -> row[0].equals("testStudent"))
            .findFirst()
            .orElse(null);

        assertNotNull(studentRow, "testStudent should appear in summary");
        assertEquals("8", studentRow[2], "Total points should be 8");
        assertEquals("8.0", studentRow[3],
            "Average should be 8.0 for 8 points across 1 post");
    }

    // ================================================================
    // assignGrade() validation tests
    // ================================================================

    /*******
     * <p> Method: testAssignGrade_ValidInputs </p>
     * <p> Description: Validates that assignGrade() returns true when all
     * inputs are valid. This is the happy path test. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() </p>
     */
    @Test
    public void testAssignGrade_ValidInputs() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            1, "testStudent", 5, "testStaff");
        assertTrue(result, "assignGrade should return true for valid inputs");
    }

    /*******
     * <p> Method: testAssignGrade_InvalidPostID_Zero </p>
     * <p> Description: Validates that assignGrade() returns false when postID
     * is 0. H2 auto-increment IDs start at 1 so 0 is never valid. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — postID of 0 </p>
     */
    @Test
    public void testAssignGrade_InvalidPostID_Zero() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            0, "testStudent", 5, "testStaff");
        assertFalse(result, "assignGrade should return false when postID is 0");
    }

    /*******
     * <p> Method: testAssignGrade_InvalidPostID_Negative </p>
     * <p> Description: Validates that assignGrade() returns false when postID
     * is negative. Negative IDs cannot exist in the database. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — negative postID </p>
     */
    @Test
    public void testAssignGrade_InvalidPostID_Negative() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            -5, "testStudent", 5, "testStaff");
        assertFalse(result, "assignGrade should return false for negative postID");
    }

    /*******
     * <p> Method: testAssignGrade_NegativePoints </p>
     * <p> Description: Validates that assignGrade() returns false when points
     * is negative. The minimum valid grade is 0. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — negative points </p>
     */
    @Test
    public void testAssignGrade_NegativePoints() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            1, "testStudent", -1, "testStaff");
        assertFalse(result, "assignGrade should return false for negative points");
    }

    /*******
     * <p> Method: testAssignGrade_EmptyStudentUsername </p>
     * <p> Description: Validates that assignGrade() returns false when
     * studentUsername is empty. Empty usernames cannot match any real student. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — empty username </p>
     */
    @Test
    public void testAssignGrade_EmptyStudentUsername() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            1, "", 5, "testStaff");
        assertFalse(result, "assignGrade should return false for empty studentUsername");
    }

    /*******
     * <p> Method: testAssignGrade_NullStudentUsername </p>
     * <p> Description: Validates that assignGrade() returns false without throwing
     * a NullPointerException when studentUsername is null. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — null username </p>
     */
    @Test
    public void testAssignGrade_NullStudentUsername() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            1, null, 5, "testStaff");
        assertFalse(result, "assignGrade should return false for null studentUsername");
    }

    /*******
     * <p> Method: testAssignGrade_EmptyGradedBy </p>
     * <p> Description: Validates that assignGrade() returns false when gradedBy
     * is empty. Every grade must be traceable to a staff member. </p>
     * <p> Validates: ControllerGradingDisplay.assignGrade() — empty gradedBy </p>
     */
    @Test
    public void testAssignGrade_EmptyGradedBy() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.assignGrade(
            1, "testStudent", 5, "");
        assertFalse(result, "assignGrade should return false when gradedBy is empty");
    }

    // ================================================================
    // submitFeedback() validation tests
    // ================================================================

    /*******
     * <p> Method: testSubmitFeedback_ValidInputs </p>
     * <p> Description: Validates that submitFeedback() returns true when all
     * inputs are valid. This is the happy path test. </p>
     * <p> Validates: ControllerGradingDisplay.submitFeedback() </p>
     */
    @Test
    public void testSubmitFeedback_ValidInputs() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.submitFeedback(
            1, "testStudent", "Great post!", "testStaff");
        assertTrue(result, "submitFeedback should return true for valid inputs");
    }

    /*******
     * <p> Method: testSubmitFeedback_EmptyFeedbackText </p>
     * <p> Description: Validates that submitFeedback() returns false when
     * feedback text is empty. Empty feedback provides no value to the student. </p>
     * <p> Validates: ControllerGradingDisplay.submitFeedback() — empty text </p>
     */
    @Test
    public void testSubmitFeedback_EmptyFeedbackText() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.submitFeedback(
            1, "testStudent", "", "testStaff");
        assertFalse(result, "submitFeedback should return false for empty feedback text");
    }

    /*******
     * <p> Method: testSubmitFeedback_NullFeedbackText </p>
     * <p> Description: Validates that submitFeedback() returns false without
     * throwing a NullPointerException when feedback text is null. </p>
     * <p> Validates: ControllerGradingDisplay.submitFeedback() — null text </p>
     */
    @Test
    public void testSubmitFeedback_NullFeedbackText() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.submitFeedback(
            1, "testStudent", null, "testStaff");
        assertFalse(result, "submitFeedback should return false for null feedback text");
    }

    /*******
     * <p> Method: testSubmitFeedback_InvalidPostID </p>
     * <p> Description: Validates that submitFeedback() returns false when postID
     * is 0. Feedback must be linked to a real existing post. </p>
     * <p> Validates: ControllerGradingDisplay.submitFeedback() — bad postID </p>
     */
    @Test
    public void testSubmitFeedback_InvalidPostID() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.submitFeedback(
            0, "testStudent", "Good work!", "testStaff");
        assertFalse(result, "submitFeedback should return false when postID is 0");
    }

    /*******
     * <p> Method: testSubmitFeedback_WhitespaceOnlyText </p>
     * <p> Description: Validates that submitFeedback() returns false for
     * whitespace-only text. trim() reduces it to empty before isEmpty() check. </p>
     * <p> Validates: ControllerGradingDisplay.submitFeedback() — whitespace </p>
     */
    @Test
    public void testSubmitFeedback_WhitespaceOnlyText() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        boolean result = ControllerGradingDisplay.submitFeedback(
            1, "testStudent", "     ", "testStaff");
        assertFalse(result, "submitFeedback should return false for whitespace-only text");
    }

    // ================================================================
    // calculateTotal() tests
    // ================================================================

    /*******
     * <p> Method: testCalculateTotal_ReturnsCorrectTotal </p>
     * <p> Description: Validates that calculateTotal() returns the correct sum
     * of graded points for a student. </p>
     * <p> Validates: ControllerGradingDisplay.calculateTotal() </p>
     */
    @Test
    public void testCalculateTotal_ReturnsCorrectTotal() {
        ControllerGradingDisplay.theDatabase = testDatabase;
        testDatabase.setPostGrade(1, "testStudent", 6, "testStaff");

        int total = ControllerGradingDisplay.calculateTotal("testStudent");
        assertEquals(6, total, "calculateTotal should return 6 after grading with 6 points");
    }

    /*******
     * <p> Method: testCalculateTotal_ZeroForNullUsername </p>
     * <p> Description: Validates that calculateTotal() returns 0 rather than
     * throwing an exception when given a null username. </p>
     * <p> Validates: ControllerGradingDisplay.calculateTotal() — null input </p>
     */
    @Test
    public void testCalculateTotal_ZeroForNullUsername() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        int total = ControllerGradingDisplay.calculateTotal(null);
        assertEquals(0, total, "calculateTotal should return 0 for null username");
    }

    /*******
     * <p> Method: testCalculateTotal_ZeroForEmptyUsername </p>
     * <p> Description: Validates that calculateTotal() returns 0 for an empty
     * string username without making a pointless database call. </p>
     * <p> Validates: ControllerGradingDisplay.calculateTotal() — empty input </p>
     */
    @Test
    public void testCalculateTotal_ZeroForEmptyUsername() {
        ControllerGradingDisplay.theDatabase = testDatabase;

        int total = ControllerGradingDisplay.calculateTotal("");
        assertEquals(0, total, "calculateTotal should return 0 for empty username");
    }
}
