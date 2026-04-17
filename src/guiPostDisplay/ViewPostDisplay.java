package guiPostDisplay;


import javafx.scene.control.ListView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import database.Database;
import entityClasses.User;
import entityClasses.Post;
import javafx.scene.control.ComboBox;
import java.util.ArrayList;

//ViewPostDisplay Class
public class ViewPostDisplay{
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	// This is a separator and it is used to partition the GUI for various tasks
		protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
		protected static Line line_Separator2 = new Line(20, 150, width-20, 150);
		
		//GUI Area 1
		protected static Label label_PageTitle = new Label();
		protected static Label label_UserDetails = new Label();
		protected static Label label_numReplies = new Label();
		

		// GUI Area 1: Invites user to create a post hw2
		protected static Label label_CreatePost = new Label("Create Post: ");
		protected static TextField text_PostContent = new TextField();
		protected static Button button_Post = new Button("Post");
		
		
		//GUI area 2: Shows all posts within database from general hw2
		
		protected static ObservableList<Post> postDisplay = FXCollections.observableArrayList();
		protected static ListView<Post> displayPosts = new ListView<>();
		
		protected static Alert alertNotImplemented = new Alert(AlertType.INFORMATION);
		protected static Alert alertPosted = new Alert(AlertType.INFORMATION);
		
		// Bren
		protected static ComboBox<String> combobox_SelectPostThread = new ComboBox<>();
		protected static ComboBox<String> combobox_FilterThread = new ComboBox<>();
		protected static Label label_FilterThread = new Label("Filter by Thread:");
		protected static List<String> filterOptions = new ArrayList<>();
		
		//HW2 Brenn 
		//search keywords
		protected static TextField text_SearchKeyword = new TextField();
		protected static Button button_Search = new Button("Search");
		protected static Button button_ClearSearch = new Button("Show All Posts");
				
		//My Posts Section
		protected static Label label_myAccount = new Label("My Posts: ");
		protected static List<String> myPosts = new ArrayList<>();
		protected static List<String> myUnread = new ArrayList<>();
		protected static Button button_myPosts = new Button("My Posts");
		protected static Button button_myUnread = new Button("Unread Replies");

		// FLAG POST BUTTONS - KYLE
		protected static Button button_FlagPost = new Button("Flag Post");
		protected static Button button_ReviewFlaggedPosts = new Button("Review Flagged Posts");
		
		// FEEDBACK BUTTONS - KYLE
		protected static Button button_SendFeedback = new Button("Send Feedback");
		protected static Button button_ViewMyFeedback = new Button("View Feedback");
	
		// This is a separator and it is used to partition the GUI for various tasks
		protected static Line line_Separator4 = new Line(20, 525, width-20,525);
		
		// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
		// logging out. 
		protected static Button button_Home = new Button("Return Home");
		protected static Button button_Quit = new Button("Quit");

		// This is the end of the GUI objects for the page.
		
		// These attributes are used to configure the page and populate it with this user's information
		private static ViewPostDisplay theView;		// Used to determine if instantiation of the class
													// is needed

		// Reference for the in-memory database so this package has access
		private static Database theDatabase = applicationMain.FoundationsMain.database;

		protected static Stage theStage;			// The Stage that JavaFX has established for us	
		protected static Pane theRootPane;			// The Pane that holds all the GUI wis
		protected static User theUser;				// The current loeged in User
		protected static int theRole;

		private static Scene theViewPostDisplayScene;	// The shared Scene each invocation populates

		/*-*******************************************************************************************

		Constructors
		
		 */
		//Display Posts for students
		public static void displayPosts(Stage ps, User user) {
			
			// Establish the references to the GUI and the current user
			theStage = ps;
			theUser = user;
			System.out.println("*** Fetching Posts");
			
			// If not yet established, populate the static aspects of the GUI
			if (theView == null) theView = new ViewPostDisplay();		// Instantiate singleton if needed
			
			// Populate the dynamic aspects of the GUI with the data from the user and the current
			// state of the system.
			theDatabase.getUserAccountDetails(user.getUserName());
			
			//will need to update later
			theRole = 2;

			applicationMain.FoundationsMain.activeHomePage = theRole;
			
			label_UserDetails.setText("User: " + theUser.getUserName());
			//patch
			label_numReplies.setText("You have " + theDatabase.getUserUnread(theDatabase.getCurrentUsername()) + " unread replies.");

			// SHOW/HIDE MODERATION BUTTONS BASED ON ROLE - KYLE
			// Only show flag and feedback buttons if user is Role2 (Staff)
			boolean isStaff = theUser.getNewRole2();
			button_FlagPost.setVisible(isStaff);
			button_ReviewFlaggedPosts.setVisible(isStaff);
			button_SendFeedback.setVisible(isStaff);
			button_ViewMyFeedback.setVisible(isStaff);
			
			// Set the title for the window, display the page, and wait for the Admin to do something
			theStage.setTitle("CSE 360 Foundations: Posts"); // ***MODIFIED*** Role1 -> Student									
			theStage.setScene(theViewPostDisplayScene);
			theStage.show();
		}
		
		
		
		/**********
		 * <p> Method: ViewRole1Home() </p>
		 * 
		 * <p> Description: This method initializes all the elements of the graphical user interface.
		 * This method determines the location, size, font, color, and change and event handlers for
		 * each GUI object.</p>
		 * 
		 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
		 * fields using the displayRole2Home method.</p>
		 * 
		 */
		private ViewPostDisplay() {

			// Create the Pane for the list of widgets and the Scene for the window
			theRootPane = new Pane();
			theViewPostDisplayScene = new Scene(theRootPane, width, height);	// Create the scene
			
			// Set the title for the window
			System.out.println("*** Fetching Posts");
			// Populate the window with the title and other common widgets and set their static state
			
			// GUI Area 1
			label_PageTitle.setText("View Posts"); // ***MODIFIED***
														  // Role1 Home Page -> Student Home Page
			setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

			setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
			//patch
			setupLabelUI(label_numReplies, "Arial", 20, width, Pos.BASELINE_LEFT, 150, 55);
			
			
			// GUI Area 2 
			setupLabelUI(label_CreatePost, "Arial", 20, 175, Pos.BASELINE_LEFT, 20, 110);
			setupTextUI(text_PostContent, "Arial", 16, 350, Pos.BASELINE_LEFT,
			140, 105, true);
			setupButtonUI(button_Post,  "Dialog", 18, 100, Pos.CENTER,680, 103);
			button_Post.setOnAction((_) -> {ControllerPostDisplay.performNewPost(); postDisplay.setAll(theDatabase.displayPostHelper());});
			
			//Brenn
			//search keywords
			setupTextUI(text_SearchKeyword, "Arial", 16, 200, Pos.BASELINE_LEFT, 340, 160, true);
			text_SearchKeyword.setPromptText("Search posts...");

			setupButtonUI(button_Search, "Dialog", 16, 100, Pos.CENTER, 550, 158);
			button_Search.setOnAction((_) -> { ControllerPostDisplay.performSearch(); });
			setupButtonUI(button_ClearSearch, "Dialog", 16, 100, Pos.CENTER, 660, 355);
			button_ClearSearch.setOnAction((_) -> { 
			    text_SearchKeyword.setText("");
			    postDisplay.setAll(theDatabase.displayPostHelper()); 
			});
			
			//brenn
			setupComboBoxUI(combobox_SelectPostThread, "Arial", 14, 130, 500, 108);
			combobox_SelectPostThread.getItems().setAll(theDatabase.getAllThreadTypes());
			combobox_SelectPostThread.setValue("General");
			combobox_SelectPostThread.setMaxWidth(170);

			setupLabelUI(label_FilterThread, "Arial", 16, 130, Pos.BASELINE_LEFT, 20, 165);
			setupComboBoxUI(combobox_FilterThread, "Arial", 14, 130, 160, 163);
			List<String> filterOptions = theDatabase.getAllThreadTypes();
			filterOptions.add(0, "All");
			combobox_FilterThread.setLayoutX(155);
			combobox_FilterThread.setLayoutY(163);
			combobox_FilterThread.setMinWidth(150);
			combobox_FilterThread.getItems().setAll(filterOptions);
			combobox_FilterThread.setValue("All");
			combobox_FilterThread.setOnAction((_) -> { ControllerPostDisplay.performFilter(); });
			setupLabelUI(label_myAccount, "Arial",  20, 130, Pos.BASELINE_LEFT, 680, 215);
			setupButtonUI(button_myPosts, "Dialog",16,100,Pos.CENTER,660,255);
			button_myPosts.setOnAction((_) -> {
				ControllerPostDisplay.showMyPosts();
			});
			setupButtonUI(button_myUnread, "Dialog",16,100,Pos.CENTER,660, 305);
			button_myUnread.setOnAction((_) -> {
				ControllerPostDisplay.showMyUnread();
			});

			// SEND FEEDBACK BUTTONS - KYLE
			setupButtonUI(button_SendFeedback, "Dialog", 14, 140, Pos.CENTER, 20, 490);
			button_SendFeedback.setStyle("-fx-text-fill: black; -fx-padding: 5; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
			button_SendFeedback.setOnAction((_) -> { ControllerPostDisplay.performSendFeedback(); });

			setupButtonUI(button_ViewMyFeedback, "Dialog", 14, 140, Pos.CENTER, 170, 490);
			button_ViewMyFeedback.setStyle("-fx-text-fill: black; -fx-padding: 5; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
			button_ViewMyFeedback.setOnAction((_) -> { ControllerPostDisplay.performViewMyFeedback(); });
			
			// FLAG POST BUTTONS - KYLE
			setupButtonUI(button_FlagPost, "Dialog", 14, 150, Pos.CENTER, 320, 490);
			button_FlagPost.setStyle("-fx-text-fill: black; -fx-padding: 5; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
			button_FlagPost.setOnAction((_) -> { ControllerPostDisplay.performFlagPost(); });

			setupButtonUI(button_ReviewFlaggedPosts, "Dialog", 14, 180, Pos.CENTER, 480, 490);
			button_ReviewFlaggedPosts.setStyle("-fx-text-fill: black; -fx-padding: 5; -fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
			button_ReviewFlaggedPosts.setOnAction((_) -> { ControllerPostDisplay.performReviewFlaggedPosts(); });
			
			//GUI Area 3
		
			postDisplay = theDatabase.displayPostHelper();
			displayPosts.setItems(postDisplay);
			displayPosts.setPrefSize(640, 280);
			displayPosts.setLayoutX(20);
			displayPosts.setLayoutY(200);
			displayPosts.setCellFactory(_ -> new ListCell<Post>() {
				@Override
				protected void updateItem(Post item, boolean empty) {
					super.updateItem(item, empty);
				
					if(empty || item ==null) {
						setText(null);}
					else {
						setText(item.toString());
						setWrapText(true);
						setAlignment(Pos.CENTER_LEFT);
					}}
				});  
			displayPosts.setOnMouseClicked(event -> {
			    if (event.getClickCount() == 2) {
			        int selected = displayPosts.getSelectionModel().getSelectedIndex();
			        if (selected >= 0) {
			        	Post somePost = displayPosts.getSelectionModel().getSelectedItem();
			        	
			        	if(theDatabase.isOwnPost(somePost)) {
			        		if(ControllerPostDisplay.deletePost(somePost)) {
			        			postDisplay.setAll(theDatabase.displayPostHelper());
			        		}
			        		if(theDatabase.getAuthorUnread(somePost) > 0) {
			        			 ControllerPostDisplay.viewReplies(somePost);
			        			ControllerPostDisplay.clearUnread(somePost);
			    				label_numReplies.setText("You have " + theDatabase.getUserUnread(theDatabase.getCurrentUsername()) + " unread replies.");
			        		}
			        		else{
			        			 ControllerPostDisplay.viewReplies(somePost);}
			        		}
			        	else {
			            ControllerPostDisplay.viewReplies(somePost);
			    }}}}
			);
			
			
			
			
			// GUI Area 4
			setupButtonUI(button_Home, "Dialog", 18, 250, Pos.CENTER, 20, 540);
	        button_Home.setOnAction((_) -> {ControllerPostDisplay.backToHomePage(theStage, theUser); });
	        
	        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
	        button_Quit.setOnAction((_) -> {ControllerPostDisplay.performQuit(); });

			// This is the end of the GUI initialization code
			// Place all of the widget items into the Root Pane's list of children
	         theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, label_numReplies, line_Separator1,label_CreatePost,text_PostContent,button_Post,
				combobox_SelectPostThread,label_FilterThread,combobox_FilterThread,label_myAccount,button_myPosts, button_myUnread,
				text_SearchKeyword, button_Search, button_ClearSearch,
				button_FlagPost, button_ReviewFlaggedPosts,button_SendFeedback, button_ViewMyFeedback,
		        line_Separator2,displayPosts, line_Separator4, button_Home, button_Quit);
	}
		
		
		/*-********************************************************************************************

		Helper methods to reduce code length

		 */
		
		/**********
		 * Private local method to initialize the standard fields for a label
		 * 
		 * @param l		The Label object to be initialized
		 * @param ff	The font to be used
		 * @param f		The size of the font to be used
		 * @param w		The width of the Button
		 * @param p		The alignment (e.g. left, centered, or right)
		 * @param x		The location from the left edge (x axis)
		 * @param y		The location from the top (y axis)
		 */
		private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
				double y){
			l.setFont(Font.font(ff, f));
			l.setMinWidth(w);
			l.setAlignment(p);
			l.setLayoutX(x);
			l.setLayoutY(y);		
		}
		/**********
		 * Private local method to initialize the standard fields for a button
		 * 
		 * @param b		The Button object to be initialized
		 * @param ff	The font to be used
		 * @param f		The size of the font to be used
		 * @param w		The width of the Button
		 * @param p		The alignment (e.g. left, centered, or right)
		 * @param x		The location from the left edge (x axis)
		 * @param y		The location from the top (y axis)
		 */
		private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
				double y){
			b.setFont(Font.font(ff, f));
			b.setMinWidth(w);
			b.setAlignment(p);
			b.setLayoutX(x);
			b.setLayoutY(y);		
		}
		
		
		private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
			t.setFont(Font.font(ff, f));
			t.setMinWidth(w);
			t.setMaxWidth(w);
			t.setAlignment(p);
			t.setLayoutX(x);
			t.setLayoutY(y);		
			t.setEditable(e);
		}	
		
		//HW2 brenn combobox helper
		private static void setupComboBoxUI(ComboBox<String> c, String ff, double f,
		        double w, double x, double y) {
		    c.setStyle("-fx-font: " + f + " " + ff + ";");
		    c.setMinWidth(w);
		    c.setLayoutX(x);
		    c.setLayoutY(y);
		}
		
		
}
