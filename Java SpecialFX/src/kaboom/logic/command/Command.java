package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.DisplayData;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;

/* 
 ** Purpose: 
 */

public class Command {

	private static final String MESSAGE_COMMAND_INVALID = "Invalid command!";

	protected COMMAND_TYPE commandType;
	protected TaskInfo taskInfo;
	protected TaskListShop taskListShop;
	protected DisplayData displayData;
	protected Vector<KEYWORD_TYPE> keywordList;  //Initialized in the individual command constructor

	public Command () {
		commandType = COMMAND_TYPE.INVALID;
		taskInfo = null;
		taskListShop = TaskListShop.getInstance();
		displayData = DisplayData.getInstance();
		keywordList = new Vector<KEYWORD_TYPE>();
	}

	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}

	public void setTaskInfo (TaskInfo info) {
		taskInfo = info;
	}

	public COMMAND_TYPE getCommandType () {
		return commandType;
	}

	public TaskInfo getTaskInfo () {
		return taskInfo;
	}

	public Result execute() {
		return createResult(null, MESSAGE_COMMAND_INVALID);
	}

	protected Result createResult (Vector<TaskInfo> taskToBeDisplayed, String feedback) {
		Result commandResult = new Result();
		commandResult.setTasksToDisplay(taskToBeDisplayed);
		commandResult.setFeedback(feedback);

		return commandResult;
	}

	public boolean undo () {
		return false;
	}
	
	public Vector<KEYWORD_TYPE> getKeywordList () {
		return keywordList;
	}

	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
	}
	
	//This function takes in the hash table that is returned from the controller
	//extracts from the hash table and stores the information in the taskInfo variable
	public void extractAndStoreTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		storeTaskInfo(infoHashes);
	}
	

	
	protected String saveTaskName(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String taskName = infoHashes.get(KEYWORD_TYPE.TASKNAME);
		task.setTaskName(taskName);
		return taskName;
	}
	
	protected String saveTaskPriority(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String taskPriority = infoHashes.get(KEYWORD_TYPE.PRIORITY);
		if(taskPriority != null) {
			task.setImportanceLevel(Integer.parseInt(taskPriority));
		}
		return taskPriority;
	}
	
	protected void saveTaskDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		saveTaskStartDateAndTime(infoHashes, task);
		saveTaskEndDateAndTime(infoHashes, task);
		determineAndSetTaskType(task);
	}
	
	protected void saveTaskStartDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String startDate = infoHashes.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoHashes.get(KEYWORD_TYPE.START_TIME);
		Calendar startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
		task.setStartDate(startDateAndTime);
	}
	
	protected void saveTaskEndDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		Calendar startDateAndTime = task.getStartDate();
		String endDate = infoHashes.get(KEYWORD_TYPE.END_DATE);
		String endTime = infoHashes.get(KEYWORD_TYPE.END_TIME);
		Calendar endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
		
		//this condition is to make the end time one hour apart of current time
		//and also maintain end date same as start date
		if (endDateAndTime == null) {
			if (startDateAndTime != null) {
				int addingHour = 1;
				int addingMins = 0;
				endDateAndTime = DateAndTimeFormat.getInstance().addTimeToCalendar(startDateAndTime, addingHour, addingMins);
			}
		}
		
		task.setEndDate(endDateAndTime);
		
//		if((startDate != null && startTime != null) || (startTime != null)) {
//			if(endDate == null && endTime == null) {
//				endDate = startDate;
//				int endtime = Integer.parseInt(startTime) + 100;
//				System.out.println(endtime);
//				if(endtime >= 2400) {
//					endtime -= 2400;
//				}
//				endTime = String.format("%04d", endtime);
//				
//			}
//		}
	}
	
	protected void determineAndSetTaskType (TaskInfo task) {
		Calendar startDateAndTime = task.getStartDate();
		Calendar endDateAndTime = task.getEndDate();
		
		if (startDateAndTime == null && endDateAndTime == null) {
			task.setTaskType(TASK_TYPE.FLOATING);
		} else {
			task.setTaskType(TASK_TYPE.TIMED);
		}
		if (startDateAndTime == null && endDateAndTime != null) {
			task.setTaskType(TASK_TYPE.DEADLINE);
		} 
	}
	
	

}
