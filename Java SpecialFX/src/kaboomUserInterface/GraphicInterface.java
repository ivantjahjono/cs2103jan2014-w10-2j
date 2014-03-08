package kaboomUserInterface;

import java.io.IOException;

import main.TaskMasterKaboom;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class GraphicInterface extends Application {
	
	Parent root;
	
	@Override
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(getClass().getResource("TaskMasterKaboom.fxml"));
			root = (Parent)(loader.load());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		MainWindow mainWindow = loader.getController();
		mainWindow.setStage(primaryStage);
		
		Scene scene = new Scene(root);
		//scene.setFill(Color.rgb(255, 255, 255, 0.5));
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();
	}
	
	public void run(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop() {
		TaskMasterKaboom.exitProgram();
	}
}
