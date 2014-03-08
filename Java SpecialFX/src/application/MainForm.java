package application;

import java.net.URL; 
import java.util.ResourceBundle; 

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class MainForm implements javafx.fxml.Initializable {
	
	@FXML private TextField commandTextInput;
	
	@Override	 
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	@FXML
	private void onTextfieldAction (ActionEvent e) {
		System.out.println("Hello world!");
		
		commandTextInput.setText("");
	}
	
	@FXML
	private void onTextfieldKeyPressed (KeyEvent keyEvent) {
		//System.out.println("Key pressed: " + keyEvent.getText());
	}
}