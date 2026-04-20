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
 * <p> Tests Covered: </p>
 * <ul>
 *   <li><b>THREAD-01:</b> Create Thread – Validates thread creation via UI input.</li>
 *   <li><b>THREAD-02:</b> Update Thread – Validates updating selected thread details.</li>
 *   <li><b>THREAD-03:</b> Delete Thread – Validates removal of selected thread.</li>
 *   <li><b>THREAD-04:</b> Archive Thread – Validates archiving functionality.</li>
 *   <li><b>THREAD-05:</b> Retrieve Threads – Validates refresh and display of thread list.</li>
 * </ul>
 * 
 * @author Gabriella Romero
 */
public class ControllerThreadManagement {

    // shared database reference used to keep thread management actions consistent with the rest of the application
    protected static Database theDatabase = applicationMain.FoundationsMain.database;

    /*****
     * <p> Method: refreshThreadList() </p>
     * 
     * <p> Description: Refreshes the thread list displayed in the UI. </p>
     * 
     * <p> Test: THREAD-05 Retrieve Threads </p>
     */
    protected static void refreshThreadList() {
        ViewThreadManagement.list_Threads.getItems().setAll(theDatabase.getAllThreadsDetailed());
    }

    /*****
     * <p> Method: performCreateThread() </p>
     * 
     * <p> Description: Creates a new thread using the values entered in the UI. </p>
     * 
     * <p> Test: THREAD-01 Create Thread </p>
     */
    protected static void performCreateThread() {
        String name = ViewThreadManagement.text_ThreadName.getText().trim();
        String description = ViewThreadManagement.text_ThreadDescription.getText().trim();
        
        // validate input here so the user receives immediate feedback before a database call is attempted
        if (name.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Thread name cannot be empty.");
            alert.showAndWait();
            return;
        }
        
        // thread creation defaults to active status so it can immediately be used in discussions
        boolean success = theDatabase.addThreadType(name, description);

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Created" : "Create Failed");
        alert.setContentText(success ? "Thread added successfully." : "Thread could not be created.");
        alert.showAndWait();
        
        // refresh after creation so the newly added thread appears without reopening the page
        refreshThreadList();
    }

    /*****
     * <p> Method: performUpdateThread() </p>
     * 
     * <p> Description: Updates the selected thread using the values in the UI. </p>
     * 
     * <p> Test: THREAD-02 Update Thread </p>
     */
    protected static void performUpdateThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        // existing thread must be selected before updating
        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to update.");
            alert.showAndWait();
            return;
        }

        String name = ViewThreadManagement.text_ThreadName.getText().trim();
        String description = ViewThreadManagement.text_ThreadDescription.getText().trim();
        
        // the selected thread keeps its current status so general updates do not accidentally archive or reactivate it
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
        
        // refresh after update so the ListView shows the newest thread values
        refreshThreadList();
    }

    /*****
     * <p> Method: performDeleteThread() </p>
     * 
     * <p> Description: Deletes the selected thread from the database. </p>
     * 
     * <p> Test: THREAD-03 Delete Thread </p>
     */
    protected static void performDeleteThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        // an existing thread must be selected first so the system does not try to delete an unknown record
        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to delete.");
            alert.showAndWait();
            return;
        }
        
        // deletion rules such as protecting the General thread are enforced in the database layer
        boolean success = theDatabase.removeThreadTypeById(selected.getId());

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Deleted" : "Delete Failed");
        alert.setContentText(success ? "Thread deleted successfully." : "Thread could not be deleted.");
        alert.showAndWait();
        
        // refresh after deletion so removed threads no longer appear in the interface
        refreshThreadList();
    }

    /*****
     * <p> Method: performArchiveThread() </p>
     * 
     * <p> Description: Archives the selected thread. </p>
     * 
     * <p> Test: THREAD-04 Archive Thread </p>
     */
    protected static void performArchiveThread() {
        ThreadType selected = ViewThreadManagement.list_Threads.getSelectionModel().getSelectedItem();

        // an existing thread must be selected first so the correct thread is moved to archived status
        if (selected == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("No Selection");
            alert.setContentText("Please select a thread to archive.");
            alert.showAndWait();
            return;
        }

        // archiving is handled through a dedicated action so status changes are intentional and traceable
        boolean success = theDatabase.archiveThreadType(selected.getId());

        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setHeaderText(success ? "Thread Archived" : "Archive Failed");
        alert.setContentText(success ? "Thread archived successfully." : "Thread could not be archived.");
        alert.showAndWait();

        // refresh after archiving so the updated status is immediately visible in the list
        refreshThreadList();
    }

    /*****
     * <p> Method: performReturnHome() </p>
     * 
     * <p> Description: Returns the staff user to the staff home page. </p>
     * 
     * <p> Test: Manual Test Required (GUI Navigation) </p>
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
