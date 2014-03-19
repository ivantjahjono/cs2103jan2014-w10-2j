package kaboomUserInterface;

import main.TaskMasterKaboom;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class GraphicInterface extends Application {
	
	private final String FXML_LOAD_ERROR_MESSAGE = "Error in loading application fxml file.";
	
	Parent root;
	
	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("TaskMasterKaboom.fxml"));
			root = (Parent)(loader.load());
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(FXML_LOAD_ERROR_MESSAGE);
		}
		
		MainWindow mainWindow = loader.getController();
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
	}
	
	public void run(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
		TaskMasterKaboom.exitProgram();
	}
}
