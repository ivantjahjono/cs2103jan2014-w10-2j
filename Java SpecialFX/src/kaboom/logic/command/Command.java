package kaboom.logic.command;

import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.FormatIdentifyComparator;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.logic.TextParser;
import kaboom.storage.History;
import kaboom.storage.TaskListShop;
import kaboom.ui.DISPLAY_STATE;
import kaboom.ui.DisplayData;
import kaboom.ui.TaskView;

/* 
 ** Purpose: 
 */

public class Command {
	protected final String MESSAGE_COMMAND_FAIL_INVALID_DATE = "Oops! Did you check the calendar? The date you've entered is invalid";
	protected final String MESSAGE_COMMAND_FAIL_NO_SUCH_TASK = "Oops! Modify wut??";
	protected final String MESSAGE_COMMAND_FAIL_NO_TASK_NAME = "Oops! What was the task name again??";
	protected final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	
	
	protected COMMAND_TYPE commandType;
	protected TextParser textParser;
	protected TaskListShop taskListShop;
	protected DisplayData displayData;
	protected KEYWORD_TYPE[] keywordList;
	Hashtable<KEYWORD_TYPE, Object> commandObjectTable;
	Hashtable<KEYWORD_TYPE, String> infoTable; //TEMP
	protected TaskView taskView;

	protected enum COMMAND_ERROR{
		NO_TASK_NAME, INVALID_DATE, NIL, STARTDATE_AFTER_ENDDATE
	}
	
	public Command () {
		commandType = COMMAND_TYPE.INVALID;
		textParser = TextParser.getInstance();
		taskListShop = TaskListShop.getInstance();
		displayData = DisplayData.getInstance();
		keywordList = new KEYWORD_TYPE[0];
		commandObjectTable = new Hashtable<KEYWORD_TYPE, Object>();
		taskView = TaskView.getInstance();
	}

	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}

	public COMMAND_TYPE getCommandType () {
		return commandType;
	}

	public Result execute() {
		return createResult(MESSAGE_COMMAND_INVALID, DISPLAY_STATE.INVALID);
	}
	
	protected Result createResult (String feedback) {
		return createResult(feedback, DISPLAY_STATE.INVALID);
	}

	protected Result createResult (String feedback, DISPLAY_STATE displayState) {
		Result commandResult = new Result();
		commandResult.setFeedback(feedback);
		commandResult.setDisplayState(displayState);

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
		info = textParser.removeFirstWord(info);
		
		//5. Extract Task Info Base on Keywords
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = textParser.testExtractList(info, keywordList);
		
		return taskInformationTable;
	}
	
	protected void updateFormatListBasedOnHashtable(Vector<FormatIdentify> indexList, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		String stringDate;
		
		Enumeration<KEYWORD_TYPE> elementItr =  taskInformationTable.keys();
		
		while (elementItr.hasMoreElements()) {
			KEYWORD_TYPE currentKeyword = elementItr.nextElement();
			KEYWORD_TYPE resultKeyword = currentKeyword;
			
			// Check current type
			switch (currentKeyword) {					
				case START_DATE:
				case END_DATE:
					stringDate = taskInformationTable.get(currentKeyword);
					
					// Check if time is valid
					if (!DateAndTimeFormat.getInstance().isDateValid(stringDate)) {
						//taskInformationTable.remove(currentKeyword);
						resultKeyword = KEYWORD_TYPE.INVALID;
					}
					break;
					
				default:
					break;
			}
			
			addThisStringToFormatList(taskInformationTable.get(currentKeyword), indexList, resultKeyword);
		}
		
		Collections.sort(indexList, new FormatIdentifyComparator());
	}
	
	protected void getCommandString(String info, Vector<FormatIdentify> indexList) {
		String commandString = textParser.getFirstWord(info);
		addThisStringToFormatList(commandString, indexList, KEYWORD_TYPE.COMMAND);
	}
	
	protected void addThisStringToFormatList(String info, Vector<FormatIdentify> indexList, KEYWORD_TYPE type) {
		FormatIdentify newIdentity = new FormatIdentify();
		
		newIdentity.setCommandStringFormat(info);
		newIdentity.setType(type);

		indexList.add(newIdentity);
	}

	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
//		taskInfo = new TaskInfo();
//		
//		
//		 //In progress
//		String stringDate;
//		String stringTime;
//		Calendar dateAndTime = null;
//		
//		// Loop through the list and update to our list
//		Enumeration<KEYWORD_TYPE> elementItr =  infoHashes.keys();
//		
//		while (elementItr.hasMoreElements()) {
//			KEYWORD_TYPE currentKeyword = elementItr.nextElement();
//			
//			switch (currentKeyword) {
//				case TASKNAME:
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					break;
//					
//				case START_TIME:
//				case END_TIME:
//					stringTime = infoHashes.get(currentKeyword);
//					
//					// Check if time is valid
//					if (DateAndTimeFormat.getInstance().is12hrTimeValid(stringTime) ||
//						DateAndTimeFormat.getInstance().is24hrTimeValid(stringTime)) {
//						commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					}
//					break;
//					
//				case START_DATE:
//				case END_DATE:
//					stringDate = infoHashes.get(currentKeyword);
//					stringTime = "";
//					// Check if time is valid
//					if (DateAndTimeFormat.getInstance().isDateValid(stringDate)) {
//						// Check if start or end time is valid
//						if (currentKeyword == KEYWORD_TYPE.START_DATE && commandObjectTable.containsKey(KEYWORD_TYPE.START_TIME)) {
//							stringTime = (String) commandObjectTable.get(KEYWORD_TYPE.START_TIME);
//						} else if (currentKeyword == KEYWORD_TYPE.START_DATE && commandObjectTable.containsKey(KEYWORD_TYPE.END_TIME)) {
//							stringTime = (String) commandObjectTable.get(KEYWORD_TYPE.END_TIME);
//						}
//						
//						if (!stringTime.equals("")) {
//							try {
//								dateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(stringDate, stringTime);
//							} catch (InvalidDateAndTimeException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						
//					} else {
//						
//					}
//					break;
//					
//				case PRIORITY:
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					break;
//					
//				case VIEWTYPE:
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					break;
//					
//				case SORT:
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					break;
//					
//				default:
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//					break;
//			}
//			
//		}
	}
	
	//This function takes in the hash table that is returned from the controller
	//extracts from the hash table and stores the information in the taskInfo variable
	public void extractAndStoreTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		storeTaskInfo(infoHashes);
		infoTable = infoHashes; //temp
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
		DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
		String startDate = datFormat.convertStringDateToDayMonthYearFormat(infoHashes.get(KEYWORD_TYPE.START_DATE));
		String startTime = datFormat.convertStringTimeTo24HourString(infoHashes.get(KEYWORD_TYPE.START_TIME));
		if(startTime == null || startTime.isEmpty()) {
			startTime = "2359";
		}
		Calendar startDateAndTime = null;
		try {
			startDateAndTime = datFormat.formatStringToCalendar(startDate, startTime);
			task.setStartDate(startDateAndTime);
		} catch (Exception e) {
			task.setStartDate(startDateAndTime);
		}
	}
	
	protected void saveTaskEndDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
		DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
		String endDate = datFormat.convertStringDateToDayMonthYearFormat(infoHashes.get(KEYWORD_TYPE.END_DATE));
		String endTime = datFormat.convertStringTimeTo24HourString(infoHashes.get(KEYWORD_TYPE.END_TIME));
		if(endTime == null || endTime.isEmpty()) {
			endTime = "0000";
		}
		Calendar endDateAndTime = null;
		try {
			endDateAndTime = datFormat.formatStringToCalendar(endDate, endTime);
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
	
	public KEYWORD_TYPE[] getKeywordList() {
		return keywordList;
	}
	
	public void initialiseCommandVariables(String userInputSentence) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = textParser.testExtractList(userInputSentence, keywordList);
		extractAndStoreTaskInfo(taskInformationTable);
	}
	
	protected void addCommandToHistory () {
		History.getInstance().addToRecentCommands(this);
	}
	
	protected Result taskDetectionWithErrorFeedback() {
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		String feedback = "";
		Result errorFeedback = null;
		
		if(!hasTaskWithTaskId()) {
			if (taskName != null){
				int taskCount = numOfTasksWithSimilarNames(taskName);
				
				if (taskCount > 1) {
					errorFeedback = callSearch();
				}
				else if (taskCount < 1) {
					feedback = MESSAGE_COMMAND_FAIL_NO_SUCH_TASK;
					errorFeedback = createResult(feedback);
				}
			} else {
				feedback = MESSAGE_COMMAND_FAIL_NO_TASK_NAME;
				errorFeedback = createResult(feedback);
			}
		}
		return errorFeedback;
	}	
	
	protected boolean hasTaskWithTaskId() {
		TaskInfo task = getTaskWithTaskId();
		if(task == null) {
			return false;
		}
		return true;
	}

	protected TaskInfo getTaskWithTaskId() {
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		if (taskId != null) {
			int taskIdInteger = Integer.parseInt(taskId);
			int index = taskView.getIndexFromView(taskIdInteger-1);
			return taskListShop.getTaskByID(index);
		}
		return null;
	}
	
	protected TaskInfo getTaskWithTaskName() {
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		if (taskName != null && !taskName.isEmpty()) {
			return taskListShop.getTaskByName(taskName);
		}
		return null;
	}
	
	protected TaskInfo getTask() {
		TaskInfo task = getTaskWithTaskId();
		if(task == null) {
			task = getTaskWithTaskName();
		}
		return task;
	}
	
	protected Result callSearch() {
		Command search = new CommandSearch();
		search.storeTaskInfo(infoTable);
		return search.execute();
	}
	
	protected int numOfTasksWithSimilarNames(String name) {
		int count = 0;
		for (int i = 0; i < taskListShop.getAllCurrentTasks().size(); i++) {
			if (taskListShop.getAllCurrentTasks().get(i).getTaskName().contains(name)) {
				count++;
			}
		}
		return count;
	}

	public DISPLAY_STATE getDisplayStateBasedOnTaskType(TASK_TYPE type) {
		switch (type) {
			case FLOATING:
				return DISPLAY_STATE.TIMELESS;
				
			case DEADLINE:
				return DISPLAY_STATE.TODAY;
			
			case TIMED:
				return DISPLAY_STATE.TODAY;
				
			default:
				return DISPLAY_STATE.INVALID;
		}
	}
}
