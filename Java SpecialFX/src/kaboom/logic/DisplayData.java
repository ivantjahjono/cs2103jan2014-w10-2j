package kaboom.logic;

import java.util.Vector;

/**
 * Returns a vector of TaskInfoDisplay which contains all the 
 * tasks that is displayed.
 * <p>
 * This Singleton class contains all the information that User Interface
 * needs to display. It holds all the tasks, command feedback,
 * current page the UI is on.
 */
public class DisplayData {
	final int NUM_OF_TASK_PER_PAGE = 10;
	
	static DisplayData instance;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<TaskInfoDisplay> searchResultDataToDisplay;
	
	String 	userFeedbackMessage;
	int 	currentPage;
	
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
		userFeedbackMessage = "";
		currentPage = 0;
	}
	
	/**
	 * Updates the information with the Result object that is
	 * passed by parameter. 
	 * 
	 *  @param commandResult information of the command that is executed
	 */
	public void updateDisplayWithResult (Result commandResult) {
		// TODO Hardcoded way of forcing to show to default if there is no tasks to display
		if (commandResult.getTasksToDisplay() != null) {
			setTaskDisplayToThese(commandResult.getTasksToDisplay());
		}
		setFeedbackMessage(commandResult.getFeedback());
		
		if (commandResult.getGoToNextPage()) {
			goToNextPage();
		} else if (commandResult.getGoToPrevPage()) {
			goToPreviousPage();
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
		int endTaskIndex = getLastIndexOfPage(currentPage);
		
		for (int i = startTaskIndex; i < endTaskIndex; i++) {
			selectedTaskToDisplay.add(tasksDataToDisplay.get(i));
		}
		
		return selectedTaskToDisplay;
	}

	private int getLastIndexOfPage(int startPage) {
		int maxCurrentPage = (currentPage+1)*NUM_OF_TASK_PER_PAGE;
		
		if (maxCurrentPage > tasksDataToDisplay.size()) {
			return tasksDataToDisplay.size();
		}
		
		return maxCurrentPage;
	}
	
	public void setTaskDisplayToThese (Vector<TaskInfo> taskList) {
		tasksDataToDisplay.clear();
		convertTasksIntoDisplayData(taskList, tasksDataToDisplay);
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
	
	public int getMaxTaskDisplayPages () {
		if (tasksDataToDisplay.size() == 0) {
			return 1;
		} else {
			return ((tasksDataToDisplay.size()-1)/NUM_OF_TASK_PER_PAGE)+1;
		}
	}
	
	public int getCurrentPage () {
		return currentPage;
	}
	
	public void goToNextPage () {
		currentPage++;
		
		int maxPage = getMaxTaskDisplayPages()-1;
		if (currentPage > maxPage) {
			currentPage = maxPage;
		}
	}
	
	public void goToPreviousPage () {
		if (currentPage > 0) {
			currentPage--;
		}
	}
}
