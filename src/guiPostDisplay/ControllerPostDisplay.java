
package guiPostDisplay;

import java.sql.SQLException;


import javafx.scene.control.ButtonBar;
import java.util.Optional;
import entityClasses.Reply;
import entityClasses.User;
import database.Database;
import entityClasses.Post;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import java.util.ArrayList;

/*******
 Controller for Post Display for students
 */

public class ControllerPostDisplay{
	//initialize variable
	private static String author = "";
	private static String content = "";
	private static String thread = "General";
	private static String role = "";
	private static int replyNumber;
	private static String replyContent = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;
	
	//go back to user homepage
protected static void goToUserHomePage(Stage theStage, User theUser) {
		
		
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
			break;
		case 3:
			guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** DISPLAY POSTS goToUserHomePage has an invalid role: " + 
					theRole);
			System.exit(0);
		}
 	}

//set author of post
protected static void setAuthor() {
	author = ViewPostDisplay.theUser.getUserName();
}

//set content
protected static void setContent() {
	content = ViewPostDisplay.text_PostContent.getText();
}

//set role
protected static void setRole() {
	if(ViewPostDisplay.theUser.getAdminRole()) {
		role = "ADMIN";}
	if(ViewPostDisplay.theUser.getNewRole1()) {
		role = "STUDENT";
		}
	if(ViewPostDisplay.theUser.getNewRole2()) {
		role = "STAFF";
	}
}






//go back to homepage
protected static void backToHomePage(Stage theStage, User theUser) {
	// Get the roles the user selected during login
			int theRole = applicationMain.FoundationsMain.activeHomePage;

			// Use that role to proceed to that role's home page
			switch (theRole) {
			case 1:
				guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
				break;
			case 2:
				guiRole1.ViewRole1Home.displayRole1Home(theStage, theUser);
				break;
			case 3:
				guiRole2.ViewRole2Home.displayRole2Home(theStage, theUser);
				break;
			default: 
				System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + 
						theRole);
				System.exit(0);
			}
	 	}


//perform a new post
protected static void performNewPost() {
	setAuthor();
	setContent();
	setRole();
	//Brenn
	thread = ViewPostDisplay.combobox_SelectPostThread.getValue();	
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
		post.setID(theDatabase.addPost(post));
	}
	catch (SQLException e) {
        System.err.println("*** ERROR *** Database error trying to create a post: " + 
        		e.getMessage());
        e.printStackTrace();
        System.exit(0);
    }
	ViewPostDisplay.text_PostContent.setText("");
	ViewPostDisplay.alertPosted.setHeaderText("Posting Successful!");
	ViewPostDisplay.alertPosted.setContentText("Your post will be displayed for all students and staff");
	ViewPostDisplay.alertPosted.showAndWait();
	
}


//delete your own post for students
	protected static boolean deletePost(Post post) {
		Alert alert2 = new Alert(Alert.AlertType.NONE);
		Alert alert = new Alert(Alert.AlertType.NONE);

		alert2.setTitle("Are you sure?");
		alert2.setContentText("Are you sure you want to delete this post? Users will still be able to see all replies.");
		
		alert.setTitle("Delete post?");
		alert.setHeaderText("This is your own post, do you want to view replies or delete it?");
		
		ButtonType goBack = new ButtonType("Back to Replies");
		ButtonType delete2 = new ButtonType("Delete Post");
		
		ButtonType reply = new ButtonType("View Replies");
		ButtonType delete = new ButtonType("Delete Post");

		ButtonType cancel = new ButtonType("ViewReplies", ButtonBar.ButtonData.CANCEL_CLOSE);
		ButtonType cancel2 = new ButtonType("Back To Replies", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().addAll(delete,cancel);
		alert2.getButtonTypes().addAll(delete2, cancel2);

		Optional<ButtonType> result = alert.showAndWait();
		
		

		if(result.isPresent()) {
			if(result.get() == delete) {
				Optional<ButtonType> result2 = alert2.showAndWait();
				if(result2.get() == goBack) {
					return false;
				}
				if(result2.get() == delete2) {
					boolean postDeleted;
					postDeleted = theDatabase.deletePost(post);
					if(postDeleted) {
						Alert deleteAlert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText("Post Deleted");
						alert.setContentText("You Have Successfully Deleted This Post");
						alert.showAndWait();
						return true;
				}
					else {
						Alert deleteAlert = new Alert(Alert.AlertType.INFORMATION);
						alert.setHeaderText("COULD NOT DELETE POST");
						alert.setContentText("THERE WAS AN ISSUE DELETING POST FROM DATABASE");
						alert.showAndWait();
						return false;
					}
				}

				
			}
		}
		return false;
		
	}
	
	// Brenn + Alex
	//called when student uses the combobox
	protected static void performFilter() {
		ViewPostDisplay.postDisplay.setAll(getFilteredPosts());
	}

	protected static void showMyPosts() {
		ViewPostDisplay.postDisplay.setAll(theDatabase.getOwnPosts());
		
	}
	
	protected static void showMyUnread() {
		ViewPostDisplay.postDisplay.setAll(theDatabase.getUnreadPosts());
	}
	
	//HW2 Brenn adjust
	protected static ObservableList<Post> getFilteredPosts() {
		String selected = ViewPostDisplay.combobox_FilterThread.getValue();
		if (selected == null || selected.equals("All")) {
				return theDatabase.displayPostHelper();
		}
		else {
			return theDatabase.getPostsByThread(selected);
		}
	}

	protected static void clearUnread(Post post) {
		Alert alert =  new Alert(Alert.AlertType.INFORMATION);
		int dec;
		dec = theDatabase.getAuthorUnread(post);
		theDatabase.DecrementUserUnread(post, dec);
		theDatabase.decrementAuthorUnread(post);
		alert.setTitle("You've read a new reply!");
		alert.setContentText("You now have only have " + theDatabase.getUserUnread(theDatabase.getCurrentUsername()) + " unread replies!"
				+ "\n Bringing you to all unread replies.");
		ViewPostDisplay.postDisplay.setAll(theDatabase.getUnreadPosts());
		
		alert.showAndWait();
	
	}
	
	

	//view replies
	protected static void viewReplies(Post ogPost) {
		int ogPostNum;
		ogPostNum = ogPost.getID();
		
		List<String[]> replies = theDatabase.getRepliesFromPost(ogPostNum);
		StringBuilder replyList = new StringBuilder();
		if(replies.isEmpty()) {
			replyList.append("There are currently no replies to this post");
		}
		else {
			for (String[] reply : replies) {
				replyList.append(String.format("%s %s to %s thread: %s \n\n",
				reply[0], reply[2], reply[3], reply[1]));}}
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Replies");
		alert.setHeaderText("Replies for " + ogPost.toString());
		alert.setContentText(replyList.toString());
	    ButtonType reply = new ButtonType("Reply to this Post");
	    
	    alert.getButtonTypes().addAll(reply);
	    
	    Optional<ButtonType> result = alert.showAndWait();

	    if(result.isPresent()) {
	    	if(result.get() == reply) {
	    		performNewReply(ogPost);
	    	}
	    }
	}
	
	
//perform a new reply
	protected static void performNewReply(Post ogPost) {
		setAuthor();
		setRole();
		
		TextInputDialog writeAReply = new TextInputDialog();
		writeAReply.setTitle(String.format("Write A Reply to %s \n", ogPost));
		writeAReply.setHeaderText("Reply to this user");
		replyContent = writeAReply.showAndWait().orElse("");
		replyNumber = ogPost.getID();
		
		//input validation
		if(replyContent.isEmpty() || replyContent == "") {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Problem with posting");
			alert.setContentText("You have to write something in the text field in order to post this reply");
			alert.showAndWait();
			return;
				}
				
		
		Reply reply = new Reply(author, role, replyContent, thread, replyNumber);
		
		
		try {
			theDatabase.addReply(reply);
			theDatabase.updateReplyNumber(ogPost);
			theDatabase.updateAuthorUnread(ogPost);
			if(ogPost.getAuthor() != theDatabase.getCurrentUsername()) {
					theDatabase.updateUserUnread(ogPost);
			}
		}
		catch (SQLException e) {
          System.err.println("*** ERROR *** Database error trying to create a reply: " + 
          		e.getMessage());
          e.printStackTrace();
          System.exit(0);
      }
		ViewPostDisplay.text_PostContent.setText("");
		ViewPostDisplay.alertPosted.setHeaderText("Reply Successful!");
		ViewPostDisplay.alertPosted.setContentText("Your reply will be added to this posts list of replies");
		ViewPostDisplay.alertPosted.showAndWait();
	}
	
	//Brenn
	//search post for keywords
	protected static void performSearch() {
	    String keyword = ViewPostDisplay.text_SearchKeyword.getText().trim();
	    if (keyword.isEmpty()) {
	        // If nothing typed, just show everything
	        ViewPostDisplay.postDisplay.setAll(theDatabase.displayPostHelper());
	        return;
	    }
	    ViewPostDisplay.postDisplay.setAll(theDatabase.searchPostsByKeyword(keyword));
	}

	/**
	 * <p> Method: performFlagPost() </p>
	 * 
	 * <p> Description: Allows staff to flag a selected post as inappropriate. </p>
	 */
	protected static void performFlagPost() {
		// ===== PERMISSION CHECK =====
		if (!ViewPostDisplay.theUser.getNewRole2()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Permission Denied");
			alert.setHeaderText("Staff Only Action");
			alert.setContentText("Only staff members can flag posts.");
			alert.showAndWait();
			return;
		}
		
		int selectedIndex = ViewPostDisplay.displayPosts.getSelectionModel().getSelectedIndex();
		
		if (selectedIndex < 0) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Post Selected");
			alert.setHeaderText("Please select a post to flag");
			alert.setContentText("Click on a post in the list, then click 'Flag Selected Post'.");
			alert.showAndWait();
			return;
		}
		
		// Prompt staff to enter reason for flagging
		TextInputDialog reasonDialog = new TextInputDialog();
		reasonDialog.setTitle("Flag Post");
		reasonDialog.setHeaderText("Why are you flagging this post?");
		reasonDialog.setContentText("Reason:");
		
		Optional<String> result = reasonDialog.showAndWait();
		if (result.isEmpty()) {
			return;
		}
		
		String reason = result.get().trim();
		if (reason.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Input");
			alert.setHeaderText("Reason cannot be empty");
			alert.setContentText("Please provide a reason for flagging this post.");
			alert.showAndWait();
			return;
		}
		
		// Get the selected post
		Post selectedPost = ViewPostDisplay.displayPosts.getSelectionModel().getSelectedItem();
		if (selectedPost == null) return;
		
		// Add the flag to the database
		boolean flagged = theDatabase.flagPost(selectedPost.getID(), ViewPostDisplay.theUser.getUserName(), reason);
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		if (flagged) {
			alert.setTitle("Post Flagged");
			alert.setHeaderText("Success");
			alert.setContentText("Post has been flagged for review.\nReason: " + reason);
		} else {
			alert.setTitle("Flag Failed");
			alert.setHeaderText("Error");
			alert.setContentText("Could not flag the post. It may have already been flagged.");
		}
		alert.showAndWait();
	}

	/**
	 * <p> Method: performReviewFlaggedPosts() </p>
	 * 
	 * <p> Description: Displays all flagged posts and allows staff to take action. </p>
	 */
	protected static void performReviewFlaggedPosts() {
		// ===== PERMISSION CHECK =====
		if (!ViewPostDisplay.theUser.getNewRole2()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Permission Denied");
			alert.setHeaderText("Staff Only Action");
			alert.setContentText("Only staff members can review flagged posts.");
			alert.showAndWait();
			return;
		}
		
		List<String[]> flaggedPosts = theDatabase.getAllFlaggedPosts();
		
		if (flaggedPosts == null || flaggedPosts.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Flagged Posts");
			alert.setHeaderText("Review Queue");
			alert.setContentText("There are no flagged posts to review.");
			alert.showAndWait();
			return;
		}
		
		// Create a list of formatted flagged posts
		List<String> formattedFlags = new ArrayList<>();
		for (String[] flag : flaggedPosts) {
			// flag[0] = flagID, flag[1] = postID, flag[2] = flaggedBy, 
			// flag[3] = reason, flag[4] = status, flag[5] = post content
			formattedFlags.add(String.format(
				"Post #%s | Flagged by: %s | Status: %s | Reason: %s",
				flag[1], flag[2], flag[4], flag[3]
			));
		}
		
		// Let staff select a flagged post to review
		ChoiceDialog<String> selectFlagDialog = 
			new ChoiceDialog<>(formattedFlags.get(0), formattedFlags);
		selectFlagDialog.setTitle("Flagged Posts Review");
		selectFlagDialog.setHeaderText("Select a flagged post to review:");
		selectFlagDialog.setContentText("Flagged Posts:");
		
		Optional<String> selection = selectFlagDialog.showAndWait();
		if (selection.isEmpty()) {
			return;
		}
		
		// Extract the flagID from the selected text
		String selectedText = selection.get();
		String flagIDStr = selectedText.substring(selectedText.indexOf("#") + 1, selectedText.indexOf("|") - 1);
		int flagID = Integer.parseInt(flagIDStr.trim());
		
		// Find the full flag details
		String[] flagDetails = null;
		for (String[] flag : flaggedPosts) {
			if (Integer.parseInt(flag[0]) == flagID) {
				flagDetails = flag;
				break;
			}
		}
		
		if (flagDetails == null) return;
		
		// Show the flagged post details and allow resolution
		Alert reviewAlert = new Alert(Alert.AlertType.NONE);
		reviewAlert.setTitle("Review Flagged Post");
		reviewAlert.setHeaderText("Flagged Post Details");
		String reviewContent = String.format(
			"Post #%s\nContent: %s\n\nFlagged by: %s\nReason: %s\nStatus: %s",
			flagDetails[1], flagDetails[5], flagDetails[2], flagDetails[3], flagDetails[4]
		);
		reviewAlert.setContentText(reviewContent);
		
		ButtonType approveBtn = new ButtonType("Approve (Content OK)");
		ButtonType removeBtn = new ButtonType("Remove (Confirm Inappropriate)");
		ButtonType pendingBtn = new ButtonType("Keep Pending");
		ButtonType cancelBtn = new ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
		
		reviewAlert.getButtonTypes().addAll(approveBtn, removeBtn, pendingBtn, cancelBtn);
		
		Optional<ButtonType> reviewResult = reviewAlert.showAndWait();
		
		if (reviewResult.isEmpty()) return;
		
		ButtonType selectedAction = reviewResult.get();
		
		if (selectedAction == approveBtn) {
			theDatabase.updateFlagStatus(flagID, "APPROVED");
			Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
			resultAlert.setTitle("Post Approved");
			resultAlert.setHeaderText("Flag Resolved");
			resultAlert.setContentText("Post has been approved. Flag status updated to APPROVED.");
			resultAlert.showAndWait();
		} 
		else if (selectedAction == removeBtn) {
			theDatabase.updateFlagStatus(flagID, "RESOLVED");
			Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
			resultAlert.setTitle("Post Confirmed Inappropriate");
			resultAlert.setHeaderText("Flag Resolved");
			resultAlert.setContentText("Post has been marked as inappropriate and flag resolved.");
			resultAlert.showAndWait();
		}
	}
	
	// KYLE
	/**
	 * <p> Method: performSendFeedback() </p>
	 * 
	 * <p> Description: Opens dialog for staff to send private feedback to a student or other staff member. </p>
	 */
	protected static void performSendFeedback() {
		// Permission check
		if (!ViewPostDisplay.theUser.getNewRole2()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Permission Denied");
			alert.setHeaderText("Staff Only Action");
			alert.setContentText("Only staff members can send feedback.");
			alert.showAndWait();
			return;
		}

		// Get list of all users to send feedback to
		List<String> userList = theDatabase.getUserList();
		if (userList == null || userList.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Users Available");
			alert.setHeaderText("Error");
			alert.setContentText("No users available to send feedback to.");
			alert.showAndWait();
			return;
		}

		// Step 1: Select recipient
		ChoiceDialog<String> selectRecipientDialog = new ChoiceDialog<>(userList.get(0), userList);
		selectRecipientDialog.setTitle("Send Feedback");
		selectRecipientDialog.setHeaderText("Select recipient:");
		selectRecipientDialog.setContentText("Send feedback to:");

		Optional<String> recipientResult = selectRecipientDialog.showAndWait();
		if (recipientResult.isEmpty()) {
			return;
		}

		String toUser = recipientResult.get();

		// Step 2: Get related post (optional)
		int targetPostID = -1;
		Post selectedPost = ViewPostDisplay.displayPosts.getSelectionModel().getSelectedItem();
		if (selectedPost != null) {
			Alert confirmPostAlert = new Alert(Alert.AlertType.CONFIRMATION);
			confirmPostAlert.setTitle("Include Post Reference");
			confirmPostAlert.setHeaderText("Relate feedback to a post?");
			confirmPostAlert.setContentText("Do you want to relate this feedback to the selected post?");
			Optional<ButtonType> confirmResult = confirmPostAlert.showAndWait();
			if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
				targetPostID = selectedPost.getID();
			}
		}

		// Step 3: Select feedback type
		java.util.List<String> feedbackTypes = java.util.List.of("COACHING", "CONCERN", "APPRECIATION", "OTHER");
		ChoiceDialog<String> selectTypeDialog = new ChoiceDialog<>(feedbackTypes.get(0), feedbackTypes);
		selectTypeDialog.setTitle("Feedback Type");
		selectTypeDialog.setHeaderText("What type of feedback is this?");
		selectTypeDialog.setContentText("Feedback type:");

		Optional<String> typeResult = selectTypeDialog.showAndWait();
		if (typeResult.isEmpty()) {
			return;
		}

		String feedbackType = typeResult.get();

		// Step 4: Write feedback content
		TextInputDialog contentDialog = new TextInputDialog();
		contentDialog.setTitle("Compose Feedback");
		contentDialog.setHeaderText("Write your feedback message");
		contentDialog.setContentText("Feedback:");

		Optional<String> contentResult = contentDialog.showAndWait();
		if (contentResult.isEmpty()) {
			return;
		}

		String feedbackContent = contentResult.get().trim();
		if (feedbackContent.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Input");
			alert.setHeaderText("Feedback cannot be empty");
			alert.setContentText("Please write some feedback.");
			alert.showAndWait();
			return;
		}

		// Submit feedback
		boolean success = theDatabase.submitFeedback(
			ViewPostDisplay.theUser.getUserName(),
			toUser,
			feedbackContent,
			targetPostID,
			feedbackType
		);

		Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
		if (success) {
			resultAlert.setTitle("Feedback Sent");
			resultAlert.setHeaderText("Success");
			resultAlert.setContentText("Your feedback has been sent to " + toUser);
		} else {
			resultAlert.setTitle("Error");
			resultAlert.setHeaderText("Failed to Send Feedback");
			resultAlert.setContentText("There was an error sending your feedback.");
		}
		resultAlert.showAndWait();
	}

	/**
	 * <p> Method: performViewMyFeedback() </p>
	 * 
	 * <p> Description: Displays all feedback received by the current user. </p>
	 */
	protected static void performViewMyFeedback() {
		// Permission check
		if (!ViewPostDisplay.theUser.getNewRole2()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Permission Denied");
			alert.setHeaderText("Staff Only Action");
			alert.setContentText("Only staff members can view feedback.");
			alert.showAndWait();
			return;
		}

		// Get feedback received
		List<String[]> receivedFeedback = theDatabase.getFeedbackForUser(ViewPostDisplay.theUser.getUserName());

		if (receivedFeedback == null || receivedFeedback.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No Feedback");
			alert.setHeaderText("You have no feedback");
			alert.setContentText("You haven't received any feedback yet.");
			alert.showAndWait();
			return;
		}

		// Display feedback
		StringBuilder feedbackDisplay = new StringBuilder();
		feedbackDisplay.append("Feedback Received:\n\n");

		for (String[] feedback : receivedFeedback) {
			// feedback[0] = feedbackID, [1] = fromUser, [2] = content, [3] = feedbackType, [4] = timestamp, [5] = isRead
			String readStatus = Boolean.parseBoolean(feedback[5]) ? "[READ]" : "[UNREAD]";
			feedbackDisplay.append(String.format(
				"%s From: %s | Type: %s | Date: %s\n%s\n\n",
				readStatus,
				feedback[1],
				feedback[3],
				feedback[4],
				feedback[2]
			));
		}

		Alert feedbackAlert = new Alert(Alert.AlertType.INFORMATION);
		feedbackAlert.setTitle("My Feedback");
		feedbackAlert.setHeaderText("Feedback Received");
		feedbackAlert.setContentText(feedbackDisplay.toString());
		feedbackAlert.showAndWait();

		// Mark all as read
		for (String[] feedback : receivedFeedback) {
			int feedbackID = Integer.parseInt(feedback[0]);
			theDatabase.markFeedbackAsRead(feedbackID);
		}
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
	
	
}

	
