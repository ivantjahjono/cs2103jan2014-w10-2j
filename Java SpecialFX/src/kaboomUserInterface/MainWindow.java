package kaboomUserInterface;

import main.DisplayData;
import main.TaskInfoDisplay;
import main.TaskMasterKaboom;

import java.net.URL; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle; 
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainWindow implements javafx.fxml.Initializable {
	
	private final int MAX_TABS = 5;
	private final int MIN_TABS = 1;
	
	private Stage windowStage;
	@FXML private AnchorPane mainPane;
	
	@FXML private TableView<TaskInfoDisplay> taskDisplayTable;
	@FXML private TableColumn<TaskInfoDisplay, Integer> columnTaskId;
	@FXML private TableColumn<TaskInfoDisplay, String> columnTaskName;
	@FXML private TableColumn<TaskInfoDisplay, String> columnStartTime;
	@FXML private TableColumn<TaskInfoDisplay, String> columnEndTime;
	@FXML private TableColumn<TaskInfoDisplay, String> columnPriority;
	@FXML private ImageView exitButton;
	@FXML private Label minimiseLabel;
	@FXML private Label counter;
	
	@FXML private Label header_all;
	@FXML private Label header_running;
	@FXML private Label header_deadline;
	@FXML private Label header_timed;
	@FXML private Label header_search;
	
	
	@FXML private TextField commandTextInput;
	@FXML private Pane feedbackBox;
	@FXML private Label feedbackText;
	
	@FXML private HBox pageTabContainer;
	
	ArrayList<Rectangle> pagesTab;
	
	private String prevCommand;
	private String currentCommand;
	
	private double initialX;
	private double initialY;
	
	private ObservableList<TaskInfoDisplay> data;
	
	Vector<Label> labelList;
	int currentLabelIndex;
	int previousLabelIndex;
	
	DisplayData uiData;
	
	public MainWindow () {
		prevCommand = "";
		currentCommand = "";
		
		data = FXCollections.observableArrayList();
		pagesTab = new ArrayList<Rectangle>();
		
		uiData = DisplayData.getInstance();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		columnTaskId.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, Integer>("taskId"));
		columnTaskName.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("taskName"));
		columnStartTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("startDate"));
		columnEndTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("endDate"));
		columnPriority.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("importanceLevel"));
		
		taskDisplayTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		
		// Disable column reordering
		//disableTableColumnReordering();
		
		mainPane.getStyleClass().add("root");
		
		labelList = new Vector<Label>();
		labelList.add(header_all);
		labelList.add(header_running);
		labelList.add(header_deadline);
		labelList.add(header_timed);
		labelList.add(header_search);
		currentLabelIndex = 0;
		previousLabelIndex = 0;
		
		setHeaderLabelToSelected(labelList.get(currentLabelIndex));
		setTablelistToRespondToExpiry();
		
		updateDisplay();
	}

	private void setTablelistToRespondToExpiry() {
		taskDisplayTable.setRowFactory(new Callback<TableView<TaskInfoDisplay>, TableRow<TaskInfoDisplay>>() {
	        @Override
	        public TableRow<TaskInfoDisplay> call(TableView<TaskInfoDisplay> tableView) {
	            final TableRow<TaskInfoDisplay> row = new TableRow<TaskInfoDisplay>() {
	                @Override
	                protected void updateItem(TaskInfoDisplay person, boolean empty){
	                    super.updateItem(person, empty);
	                    
	                    getStyleClass().removeAll(Collections.singleton("isNotExpired"));
	                    getStyleClass().removeAll(Collections.singleton("isExpired"));
	                    if (person != null && person.isExpired()) {
	                    //if (highlightRows.contains(getIndex())) {
	                        if (! getStyleClass().contains("isExpired")) {
	                            getStyleClass().add("isExpired");
	                        }
	                    } else {
	                        
	                        getStyleClass().add("isNotExpired");
	                    }
	                }
	            };
//	            highlightRows.addListener(new ListChangeListener<Integer>() {
//	                @Override
//	                public void onChanged(Change<? extends Integer> change) {
//	                    if (highlightRows.contains(row.getIndex())) {
//	                        if (! row.getStyleClass().contains("isExpired")) {
//	                            row.getStyleClass().add("isExpired");
//	                        }
//	                    } else {
//	                        row.getStyleClass().removeAll(Collections.singleton("isExpired"));
//	                    }
//	                }
//	            });
	            return row;
	        }
	    });
	}

	private void disableTableColumnReordering() {
		taskDisplayTable.getColumns().addListener(new ListChangeListener() {
	        @Override
	        public void onChanged(Change change) {
	          change.next();
	          if(change.wasReplaced()) {
	        	  taskDisplayTable.getColumns().clear();
	        	  taskDisplayTable.getColumns().addAll(columnTaskId, columnTaskName, columnStartTime, columnEndTime, columnPriority);
	          }
	        }
	    });
	}
	
	public void setStage (Stage currentStage) {
		windowStage = currentStage;
	}
	
	public void prepareTextfieldFocus () {
		commandTextInput.requestFocus();
	}
	
	@FXML
	private void onTextfieldAction (ActionEvent e) {
		String command = commandTextInput.getText();
		if (command.equals("exit")) {
			Platform.exit();
			return;
		}
		
		// Check if need to switch header
		int switchIndexResult = getSwitchIndexFromCommand(command);
		if (switchIndexResult != -1) {
			previousLabelIndex = currentLabelIndex;
			currentLabelIndex = switchIndexResult;
			switchMainHeaderHighlight(previousLabelIndex, currentLabelIndex);
		} if (isPageToggle(command)) {
			activatePageToggle(command);
		} else {
			TaskMasterKaboom.processCommand(command);
		}
		
		prevCommand = command;
		currentCommand = "";
		
		commandTextInput.setText("");
		
		updateDisplay();
	}

	private void updateDisplay() {
		updateTaskTable();
		updateFeedbackMessage();
		updatePagesTab();
	}

	private void activatePageToggle(String command) {
		switch (command) {
			case "next page":
				uiData.goToNextPage();
				break;
				
			case "prev page":
				uiData.goToPreviousPage();
				break;
				
			default:
				break;
		}
	}

	private boolean isPageToggle(String command) {
		switch (command) {
			case "next page":
			case "prev page":
				return true;
				
			default:
				return false;
		}
	}

	private void updateTaskTable() {
		data.clear();
		
		Vector<TaskInfoDisplay> taskList = uiData.getTaskDisplay();
		
		for (int i = 0; i < taskList.size(); i++) {
			data.add(taskList.get(i));
		}
		taskDisplayTable.setItems(data);
	}
	
	private void updateFeedbackMessage() {
		String feedback = uiData.getFeedbackMessage();
		feedbackText.setText(feedback);
	}
	
	private void recallPreviousCommand () {
		if (!prevCommand.equals(commandTextInput.getText())) {
			currentCommand = commandTextInput.getText();
			commandTextInput.setText(prevCommand);
			commandTextInput.end();
		}
	}
	
	private void recallStoredTypedCommand () {
		if (!currentCommand.equals(commandTextInput.getText())) {
			commandTextInput.setText(currentCommand);
			commandTextInput.end();
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
				
			case ESCAPE:
				windowStage.setIconified(true);
				break;
				
			default:
				break;
		}
	}
	
	@FXML
	private void onExitButtonPressed (MouseEvent mouseEvent) {
		Platform.exit();
	}
	
	@FXML
	private void onWindowMouseDrag (MouseEvent mouseEvent) {
		//System.out.printf("[%f, %f]\n", mouseEvent.getSceneX(), mouseEvent.getSceneY());
		
		windowStage.setX(mouseEvent.getScreenX() - initialX);
		windowStage.setY(mouseEvent.getScreenY() - initialY);
	}
	
	@FXML
	private void onWindowMousePressed (MouseEvent mouseEvent) {
		initialX = mouseEvent.getSceneX();
		initialY = mouseEvent.getSceneY();
	}
	
	@FXML
	private void onWindowKeyPressed (KeyEvent keyEvent) {
		//System.out.println("Key pressed: " + keyEvent.getText());
		
		if (!keyEvent.isControlDown()) {
			return;
		}
		
		previousLabelIndex = currentLabelIndex;
		switch(keyEvent.getCode()) {
			case LEFT:
				currentLabelIndex--;
				
				if (currentLabelIndex < 0) {
					currentLabelIndex = labelList.size()-1;
				}
				break;
				
			case RIGHT:
				currentLabelIndex++;
				currentLabelIndex %= labelList.size();
				break;
				
			default:
				break;
		}
		
		setHeaderLabelToNormal(labelList.get(previousLabelIndex));
		setHeaderLabelToSelected(labelList.get(currentLabelIndex));
	}
	
	private int getSwitchIndexFromCommand(String command) {
		int selected = -1;
		
		switch (command) {
			case "view all":
				selected = 0;
				break;
				
			case "view running":
				selected = 1;
				break;
				
			case "view deadline":
				selected = 2;
				break;
				
			case "view timed":
				selected = 3;
				break;
				
			case "view search":
				selected = 4;
				break;
				
			default:
				break;
		}
		
		return selected;
	}

	private void switchMainHeaderHighlight(int prevIndex, int currIndex) {
		setHeaderLabelToNormal(labelList.get(prevIndex));
		setHeaderLabelToSelected(labelList.get(currIndex));
	}
	
	private void setHeaderLabelToNormal (Label labelToChange) {
		labelToChange.getStyleClass().remove("header-label-selected");
		labelToChange.getStyleClass().add("header-label-normal");
	}
	
	private void setHeaderLabelToSelected (Label labelToChange) {
		labelToChange.getStyleClass().remove("header-label-normal");
		labelToChange.getStyleClass().add("header-label-selected");
	}
	
	@FXML
	private void onMinimiseMousePressed () {
		windowStage.setIconified(true);
	}
	
	public void updateCounter(int number) {
		counter.setText("Test Counter: "+number);
	}

	private void updatePagesTab() {
		int maxPages = uiData.getMaxTaskDisplayPages();
		int maxTabs = maxPages;
		
		if (maxTabs > MAX_TABS) {
			maxTabs = MAX_TABS;
		}
		
		resizePageTabToMaxPages(maxTabs);
		updatePageTabStyles();
		refreshPageTabContainerWithNewPageTabs();
	}
	
	private Rectangle createPageTabRectangle() {
		RectangleBuilder pageTabBuilder = RectangleBuilder.create()
			    .x(100).y(100)
			    .width(25).height(5)
			    .styleClass("pagesOn");
		
		Rectangle rect1 = pageTabBuilder.build();
		return rect1;
	}

	private void updatePageTabStyles() {
		int currentTabPage = uiData.getCurrentPage()%MAX_TABS;
		
		for (int i = 0; i < pagesTab.size(); i++) {
			
			Rectangle currentTab = pagesTab.get(i);
			currentTab.getStyleClass().removeAll(Collections.singleton("pagesOn"));
			currentTab.getStyleClass().removeAll(Collections.singleton("pagesOff"));
			
			if (i == currentTabPage) {
				pagesTab.get(i).getStyleClass().add("pagesOn");
			} else {
				pagesTab.get(i).getStyleClass().add("pagesOff");
			}
		}
	}

	private void resizePageTabToMaxPages(int maxTab) {
		if (pagesTab.size() < maxTab) {
			int tabsToCreate = maxTab - pagesTab.size();
			for (int i = 0; i < tabsToCreate; i++) {
				pagesTab.add(createPageTabRectangle());
			}
		} else if (pagesTab.size() > maxTab ) {
			int tabsToDelete = pagesTab.size() - maxTab;
			for (int i = 0; i < tabsToDelete; i++) {
				pagesTab.remove(pagesTab.size()-1);
			}
		}
	}
	
	private void refreshPageTabContainerWithNewPageTabs() {
		pageTabContainer.getChildren().clear();
		pageTabContainer.getChildren().addAll(pagesTab);
	}
}