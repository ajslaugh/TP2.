package guiAdminHome;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import database.Database;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
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

public class ControllerAdminHome {
	private static ChoiceDialog<String> dialogManageInvites;
	private static Optional<String> result;
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		
		// ***MODIFIED***
		// adding a placeholder so it doesn't default to admin
		// a role must be chosen before sending invitation
		if (theSelectedRole.equals("<Select a role>")) {
		    ViewAdminHome.alertEmailError.setContentText("Please select a role before sending an invitation.");
		    ViewAdminHome.alertEmailError.showAndWait();
		    return;
		}
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress +
				"\nThis invitation code will expire in 1 minute.";
		
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		// **MODIFIED** Brenna Coe
		//View Outgoing Invitations
		
		//get all the invitations from the database
		List<String[]> invitations = theDatabase.getAllInvitations();
		
		//check if there are any invitations
		if (invitations.isEmpty()) {
			ViewAdminHome.alertInvitationsList.setTitle("Invitations");
			ViewAdminHome.alertInvitationsList.setHeaderText("No Outstanding Invitations");
			ViewAdminHome.alertInvitationsList.setContentText(
					"There are currently no pending invitations.");
			ViewAdminHome.alertInvitationsList.showAndWait();
			return;
		}
		
		//create a list to display all the invitations
		StringBuilder invitationList = new StringBuilder();
		invitationList.append("Outstanding Invitations:\n\n");
		
		for (String[] invitation : invitations) {
			invitationList.append(String.format("Code: %s | Email: %s | Role: %s\n", 
					invitation[0], invitation[1], invitation[2]));
		}
		
		//display the invitations
		ViewAdminHome.alertInvitationsList.setTitle("Manage Invitations");
		ViewAdminHome.alertInvitationsList.setHeaderText(
				"Outstanding Invitations (" + invitations.size() + ")");
		ViewAdminHome.alertInvitationsList.setContentText(invitationList.toString());
		ViewAdminHome.alertInvitationsList.showAndWait();

		//Resend and Recind Invitations
		if(invitations == null || invitations.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invitation Management");
            alert.setHeaderText(null);
            alert.setContentText("There are no invitations to manage.");
            alert.showAndWait();
			return;
		}
        //Convert invitations to formatted strings for the ChoiceDialog
		List<String> formattedInvitations = new ArrayList<>();
		for (String[] invitation : invitations) {
		    formattedInvitations.add(String.format("Code: %s | Email: %s | Role: %s",
		            invitation[0], invitation[1], invitation[2]));
		}

		dialogManageInvites = new ChoiceDialog<String>(
		    formattedInvitations.get(0), 
		    formattedInvitations
		);
        dialogManageInvites.setHeaderText("Please select an invitation you'd like to adjust:");
			
        //open 2nd choiceDialog - to rescind or resend
        result = dialogManageInvites.showAndWait();
        if (result.isEmpty()) return;

        String selectedInvite = result.get();
        
        List<String> actions = List.of("Resend Invitation", "Rescind Invitation");

        ChoiceDialog<String> actionDialog =
                new ChoiceDialog<>(actions.get(0), actions);

        actionDialog.setTitle("Invitation Action");
        actionDialog.setHeaderText("What would you like to do?");
        actionDialog.setContentText("Choose an action:");

        Optional<String> actionResult = actionDialog.showAndWait();
        if (actionResult.isEmpty()) return;

        String action = actionResult.get();
        
        String code = selectedInvite.substring(
                selectedInvite.indexOf("Code: ") + 6,
                selectedInvite.indexOf(" | Email")
        );
        
        if (action.equals("Rescind Invitation")) {
            boolean deleted = theDatabase.deleteInvitationByCode(code);
            System.out.println("Deleted? " + deleted);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invitation Rescinded");
            alert.setHeaderText(null);
            alert.setContentText("The invitation has been successfully rescinded.");
            alert.showAndWait();
            
            ViewAdminHome.refreshInvitationCount();
            
        } else {
        	boolean resent = theDatabase.resendInvitation(code);
            System.out.println("Re-sent? " + resent);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invitation Resent");
            alert.setHeaderText(null);
            alert.setContentText("The invitation has been resent to the user.");
            alert.showAndWait();
            
            ViewAdminHome.refreshInvitationCount();

        }
	
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		// ***MODIFIED***
	    // pick user to send OTP
	    javafx.scene.control.ChoiceDialog<String> pickUser =
	        new javafx.scene.control.ChoiceDialog<>(
	            "<Select a User>",
	            theDatabase.getUserList()
	        );
	    pickUser.setTitle("Set One-Time Password");
	    pickUser.setHeaderText("Select a user.");
	    pickUser.setContentText("User:");

	    java.util.Optional<String> userChoice = pickUser.showAndWait();
	    if (userChoice.isEmpty()) return;

	    String username = userChoice.get();
	    if (username.equals("<Select a User>")) return;

	    // enter a OTP
	    javafx.scene.control.TextInputDialog pwdDialog = new javafx.scene.control.TextInputDialog("");
	    pwdDialog.setTitle("Set One-Time Password");
	    pwdDialog.setHeaderText("Enter a one-time password for: " + username);
	    pwdDialog.setContentText("One-time password:");

	    java.util.Optional<String> pwdChoice = pwdDialog.showAndWait();
	    if (pwdChoice.isEmpty()) return;

	    String tempPassword = pwdChoice.get().trim();
	    if (tempPassword.length() < 1) {
	        ViewAdminHome.alertEmailError.setTitle("*** ERROR ***");
	        ViewAdminHome.alertEmailError.setHeaderText("One-Time Password Issue");
	        ViewAdminHome.alertEmailError.setContentText("One-time password cannot be empty.");
	        ViewAdminHome.alertEmailError.showAndWait();
	        return;
	    }

	    // save the password
	    boolean ok = theDatabase.setOneTimePassword(username, tempPassword);
	    if (ok) {
	        ViewAdminHome.alertEmailSent.setTitle("One-Time Password Set");
	        ViewAdminHome.alertEmailSent.setHeaderText("Success");
	        ViewAdminHome.alertEmailSent.setContentText(
	            "A one-time password has been set for: " + username +
	            "\nThey must change it after logging in."
	        );
	        ViewAdminHome.alertEmailSent.showAndWait();
	    } else {
	        ViewAdminHome.alertEmailError.setTitle("*** ERROR ***");
	        ViewAdminHome.alertEmailError.setHeaderText("Database Error");
	        ViewAdminHome.alertEmailError.setContentText("Could not set one-time password.");
	        ViewAdminHome.alertEmailError.showAndWait();
	    }
	}

	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
    System.out.println("\n[ADMIN_DELETE_USER] AdminHome -> deleteUser()");

    // 1) Pull current users
    List<String> users = theDatabase.getUserList();

    if (users == null) {
        System.out.println("[ADMIN_DELETE_USER][ERROR] getUserList() returned null");
        ViewAdminHome.alertNotImplemented.setTitle("Delete User");
        ViewAdminHome.alertNotImplemented.setHeaderText("Database Error");
        ViewAdminHome.alertNotImplemented.setContentText("getUserList() returned null.");
        ViewAdminHome.alertNotImplemented.showAndWait();
        return;
    }

    // 2) Build a readable list for the admin to see while choosing
    StringBuilder userListText = new StringBuilder();
    for (int i = 1; i < users.size(); i++) { // skip "<Select a User>"
        userListText.append("- ").append(users.get(i)).append("\n");
    }

    if (userListText.length() == 0) {
        System.out.println("[ADMIN_DELETE_USER] No users available to delete");
        ViewAdminHome.alertNotImplemented.setTitle("Delete User");
        ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
        ViewAdminHome.alertNotImplemented.setContentText("There are no users to delete.");
        ViewAdminHome.alertNotImplemented.showAndWait();
        return;
    }

    // 3) Ask which user to delete (admin types the username)
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Delete User");
    dialog.setHeaderText("Type the EXACT username to delete");
    dialog.setContentText("Existing users:\n" + userListText + "\nUsername to delete:");

    Optional<String> result = dialog.showAndWait();
    if (result.isEmpty()) {
        System.out.println("[ADMIN_DELETE_USER] Delete canceled by admin");
        return;
    }

    String userToDelete = result.get().trim();

    // Safety checks
    if (userToDelete.equals("") || userToDelete.equals("<Select a User>")) {
        System.out.println("[ADMIN_DELETE_USER][VALIDATION] Invalid username input");
        ViewAdminHome.alertNotImplemented.setTitle("Delete User");
        ViewAdminHome.alertNotImplemented.setHeaderText("Invalid Input");
        ViewAdminHome.alertNotImplemented.setContentText("Please enter a real username.");
        ViewAdminHome.alertNotImplemented.showAndWait();
        return;
    }

    //prevent deleting yourself
    if (ViewAdminHome.theUser != null && userToDelete.equals(ViewAdminHome.theUser.getUserName())) {
        System.out.println("[ADMIN_DELETE_USER][SECURITY] Admin attempted self-delete: " + userToDelete);
        ViewAdminHome.alertNotImplemented.setTitle("Delete User");
        ViewAdminHome.alertNotImplemented.setHeaderText("Not Allowed");
        ViewAdminHome.alertNotImplemented.setContentText("You cannot delete the account you are currently logged in with.");
        ViewAdminHome.alertNotImplemented.showAndWait();
        return;
    }

    // 4) Delete it in DB
    System.out.println("[DB_DELETE_USER] Attempting deleteUser('" + userToDelete + "')");
    boolean deleted = theDatabase.deleteUser(userToDelete);

    // 5) Show result
    ViewAdminHome.alertNotImplemented.setTitle("Delete User");
    if (deleted) {
        System.out.println("[DB_DELETE_USER] Deleted user: " + userToDelete);
        ViewAdminHome.alertNotImplemented.setHeaderText("User Deleted");
        ViewAdminHome.alertNotImplemented.setContentText("Deleted: " + userToDelete);

        ViewAdminHome.label_NumberOfUsers.setText("Number of users: " + theDatabase.getNumberOfUsers());
    } else {
        System.out.println("[DB_DELETE_USER][FAIL] No user deleted for: " + userToDelete);
        ViewAdminHome.alertNotImplemented.setHeaderText("Delete Failed");
        ViewAdminHome.alertNotImplemented.setContentText("No user was deleted. Check that the username exists.");
    }

    ViewAdminHome.alertNotImplemented.showAndWait();
}

	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 * MODIFIED by KYLE PORCHE
	 */
	protected static void listUsers() {
		List<String[]> users = theDatabase.getAllUsers();

	    if (users == null || users.isEmpty()) {
	        ViewAdminHome.alertNotImplemented.setTitle("List Users");
	        ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
	        ViewAdminHome.alertNotImplemented.setContentText("There are no users in the database.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	        return;
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("Users in Database:\n\n");

	 
	    for (int i = 0; i < users.size(); i++) {
	        String[] user = users.get(i);

	        String username = user[0];
	        String firstName = user[1];
	        String middleName = user[2];
	        String lastName = user[3];
	        String email = user[4];
	        boolean isAdmin = Boolean.parseBoolean(user[5]);
	        boolean role1 = Boolean.parseBoolean(user[6]);
	        boolean role2 = Boolean.parseBoolean(user[7]);

	        // Build a string of active roles
	        List<String> roles = new ArrayList<>();
	        if (isAdmin) roles.add("Admin");
	        if (role1) roles.add("Role1");
	        if (role2) roles.add("Role2");
	        String rolesText = roles.isEmpty() ? "No roles assigned" : String.join(", ", roles);

	        // Build full name
	        String fullName = firstName + (middleName.isEmpty() ? " " : " " + middleName + " ") + lastName;

	        // Append user info to the string builder
	        sb.append(String.format("%d) Username: %s\n   Name: %s\n   Email: %s\n   Roles: %s\n\n",
	                i + 1, username, fullName, email, rolesText));
	    }

	    // Display the list in GUI alert
	    ViewAdminHome.alertNotImplemented.setTitle("List Users");
	    ViewAdminHome.alertNotImplemented.setHeaderText("Users in Database (" + (users.size()) + ")");
	    ViewAdminHome.alertNotImplemented.setContentText(sb.toString());
	    ViewAdminHome.alertNotImplemented.showAndWait();
	}

	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
