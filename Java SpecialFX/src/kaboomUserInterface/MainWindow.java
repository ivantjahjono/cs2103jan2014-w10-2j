package kaboomUserInterface;

import main.DisplayData;
import main.TaskInfoDisplay;
import main.TaskMasterKaboom;

import java.net.URL; 
import java.util.ResourceBundle; 
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class MainWindow implements javafx.fxml.Initializable {
	
	@FXML private TableView<TaskInfoDisplay> taskDisplayTable;
	@FXML private TableColumn<TaskInfoDisplay, Integer> columnTaskId;
	@FXML private TableColumn<TaskInfoDisplay, String> columnTaskName;
	@FXML private TableColumn<TaskInfoDisplay, String> columnStartTime;
	@FXML private TableColumn<TaskInfoDisplay, String> columnEndTime;
	@FXML private TableColumn<TaskInfoDisplay, String> columnPriority;
	
	@FXML private TextField commandTextInput;
	@FXML private Pane feedbackBox;
	@FXML private Label feedbackText;
	
	private String prevCommand;
	private String currentCommand;
	
	private ObservableList<TaskInfoDisplay> data = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		columnTaskId.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, Integer>("taskId"));
		columnTaskName.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("taskName"));
		columnStartTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("startDate"));
		columnEndTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("endDate"));
		columnPriority.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("importanceLevel"));
		
		taskDisplayTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		updateTaskTable();
		updateFeedbackMessage();
	}
	
	@FXML
	private void onTextfieldAction (ActionEvent e) {
		String command = commandTextInput.getText();
		String feedback = TaskMasterKaboom.processCommand(command);
		
		prevCommand = command;
		currentCommand = "";
		
		commandTextInput.setText("");
		
		// Update the table
		updateTaskTable();
		updateFeedbackMessage();
	}

	private void updateTaskTable() {
		data.clear();
		
		Vector<TaskInfoDisplay> taskList = DisplayData.getInstance().getAllTaskDisplayInfo();
		
		for (int i = 0; i < taskList.size(); i++) {
			data.add(taskList.get(i));
		}
		taskDisplayTable.setItems(data);
	}
	
	private void updateFeedbackMessage() {
		String feedback = DisplayData.getInstance().getFeedbackMessage();
		feedbackText.setText(feedback);
	}
	
	private void recallPreviousCommand () {
		if (!prevCommand.equals(commandTextInput.getText())) {
			currentCommand = commandTextInput.getText();
			commandTextInput.setText(prevCommand);
		}
	}
	
	private void recallStoredTypedCommand () {
		if (!currentCommand.equals(commandTextInput.getText())) {
			commandTextInput.setText(currentCommand);
		}
	}

	@FXML
	private void onTextfieldKeyPressed (KeyEvent keyEvent) {
		//System.out.println("Key pressed: " + keyEvent.getText());
		
		switch(keyEvent.getCode()) {
			case UP:
				recallPreviousCommand();
				break;
				
			case DOWN:
				recallStoredTypedCommand();
				break;
				
			default:
				break;
		}
	}
}