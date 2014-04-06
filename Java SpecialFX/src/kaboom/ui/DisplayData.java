package kaboom.ui;

import java.util.Observable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.TaskInfoDisplay;
import kaboom.storage.TaskListShop;

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

	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<FormatIdentify> 	formattingCommand;
	Vector<Integer> 		taskCountList;

	String 	userFeedbackMessage;
	int 	currentPage;
	
	String currentWeekDay;
	String currentDate;
	String currentTime;

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

		tasksDataToDisplay = new Vector<TaskInfoDisplay>();
		formattingCommand = new Vector<FormatIdentify>();
		taskCountList = new Vector<Integer>();

		userFeedbackMessage = "";
		currentPage = 0;
		currentDisplayState = DISPLAY_STATE.TODAY;
	}

	private void updateTaskCountList() {
		taskCountList.clear();

		// TODO Hardcoded to get each task !!!!
		for (int i = 0; i < 6; i++) {
			int currentCount = 0;

			switch (i) {
			case 0:
				currentCount = taskListShop.getToday().size();
				break;

			case 1:
				currentCount = taskListShop.getFloatingTasks().size();
				break;

			case 2:
				currentCount = taskListShop.getExpiredTasks().size();
				break;

			case 3:
				currentCount = TaskView.getInstance().getSearchView().size();
				break;

			case 4:
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

		// Pull data from task view class
		extractTasksBasedOnDisplayState(currentDisplayState);
		setFeedbackMessage(commandResult.getFeedback());

		// Update header counters
		updateTaskCountList ();

		if (commandResult.getGoToNextPage()) {
			goToNextPage();
		} else if (commandResult.getGoToPrevPage()) {
			goToPreviousPage();
		}

		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay)-1;
		if (currentPage > maxPages) {
			currentPage = maxPages;
		}
		
		currentWeekDay = DateAndTimeFormat.getInstance().getCurrentWeekday();
		currentDate = DateAndTimeFormat.getInstance().getDateToday();
		currentTime = DateAndTimeFormat.getInstance().getTimeNow();

		setChanged();
		notifyObservers();
	}
	
	public void updateDisplayWithResult () {
		extractTasksBasedOnDisplayState(currentDisplayState);
		updateTaskCountList ();
		
		int maxPages = getMaxTaskDisplayPages(tasksDataToDisplay)-1;
		if (currentPage > maxPages) {
			currentPage = maxPages;
		}
		
		setChanged();
		notifyObservers();
	}

	private void extractTasksBasedOnDisplayState(DISPLAY_STATE displayState) {
		setTaskDisplayToThese(TaskView.getInstance().setAndGetView(displayState), tasksDataToDisplay);
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
