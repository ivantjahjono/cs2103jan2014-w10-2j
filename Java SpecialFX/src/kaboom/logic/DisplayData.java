package kaboom.logic;

import java.util.Observable;
import java.util.Vector;

import kaboom.ui.DISPLAY_STATE;

/**
 * Returns a vector of TaskInfoDisplay which contains all the 
 * tasks that is displayed.
 * <p>
 * This Singleton class contains all the information that User Interface
 * needs to display. It holds all the tasks, command feedback,
 * current page the UI is on.
 */
public class DisplayData extends Observable {
	// TODO clean up methods
	
	final int NUM_OF_TASK_PER_PAGE = 10;
	
	static DisplayData instance;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<TaskInfoDisplay> searchResultDataToDisplay;
	
	String 	userFeedbackMessage;
	int 	currentPage;
	
	DISPLAY_STATE currentDisplayState;
	
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
		searchResultDataToDisplay = new Vector<TaskInfoDisplay>();
		userFeedbackMessage = "";
		currentPage = 0;
		currentDisplayState = DISPLAY_STATE.ALL;
	}
	
	/**
	 * Updates the information with the Result object that is
	 * passed by parameter. 
	 * 
	 *  @param commandResult information of the command that is executed
	 */
	public void updateDisplayWithResult (Result commandResult) {
		// TODO Hardcoded way of forcing to show to default if there is no tasks to display
		
		// Update display state
		DISPLAY_STATE stateChange = commandResult.getDisplayState();
		if (stateChange != null) {
			currentDisplayState = stateChange; 
		}
		
		if (commandResult.getTasksToDisplay() != null) {
			if (stateChange == DISPLAY_STATE.SEARCH) {
				setTaskDisplayToThese(commandResult.getTasksToDisplay(), searchResultDataToDisplay);
			} else {
				setTaskDisplayToThese(commandResult.getTasksToDisplay(), tasksDataToDisplay);
			}
		}
		
		setFeedbackMessage(commandResult.getFeedback());
		
		if (commandResult.getGoToNextPage()) {
			goToNextPage();
		} else if (commandResult.getGoToPrevPage()) {
			goToPreviousPage();
		}
		
		Vector<TaskInfoDisplay> currentTaskList = getCurrentTaskListBasedOnView();
		int maxPages = getMaxTaskDisplayPages(currentTaskList)-1;
		if (currentPage > maxPages) {
			currentPage = maxPages;
		}
		
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Returns a vector of TaskInfoDisplay which contains all the 
	 * tasks that is displayed.
	 * <p>
	 * This method always return DisplayData. The instance will be
	 * created when it is first called. Subsequent calls will return
	 * the first created instance.
	 *
	 */
	public Vector<TaskInfoDisplay> getAllTaskDisplayInfo () {
		return tasksDataToDisplay;
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
		
		Vector<TaskInfoDisplay> taskListToReturn = getCurrentTaskListBasedOnView();
		for (int i = startTaskIndex; i < endTaskIndex; i++) {
			selectedTaskToDisplay.add(taskListToReturn.get(i));
		}
		
		return selectedTaskToDisplay;
	}

	private int getLastIndexOfCurrentPage(int startPage) {
		int maxCurrentPage = (currentPage+1)*NUM_OF_TASK_PER_PAGE;
		
		Vector<TaskInfoDisplay> currentTaskList = getCurrentTaskListBasedOnView();
		if (maxCurrentPage > currentTaskList.size()) {
			return currentTaskList.size();
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
			TaskInfoDisplay infoToDisplay = convertTaskInfoIntoTaskInfoDisplay(currentTaskInfo, i%NUM_OF_TASK_PER_PAGE+1);
			
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
		Vector<TaskInfoDisplay> currentTaskList = getCurrentTaskListBasedOnView();
		int maxPages = getMaxTaskDisplayPages(currentTaskList)-1;
		
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
	
	private Vector<TaskInfoDisplay> getCurrentTaskListBasedOnView () {
		if (currentDisplayState == DISPLAY_STATE.SEARCH) {
			return searchResultDataToDisplay;
		} else {
			return tasksDataToDisplay;
		}
	}
	
	public void goToNextPage () {
		currentPage++;
		
		int maxPage = getMaxTaskDisplayPagesForCurrentView();
		if (currentPage > maxPage) {
			currentPage = maxPage;
		}
		
		setChanged();
		notifyObservers();
	}
	
	public void goToPreviousPage () {
		if (currentPage > 0) {
			currentPage--;
		}
		
		setChanged();
		notifyObservers();
	}
	
	public DISPLAY_STATE getCurrentDisplayState() {
		return currentDisplayState;
	}
}
