//@author A0073731J
package kaboom.logic.command;

import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.TextParser;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TASK_TYPE;
import kaboom.shared.TaskInfo;
import kaboom.shared.comparators.FormatIdentifyComparator;
import kaboom.storage.History;
import kaboom.storage.TaskDepository;
import kaboom.storage.TaskView;
import kaboom.ui.DisplayData;

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
	protected TaskDepository taskListShop;
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
		taskListShop = TaskDepository.getInstance();
		displayData = DisplayData.getInstance();
		keywordList = new KEYWORD_TYPE[0];
		commandObjectTable = new Hashtable<KEYWORD_TYPE, Object>();
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
		taskView = TaskView.getInstance();
	}
	
	//used
	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}
	
	//used
	public COMMAND_TYPE getCommandType () {
		return commandType;
	}

	//used
	public Result execute() {
		return createResult(MESSAGE_COMMAND_INVALID);
	}
	
	//used
	protected Result createResult (String feedback) {
		return createResult(feedback, DISPLAY_STATE.INVALID, null);
	}

	//used
	protected Result createResult (String feedback, DISPLAY_STATE displayState, TaskInfo taskToFocus) {
		Result commandResult = new Result();
		commandResult.setFeedback(feedback);
		commandResult.setDisplayState(displayState);
		commandResult.setTaskToFocus(taskToFocus);

		return commandResult;
	}
	
	public boolean undo() {
		return false;
	}
	
	//used
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		if (info.equals("")) {
			return true;
		}
		
		// All in command are invalid
		addThisStringToFormatList(info, indexList, KEYWORD_TYPE.INVALID);
		
		return false;
	}
	
	//used
	protected Hashtable<KEYWORD_TYPE, String> updateFormatList (String info, Vector<FormatIdentify> indexList) {
		getCommandString(info, indexList);
		info = textParser.removeFirstWord(info);
		
		//5. Extract Task Info Base on Keywords
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = textParser.testExtractList(info, keywordList);
		
		return taskInformationTable;
	}
	
	//used
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


	
	//used
	protected void determineAndSetTaskType (TaskInfo task) {
		Calendar startDateAndTime = task.getStartDate();
		Calendar endDateAndTime = task.getEndDate();
		
		if (startDateAndTime == null && endDateAndTime == null) {
			task.setTaskType(TASK_TYPE.FLOATING);
		} else if (startDateAndTime == null && endDateAndTime != null) {
			task.setTaskType(TASK_TYPE.DEADLINE);
		} else {
			task.setTaskType(TASK_TYPE.TIMED);
		} 
	}
	
	//used
	public void initialiseCommandVariables(String userInputSentence) {
		infoTable = textParser.testExtractList(userInputSentence, keywordList);
		//extractAndStoreTaskInfo(infoTable);
	}
	
	public void initialiseCommandVariables(Hashtable<KEYWORD_TYPE, String> infoTable) {
		this.infoTable = infoTable;
	}
	
	//used
	protected void addCommandToHistory () {
		History.getInstance().addToRecentCommands(this);
	}
	
	//used
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
	
	//used
	protected boolean hasTaskWithTaskId() {
		TaskInfo task = getTaskWithTaskId();
		if(task == null) {
			return false;
		}
		return true;
	}

	//used
	protected TaskInfo getTaskWithTaskId() {
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		if (taskId != null) {
			int taskIdInteger = Integer.parseInt(taskId);
			return taskView.getTaskFromViewByID(taskIdInteger-1);
		}
		return null;
	}
	
	//used
	protected TaskInfo getTaskWithTaskName() {
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		if (taskName != null && !taskName.isEmpty()) {
			return taskView.getTaskFromViewByName(taskName);
		}
		return null;
	}
	
	//used
	protected TaskInfo getTask() {
		TaskInfo task = getTaskWithTaskId();
		if(task == null) {
			task = getTaskWithTaskName();
		}
		return task;
	}
	
	//used
	protected Result callSearch() {
		Command search = new CommandSearch();
		search.initialiseCommandVariables(infoTable);
		return search.execute();
	}
	
	//used
	protected int numOfTasksWithSimilarNames(String name) {
		int count = 0;
		Vector<TaskInfo> currentViewList = taskView.getCurrentView();
		for (int i = 0; i < currentViewList.size(); i++) {
			String nameFromCurrentViewListInLowerCase = currentViewList.get(i).getTaskName().toLowerCase();
			if (nameFromCurrentViewListInLowerCase.contains(name.toLowerCase())) {
				count++;
			}
		}
		return count;
	}
	
//	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
//	taskInfo = new TaskInfo();
//	
//	
//	 //In progress
//	String stringDate;
//	String stringTime;
//	Calendar dateAndTime = null;
//	
//	// Loop through the list and update to our list
//	Enumeration<KEYWORD_TYPE> elementItr =  infoHashes.keys();
//	
//	while (elementItr.hasMoreElements()) {
//		KEYWORD_TYPE currentKeyword = elementItr.nextElement();
//		
//		switch (currentKeyword) {
//			case TASKNAME:
//				commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				break;
//				
//			case START_TIME:
//			case END_TIME:
//				stringTime = infoHashes.get(currentKeyword);
//				
//				// Check if time is valid
//				if (DateAndTimeFormat.getInstance().is12hrTimeValid(stringTime) ||
//					DateAndTimeFormat.getInstance().is24hrTimeValid(stringTime)) {
//					commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				}
//				break;
//				
//			case START_DATE:
//			case END_DATE:
//				stringDate = infoHashes.get(currentKeyword);
//				stringTime = "";
//				// Check if time is valid
//				if (DateAndTimeFormat.getInstance().isDateValid(stringDate)) {
//					// Check if start or end time is valid
//					if (currentKeyword == KEYWORD_TYPE.START_DATE && commandObjectTable.containsKey(KEYWORD_TYPE.START_TIME)) {
//						stringTime = (String) commandObjectTable.get(KEYWORD_TYPE.START_TIME);
//					} else if (currentKeyword == KEYWORD_TYPE.START_DATE && commandObjectTable.containsKey(KEYWORD_TYPE.END_TIME)) {
//						stringTime = (String) commandObjectTable.get(KEYWORD_TYPE.END_TIME);
//					}
//					
//					if (!stringTime.equals("")) {
//						try {
//							dateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(stringDate, stringTime);
//						} catch (InvalidDateAndTimeException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					
//				} else {
//					
//				}
//				break;
//				
//			case PRIORITY:
//				commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				break;
//				
//			case VIEWTYPE:
//				commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				break;
//				
//			case SORT:
//				commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				break;
//				
//			default:
//				commandObjectTable.put(currentKeyword, infoHashes.get(currentKeyword));
//				break;
//		}
//		
//	}
//}

//This function takes in the hash table that is returned from the controller
//extracts from the hash table and stores the information in the taskInfo variable
//public void extractAndStoreTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
//	storeTaskInfo(infoHashes);
//}


//protected String saveTaskName(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	String taskName = infoHashes.get(KEYWORD_TYPE.TASKNAME);
//	task.setTaskName(taskName);
//	return taskName;
//}
//
//protected String saveModifiedTaskName(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	String taskName = infoHashes.get(KEYWORD_TYPE.MODIFIED_TASKNAME);
//	task.setTaskName(taskName);
//	return taskName;
//}
//
//protected String saveTaskPriority(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	String taskPriority = infoHashes.get(KEYWORD_TYPE.PRIORITY);
//	if(taskPriority != null) {
//		task.setPriority(Integer.parseInt(taskPriority));
//	}
//	return taskPriority;
//}

//protected void saveTaskDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	saveTaskStartDateAndTime(infoHashes, task);
//	saveTaskEndDateAndTime(infoHashes, task);
//	determineAndSetTaskType(task);
//}
//
//protected void saveTaskStarsearctDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
//	String startDate = datFormat.convertStringDateToDayMonthYearFormat(infoHashes.get(KEYWORD_TYPE.START_DATE));
//	String startTime = datFormat.convertStringTimeTo24HourString(infoHashes.get(KEYWORD_TYPE.START_TIME));
//	if(startTime == null || startTime.isEmpty()) {
//		startTime = "2359";
//	}
//	Calendar startDateAndTime = null;
//	try {
//		startDateAndTime = datFormat.formatStringToCalendar(startDate, startTime);
//		task.setStartDate(startDateAndTime);
//	} catch (Exception e) {
//		task.setStartDate(startDateAndTime);
//	}
//}
//
//protected void saveTaskEndDateAndTime(Hashtable<KEYWORD_TYPE, String> infoHashes, TaskInfo task) {
//	DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
//	String endDate = datFormat.convertStringDateToDayMonthYearFormat(infoHashes.get(KEYWORD_TYPE.END_DATE));
//	String endTime = datFormat.convertStringTimeTo24HourString(infoHashes.get(KEYWORD_TYPE.END_TIME));
//	if(endTime == null || endTime.isEmpty()) {
//		endTime = "0000";
//	}
//	Calendar endDateAndTime = null;
//	try {
//		endDateAndTime = datFormat.formatStringToCalendar(endDate, endTime);
//		task.setEndDate(endDateAndTime);
//	} catch (Exception e) {
//		task.setEndDate(endDateAndTime);
//	}
//}
//
//protected void setEndDateAndTimeToHourBlock (TaskInfo task) {
//	Calendar startDateAndTime = task.getStartDate();
//	Calendar endDateAndTime = task.getEndDate();
//	//this condition is to make the end time one hour apart of current time
//	//and also maintain end date same as start date
//	if(endDateAndTime == null) {
//		if (startDateAndTime != null) {
//			int addingHour = 1;
//			int addingMins = 0;
//			endDateAndTime = DateAndTimeFormat.getInstance().addTimeToCalendar(startDateAndTime, addingHour, addingMins);
//			task.setEndDate(endDateAndTime);
//		}
//	}
//
//}
}
