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

	protected static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	protected static final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add %1$s";
	protected static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	protected static final String MESSAGE_COMMAND_DELETE_FAIL = "%1$s fail to delete.";
	protected static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	protected static final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to modify %1$s";
	protected static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done. %d item(s) found.";
	protected static final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	protected static final String MESSAGE_COMMAND_UNDO_SUCCESS = "Command undone!";
	protected static final String MESSAGE_COMMAND_UNDO_FAIL = "Fail to undo.";
	protected static final String MESSAGE_COMMAND_CLEAR_SUCCESS = "Cleared memory";


	protected COMMAND_TYPE commandType;
	protected TaskInfo taskInfoToBeModified;
	protected TaskInfo taskInfo;
	protected TaskListShop taskListShop;
	protected DisplayData displayData;
	protected Vector<KEYWORD_TYPE> keywordList;  //Initialized in the individual command constructor

	public Command () {
		commandType = COMMAND_TYPE.INVALID;
		taskInfo = null;
		taskInfoToBeModified = null;
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

	public void setTaskInfoToBeModified (TaskInfo info) {
		taskInfoToBeModified = info;
	}

	public COMMAND_TYPE getCommandType () {
		return commandType;
	}

	public TaskInfo getTaskInfo () {
		return taskInfo;
	}

	public TaskInfo getTaskInfoToBeModified () {
		return taskInfoToBeModified;
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

	public Vector<KEYWORD_TYPE> getKeywordList () {
		return keywordList;
	}

	//This function takes in the hash table that is returned from the controller
	//extracts from the hash table and stores the information in the taskInfo variable
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		String taskName = infoHashes.get(KEYWORD_TYPE.TASKNAME);
		String priority = infoHashes.get(KEYWORD_TYPE.PRIORITY);

		taskInfo = new TaskInfo();
		
		if (taskName != null) {
			taskInfo.setTaskName(taskName);
		}
		if (priority != null) {
			taskInfo.setImportanceLevel(Integer.parseInt(priority));
		}

		//The below are taken from the old controller methods
		String startDate = infoHashes.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoHashes.get(KEYWORD_TYPE.START_TIME);
		Calendar startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
		taskInfo.setStartDate(startDateAndTime);
		String endDate = infoHashes.get(KEYWORD_TYPE.END_DATE);
		String endTime = infoHashes.get(KEYWORD_TYPE.END_TIME);
		Calendar endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
		taskInfo.setEndDate(endDateAndTime);
		
		//HARDCODE TASK TYPE
		taskInfo.setTaskType(TASK_TYPE.TIMED);
	}

	public String undo () {
		return MESSAGE_COMMAND_UNDO_FAIL;
	}
}
