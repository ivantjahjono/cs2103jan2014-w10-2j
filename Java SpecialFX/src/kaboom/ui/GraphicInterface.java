//@author A0099175N

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
import javafx.scene.image.Image;
import javafx.util.Duration;
import kaboom.logic.TaskMasterKaboom;


public class GraphicInterface extends Application {
	private final int UPDATE_INTERVAL = 10;
	private final int WINDOW_WIDTH  = 700;
	private final int WINDOW_HEIGHT = 700;
	
	private final String APPLICATION_NAME = "Task Master Kaboom";
	private final String ICON_FILENAME = "img/taskmasterkaboom.png";
	private final String FXML_FILENAME = "TaskMasterKaboomUiUpgrade.fxml";
	
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
			loader = new FXMLLoader(getClass().getResource(FXML_FILENAME));
			root = (Parent)(loader.load());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		mainWindow = loader.getController();
		mainWindow.setStage(primaryStage);
		
		Scene scene = new Scene(root);
		scene.setFill(null);
		setupStage(primaryStage, scene);
		
		mainWindow.prepareTextfieldFocus();

		updateApplicationIconAndTitle(primaryStage);
		
		updateTimeline = setupRunningUpdate();
		initialiseAndStartUpdateService();
	}

	private void setupStage(Stage primaryStage, Scene scene) {
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setWidth(WINDOW_WIDTH);
		primaryStage.setHeight(WINDOW_HEIGHT);
		primaryStage.show();
	}

	private void updateApplicationIconAndTitle(Stage primaryStage) {
		Image ico = new Image(this.getClass().getResourceAsStream(ICON_FILENAME));
		primaryStage.getIcons().add(ico);
		primaryStage.setTitle(APPLICATION_NAME);
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
	    		controllerInstance.updateTaskList();
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
