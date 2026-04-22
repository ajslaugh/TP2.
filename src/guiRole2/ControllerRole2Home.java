package guiRole2;

import database.Database;

import java.sql.SQLException;
import java.util.Optional;
import entityClasses.Reply;
import entityClasses.Request;
import database.Database;
import entityClasses.User;
import guiRole1.ViewRole1Home;
import javafx.stage.Stage;
import entityClasses.Post;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

/*******
 * <p> Title: ControllerRole2Home Class. </p>
 * 
 * <p> Description: The Java/FX-based Role 2 Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page is a stub for establish future roles for the application.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerRole2Home {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	
	private static String author = "";
	private static String content = "";
	private static String thread = "General";
	private static String role = "";
	private static int replyNumber;
	private static String replyContent = "";
	private static String requestContent = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	public ControllerRole2Home() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewRole2Home.theStage, ViewRole2Home.theUser);
	}	

	protected static void setAuthor() {
		author = ViewRole2Home.theUser.getUserName();
	}
	
	protected static void setContent() {
		content = ViewRole2Home.text_PostContent.getText();
	}
	

	
	protected static void setRole() {
		if(ViewRole2Home.theUser.getAdminRole()) {
			role = "ADMIN";}
		if(ViewRole2Home.theUser.getNewRole1()) {
			role = "STUDENT";
			}
		if(ViewRole2Home.theUser.getNewRole2()) {
			role = "STAFF";
		}
	}

	/**********
	 * <p> Method:  setRequestContent() </p>
	 * 
	 * <p> Description: Sets the request content of a new request </p>
	 * 
	 */
	
	protected static void setRequestContent() {
		result = ViewRole2Home.dialogNewRequest.showAndWait();
		if(result.isPresent()) {
			requestContent = result.get();
		}
		else {
			return;
		}
	}

	/**********
	 * <p> Method:  performNewRequest() </p>
	 * 
	 * <p> Description: Gives user option to create a new request and adds it to database. </p>
	 * 
	 */
//ALEX TP3 add new request to the database
	protected static void performNewRequest() {
		setRequestContent();
		
		if(requestContent.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with Request Submission");
			alert.setContentText("You must right something within the text field in order to submit your request");
			alert.showAndWait();
			return;
		}
		
		Request request = new Request(theDatabase.getCurrentUsername(), requestContent);
		request.setId(theDatabase.addRequest(request));
		ViewRole2Home.openRequestsDisplay.setAll(theDatabase.getStaffRequests(false));
		
		ViewRole2Home.alertPosted.setHeaderText("Request Successfully Submitted");
		ViewRole2Home.alertPosted.setContentText("Your request will be reviewed by admin shortly.");
		ViewRole2Home.alertPosted.showAndWait();
		
		
	}

	/**********
	 * <p> Method:  resubmitRequest() </p>
	 * 
	 * <p> Description: Allows user to resubmit a closed request and adds it to database. </p>
	 * 
	 */

	//Alex tp3
	protected static void resubmitRequest(Request request) {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.setHeaderText("Would you like to resubmit this request?");
		alert.setTitle("Would you like to resubmit this request?");
		alert.setContentText("Resubmitting this request will not delete the original. A link to the original request will be added");
		
		ButtonType resubmit = new ButtonType("Resubmit");
		ButtonType cancel = new ButtonType("Close");
		
		alert.getButtonTypes().addAll(resubmit, cancel);
		
		Optional<ButtonType>result = alert.showAndWait();
		
		if(result.isPresent() && result.get() == resubmit) {
			TextInputDialog addNote = new TextInputDialog();
			addNote.setTitle("Add a note?");
			addNote.setHeaderText("Would you like to add an additional note to your request resubmission?");
			addNote.setContentText("Add any additional note you'd like to send to the admin here:");
			String note = addNote.showAndWait().orElse("");
			
			
			
			Request newRequest = new Request(theDatabase.getCurrentUsername(), request.getContent() + "\n Resubmission Note: " + note, request.getIndex());
			newRequest.setId(theDatabase.addRequest(newRequest));
			ViewRole2Home.openRequestsDisplay.setAll(theDatabase.getStaffRequests(false));
			ViewRole2Home.closedRequestsDisplay.setAll(theDatabase.getStaffRequests(true));
		
			
			Alert alert2= new Alert(Alert.AlertType.INFORMATION);
			alert2.setHeaderText("Your request has been resubmitted");
			alert2.setTitle("You have resubmitted this post!");
			alert2.setContentText("An admin will review your request shortly. Come back soon!");
			
			alert.showAndWait();
			
		}
		
	}

	
	
	
	
    /*****
     * Navigates to post display page
     */
    protected static void performViewPosts() {
        guiPostDisplay.ViewPostDisplay.displayPosts(
            ViewRole2Home.theStage,
            ViewRole2Home.theUser
        );
    }

    /*****
     * Displays list of students <-- TP3
     */
    protected static void performViewStudents() {
        List<String> students = theDatabase.getStudentList();
        //theDatabase.debugPrintColumns();

        if (students.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Students");
            alert.setHeaderText(null);
            alert.setContentText("There are no students in the database.");
            alert.showAndWait();
            return;
        }
		
		Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Students");
		dialog.setHeaderText("Choose a student to grade:");

		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        // ListView for clickable students
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(students);
		listView.setPrefSize(300, 400);

        // Double-click support
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    dialog.setResult(selected);
                    dialog.close();
                }
            }
        });

        dialog.getDialogPane().setContent(listView);
      Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String selectedStudent = result.get();
        showStudentOptions(selectedStudent);
   }

	/*******
     * <p> Method: performViewGradeSummary() </p>
     *
     * <p> Description: Displays a popup dialog showing a grade summary table
     * for all students. Each row shows the student's username, total number
     * of posts, total points awarded, and average points per post.
     * Students are sorted highest points first.
     * Called when the staff member clicks "Grade Summary" on the staff home page. </p>
     *
     */
    protected static void performViewGradeSummary() {
     
        // Fetch the summary data from the database
        List<String[]> summary = theDatabase.getGradeSummaryAllStudents();
     
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Grade Summary");
        dialog.setHeaderText("Grade Summary — All Students");
     
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        
        TableView<String[]> table = new TableView<>();
        table.setPrefSize(550, 400);
     
        // Column 1: Student Username
        TableColumn<String[], String> col_Student = new TableColumn<>("Student");
        col_Student.setPrefWidth(180);
        col_Student.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
     
        // Column 2: Total Posts
        TableColumn<String[], String> col_Posts = new TableColumn<>("Total Posts");
        col_Posts.setPrefWidth(100);
        col_Posts.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
     
        // Column 3: Total Points
        TableColumn<String[], String> col_Points = new TableColumn<>("Total Points");
        col_Points.setPrefWidth(110);
        col_Points.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
     
        // Column 4: Average Points Per Post
        TableColumn<String[], String> col_Avg = new TableColumn<>("Avg Per Post");
        col_Avg.setPrefWidth(110);
        col_Avg.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
     
        // Add all columns to the table
        table.getColumns().addAll(col_Student, col_Posts, col_Points, col_Avg);
     
        // Populate the table with data
        if (summary.isEmpty()) {
            // No students have posts yet — show a placeholder message
            dialog.setHeaderText("Grade Summary — No student posts found yet.");
        } else {
            javafx.collections.ObservableList<String[]> data =
                javafx.collections.FXCollections.observableArrayList(summary);
            table.setItems(data);
        }
     
       
        table.setEditable(false);
     
        // Put the table inside the dialog
        dialog.getDialogPane().setContent(table);
     
        // Show and wait
        dialog.showAndWait();
    }
    
    private static void showStudentOptions(String student) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Student Selected");
        dialog.getDialogPane().setPrefSize(400, 200);
        
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label nameLabel = new Label("Selected Student: " + student);

        Button btnBack = new Button("Back");
        Button btnEvaluate = new Button("Evaluate");
        Button btnCancel = new Button("Cancel");

        btnBack.setOnAction(_ -> {
            dialog.close();
            performViewStudents(); // go back to list
        });

        btnEvaluate.setOnAction(_ -> {
            dialog.close();
            openEvaluationPage(student);
        });

        btnCancel.setOnAction(_ -> dialog.close());

        HBox buttons = new HBox(10, btnBack, btnEvaluate, btnCancel);

        box.getChildren().addAll(nameLabel, buttons);
        dialog.getDialogPane().setContent(box);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

	/*****
	 * Evaluation Page ready, not functional right now
	 */
	 
	private static void openEvaluationPage(String student) {
        // Navigate to the grading page — passes stage, user, and selected student.
        // ViewGradingDisplay will load that student's posts from the database.
        guiGrading.ViewGradingDisplay.displayGrading(
            ViewRole2Home.theStage,
            ViewRole2Home.theUser,
            student
        );
    } 


	//hw2 perform new post
	protected static void performNewPost() {
		setAuthor();
		setContent();
		setRole();
		
	//input validation	
		if(content.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with posting");
			alert.setContentText("You have to write something in the text field in order to post");
			alert.showAndWait();
			return;
		}
		
		Post post = new Post(author, role, content, thread);
		
		
		try {
			theDatabase.addPost(post);
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to create a post: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		ViewRole2Home.text_PostContent.setText("");
		ViewRole2Home.alertPosted.setHeaderText("Posting Successful!");
		ViewRole2Home.alertPosted.setContentText("Your post will be displayed for all students and staff");
		ViewRole2Home.alertPosted.showAndWait();
		
		
	}
	/*******
	 * <p> Method: viewReplies(int) </p>
	 * 
	 * <p> Description: Help display replies when user clicks on a post. </p>
	 *  
	 * 
	 * 
	 */
	//hw2 view replies
	protected static void viewReplies(int ogPost) {
		List<String[]> replies = theDatabase.getRepliesFromPost(ogPost);
		Alert alert = new Alert(Alert.AlertType.NONE);
		StringBuilder replyList = new StringBuilder();
		if(replies.isEmpty()) {
			replyList.append("There are currently no replies to this post");
		}
		else {
			for (String[] reply : replies) {
				replyList.append(String.format("%s %s to %s thread: %s \n\n",
				reply[0], reply[2], reply[3], reply[1]));}}
	
		alert.setTitle("Replies");
		alert.setHeaderText("These are the replies to this post");
		alert.setContentText(replyList.toString());
	    ButtonType reply = new ButtonType("Reply to this Post");
	    ButtonType exit = new ButtonType("Exit");
	    
	    alert.getButtonTypes().addAll(reply,exit);
	    
	    Optional<ButtonType> result = alert.showAndWait();
	    
	    if(result.isPresent()) {
	    	if(result.get() == reply) {
	    		performNewReply(ogPost);
	    	}
	    	if(result.get() == exit) {
	    		return;
	    	}
	    }
		
	}
	
	/*******
	 * <p> Method: performNewReply(int)  </p>
	 * 
	 * <p> Description: Perform new reply to comment. </p>
	 *  
	 *  
	 * 
	 */
	//hw2 performnewreply
	protected static void performNewReply(int ogPost) {
		setAuthor();
		setRole();
		
		TextInputDialog writeAReply = new TextInputDialog();
		writeAReply.setTitle(String.format("Write A Reply to %s \n", ogPost));
		writeAReply.setHeaderText("Reply to this user");
		replyContent = writeAReply.showAndWait().orElse("");
		replyNumber = ogPost;
		
		//input validation
		if(replyContent.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with posting");
			alert.setContentText("You have to write something in the text field in order to post this reply");
			alert.showAndWait();
			return;
		}

		Reply reply = new Reply(author, role, replyContent, thread, replyNumber);
		
		try {
			theDatabase.addReply(reply);
		}
		catch (SQLException e) {
            System.err.println("*** ERROR *** Database error trying to create a reply: " + 
            		e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
		ViewRole2Home.text_PostContent.setText("");
		ViewRole2Home.alertPosted.setHeaderText("Reply Successful!");
		ViewRole2Home.alertPosted.setContentText("Your reply will be added to this posts list of replies");
		ViewRole2Home.alertPosted.showAndWait();
	}
	
	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole2Home.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
}
