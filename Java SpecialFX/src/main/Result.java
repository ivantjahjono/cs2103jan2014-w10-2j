package main;

import java.util.Vector;


// TODO
// 1) Have to include information to somehow inform GUI to go to next or previous or first page or last page
//    of task display.
// 2) If the taskToDisplayList is null, does it mean that it has nothing to show or remain to stick
//    to what it is showing now ?

public class Result {
	Vector<TaskInfo> taskToDisplayList;
	String feedback;
	
	boolean isGoToNextPage;
	boolean isGoToPreviousPage;
	
	public Result () {
		feedback = "";
		isGoToNextPage = false;
		isGoToPreviousPage = false;
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
}
