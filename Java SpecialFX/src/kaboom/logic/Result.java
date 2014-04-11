//@author A0073731J

package kaboom.logic;

import java.util.Vector;

import kaboom.ui.DISPLAY_STATE;


public class Result {
	Vector<TaskInfo> taskToDisplayList;
	String			 feedback;
	
	boolean isGoToNextPage;
	boolean isGoToPreviousPage;
	
	DISPLAY_STATE stateToChangeTo;
	
	public Result () {
		feedback = "";
		isGoToNextPage = false;
		isGoToPreviousPage = false;
		stateToChangeTo = DISPLAY_STATE.INVALID;
	}
	
	public void setTasksToDisplay (Vector<TaskInfo> taskList) {
		taskToDisplayList = taskList;
	}
	
	public Vector<TaskInfo> getTasksToDisplay () {
		return taskToDisplayList;
	}
	
	public void setFeedback (String newFeedback) {
		feedback = newFeedback;
	}
	
	public void setDisplayState (DISPLAY_STATE newState) {
		stateToChangeTo = newState;
	}
	
	public String getFeedback () {
		return feedback;
	}
	
	public void setGoToNextPage (boolean flag) {
		isGoToNextPage = flag;
	}
	
	public boolean getGoToNextPage () {
		return isGoToNextPage;
	}
	
	public void setGoToPrevPage (boolean flag) {
		isGoToPreviousPage = flag;
	}
	
	public boolean getGoToPrevPage () {
		return isGoToPreviousPage;
	}
	
	public DISPLAY_STATE getDisplayState () {
		return stateToChangeTo;	
	}
}
