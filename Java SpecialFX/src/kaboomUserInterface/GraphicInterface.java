package kaboomUserInterface;

import main.TaskMasterKaboom;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;


public class GraphicInterface extends Application {
	
	Parent root;
	MainWindow mainWindow;
	
	public static class MyService extends Service<Void> {
		
		MainWindow mainWindow;
		int counter;
		
		MyService (MainWindow window) {
			mainWindow = window;
			counter = 0;
		}
		
	    @Override
	    protected Task<Void> createTask() {
	      return new Task<Void>() {
	        @Override
	        protected Void call() throws Exception {
	        	//System.out.println("Begin task");
	        	++counter;
	        	return null;
	        }
	      };
	    }
	  }
	
	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("TaskMasterKaboom.fxml"));
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
		
		final MyService myService = new MyService(mainWindow);

	    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent event) {
	    		myService.restart();   // automatically on JavaFX thread, so can call restart directly
	    		mainWindow.updateCounter(myService.counter);
	    	}
	    }));

	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.playFrom("end"); // can also play from start but you will have an initial 5 second delay
	}
	
	public void run(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
		TaskMasterKaboom.exitProgram();
	}
}
