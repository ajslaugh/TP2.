
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
	
	protected static void performQuit() {
		System.exit(0);
	}
	
	
}

	
