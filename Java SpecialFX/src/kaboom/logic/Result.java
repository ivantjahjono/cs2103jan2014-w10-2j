//@author A0073731J

package kaboom.logic;

import java.util.Vector;

import kaboom.ui.DISPLAY_STATE;
import kaboom.ui.HELP_STATE;


public class Result {
	Vector<TaskInfo> taskToDisplayList;
	String			 feedback;
	
	TaskInfo		taskInFocus;
	
	boolean isGoToNextPage;
	boolean isGoToPreviousPage;
	
	DISPLAY_STATE 	stateToChangeTo;
	HELP_STATE		helpStateToChangeTo;
	
	public Result () {
		feedback = "";
		taskInFocus = null;
		isGoToNextPage = false;
		isGoToPreviousPage = false;
		stateToChangeTo = DISPLAY_STATE.INVALID;
		helpStateToChangeTo = HELP_STATE.INVALID;
	}
	
	public void setTasksToDisplay (Vector<TaskInfo> taskList) {
		taskToDisplayList = taskList;
	}
	
	public void setFeedback (String newFeedback) {
		feedback = newFeedback;
	}
	
	public void setTaskToFocus (TaskInfo task) {
		taskInFocus = task;
	}
	
	public void setGoToNextPage (boolean flag) {
		isGoToNextPage = flag;
	}
	
	public void setGoToPrevPage (boolean flag) {
		isGoToPreviousPage = flag;
	}
	
	public void setDisplayState (DISPLAY_STATE newState) {
		stateToChangeTo = newState;
	}
	
	public void setHelpState (HELP_STATE newState) {
		helpStateToChangeTo = newState;
	}
	
	public Vector<TaskInfo> getTasksToDisplay () {
		return taskToDisplayList;
	}
	
	public String getFeedback () {
		return feedback;
	}
	
	public TaskInfo getTaskToFocus () {
		return taskInFocus;
	}
	
	public boolean getGoToNextPage () {
		return isGoToNextPage;
	}
	
	public boolean getGoToPrevPage () {
		return isGoToPreviousPage;
	}
	
	public DISPLAY_STATE getDisplayState () {
		return stateToChangeTo;	
	}
	
	public HELP_STATE getHelpState () {
		return helpStateToChangeTo;	
	}
}
