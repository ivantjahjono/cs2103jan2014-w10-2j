package kaboomUserInterface;

import main.TaskMasterKaboom;

import java.net.URL; 
import java.util.ResourceBundle; 

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class MainWindow implements javafx.fxml.Initializable {
	
	@FXML private TextField commandTextInput;
	@FXML private Pane feedbackBox;
	@FXML private Label feedbackText;
	
	@Override	 
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML
	private void onTextfieldAction (ActionEvent e) {
		System.out.println("Hello world!");
		
		String command = commandTextInput.getText();
		String feedback = TaskMasterKaboom.processCommand(command);
		
		commandTextInput.setText("");
		feedbackText.setText(feedback);
		//resetCommandTextfield();
		//updateFeedbackTextfield(feedback);
	}
	
	@FXML
	private void onTextfieldKeyPressed (KeyEvent keyEvent) {
		//System.out.println("Key pressed: " + keyEvent.getText());
	}
}