package kaboom.logic;

import java.util.Observable;
import java.util.Vector;

import kaboom.storage.History;
import kaboom.storage.TaskListShop;
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
	
	TaskListShop 	taskListShop;
	History 		history;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<FormatIdentify> formattingCommand;
	Vector<Integer> taskCountList;
	
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
		taskListShop = TaskListShop.getInstance();
		history = History.getInstance();
		
		tasksDataToDisplay = new Vector<TaskInfoDisplay>();
		formattingCommand = new Vector<FormatIdentify>();
		taskCountList = new Vector<Integer>();
		
		userFeedbackMessage = "";
		currentPage = 0;
		currentDisplayState = DISPLAY_STATE.ALL;
		
		updateTaskCountList ();
	}
	
	private void updateTaskCountList() {
		taskCountList.clear();
		
		// TODO Hardcoded to get each task !!!!
		for (int i = 0; i < 6; i++) {
			int currentCount = 0;
			
			switch (i) {
				case 0:
					currentCount = taskListShop.getAllCurrentTasks().size();
					break;
					
				case 1:
					currentCount = taskListShop.getFloatingTasks().size();
					break;
					
				case 2:
					currentCount = taskListShop.getDeadlineTasks().size();
					break;
					
				case 3:
					currentCount = taskListShop.getTimedTasks().size();
					break;
					
				case 4:
					currentCount = history.getTaskToView().size();
					break;
					
				case 5:
					currentCount = taskListShop.getAllArchivedTasks().size();
					break;
			
			}
			taskCountList.add(currentCount);
		}
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
		
		extractTasksBasedOnDisplayState(currentDisplayState);
		setFeedbackMessage(commandResult.getFeedback());
		
		if (commandResult.getGoToNextPage()) {
			goToNextPage();
		} else if (commandResult.getGoToPrevPage()) {
			goToPreviousPage();
		}
		
		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay)-1;
		if (currentPage > maxPages) {
			currentPage = maxPages;
		}
		
		setChanged();
		notifyObservers();
	}
	
	private void extractTasksBasedOnDisplayState(DISPLAY_STATE displayState) {
		switch (displayState) {
			case ALL:
				setTaskDisplayToThese(taskListShop.getAllCurrentTasks(), tasksDataToDisplay);
				break;
				
			case RUNNING:
				setTaskDisplayToThese(taskListShop.getFloatingTasks(), tasksDataToDisplay);
				break;
				
			case DEADLINE:
				setTaskDisplayToThese(taskListShop.getDeadlineTasks(), tasksDataToDisplay);
				break;
				
			case TIMED:
				setTaskDisplayToThese(taskListShop.getTimedTasks(), tasksDataToDisplay);
				break;
				
			case SEARCH:
				setTaskDisplayToThese(history.getTaskToView(), tasksDataToDisplay);
				break;
				
			case ARCHIVE:
				setTaskDisplayToThese(taskListShop.getAllArchivedTasks(), tasksDataToDisplay);
				break;
				
			default:
				setTaskDisplayToThese(taskListShop.getAllCurrentTasks(), tasksDataToDisplay);
				System.out.println("Encountered an invalid view!");
				break;
		}
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
}
