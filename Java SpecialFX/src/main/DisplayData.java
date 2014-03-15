package main;

import java.util.Vector;

// Purpose: DisplayData is used in conjunction with KaboomGUI. It holds all the task information and
//          feedback message that will be displayed on the GUI.
//
// Properties: Singleton
//
public class DisplayData {
	final int NUM_OF_TASK_PER_PAGE = 10;
	
	static DisplayData instance;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	String userFeedbackMessage;
	int currentPage;
	
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
	
	public Vector<TaskInfoDisplay> getAllTaskDisplayInfo () {
		return tasksDataToDisplay;
	}
	
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
	
	public void goToNextPage () {
		currentPage++;
		
		int maxPage = tasksDataToDisplay.size()/NUM_OF_TASK_PER_PAGE;
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
