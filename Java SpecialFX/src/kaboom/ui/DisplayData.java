//@author A0099175N

package kaboom.ui;

import java.util.Observable;
import java.util.Vector;

import kaboom.logic.TaskMasterKaboom;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.FormatIdentify;
import kaboom.shared.HELP_STATE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

/**
 * This Singleton class contains all the information that User Interface
 * needs to display. It holds all the tasks, command feedback,
 * current page the UI is on.
 */
public class DisplayData extends Observable {
	final int NUM_OF_TASK_PER_PAGE = 10;

	static DisplayData instance;

	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<FormatIdentify> 	formattingCommand;
	Vector<Integer> 		taskCountList;

	String 	userFeedbackMessage;
	int 	currentPage;
	
	String currentWeekDay;
	String currentDate;
	String currentTime;

	DISPLAY_STATE 	currentDisplayState;
	HELP_STATE 		currentHelpState;
	
	/**
	 * Returns a DisplayData instance of the class.
	 * <p>
	 * This method always return DisplayData. The instance will be
	 * created when it is first called. Subsequent calls will return
	 * the first created instance.
	 *
	 */
	public static DisplayData getInstance () {
		if (instance == null) {
			instance = new DisplayData();
		}
		return instance;
	}

	private DisplayData () {
		tasksDataToDisplay = new Vector<TaskInfoDisplay>();
		formattingCommand = new Vector<FormatIdentify>();
		taskCountList = new Vector<Integer>();

		userFeedbackMessage = "";
		currentPage = 0;
		currentDisplayState = DISPLAY_STATE.TODAY;
		currentHelpState =  HELP_STATE.CLOSE;
	}

	private void updateTaskCountList() {
		taskCountList = TaskMasterKaboom.getInstance().updateTaskCount();
	}

	/**
	 * Updates the information with the Result object that is
	 * passed by parameter. 
	 * 
	 *  @param commandResult information of the command that is executed
	 */
	public void updateDisplayWithResult (Result commandResult) {
		updateDisplayStateBasedOnResult(commandResult);
		updateHelpPanelStateBasedOnResult(commandResult);

		extractTasksBasedOnDisplayState(currentDisplayState);
		setFeedbackMessage(commandResult.getFeedback());

		updateTaskCountList();
		updateTaskToFocus(commandResult);
		updatePageSwitching(commandResult);

		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay)-1;
		if (isMoreThanMaxPage(maxPages)) {
			currentPage = maxPages;
		}
		
		updateCurrentDateAndTime();

		setChanged();
		notifyObservers();
	}

	private void updatePageSwitching(Result commandResult) {
		int pageToGoTo = commandResult.getPageToGoTo();
		if (pageToGoTo != -1) {
			currentPage = pageToGoTo-1;
		}
		
		if (commandResult.getGoToNextPage()) {
			goToNextPage();
		} else if (commandResult.getGoToPrevPage()) {
			goToPreviousPage();
		}
	}

	private void updateTaskToFocus(Result commandResult) {
		TaskInfo taskToFocus = commandResult.getTaskToFocus();
		int indexToGo = -1;
		if (taskToFocus != null) {
			indexToGo = TaskMasterKaboom.getInstance().indexToGoTo(taskToFocus);
		}
		
		if (indexToGo != -1) {
			currentPage = indexToGo/NUM_OF_TASK_PER_PAGE;
		}
	}

	private void updateCurrentDateAndTime() {
		currentWeekDay = DateAndTimeFormat.getInstance().getCurrentWeekday().toUpperCase();
		currentDate = DateAndTimeFormat.getInstance().getDateToday();
		currentTime = DateAndTimeFormat.getInstance().getTimeNow();
	}

	private void updateHelpPanelStateBasedOnResult(Result commandResult) {
		HELP_STATE helpStateChange = commandResult.getHelpState();
		if (helpStateChange != HELP_STATE.INVALID) {
			if (currentHelpState == helpStateChange) {
				currentHelpState = HELP_STATE.CLOSE;
			} else {
				currentHelpState = helpStateChange; 
			}
		}
	}

	private void updateDisplayStateBasedOnResult(Result commandResult) {
		DISPLAY_STATE stateChange = commandResult.getDisplayState();
		if (stateChange != DISPLAY_STATE.INVALID) {
			currentDisplayState = stateChange; 
		}
	}
	
	public void updateDisplayWithResult () {
		extractTasksBasedOnDisplayState(currentDisplayState);
		updateTaskCountList ();
		
		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay)-1;
		if (isMoreThanMaxPage(maxPages)) {
			currentPage = maxPages;
		}
		
		setChanged();
		notifyObservers();
	}

	private void extractTasksBasedOnDisplayState(DISPLAY_STATE displayState) {
		setTaskDisplayToThese(TaskMasterKaboom.getInstance().setAndGetView(displayState), tasksDataToDisplay);
	}

	/**
	 * Returns a vector of TaskInfoDisplay which contains all the 
	 * tasks that is displayed. The size of vector is limited
	 * by the maximum page.
	 * <p>
	 * This method will get all the tasks on the current page and
	 * will be truncated to max task per page. 
	 *
	 */
	public Vector<TaskInfoDisplay> getTaskDisplay () {
		Vector<TaskInfoDisplay> selectedTaskToDisplay = new Vector<TaskInfoDisplay>();
		int startTaskIndex = currentPage*NUM_OF_TASK_PER_PAGE;
		int endTaskIndex = getLastIndexOfCurrentPage(currentPage);

		for (int i = startTaskIndex; i < endTaskIndex; i++) {
			selectedTaskToDisplay.add(tasksDataToDisplay.get(i));
		}

		return selectedTaskToDisplay;
	}

	private int getLastIndexOfCurrentPage(int startPage) {
		int maxCurrentPage = (currentPage+1)*NUM_OF_TASK_PER_PAGE;

		if (maxCurrentPage > tasksDataToDisplay.size()) {
			return tasksDataToDisplay.size();
		}

		return maxCurrentPage;
	}

	public void setTaskDisplayToThese (Vector<TaskInfo> taskList, Vector<TaskInfoDisplay> taskListToStoreIn) {
		taskListToStoreIn.clear();
		convertTasksIntoDisplayData(taskList, taskListToStoreIn);
	}

	private void convertTasksIntoDisplayData(Vector<TaskInfo> taskList, Vector<TaskInfoDisplay> taskListToAddinto) {
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo currentTaskInfo = taskList.get(i);
			TaskInfoDisplay infoToDisplay = convertTaskInfoIntoTaskInfoDisplay(currentTaskInfo, i+1);

			taskListToAddinto.add(infoToDisplay);
		}
	}

	private TaskInfoDisplay convertTaskInfoIntoTaskInfoDisplay(TaskInfo taskInfoToConvert, int taskId) {
		TaskInfoDisplay infoToDisplay = new TaskInfoDisplay();

		infoToDisplay.updateFromThisInfo(taskInfoToConvert);
		infoToDisplay.setTaskId(taskId);
		return infoToDisplay;
	}

	public String getFeedbackMessage () {
		return userFeedbackMessage;
	}

	public void setFeedbackMessage (String message) {
		userFeedbackMessage = message;
	}

	public int getMaxTaskDisplayPagesForCurrentView () {
		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay);

		return maxPages;
	}

	private int getMaxTaskDisplayPages (Vector<TaskInfoDisplay> taskUnderConcern) {
		if (taskUnderConcern.size() == 0) {
			return 1;
		} else {
			return ((taskUnderConcern.size()-1)/NUM_OF_TASK_PER_PAGE)+1;
		}
	}

	public int getCurrentPage () {
		return currentPage;
	}

	public void goToNextPage () {
		currentPage++;

		int maxPage = getMaxTaskDisplayPagesForCurrentView()-1;
		if (isMoreThanMaxPage(maxPage)) {
			currentPage = maxPage;
		}

		setChanged();
		notifyObservers();
	}

	private boolean isMoreThanMaxPage(int maxPage) {
		return currentPage > maxPage;
	}

	public void goToPreviousPage () {
		if (currentPage > 0) {
			currentPage--;
		}

		setChanged();
		notifyObservers();
	}
	
	public boolean goToPage (int pageNumber) {
		if (pageNumber > getMaxTaskDisplayPagesForCurrentView()) {
			return false;
		}
		currentPage = pageNumber;
		return true;
	}

	public DISPLAY_STATE getCurrentDisplayState() {
		return currentDisplayState;
	}
	
	public HELP_STATE getCurrentHelpState() {
		return currentHelpState;
	}

	public void setFormatDisplay (Vector<FormatIdentify> formatList) {
		formattingCommand = formatList;

		setChanged();
		notifyObservers();
	}

	public Vector<FormatIdentify> getFormatDisplay () {
		return formattingCommand;
	}

	public Vector<Integer> getTaskCountList () {
		return taskCountList;
	}
	
	public String getCurrentWeekDay () {
		return currentWeekDay;
	}
	
	public String getCurrentTime () {
		return currentTime;
	}
	
	public String getCurrentDate () {
		return currentDate;
	}
	
	public int getMaxTasksPerPage () {
		return NUM_OF_TASK_PER_PAGE;
	}
}
