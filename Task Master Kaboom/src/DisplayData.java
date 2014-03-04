import java.util.Vector;

// Purpose: DisplayData is used in conjunction with KaboomGUI. It holds all the task information and
//          feedback message that will be displayed on the GUI.
//
// Properties: Singleton
//
public class DisplayData {
	final int MAX_TASK_DISPLAY_COUNT = 10;
	
	Vector<TaskInfoDisplay> tasksDataToDisplay;
	String userFeedbackMessage;
	
	static DisplayData instance;
	
	public DisplayData getInstance () {
		if (instance == null) {
			instance = new DisplayData();
		}
		return instance;
	}
	
	private DisplayData () {
		tasksDataToDisplay = new Vector<TaskInfoDisplay>();
		userFeedbackMessage = "Hello World!";
	}
	
	public Vector<TaskInfoDisplay> getAllTaskDisplayInfo () {
		return tasksDataToDisplay;
	}
	
	public void setTaskDataToDisplay (Vector<TaskInfo> taskList) {
		tasksDataToDisplay.clear();
		convertTasksIntoDisplayData(taskList);
	}

	private void convertTasksIntoDisplayData(Vector<TaskInfo> taskList) {
		for (int i = 0; i < taskList.size(); i++) {
			TaskInfo currentTaskInfo = taskList.get(i);
			
			TaskInfoDisplay infoToDisplay = new TaskInfoDisplay();
			infoToDisplay.updateFromThisInfo(currentTaskInfo);
			infoToDisplay.setTaskId(i+1);
			
			tasksDataToDisplay.add(infoToDisplay);
		}
	}
	
	public String getFeedbackMessage () {
		return userFeedbackMessage;
	}
	
	public void setFeedbackMessage (String message) {
		userFeedbackMessage = message;
	}
}
