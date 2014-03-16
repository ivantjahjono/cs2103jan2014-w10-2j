package kaboom.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import kaboom.logic.TaskMasterKaboom;


public class GraphicInterface extends Application {
	
	Parent root;
	MainWindow mainWindow;
	
	UpdateService myService;
	Timeline updateTimeline;
	
	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("TaskMasterKaboomUi.fxml"));
			root = (Parent)(loader.load());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		mainWindow = loader.getController();
		mainWindow.setStage(primaryStage);
		
		Scene scene = new Scene(root);
		scene.setFill(null);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setWidth(700);
		primaryStage.setHeight(500);
		primaryStage.show();
		
		mainWindow.prepareTextfieldFocus();
		
		updateTimeline = setupRunningUpdate();
		initialiseAndStartUpdateService();
	}

	private void initialiseAndStartUpdateService() {
		updateTimeline.setCycleCount(Animation.INDEFINITE);
		updateTimeline.playFrom("end"); // can also play from start but you will have an initial 5 second delay
	}

	private Timeline setupRunningUpdate() {
		myService = new UpdateService(mainWindow);

	    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent event) {
	    		myService.restart();   // automatically on JavaFX thread, so can call restart directly
	    		mainWindow.updateCounter(myService.counter);
	    	}
	    }));
		return timeline;
	}
	
	public static void run(String[] args) {
		launch(args);
	}
	
	public static void main (String[] args) {
		TaskMasterKaboom.getInstance().initialiseKaboom(); 
		
		run(args);
	}
}
