package application;

import java.io.IOException;

import Database.DatabaseConnection;
import Factory.NotificationFactory;
import Model.Notification;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author Bartosz Gardziejewski
 * @version 1.0.0
 */
public class ApplicationManager  extends Application {
	
	private Stage currentStage;
	private ApplicationController controller;
	
	private double windowWidth = 800 ;
	private double windowHeight= 600 ;

	private Notification currentNotification;

    private NotificationFactory notificationFactory;
	private DatabaseConnection database;

    public static void main( String[] args )
    {
    	launch(args);
    }

    private void connectToDatabase(){
        database = new DatabaseConnection("mysecretpassword","postgres");
        notificationFactory = new NotificationFactory();
    }
    
    @Override
	public void start(Stage stage){

    	System.out.println("Application Start");

        connectToDatabase();


    	currentStage = stage;

		currentStage.setHeight(windowHeight);
		currentStage.setWidth(windowWidth);

    	loadMainWindow();
    	
		try {			
	    	currentStage.initStyle(StageStyle.DECORATED);
			currentStage.show();			
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
    
    @Override
    public void stop() {
    	System.out.println("Application stopped");
    }
 
    
    private void loadMainWindow(){    	
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CalendarWindow.fxml"));
			
			windowHeight = currentStage.getHeight();
	    	windowWidth = currentStage.getWidth();
	    	
			Parent root = loader.load();
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add("ApplicationStyle.css");
			scene.setFill(Color.TRANSPARENT);
			
			controller = loader.getController();
			controller.setManager(this);

			currentStage.setScene(scene);
			currentStage.setHeight(windowHeight);
			currentStage.setWidth(windowWidth);

            refreshView();

		}catch(Exception e) {
			e.printStackTrace();
		}	
    	
    }

	public void refreshView(){
    	controller.eventsBox.getChildren().clear();


		for (Notification notification: notificationFactory.getNotifications()) {
			controller.eventsBox.getChildren().add(new NotificationLabel(notification,this));
		}

	}

 	public void refreshList(){
		//controller.refreshNotification();
	}

    
    public void loadNotificationDetailWindow(Notification notification) {

    	if( notification == null ) {
    		
    		controller.ElementsAnchor.setCenter(new Label("Brak wybranego powiadomienia"));
    		
    		return;
    	}
    	
    	currentNotification = notification;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EventDetal.fxml"));

		Parent root = null;
		
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		ApplicationController eventController = loader.getController();
		
		controller.ElementsAnchor.setCenter(root);
		
		eventController.Title.setText( notification.getTopic() );
		eventController.Content.setText( notification.getMessage() );
    }
    
    public void removeNotification() {
    	//notifications.removeNotification(currentNotification);
		refreshView();
		loadNotificationDetailWindow(null);
    }
    
}


class NotificationLabel extends StackPane{

	private Notification notification;

	private HBox content;
	
	private Button eventDetale;
	
	private Label title;
	private Label date;
	
	
	
	{
		content = new HBox();
		eventDetale = new Button();

		eventDetale.setPrefWidth(200);
		eventDetale.setPrefHeight(20);		
		
		eventDetale.setStyle("-fx-background-color:transparent;");
		
		
		this.getStyleClass().add("button-event");
		
		this.getChildren().addAll(eventDetale,content);
		
		
		content.setPadding(new Insets(8.0, 16.0, 8.0, 16.0));
		content.setSpacing(8.0);
		content.setMouseTransparent(true);
	}
	
	
	NotificationLabel(Notification notification, ApplicationManager manager){
		this.notification = notification;
		this.title = new Label(notification.getTopic());
		this.date = new Label(notification.getTime().toString());
		
		content.getChildren().addAll(this.title,this.date);


		final Notification notif = notification;
		final ApplicationManager m = manager;
		
		
		eventDetale.setOnAction(new EventHandler<ActionEvent>() {
			
			public void handle(ActionEvent event) {
				m.loadNotificationDetailWindow(notif);
			}
		  
		});
		
	}
}
