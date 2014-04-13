//@author A0099863H
package kaboom.shared;

import java.util.Calendar; 

public class TaskInfo {
		
	String taskname;
	TASK_TYPE taskType;
	
	Calendar startDate;
	Calendar endDate;
	
	int priority;
	
	boolean isExpired;
	boolean isDone;
	boolean isRecent;
	
	public TaskInfo () {
		taskname = "";
		
		startDate = null;
		endDate = null;
		
		priority = 1;
		isExpired = false;
		isDone = false;
		isRecent = false;
	}
	
	public TaskInfo (TaskInfo info) {
		taskname = info.getTaskName();
		startDate = info.getStartDate();
		endDate = info.getEndDate();
		priority = info.getPriority();
		isExpired = info.getExpiryFlag();
		taskType = info.getTaskType();
	}
	
	public void setTask(TaskInfo taskInfo) {
		//TODO for undo of modify
	}
	
	public void setTaskName (String name) { 
		taskname = name;
	}
	
	public void setTaskType (TASK_TYPE type) { 
		taskType = type;
	}
	
	public void setStartDate (Calendar date) { 
		startDate = date;
	}
	
	public void setEndDate (Calendar date) { 
		endDate = date;
	}
	
	public void setPriority (int level) { 
		priority = level;
	}
	
	public void setExpiryFlag (boolean flag) { 
		isExpired = flag;
	}
	
	public void setDone (boolean flag) {
		isDone = flag;
	}
	
	public void setRecent (boolean flag) {
		isRecent = flag;
	}
	
	public String getTaskName () { 
		return taskname;
	}
	
	public TASK_TYPE getTaskType () { 
		return taskType;
	}
	
	public Calendar getStartDate () { 
		return startDate;
	}
	
	public Calendar getEndDate () { 
		return endDate;
	}
	
	public int getPriority () { 
		return priority;
	}
	
	public boolean getExpiryFlag () { 
		return isExpired;
	}
	
	public boolean getDone () {
		return isDone;
	}
	
	public boolean isEmpty () {
	
		if (taskname == "" && startDate == null && endDate == null && priority == 0) {
			return true;
		}		
		
		return false;
	}
	
	public boolean isRecent () {
		return isRecent;
	}
	
	public static TASK_TYPE getTaskType(String taskType) {
		if (taskType.equalsIgnoreCase("DEADLINE")) {
			return TASK_TYPE.DEADLINE;
		}
		else if (taskType.equalsIgnoreCase("FLOATING")) {
			return TASK_TYPE.FLOATING;
		}
		else {
			return TASK_TYPE.TIMED;
		}
	}
	
	public static String taskTypeToString(TASK_TYPE taskType) {
		switch (taskType) {
		case DEADLINE:
			return "DEADLINE";
		case FLOATING:
			return "FLOATING";
		case TIMED:
			return "TIMED";
		default:
			return "Unrecognized";  //This should never be reached
		}
	}
}
