import java.util.Vector;

// Purpose: DisplayData is used in conjunction with KaboomGUI. It holds all the task information and
//          feedback message that will be displayed on the GUI.
//
// Properties: Singleton
//
public class DisplayData {
	final int MAX_TASK_DISPLAY_COUNT = 10;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	Vector<TaskInfoDisplay> taskSearchResult;
	
	String userFeedbackMessage;
	
	static DisplayData instance;
	
	public static DisplayData getInstance () {
		if (instance == null) {
			instance = new DisplayData();
		}
		return instance;
	}
	
	private DisplayData () {
		tasksDataToDisplay = new Vector<TaskInfoDisplay>();
		taskSearchResult = new Vector<TaskInfoDisplay>();
		userFeedbackMessage = "Hello World!";
	}
	
	public Vector<TaskInfoDisplay> getAllTaskDisplayInfo () {
		return tasksDataToDisplay;
	}
	
	public void setTaskDataToDisplay (Vector<TaskInfo> taskList) {
		tasksDataToDisplay.clear();
		convertTasksIntoDisplayData(taskList, tasksDataToDisplay);
	}
	
	public Vector<TaskInfoDisplay> getAllSearchResult () {
		return taskSearchResult;
	}
	
	public void setTaskSearchResult (Vector<TaskInfo> taskList) {
		taskSearchResult.clear();
		convertTasksIntoDisplayData(taskList, taskSearchResult);
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
}
