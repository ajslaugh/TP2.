package guiThreadManagement;

import database.Database;
import entityClasses.ThreadType;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewThreadManagement Class. </p>
 * 
 * <p> Description: The JavaFX-based GUI for staff thread management. This page
 * allows staff to create, update, delete, archive, and review discussion threads. </p>
 * 
 * @author Gabriella Romero
 */
public class ViewThreadManagement {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label();
    protected static Label label_UserDetails = new Label();

    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    protected static Line line_Separator2 = new Line(20, 150, width - 20, 150);
    protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

    protected static Label label_ThreadName = new Label("Thread Name:");
    protected static Label label_ThreadDescription = new Label("Description:");

    protected static ListView<ThreadType> list_Threads = new ListView<>();

    protected static TextField text_ThreadName = new TextField();
    protected static TextField text_ThreadDescription = new TextField();

    protected static Button button_ViewPosts = new Button("View Posts");
    protected static Button button_CreateThread = new Button("Create Thread");
    protected static Button button_UpdateThread = new Button("Update Thread");
    protected static Button button_DeleteThread = new Button("Delete Thread");
    protected static Button button_ArchiveThread = new Button("Archive Thread");
    protected static Button button_RefreshThreads = new Button("Refresh Threads");

    protected static Button button_Home = new Button("Return Home");
    protected static Button button_Quit = new Button("Quit");

    private static ViewThreadManagement theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    private static Scene theThreadManagementScene;

    /**********
     * <p> Method: displayThreadManagement(Stage ps, User user) </p>
     * 
     * <p> Description: Displays the thread management page for the current staff user. </p>
     * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
     */
    public static void displayThreadManagement(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewThreadManagement();

        theDatabase.getUserAccountDetails(user.getUserName());
        label_UserDetails.setText("User: " + theUser.getUserName());

        theStage.setTitle("CSE 360 Foundations: Thread Management");
        theStage.setScene(theThreadManagementScene);
        theStage.show();

        ControllerThreadManagement.refreshThreadList();
    }

    /**********
     * <p> Method: ViewThreadManagement() </p>
     * 
     * <p> Description: Initializes all GUI components for the thread management page. </p>
     */
    private ViewThreadManagement() {
        theRootPane = new Pane();
        theThreadManagementScene = new Scene(theRootPane, width, height);

        label_PageTitle.setText("Thread Management");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

        setupLabelUI(label_ThreadName, "Arial", 14, 110, Pos.BASELINE_LEFT, 20, 180);
        setupLabelUI(label_ThreadDescription, "Arial", 14, 110, Pos.BASELINE_LEFT, 20, 220);

        setupTextUI(text_ThreadName, "Arial", 14, 180, Pos.BASELINE_LEFT, 130, 180, true);
        setupTextUI(text_ThreadDescription, "Arial", 14, 180, Pos.BASELINE_LEFT, 130, 220, true);

        text_ThreadName.setPromptText("Enter thread name");
        text_ThreadDescription.setPromptText("Enter description");

        list_Threads.setLayoutX(370);
        list_Threads.setLayoutY(170);
        list_Threads.setPrefWidth(400);
        list_Threads.setPrefHeight(200);

        // user must select the thread before they can edit it
        list_Threads.setOnMouseClicked(e -> {
            ThreadType selected = list_Threads.getSelectionModel().getSelectedItem();
            if (selected != null) {
                text_ThreadName.setText(selected.getName());
                text_ThreadDescription.setText(selected.getDescription());
            }
        });

        setupButtonUI(button_ViewPosts, "Dialog", 14, 150, Pos.CENTER, 600, 110);
        setupButtonUI(button_CreateThread, "Dialog", 14, 150, Pos.CENTER, 20, 300);
        setupButtonUI(button_UpdateThread, "Dialog", 14, 150, Pos.CENTER, 180, 300);
        setupButtonUI(button_DeleteThread, "Dialog", 14, 150, Pos.CENTER, 20, 350);
        setupButtonUI(button_ArchiveThread, "Dialog", 14, 150, Pos.CENTER, 180, 350);
        setupButtonUI(button_RefreshThreads, "Dialog", 14, 150, Pos.CENTER, 100, 400);

        setupButtonUI(button_Home, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);

        button_CreateThread.setOnAction((_) -> { ControllerThreadManagement.performCreateThread(); });
        button_UpdateThread.setOnAction((_) -> { ControllerThreadManagement.performUpdateThread(); });
        button_DeleteThread.setOnAction((_) -> { ControllerThreadManagement.performDeleteThread(); });
        button_ArchiveThread.setOnAction((_) -> { ControllerThreadManagement.performArchiveThread(); });
        button_RefreshThreads.setOnAction((_) -> { ControllerThreadManagement.refreshThreadList(); });
        button_ViewPosts.setOnAction((_) -> { guiPostDisplay.ViewPostDisplay.displayPosts(theStage, theUser); });
        
        button_Home.setOnAction((_) -> { ControllerThreadManagement.performReturnHome(); });
        button_Quit.setOnAction((_) -> { ControllerThreadManagement.performQuit(); });

        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, line_Separator1, line_Separator2, line_Separator4,
            label_ThreadName, label_ThreadDescription, button_ViewPosts,
            text_ThreadName, text_ThreadDescription, list_Threads,
            button_CreateThread, button_UpdateThread, button_DeleteThread,
            button_ArchiveThread, button_RefreshThreads, button_Home, button_Quit
        );
    }

    /**********
     * Private local method to initialize the standard fields for a label
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**********
     * Private local method to initialize the standard fields for a button
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**********
     * Private local method to initialize the standard fields for a text field
     */
    private static void setupTextUI(TextField t, String ff, double f, double w, Pos p,
            double x, double y, boolean e) {
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setEditable(e);
    }
}