package kaboom.logic.command;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.DisplayData;
import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.TextParser;
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
	
	public boolean undo() {
		return false;
	}
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		if (info.equals("")) {
			return true;
		}
		
		// All in command are invalid
		addThisStringToFormatList(info, indexList, KEYWORD_TYPE.INVALID);
		
		return false;
	}
	
	protected Hashtable<KEYWORD_TYPE, String> updateFormatList (String info, Vector<FormatIdentify> indexList) {
		getCommandString(info, indexList);
		info = TextParser.removeFirstWord(info);
		
		//5. Extract Task Info Base on Keywords
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = TextParser.testExtractList(info, keywordList);
		
		return taskInformationTable;
	}
	
	protected void updateFormatListBasedOnHashtable(Vector<FormatIdentify> indexList, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		Enumeration<KEYWORD_TYPE> elementItr =  taskInformationTable.keys();
		
		while (elementItr.hasMoreElements()) {
			KEYWORD_TYPE currentKeyword = elementItr.nextElement();
			addThisStringToFormatList(taskInformationTable.get(currentKeyword), indexList, currentKeyword);
		}
	}
	
	protected void getCommandString(String info, Vector<FormatIdentify> indexList) {
		String commandString = TextParser.getFirstWord(info);
		addThisStringToFormatList(commandString, indexList, KEYWORD_TYPE.COMMAND);
	}
	
	protected void addThisStringToFormatList(String info, Vector<FormatIdentify> indexList, KEYWORD_TYPE type) {
		FormatIdentify newIdentity = new FormatIdentify();
		
		newIdentity.setCommandStringFormat(info);
		newIdentity.setType(type);
		
		indexList.add(newIdentity);
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
	
	protected String saveModifiedTaskName(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String taskName = infoHashes.get(KEYWORD_TYPE.MODIFIED_TASKNAME);
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
	
//	protected void saveTaskDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//		saveTaskStartDateAndTime(infoHashes, task);
//		saveTaskEndDateAndTime(infoHashes, task);
//		determineAndSetTaskType(task);
//	}
	
	protected void saveTaskStartDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String startDate = infoHashes.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoHashes.get(KEYWORD_TYPE.START_TIME);
		Calendar startDateAndTime = null;
		try {
			startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
			task.setStartDate(startDateAndTime);
		} catch (Exception e) {
			task.setStartDate(startDateAndTime);
		}
	}
	
	protected void saveTaskEndDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		String endDate = infoHashes.get(KEYWORD_TYPE.END_DATE);
		String endTime = infoHashes.get(KEYWORD_TYPE.END_TIME);
		Calendar endDateAndTime = null;
		try {
			endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
			task.setEndDate(endDateAndTime);
		} catch (Exception e) {
			task.setEndDate(endDateAndTime);
		}
	}
	
	protected void setEndDateAndTimeToHourBlock (TaskInfo task) {
		Calendar startDateAndTime = task.getStartDate();
		Calendar endDateAndTime = task.getEndDate();
		//this condition is to make the end time one hour apart of current time
		//and also maintain end date same as start date
		if(endDateAndTime == null) {
			if (startDateAndTime != null) {
				int addingHour = 1;
				int addingMins = 0;
				endDateAndTime = DateAndTimeFormat.getInstance().addTimeToCalendar(startDateAndTime, addingHour, addingMins);
				task.setEndDate(endDateAndTime);
			}
		}
	
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
	
	public int getNumberOfTasksWithName (String name) {
		int count = 0;
		Vector<TaskInfo> taskList = taskListShop.getAllCurrentTasks();
		for (int i = 0; i < taskList.size(); i++) {
			if (taskList.get(i).getTaskName().equals(name)) {
				count++;
			}
		}
		return count;
	}
	
	protected boolean hasMultipleTaskOfSimilarName(String name) {
		//id and name
		int count = 0;
		count += getNumberOfTasksWithName(name);
		if(name.matches("\\d+")) {
			if(Integer.parseInt(name) < taskListShop.shopSize()) {
				count++;
			}
		}
		if(count > 1) {
			return true;
		}
		return false;
	}

}
