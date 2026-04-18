package guiGrading;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import database.Database;
import entityClasses.User;
import java.util.List;

	
	/*******
	 * <p> Title: ViewGradingDisplay Class </p>
	 *
	 * <p> Description: The JavaFX View for the Grading Display page. Follows the
	 * exact same singleton pattern and helper method style as ViewRole2Home and
	 * ViewPostDisplay. Staff can select a student, see all of that student's posts
	 * in a table with editable point fields, save grades, and see a running total. </p>
	 *
	 * <p> Layout overview:
	 *   Area 1 (top):    Page title, logged-in staff username
	 *   Area 2 (middle): Student name label, TableView of posts with editable points
	 *   Area 3 (bottom): Total points label, Save button, Back button, Quit button </p>
	 *
	 * <p> This page is opened from ControllerRole2Home.openEvaluationPage(student)
	 * by calling ViewGradingDisplay.displayGrading(stage, user, student). </p>
	 *
	 * @author Brenna
	 *
	 * @version 1.00    TP3 Initial version
	 */
	public class ViewGradingDisplay {

	 
	    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	    // ----------------------------------------------------------------
	    // GUI Area 1: Page title and logged-in user info
	    // ----------------------------------------------------------------

	    /** Displays "Grading" as the page title at the top center. */
	    protected static Label label_PageTitle = new Label("Grading");

	    /** Displays the username of the currently logged-in staff member. */
	    protected static Label label_UserDetails = new Label();

	    // Separator lines — same style as ViewRole2Home and ViewPostDisplay
	    protected static Line line_Separator1 = new Line(20, 95,  width - 20, 95);
	    protected static Line line_Separator2 = new Line(20, 150, width - 20, 150);
	    protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	    // ----------------------------------------------------------------
	    // GUI Area 2: Student label and posts TableView
	    // ----------------------------------------------------------------

	    /** Shows which student is currently being graded. */
	    protected static Label label_StudentName = new Label("Student: ");

	    /**
	     * The TableView that shows all posts by the selected student.
	     * Each row contains: Post ID | Post Content | Thread | Points (editable).
	     * Using ObservableList<String[]> so rows stay in sync with the database data.
	     */
	    protected static TableView<String[]> table_Posts = new TableView<>();

	    /**
	     * The data backing the TableView. Each String[] has four elements:
	     * [0] postID, [1] post content, [2] thread, [3] points.
	     */
	    protected static ObservableList<String[]> postData = FXCollections.observableArrayList();

	    // ----------------------------------------------------------------
	    // GUI Area 3: Total points, Save, Back, Quit
	    // ----------------------------------------------------------------

	    /** Displays the sum of all points awarded to the current student. */
	    protected static Label label_Total = new Label("Total Points: 0");

	    /** Saves all point values currently shown in the table to the database. */
	    protected static Button button_SaveGrades = new Button("Save Grades");

	    /** Returns the staff member to the Staff Home Page. */
	    protected static Button button_Back = new Button("Back to Staff Home");

	    /** Quits the application — consistent with every other page. */
	    protected static Button button_Quit = new Button("Quit");

	    /** Singleton guard — the constructor only runs once. */
	    private static ViewGradingDisplay theView;

	    /** The database reference — same line used in every other View. */
	    private static Database theDatabase = applicationMain.FoundationsMain.database;

	    /** The JavaFX Stage shared across the whole application. */
	    protected static Stage theStage;

	    /** The root pane that holds all widgets on this page. */
	    protected static Pane theRootPane;

	    /** The currently logged-in staff member. */
	    protected static User theUser;

	    /**
	     * The username of the student currently being graded.
	     * Set by displayGrading() and read by the Save button handler.
	     */
	    protected static String currentStudent = "";

	    /** The Scene for this page — created once, reused on each visit. */
	    private static Scene theGradingScene;

	    /*******
	     * <p> Method: displayGrading(Stage ps, User user, String student) </p>
	     *
	     * <p> Description: Single entry point from outside this package.
	     * Sets up shared state, instantiates the singleton if needed,
	     * then populates the dynamic parts of the GUI (student name, posts table,
	     * total) before showing the page. Follows the same pattern as
	     * ViewRole2Home.displayRole2Home(). </p>
	     *
	     * @param ps      the JavaFX Stage to display this page on
	     * @param user    the currently logged-in staff member
	     * @param student the username of the student to grade
	     *
	     * <p> Tested by: Manual test — see Manual Tests PDF </p>
	     */
	    public static void displayGrading(Stage ps, User user, String student) {

	        // Store shared references so controller methods can reach them
	        theStage   = ps;
	        theUser    = user;
	        currentStudent = student;

	        // Build the singleton GUI exactly once
	        if (theView == null) theView = new ViewGradingDisplay();

	        // --- Dynamic section: update everything that changes per student ---

	        // Show the logged-in staff member's username in the top bar
	        label_UserDetails.setText("Staff: " + theUser.getUserName());

	        // Show the student being graded
	        label_StudentName.setText("Grading Student: " + student);

	        // Load this student's posts (with any existing grades) from the DB
	        refreshTable(student);

	        // Set page title and switch the scene
	        theStage.setTitle("CSE 360 Foundations: Grading");
	        theStage.setScene(theGradingScene);
	        theStage.show();
	    }

	    // Constructor 
	

	    /*******
	     * <p> Method: ViewGradingDisplay() </p>
	     *
	     * <p> Description: Initializes all static GUI widgets: their position,
	     * font, size, and event handlers. Only called once due to the singleton
	     * pattern. Subsequent visits to this page use displayGrading() to refresh
	     * the dynamic content. </p>
	     */
	    private ViewGradingDisplay() {

	        // Create the root pane and scene — same two lines as every other View
	        theRootPane    = new Pane();
	        theGradingScene = new Scene(theRootPane, width, height);

	        // ---- GUI Area 1: Title and user info ----

	        setupLabelUI(label_PageTitle,   "Arial", 28, width, Pos.CENTER,        0,  5);
	        setupLabelUI(label_UserDetails, "Arial", 18, width, Pos.BASELINE_LEFT, 20, 55);
	        setupLabelUI(label_StudentName, "Arial", 20, 500,   Pos.BASELINE_LEFT, 20, 110);

	        // ---- GUI Area 2: TableView ----

	        // Column 1: Post ID — narrow, non-editable, just shows the ID number
	        TableColumn<String[], String> col_PostID = new TableColumn<>("Post ID");
	        col_PostID.setPrefWidth(70);
	        // row[0] holds the postID string
	        col_PostID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
	        col_PostID.setEditable(false);  // IDs are read-only — staff cannot change them

	        // Column 2: Content — wide column showing the text of the post
	        TableColumn<String[], String> col_Content = new TableColumn<>("Post Content");
	        col_Content.setPrefWidth(350);
	        // row[1] holds the post content string
	        col_Content.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
	        col_Content.setEditable(false); // Content is read-only on this page

	        // Column 3: Thread — shows which discussion thread the post belongs to
	        TableColumn<String[], String> col_Thread = new TableColumn<>("Thread");
	        col_Thread.setPrefWidth(100);
	        // row[2] holds the thread name
	        col_Thread.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
	        col_Thread.setEditable(false);

	        // Column 4: Points — EDITABLE so staff can type in a grade per post
	        TableColumn<String[], String> col_Points = new TableColumn<>("Points");
	        col_Points.setPrefWidth(80);
	        // row[3] holds the current points value as a string
	        col_Points.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));

	        // TextFieldTableCell makes this column's cells editable text fields
	        col_Points.setCellFactory(TextFieldTableCell.forTableColumn());

	        // When the staff member finishes editing a cell (presses Enter or clicks away),
	        // update the in-memory data array immediately so Save Grades captures it.
	        col_Points.setOnEditCommit(event -> {
	            String newValue = event.getNewValue().trim();

	            // Only accept digits — reject anything that is not a non-negative integer
	            if (!newValue.matches("\\d+")) {
	                showError("Invalid Points",
	                    "Points must be a whole number (0 or greater). Please re-enter.");
	                // Refresh the table to revert the cell to its previous value
	                table_Posts.refresh();
	                return;
	            }

	            // Write the new value back into the underlying data array
	            // getRowValue() returns the String[] for the edited row
	            event.getRowValue()[3] = newValue;

	            // Immediately recalculate and display the new total
	            updateTotalLabel();
	        });

	        // Add all four columns to the table
	        table_Posts.getColumns().addAll(col_PostID, col_Content, col_Thread, col_Points);

	        // Bind the observable data list to the table
	        table_Posts.setItems(postData);

	        // Allow cells in the table to be edited (required for TextFieldTableCell)
	        table_Posts.setEditable(true);

	        // Position and size the table on the page
	        table_Posts.setLayoutX(20);
	        table_Posts.setLayoutY(170);
	        table_Posts.setPrefWidth(width - 40);  // span almost full width
	        table_Posts.setPrefHeight(300);

	        // ---- GUI Area 3: Total label, Save, Back, Quit ----

	        setupLabelUI(label_Total, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 490);

	        // Save Grades button — collects all rows and calls the controller
	        setupButtonUI(button_SaveGrades, "Dialog", 18, 160, Pos.CENTER, 400, 483);
	        button_SaveGrades.setOnAction(_ -> performSaveGrades());

	        // Back button — navigates to Staff Home Page
	        setupButtonUI(button_Back, "Dialog", 18, 220, Pos.CENTER, 20, 540);
	        button_Back.setOnAction(_ -> ControllerGradingDisplay.performBack());

	        // Quit button — exits the application
	        setupButtonUI(button_Quit, "Dialog", 18, 220, Pos.CENTER, 300, 540);
	        button_Quit.setOnAction(_ -> ControllerGradingDisplay.performQuit());

	        // ---- Add all widgets to the root pane ----
	        theRootPane.getChildren().addAll(
	            label_PageTitle, label_UserDetails,
	            line_Separator1, line_Separator2,
	            label_StudentName,
	            table_Posts,
	            label_Total,
	            line_Separator4,
	            button_SaveGrades, button_Back, button_Quit
	        );
	    }

	
	    /*******
	     * <p> Method: refreshTable(String student) </p>
	     *
	     * <p> Description: Clears the TableView and reloads all posts for the
	     * given student from the database. Also updates the total points label.
	     * Called by displayGrading() each time a new student is selected so the
	     * table always reflects the current database state. </p>
	     *
	     * @param student the username of the student whose posts to display
	     */
	    private static void refreshTable(String student) {

	        // Clear existing rows so we don't show stale data from the last student
	        postData.clear();

	        // Ask the controller to load posts — it validates the username first
	        List<String[]> posts = ControllerGradingDisplay.loadPostsForStudent(student);

	        // Add each row to the observable list — the TableView updates automatically
	        postData.addAll(posts);

	        // Update the running total to reflect the freshly loaded grades
	        updateTotalLabel();
	    }

	    /*******
	     * <p> Method: performSaveGrades() </p>
	     *
	     * <p> Description: Iterates over every row in the table and calls the
	     * controller's assignGrade() method to save each post's current point value.
	     * After saving, shows a success alert and refreshes the total.
	     * Called when the staff member clicks "Save Grades". </p>
	     */
	    private static void performSaveGrades() {

	        // Track how many saves succeeded 
	        int saved   = 0;
	        int skipped = 0;

	        for (String[] row : postData) {
	            // row[0] = postID string, row[3] = points string
	            int postID;
	            int points;

	            // Safely parse postID 
	            try {
	                postID = Integer.parseInt(row[0]);
	            } catch (NumberFormatException e) {
	                skipped++;
	                continue; // skip malformed rows silently
	            }

	            // Safely parse points 
	            try {
	                points = Integer.parseInt(row[3]);
	            } catch (NumberFormatException e) {
	                skipped++;
	                continue;
	            }

	            // Delegate to controller which does full validation before DB call
	            boolean success = ControllerGradingDisplay.assignGrade(
	                postID,
	                currentStudent,
	                points,
	                theUser.getUserName()  // the logged-in staff member is "gradedBy"
	            );

	            if (success) saved++;
	            else         skipped++;
	        }

	        // Show a confirmation alert summarizing what was saved
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Grades");
	        alert.setHeaderText("Grades Saved");
	        alert.setContentText(
	            saved   + " grade(s) saved successfully.\n" +
	            skipped + " skipped due to errors."
	        );
	        alert.showAndWait();

	        // Refresh total to reflect whatever was just saved
	        updateTotalLabel();
	    }

	    /*******
	     * <p> Method: updateTotalLabel() </p>
	     *
	     * <p> Description: Adds up the points values currently shown in the table
	     * (not yet saved) and updates the "Total Points" label in real time.
	     * This gives staff immediate visual feedback as they type grades, before
	     * they click Save. </p>
	     */
	    private static void updateTotalLabel() {

	        int total = 0;
	        for (String[] row : postData) {
	            try {
	                // row[3] holds the current points string for each post
	                total += Integer.parseInt(row[3]);
	            } catch (NumberFormatException e) {
	                // If a cell has a non-numeric value somehow, treat it as 0
	            }
	        }
	        label_Total.setText("Total Points: " + total);
	    }

	    /*******
	     * <p> Method: showError(String header, String message) </p>
	     *
	     * <p> Description: Shows a JavaFX ERROR alert. Private helper to avoid
	     * repeating the same four lines everywhere, consistent with the style
	     * used in ControllerGradingDisplay. </p>
	     *
	     * @param header  the bold header text of the alert
	     * @param message the detailed content message
	     */
	    private static void showError(String header, String message) {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setHeaderText(header);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }

		//Helper methods

	    /*******
	     * <p> Method: setupLabelUI(...) </p>
	     *
	     * <p> Description: Initializes standard fields for a Label widget.
	     * Identical to the same helper in ViewRole2Home and ViewPostDisplay. </p>
	     *
	     * @param l  the Label to configure
	     * @param ff font family name
	     * @param f  font size
	     * @param w  minimum width
	     * @param p  text alignment
	     * @param x  x position on the Pane
	     * @param y  y position on the Pane
	     */
	    private static void setupLabelUI(Label l, String ff, double f,
	                                     double w, Pos p, double x, double y) {
	        l.setFont(Font.font(ff, f));
	        l.setMinWidth(w);
	        l.setAlignment(p);
	        l.setLayoutX(x);
	        l.setLayoutY(y);
	    }

	    /*******
	     * <p> Method: setupButtonUI(...) </p>
	     *
	     * <p> Description: Initializes standard fields for a Button widget.
	     * Identical to the same helper in ViewRole2Home and ViewPostDisplay. </p>
	     *
	     * @param b  the Button to configure
	     * @param ff font family name
	     * @param f  font size
	     * @param w  minimum width
	     * @param p  text alignment
	     * @param x  x position on the Pane
	     * @param y  y position on the Pane
	     */
	    private static void setupButtonUI(Button b, String ff, double f,
	                                      double w, Pos p, double x, double y) {
	        b.setFont(Font.font(ff, f));
	        b.setMinWidth(w);
	        b.setAlignment(p);
	        b.setLayoutX(x);
	        b.setLayoutY(y);
	    }
	}

