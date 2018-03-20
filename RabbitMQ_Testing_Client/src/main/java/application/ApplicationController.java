package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
public class ApplicationController {

	private ApplicationManager manager;

	public VBox eventsBox;
	
	public BorderPane ElementsAnchor;
	
	public Label Title;
	public Label Content;
	
	public void setManager(ApplicationManager manager) {
		this.manager = manager;
	}

	public void removeNotification(){
		manager.removeNotification();
	}

	public void refreshNotification(){
		manager.refreshView();
	}

	public void exitApplication(ActionEvent event) {
		 System.out.println("exited");
		 try {
			 Platform.exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
