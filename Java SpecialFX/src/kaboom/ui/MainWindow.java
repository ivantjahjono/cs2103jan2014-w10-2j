package kaboom.ui;

import java.io.IOException;
import java.net.URL; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle; 
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import kaboom.logic.DisplayData;
import kaboom.logic.FormatIdentify;
import kaboom.logic.TaskInfoDisplay;
import kaboom.logic.TaskMasterKaboom;

public class MainWindow implements javafx.fxml.Initializable, Observer {	
	private final int MAX_TABS = 5;
	
	// User interface elements
			private Stage 		windowStage;
	@FXML	private AnchorPane 	mainPane;
	
	// Table ui elements and its ui columns
	@FXML 	private TableView<TaskInfoDisplay> 				taskDisplayTable;
	@FXML	private TableColumn<TaskInfoDisplay, Integer> 	columnTaskId;
	@FXML 	private TableColumn<TaskInfoDisplay, String> 	columnTaskName;
	@FXML 	private TableColumn<TaskInfoDisplay, String> 	columnStartTime;
	@FXML 	private TableColumn<TaskInfoDisplay, String> 	columnEndTime;
	@FXML 	private TableColumn<TaskInfoDisplay, String> 	columnPriority;
	
	// Data for the task table
			private ObservableList<TaskInfoDisplay> data;
	
	// Top window toolbar buttons
	@FXML 	private ImageView 	exitButton;
	@FXML 	private Label 		minimiseLabel;
	@FXML 	private Label 		counter;
	
	// Task header to show the current type of tasks displayed
	@FXML 	private Label header_all;
	@FXML 	private Label header_running;
	@FXML 	private Label header_deadline;
	@FXML 	private Label header_timed;
	@FXML 	private Label header_search;
	@FXML 	private Label header_archive;
	
	@FXML 	private Label header_all_count;
	@FXML 	private Label header_running_count;
	@FXML 	private Label header_deadline_count;
	@FXML 	private Label header_timed_count;
	@FXML 	private Label header_search_count;
	@FXML 	private Label header_archive_count;
	
	private final String HEADER_ALL_NAME 		= "header_all";
	private final String HEADER_RUNNING_NAME 	= "header_running";
	private final String HEADER_DEADLINE_NAME 	= "header_deadline";
	private final String HEADER_TIMED_NAME 		= "header_timed";
	private final String HEADER_SEARCH_NAME 	= "header_search";
	private final String HEADER_ARCHIVE_NAME 	= "header_archive";
	
	// List and tracks of previously activated headers
	private Vector<Label>	labelList;
	private int 			currentLabelIndex;
	private int 			previousLabelIndex;
	
	// Main command input and user feedback text
	@FXML private TextField 	commandTextInput;
	@FXML private Pane 			feedbackBox;
	@FXML private Label 		feedbackText;
	@FXML private HBox 			commandFormatFeedback;
	
	// Container to keep the pages tabs 
	@FXML private HBox 					pageTabContainer;
		  private ArrayList<Rectangle> 	pagesTab;
	
	// Tracks previous commands
	private String prevCommand;
	private String currentCommand;
	
	// Used in tracking window dragging
	private double initialX;
	private double initialY;
	
	// Class references
	private TaskMasterKaboom 	applicationController;
	private DisplayData 		uiData;
	
	// Logging unit and file handler for output
	private final static Logger loggerUnit = Logger.getLogger(MainWindow.class.getName());
	private static FileHandler fh;
	
	public MainWindow () {
		prevCommand = "";
		currentCommand = "";
		
		data = FXCollections.observableArrayList();
		pagesTab = new ArrayList<Rectangle>();
		
		uiData = DisplayData.getInstance();
		
		labelList = new Vector<Label>();
		
		applicationController = TaskMasterKaboom.getInstance();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) throws NullPointerException {
		createAndStartLogging();
		
		try {
			columnTaskId.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, Integer>("taskId"));
			columnTaskName.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("taskName"));
			columnStartTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("startDate"));
			columnEndTime.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("endDate"));
			columnPriority.setCellValueFactory(new PropertyValueFactory<TaskInfoDisplay, String>("importanceLevel"));
			
			taskDisplayTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		} catch (NullPointerException e) {
			return;
		}
		
		// Disable column reordering
		//disableTableColumnReordering();
		
		mainPane.getStyleClass().add("root");
		
		labelList.add(header_all);
		labelList.add(header_running);
		labelList.add(header_deadline);
		labelList.add(header_timed);
		labelList.add(header_search);
		labelList.add(header_archive);
		currentLabelIndex = 0;
		previousLabelIndex = 0;
		
		setHeaderLabelToSelected(labelList.get(currentLabelIndex));
		setTablelistToRespondToExpiry();
		
		DisplayData.getInstance().addObserver(this);
		updateDisplay();
	}

	private void createAndStartLogging() {
		try {
			fh = new FileHandler("KaboomUI.log", false);
		} catch (SecurityException e) {
			// TODO find ways to handle exceptions
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger logger = Logger.getLogger("");
		fh.setFormatter(new SimpleFormatter());
		logger.addHandler(fh);
		logger.setLevel(Level.CONFIG);
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
	                    getStyleClass().removeAll(Collections.singleton("isComplete"));
	                    getStyleClass().removeAll(Collections.singleton("isEmpty"));
	                    getStyleClass().removeAll(Collections.singleton("isRecent"));
	                    
	                    if (person == null) {
	                    	 getStyleClass().add("isEmpty");
	                    	 return;
	                    }
	                    
	                    if (person.isDone()) {
	                    	getStyleClass().add("isComplete");
	                    } else if (person.isExpired()) {
	                    	getStyleClass().add("isExpired");
	                    } else if (person.isRecent()) {
	                    	getStyleClass().add("isRecent");
	                    } else {   
	                        getStyleClass().add("isNotExpired");
	                    }
	                }
	            };
	            return row;
	        }
	    });
	}

//	private void disableTableColumnReordering() {
//		taskDisplayTable.getColumns().addListener(new ListChangeListener() {
//	        @Override
//	        public void onChanged(Change change) {
//	          change.next();
//	          if(change.wasReplaced()) {
//	        	  taskDisplayTable.getColumns().clear();
//	        	  taskDisplayTable.getColumns().addAll(columnTaskId, columnTaskName, columnStartTime, columnEndTime, columnPriority);
//	          }
//	        }
//	    });
//	}
	
	public void setStage (Stage currentStage) {
		windowStage = currentStage;
	}
	
	public void prepareTextfieldFocus () {
		commandTextInput.requestFocus();
	}
	
	@FXML
	private void onTextfieldAction (ActionEvent e) {
		String command = commandTextInput.getText();
		
		loggerUnit.log(Level.INFO, command);
		
		if (isExitCommand(command)) {
			Platform.exit();
			return;
		}
		
		// Check if need to switch header
		if (isPageToggle(command)) {
			activatePageToggle(command);
		} else {
			applicationController.processCommand(command);
		}
		
		prevCommand = command;
		currentCommand = "";
		
		commandTextInput.setText("");
	}

	private boolean isExitCommand(String command) {
		return command.equals("exit");
	}

	private void switchToNewHeader(int switchIndexResult) {
		// Is it the same label activated ?
		if (switchIndexResult == currentLabelIndex) {
			return;
		}
		
		previousLabelIndex = currentLabelIndex;
		currentLabelIndex = switchIndexResult;
		switchMainHeaderHighlight(previousLabelIndex, currentLabelIndex);
	}

	private void updateDisplay() {
		updateTaskTable();
		updateFeedbackMessage();
		updatePagesTab();
		updateHeader();
		updateHeaderTaskCount();
		
		updateCommandFormat();
	}

	private void updateHeaderTaskCount() {
		Vector<Integer> taskCountList = uiData.getTaskCountList();
		for (int i = 0; i < taskCountList.size(); i++) {
			String countString = "" + taskCountList.get(i);
			
			switch (i) {
				case 0:
					header_all_count.setText(countString);
					break;
					
				case 1:
					header_running_count.setText(countString);
					break;
					
				case 2:
					header_deadline_count.setText(countString);
					break;
					
				case 3:
					header_timed_count.setText(countString);
					break;
					
				case 4:
					header_search_count.setText(countString);
					break;
					
				case 5:
					header_archive_count.setText(countString);
					break;
			}
		}
	}

	private void updateCommandFormat() {
		Label newLabel;
		
		// TODO Auto-generated method stub
		if (commandFormatFeedback.getChildren() != null) {
			commandFormatFeedback.getChildren().clear();
		}
		
		// get the text
		String textToFormat = "";
		
		// Get list from uidata
		Vector<FormatIdentify> list = uiData.getFormatDisplay();
		
		// loop through the list
		for (int i = 0; i < list.size(); i++) {
			FormatIdentify currentInfo = list.get(i);
			
			textToFormat = currentInfo.getCommandStringFormat() + " ";	// Extra space is needed to separate each parsing info
			newLabel = new Label();
			newLabel.setText(textToFormat);
			
			switch (currentInfo.getType()) {
				case COMMAND:
					newLabel.getStyleClass().add("parseCommandTypeName");
					break;
					
				case TASKNAME:
				case VIEWTYPE:
					newLabel.getStyleClass().add("parseCommandName");
					break;
					
				case START_DATE:
				case START_TIME:
					newLabel.getStyleClass().add("parseCommandStartDate");
					break;
					
				case END_DATE:
				case END_TIME:
					newLabel.getStyleClass().add("parseCommandEndDate");
					break;
					
				case PRIORITY:
					newLabel.getStyleClass().add("parseCommandPriority");
					break;
					
				default:
					newLabel.getStyleClass().add("parseCommandInvalid");
					break;
			}
			
			commandFormatFeedback.getChildren().add(newLabel);
		}
	}

	private void updateHeader() {
		DISPLAY_STATE newDisplayState = uiData.getCurrentDisplayState();
		
		int newHeaderIndex = 0;
		switch (newDisplayState) {
			case ALL:
				newHeaderIndex = 0;
				break;
				
			case RUNNING:
				newHeaderIndex = 1;
				break;
				
			case DEADLINE:
				newHeaderIndex = 2;
				break;
				
			case TIMED:
				newHeaderIndex = 3;
				break;
				
			case SEARCH:
				newHeaderIndex = 4;
				break;
				
			case ARCHIVE:
				newHeaderIndex = 5;
				break;
				
			default:
				break;
				
		}
		
		switchToNewHeader(newHeaderIndex);
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
	private void onTextfieldKeyReleased (KeyEvent keyEvent) {
		//System.out.println("Key pressed: " + keyEvent.getText());
		boolean processResult = false;
		switch(keyEvent.getCode()) {
			case UP:
				loggerUnit.log(Level.FINE, "Recalling previous command.");
				recallPreviousCommand();
				break;
				
			case DOWN:
				loggerUnit.log(Level.FINE, "Recalling next command.");
				recallStoredTypedCommand();
				break;
				
			case ESCAPE:
				loggerUnit.log(Level.FINE, "ESC pressed for minimise.");
				windowStage.setIconified(true);
				break;
				
			case F1:
				activateHeaderBasedOnName(HEADER_ALL_NAME);
				break;
				
			case F2:
				activateHeaderBasedOnName(HEADER_RUNNING_NAME);
				break;
				
			case F3:
				activateHeaderBasedOnName(HEADER_DEADLINE_NAME);
				break;
				
			case F4:
				activateHeaderBasedOnName(HEADER_TIMED_NAME);
				break;
				
			case F5:
				activateHeaderBasedOnName(HEADER_SEARCH_NAME);
				break;
				
			case F6:
				activateHeaderBasedOnName(HEADER_ARCHIVE_NAME);
				break;	
				
			default:
				String command = commandTextInput.getText();
				if (isPageToggle(command) || isExitCommand(command)) {
					processResult = true;
				} else {
					processResult = applicationController.processSyntax(command);
				}
				
				updateCommandStatusIndicator(processResult);
				break;
		}
	}
	
	@FXML
	private void onExitButtonPressed (MouseEvent mouseEvent) {
		loggerUnit.log(Level.FINE, "Close button pressed.");
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
	
	@FXML
	private void onHeaderMouseClicked (MouseEvent mouseEvent) {
		Node nodePressed = (Node)mouseEvent.getSource();
		
		loggerUnit.log(Level.FINE, nodePressed.getId()+" header clicked.");
		// TODO need to activate the view command without creating string.
		activateHeaderBasedOnName(nodePressed.getId());
	}

	private void activateHeaderBasedOnName(String command) {
		switch (command) {
			case HEADER_ALL_NAME:
				command = "view all";
				break;
				
			case HEADER_RUNNING_NAME:
				command = "view running";
				break;
				
			case HEADER_DEADLINE_NAME:
				command = "view deadline";
				break;
				
			case HEADER_TIMED_NAME:
				command = "view timed";
				break;
				
			case HEADER_SEARCH_NAME:
				command = "view search";
				break;
				
			case HEADER_ARCHIVE_NAME:
				command = "view archive";
				break;
				
			default:
				return;
		}
		
		applicationController.processCommand(command);
	}

	private void switchMainHeaderHighlight(int prevIndex, int currIndex) {
		setHeaderLabelToNormal(labelList.get(prevIndex));
		setHeaderLabelToSelected(labelList.get(currIndex));
	}
	
	private void setHeaderLabelToNormal (Label labelToChange) {
		labelToChange.getStyleClass().removeAll(Collections.singleton("header-label-selected"));
		labelToChange.getStyleClass().add("header-label-normal");
	}
	
	private void setHeaderLabelToSelected (Label labelToChange) {
		labelToChange.getStyleClass().removeAll(Collections.singleton("header-label-normal"));
		labelToChange.getStyleClass().add("header-label-selected");
	}
	
	@FXML
	private void onMinimiseMousePressed () {
		loggerUnit.log(Level.FINE, "Minimise button pressed.");
		windowStage.setIconified(true);
	}
	
	public void updateCounter(int number) {
		counter.setText("Test Counter: "+number);
	}
	
	@FXML
	private void onPagesArrowMouseClicked (MouseEvent mouseEvent) {
		Node nodePressed = (Node)mouseEvent.getSource();
		
		loggerUnit.log(Level.FINE, nodePressed.getId()+" button pressed.");
		switch (nodePressed.getId()) {
			case "nextArrow":
				uiData.goToNextPage();
				break;
				
			case "prevArrow":
				uiData.goToPreviousPage();
				break;
		}
		
		updateDisplay();
	}

	private void updatePagesTab() {
		int maxPages = uiData.getMaxTaskDisplayPagesForCurrentView();
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

	@Override
	public void update(Observable arg0, Object arg1) {
		updateDisplay();
	}
	
	private void updateCommandStatusIndicator(boolean status) {
		String newStyleName;
		String styleToRemove;
		
		if (status) {
			newStyleName = "text-field-correct";
			styleToRemove = "text-field-wrong";
		} else {
			styleToRemove = "text-field-correct";
			newStyleName = "text-field-wrong";
		}
		
		changeStatusIndicatorStyle(styleToRemove, newStyleName);
	}
	
	private void changeStatusIndicatorStyle (String oldStyle, String newStyle) {
		commandTextInput.getStyleClass().removeAll(Collections.singleton(oldStyle));
		commandTextInput.getStyleClass().add(newStyle);
	}
}