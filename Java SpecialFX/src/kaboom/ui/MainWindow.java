//@author A0099175N

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import kaboom.logic.TaskMasterKaboom;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.HELP_STATE;

public class MainWindow implements javafx.fxml.Initializable, Observer {	
	private final int MAX_TABS = 5;
	private final int MAX_COMMAND_KEEP = 20;
	
	// User interface elements
			private Stage 		windowStage;
	@FXML	private AnchorPane 	mainPane;
			
	@FXML 	private Label		todayWeekDay;
	@FXML 	private Label		todayDate;
	@FXML 	private Label		todayTime;
			
	// Individual tasks
	Vector<TaskUiContainer> taskUiList;
	@FXML	private VBox		taskListContainer;
	
	// Top window toolbar buttons
	@FXML 	private ImageView 	exitButton;
	@FXML 	private ImageView 	minimiseButton;
	@FXML 	private Label 		counter;
	
	// Task header to show the current type of tasks displayed
	@FXML 	private Label header_today;
	@FXML 	private Label header_future;
	@FXML 	private Label header_timeless;
	@FXML 	private Label header_expired;
	@FXML 	private Label header_archive;
	
	@FXML 	private Label header_today_count;
	@FXML 	private Label header_future_count;
	@FXML 	private Label header_timeless_count;
	@FXML 	private Label header_expired_count;
	@FXML 	private Label header_archive_count;
	
	private final String HEADER_TODAY_NAME 		= "header_today";
	private final String HEADER_FUTURE_NAME 	= "header_future";
	private final String HEADER_TIMELESS_NAME 	= "header_timeless";
	private final String HEADER_EXPIRED_NAME 	= "header_expired";
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
	@FXML private Label 				pageNumber;
	@FXML private HBox 					pageTabContainer;
		  private ArrayList<Rectangle> 	pagesTab;
		  
	// Help boxes
	@FXML private Pane 	helpPane;
	@FXML private Pane 	helpAddPane;
	@FXML private Pane 	helpDeletePane;
	@FXML private Pane 	helpModifyPane;
	@FXML private Pane 	helpCompletePane;
	@FXML private Pane 	helpViewPane;
	@FXML private Pane 	helpSearchPane;
	@FXML private Pane 	helpPagePane;
	@FXML private Pane 	helpClearPane;
		  private Pane 	activeHelpPanel;
	
	// Tracks previous commands
	private String 			currentCommand;
	private Vector<String> 	commandsEnteredList;
	private int 			currentCommandIndex;
	
	// Used in tracking window dragging
	private double initialX;
	private double initialY;
	
	// Class references
	private TaskMasterKaboom 	applicationController;
	private DisplayData 		uiData;
	
	// Logging unit and file handler for output
	private final static Logger loggerUnit = Logger.getLogger(MainWindow.class.getName());
	private static FileHandler 	fh;
	private final String LOG_FILENAME = "KaboomUI.log";
	
	public MainWindow () {
		currentCommand = "";
		
		pagesTab = new ArrayList<Rectangle>();
		
		uiData = DisplayData.getInstance();
		
		labelList = new Vector<Label>();
		
		commandsEnteredList = new Vector<String>();
		currentCommandIndex = 0;
		
		applicationController = TaskMasterKaboom.getInstance();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) throws NullPointerException {
		createAndStartLogging();
		
		taskUiList = new Vector<TaskUiContainer>();
		
		TaskUiContainer tempTaskUi;
		int maxTaskToDisplay = uiData.getMaxTasksPerPage();
		for (int i = 0; i < maxTaskToDisplay; i++) {
			tempTaskUi = new TaskUiContainer();
			taskListContainer.getChildren().add(tempTaskUi.getPaneContainer());
			taskUiList.add(tempTaskUi);
		}
		
		mainPane.getStyleClass().add("root");
		
		labelList.add(header_today);
		labelList.add(header_future);
		labelList.add(header_timeless);
		labelList.add(header_expired);
		labelList.add(header_archive);
		currentLabelIndex = 0;
		previousLabelIndex = 0;
		
		setHeaderLabelToSelected(labelList.get(currentLabelIndex));
		
		DisplayData.getInstance().addObserver(this);
		updateDisplay();
	}

	private void createAndStartLogging() {
		try {
			fh = new FileHandler(LOG_FILENAME, false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Logger logger = Logger.getLogger("");
		fh.setFormatter(new SimpleFormatter());
		logger.addHandler(fh);
		logger.setLevel(Level.CONFIG);
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
		
		loggerUnit.log(Level.INFO, command);
		
		if (isExitCommand(command)) {
			Platform.exit();
			return;
		}
		
		applicationController.processCommand(command);
		
		storeCommandEntered(command);
		
		commandTextInput.setText("");
	}

	private boolean isExitCommand(String command) {
		return command.equals("exit");
	}

	private void updateDisplay() {
		updateHeaderDateTime();
		updateTaskTable();
		updateFeedbackMessage();
		
		updateHeader();
		updateHeaderTaskCount();
		
		updatePagesTab();
		updatePageNumber();
		
		updateCommandFormat();
		
		updateHelpPanel();
	}
	
	private void updateHeaderDateTime() {
		todayWeekDay.setText(uiData.getCurrentWeekDay());
		todayDate.setText(uiData.getCurrentDate());
		todayTime.setText(uiData.getCurrentTime());
	}
	
	private void updateTaskTable() {	
		Vector<TaskInfoDisplay> taskList = uiData.getTaskDisplay();
		for (int i = 0; i < taskList.size(); i++) {
			TaskUiContainer currentTaskContainer = taskUiList.get(i);
			currentTaskContainer.updateWithTaskDisplay(taskList.get(i));
			currentTaskContainer.setVisibleFlag(true);
		}
		
		for (int i = taskList.size(); i < taskUiList.size(); i++) {
			TaskUiContainer currentTaskContainer = taskUiList.get(i);
			currentTaskContainer.setVisibleFlag(false);
		}
	}
	
	private void updateFeedbackMessage() {
		String feedback = uiData.getFeedbackMessage();
		feedbackText.setText(feedback);
	}
	
	private void updateHeader() {
		DISPLAY_STATE newDisplayState = uiData.getCurrentDisplayState();
		
		int newHeaderIndex = 0;
		switch (newDisplayState) {
			case TODAY:
				newHeaderIndex = 0;
				break;
				
			case FUTURE:
				newHeaderIndex = 1;
				break;
				
			case TIMELESS:
				newHeaderIndex = 2;
				break;
				
			case EXPIRED:
				newHeaderIndex = 3;
				break;
				
			case ARCHIVE:
				newHeaderIndex = 4;
				break;
				
			case SEARCH:
				newHeaderIndex = -1;
				break;
				
			default:
				return;
		}
		
		switchToNewHeader(newHeaderIndex);
	}
	
	private void updateHeaderTaskCount() {
		Vector<Integer> taskCountList = uiData.getTaskCountList();
		for (int i = 0; i < taskCountList.size(); i++) {
			String countString = "" + taskCountList.get(i);
			
			switch (i) {
				case 0:
					header_today_count.setText(countString);
					break;
					
				case 1:
					header_future_count.setText(countString);
					break;
					
				case 2:
					header_timeless_count.setText(countString);
					break;
					
				case 3:
					header_expired_count.setText(countString);
					break;
					
				case 4:
					header_archive_count.setText(countString);
					break;
			}
		}
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
		@SuppressWarnings("rawtypes")
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
	
	private void updatePageNumber() {
		int totalPages = uiData.getMaxTaskDisplayPagesForCurrentView();
		int currentPage = uiData.getCurrentPage()+1;
		
		String pageString = String.format("Pg %d - %d", currentPage, totalPages);
		pageNumber.setText(pageString);
	}
	
	private void updateCommandFormat() {
		Label newLabel;
		
		if (commandFormatFeedback.getChildren() != null) {
			commandFormatFeedback.getChildren().clear();
		}
		
		String textToFormat = "";
		Vector<FormatIdentify> list = uiData.getFormatDisplay();
		
		for (int i = 0; i < list.size(); i++) {
			FormatIdentify currentInfo = list.get(i);
			
			if (currentInfo.getCommandStringFormat().equals("")) {
				continue;
			}
			
			textToFormat = currentInfo.getCommandStringFormat() + " ";
			newLabel = new Label();
			newLabel.setText(textToFormat);
			
			setStyleBasedOnKeywordType(newLabel, currentInfo);
			
			commandFormatFeedback.getChildren().add(newLabel);
		}
	}

	private void setStyleBasedOnKeywordType(Label newLabel,
			FormatIdentify currentInfo) {
		switch (currentInfo.getType()) {
			case COMMAND:
				newLabel.getStyleClass().add("parseCommandTypeName");
				break;
				
			case TASKNAME:
			case TASKID:
			case VIEWTYPE:
			case MODIFIED_TASKNAME:
			case DATE:
			case CLEARTYPE:
			case HELP:
			case PAGE:
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
	}
	
	private void updateHelpPanel() {		
		HELP_STATE currentHelpState = uiData.getCurrentHelpState();
		
		if (activeHelpPanel != null) {
			activeHelpPanel.setVisible(false);
		}
		
		switch (currentHelpState) {
			case MAIN:
				activeHelpPanel = helpPane;
				break;
				
			case ADD:
				activeHelpPanel = helpAddPane;
				break;
				
			case DELETE:
				activeHelpPanel = helpDeletePane;
				break;
				
			case MODIFY:
				activeHelpPanel = helpModifyPane;
				break;
				
			case COMPLETE:
				activeHelpPanel = helpCompletePane;
				break;
				
			case SEARCH:
				activeHelpPanel = helpSearchPane;
				break;
				
			case VIEW:
				activeHelpPanel = helpViewPane;
				break;
				
			case PAGE:
				activeHelpPanel = helpPagePane;
				break;
				
			case CLEAR:
				activeHelpPanel = helpClearPane;
				break;
				
			default:
				activeHelpPanel = null;
				break;
		}
		
		if (activeHelpPanel != null) {
			activeHelpPanel.setVisible(true);
		}
	}
	
	private void storeCommandEntered(String command) {
		commandsEnteredList.add(command);
		
		while (commandsEnteredList.size() > MAX_COMMAND_KEEP) {
			commandsEnteredList.remove(0);
		}

		currentCommandIndex = commandsEnteredList.size();
	}
	
	private boolean recallPreviousCommand () {
		if (currentCommandIndex > 0) {
			currentCommandIndex--;
			commandTextInput.setText(commandsEnteredList.get(currentCommandIndex));
			
			return true;
		}
		return false;
	}
	
	private boolean recallStoredTypedCommand () {
		if (currentCommandIndex < commandsEnteredList.size()-1) {
			currentCommandIndex++;
			commandTextInput.setText(commandsEnteredList.get(currentCommandIndex));
			
			return true;
		} else if (currentCommandIndex < commandsEnteredList.size()){
			currentCommandIndex =  commandsEnteredList.size();
			commandTextInput.setText(currentCommand);
			
			return true;
		}
		
		return false;
	}
	
	@FXML
	private void onTextfieldKeyPressed (KeyEvent keyEvent) {
		switch(keyEvent.getCode()) {
			case Z:
				if (keyEvent.isControlDown()) {
					applicationController.processCommand("undo");
				}
				break;
				
			case H:
				if (keyEvent.isControlDown()) {
					if (activeHelpPanel == null) {
						applicationController.processCommand("help");
					} else {
						applicationController.processCommand("help close");
					}
				}
				break;
				
			case UP:
			case DOWN:
				keyEvent.consume();
				break;
				
			default:
				break;
		}
	}

	@FXML
	private void onTextfieldKeyReleased (KeyEvent keyEvent) {
		activateProcessBasedKeyPressed(keyEvent);
	}

	private void activateProcessBasedKeyPressed(KeyEvent keyEvent) {
		boolean processResult = false;
		switch(keyEvent.getCode()) {
			case UP:
				if (commandsEnteredList.size() < currentCommandIndex+1) {
					currentCommand = commandTextInput.getText();
				}
				
				loggerUnit.log(Level.FINE, "Recalling previous command.");
				if (recallPreviousCommand()) {
					setTextfieldCursorToLast();
				}
				break;
				
			case DOWN:
				loggerUnit.log(Level.FINE, "Recalling next command.");
				if (recallStoredTypedCommand()) {
					setTextfieldCursorToLast();
				}
				break;
				
			case ESCAPE:
				loggerUnit.log(Level.FINE, "ESC pressed for minimise.");
				windowStage.setIconified(true);
				break;
				
			case F1:
				activateHeaderBasedOnName(HEADER_TODAY_NAME);
				break;
				
			case F2:
				activateHeaderBasedOnName(HEADER_FUTURE_NAME);
				break;
				
			case F3:
				activateHeaderBasedOnName(HEADER_TIMELESS_NAME);
				break;
				
			case F4:
				activateHeaderBasedOnName(HEADER_EXPIRED_NAME);
				break;
				
			case F5:
				activateHeaderBasedOnName(HEADER_ARCHIVE_NAME);
				break;
				
			case PAGE_UP:
				uiData.goToNextPage();
				break;
				
			case PAGE_DOWN:
				uiData.goToPreviousPage();
				break;
				
			default:
				String command = commandTextInput.getText();
				if (isExitCommand(command)) {
					processResult = true;
					commandFormatFeedback.getChildren().clear();
				} else {
					processResult = applicationController.processSyntax(command);
				}
				
				updateCommandStatusIndicator(processResult);
				break;
		}
	}
	
	private void setTextfieldCursorToLast () {
		commandTextInput.positionCaret(commandTextInput.getText().length());
	}
	
	@FXML
	private void onExitButtonPressed (MouseEvent mouseEvent) {
		loggerUnit.log(Level.FINE, "Close button pressed.");
		Platform.exit();
	}
	
	@FXML
	private void onWindowMouseDrag (MouseEvent mouseEvent) {
		windowStage.setX(mouseEvent.getScreenX() - initialX);
		windowStage.setY(mouseEvent.getScreenY() - initialY);
	}
	
	@FXML
	private void onWindowMousePressed (MouseEvent mouseEvent) {
		initialX = mouseEvent.getSceneX();
		initialY = mouseEvent.getSceneY();
	}

// Header manipulation
	@FXML
	private void onHeaderMouseClicked (MouseEvent mouseEvent) {
		Node nodePressed = (Node)mouseEvent.getSource();
		
		loggerUnit.log(Level.FINE, nodePressed.getId()+" header clicked.");
		activateHeaderBasedOnName(nodePressed.getId());
	}

	private void activateHeaderBasedOnName(String command) {
		switch (command) {
			case HEADER_TODAY_NAME:
				command = "view today";
				break;
				
			case HEADER_FUTURE_NAME:
				command = "view future";
				break;
				
			case HEADER_TIMELESS_NAME:
				command = "view timeless";
				break;
				
			case HEADER_EXPIRED_NAME:
				command = "view expired";
				break;
				
			case HEADER_ARCHIVE_NAME:
				command = "view archive";
				break;
				
			default:
				return;
		}
		
		applicationController.processCommand(command);
	}
	
	private void switchToNewHeader(int switchIndexResult) {
		if (switchIndexResult == currentLabelIndex) {
			return;
		}
		
		previousLabelIndex = currentLabelIndex;
		currentLabelIndex = switchIndexResult;
		if (switchIndexResult == -1) {
			setHeaderLabelToNormal(labelList.get(previousLabelIndex));
		} else if (previousLabelIndex == -1) { 
			setHeaderLabelToSelected(labelList.get(currentLabelIndex));
		} else {
			setHeaderLabelToNormal(labelList.get(previousLabelIndex));
			setHeaderLabelToSelected(labelList.get(currentLabelIndex));
		}
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