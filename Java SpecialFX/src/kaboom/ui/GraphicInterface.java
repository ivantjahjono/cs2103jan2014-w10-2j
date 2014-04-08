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
import javafx.scene.text.Font;
import javafx.util.Duration;
import kaboom.logic.TaskMasterKaboom;


public class GraphicInterface extends Application {
	
	private final int UPDATE_INTERVAL = 30;
	private final int WINDOW_WIDTH = 700;
	private final int WINDOW_HEIGHT = 700;
	
	Parent root;
	MainWindow mainWindow;
	
	UpdateService myService;
	Timeline updateTimeline;
	
	private static TaskMasterKaboom controllerInstance;
	static int counter = 0;
	
	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("TaskMasterKaboomUiUpgrade.fxml"));
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
		primaryStage.setWidth(WINDOW_WIDTH);
		primaryStage.setHeight(WINDOW_HEIGHT);
		primaryStage.show();
		
		mainWindow.prepareTextfieldFocus();
		
		updateTimeline = setupRunningUpdate();
		initialiseAndStartUpdateService();
	}

	private void loadAllFonts() {
		String resourcesPath = "resources/";
		
		String fullFontfilePath;
		for (int i = 0; i < fontList.length; i++) {
			fullFontfilePath = resourcesPath + fontList[i];
			Font.loadFont(GraphicInterface.class.getResource(fullFontfilePath).toExternalForm(), 10);
		}
	}

	private void initialiseAndStartUpdateService() {
		updateTimeline.setCycleCount(Animation.INDEFINITE);
		updateTimeline.playFrom("end");
	}

	private Timeline setupRunningUpdate() {
		myService = new UpdateService(mainWindow);

	    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_INTERVAL), new EventHandler<ActionEvent>() {
	    	@Override
			public void handle(ActionEvent event) {
	    		counter++;
	    		controllerInstance.updateTaskList();
	    		mainWindow.updateCounter(counter);
	    	}
	    }));
		return timeline;
	}
	
	public static void run(String[] args) {
		launch(args);
	}
	
	public static void main (String[] args) {
		controllerInstance = TaskMasterKaboom.getInstance();
		controllerInstance.initialiseKaboom(); 
		run(args);
	}
}
