package guiThreadManagement;

import database.Database;
import entityClasses.ThreadType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/*******
 * <p> Title: ControllerThreadManagement Class </p>
 * 
 * <p> Description: Handles the controller logic for the thread management page.
 * This includes creating, updating, deleting, archiving, and refreshing threads. </p>
 * 
 * @author Gabriella Romero
 */
public class ControllerThreadManagement {

    protected static Database theDatabase = applicationMain.FoundationsMain.database;

    /*****
     * <p> Method: refreshThreadList() </p>
     * 
     * <p> Description: Refreshes the thread list displayed in the UI. </p>
     */
    protected static void refreshThreadList() {
        ViewThreadManagement.list_Threads.getItems().setAll(theDatabase.getAllThreadsDetailed());
    }

    /*****
     * <p> Method: performCreateThread() </p>
     * 
     * <p> Description: Creates a new thread using the values entered in the UI. </p>
     */
    protected static void performCreateThread() {
        String name = ViewThreadManagement.text_ThreadName.getText().trim();
        String description = ViewThreadManagement.text_ThreadDescription.getText().trim();

        if (name.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Thread name cannot be empty.");
            alert.showAndWait();
            return;
        }

        boolean success = theDatabase.addThreadType(name, description);

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Created" : "Create Failed");
        alert.setContentText(success ? "Thread added successfully." : "Thread could not be created.");
        alert.showAndWait();

        refreshThreadList();
    }

    /*****
     * <p> Method: performUpdateThread() </p>
     * 
     * <p> Description: Updates the selected thread using the values in the UI. </p>
     */
    protected static void performUpdateThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to update.");
            alert.showAndWait();
            return;
        }

        String name = ViewThreadManagement.text_ThreadName.getText().trim();
        String description = ViewThreadManagement.text_ThreadDescription.getText().trim();

        boolean success = theDatabase.updateThreadType(
            selected.getId(),
            name,
            description,
            selected.getStatus()
        );

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Updated" : "Update Failed");
        alert.setContentText(success ? "Thread updated successfully." : "Thread could not be updated.");
        alert.showAndWait();

        refreshThreadList();
    }

    /*****
     * <p> Method: performDeleteThread() </p>
     * 
     * <p> Description: Deletes the selected thread from the database. </p>
     */
    protected static void performDeleteThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to delete.");
            alert.showAndWait();
            return;
        }

        boolean success = theDatabase.removeThreadTypeById(selected.getId());

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Deleted" : "Delete Failed");
        alert.setContentText(success ? "Thread deleted successfully." : "Thread could not be deleted.");
        alert.showAndWait();

        refreshThreadList();
    }

    /*****
     * <p> Method: performArchiveThread() </p>
     * 
     * <p> Description: Archives the selected thread. </p>
     */
    protected static void performArchiveThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to archive.");
            alert.showAndWait();
            return;
        }

        boolean success = theDatabase.archiveThreadType(selected.getId());

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Archived" : "Archive Failed");
        alert.setContentText(success ? "Thread archived successfully." : "Thread could not be archived.");
        alert.showAndWait();

        refreshThreadList();
    }

    /*****
     * <p> Method: performReturnHome() </p>
     * 
     * <p> Description: Returns the staff user to the staff home page. </p>
     */
    protected static void performReturnHome() {
        guiRole2.ViewRole2Home.displayRole2Home(ViewThreadManagement.theStage, ViewThreadManagement.theUser);
    }

    /*****
     * <p> Method: performQuit() </p>
     * 
     * <p> Description: Terminates the application. </p>
     */
    protected static void performQuit() {
        System.exit(0);
    }
}