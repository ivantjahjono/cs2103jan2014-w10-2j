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
import kaboom.storage.TaskView;
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
	protected KEYWORD_TYPE[] keywordList;
	Hashtable<KEYWORD_TYPE, String> infoTable;
	protected TaskView taskView;
	
	protected enum COMMAND_ERROR{
		CLASH, TASK_DOES_NOT_EXIST, NO_TASK_NAME, INVALID_DATE, NIL
	}
	
	public Command () {
		commandType = COMMAND_TYPE.INVALID;
		textParser = TextParser.getInstance();
		taskView = TaskView.getInstance();
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
		keywordList = new KEYWORD_TYPE[0];
	}
	
	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}
	
	public COMMAND_TYPE getCommandType () {
		return commandType;
	}

	public Result execute() {
		return createResult(MESSAGE_COMMAND_INVALID);
	}
	
	protected Result createResult (String feedback) {
		return createResult(feedback, DISPLAY_STATE.INVALID, null);
	}

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
	
	public void initialiseCommandInfoTable(String userInputSentence) {
		infoTable = textParser.testExtractList(userInputSentence, keywordList);
	}
	
	public void initialiseCommandInfoTable(Hashtable<KEYWORD_TYPE, String> infoTable) {
		this.infoTable = infoTable;
	}
	
	//TODO
	protected void addCommandToHistory () {
		History.getInstance().addToRecentCommands(this);
	}
	
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
	
	protected Result callSearch() {
		Command search = new CommandSearch();
		search.initialiseCommandInfoTable(infoTable);
		return search.execute();
	}
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ DONE ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
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

	//*******************************************RETRIEVAL METHODS FROM INFOTABLE***********************************************
	protected String getTaskNameFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.TASKNAME);
	}
	
	protected String getTaskIdFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.TASKID);
	}
	
	protected String getTaskNameToModifyToFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME);
	}

	protected String getTaskPriorityFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.PRIORITY);
	}
	
	protected String getTaskEndTimeFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.END_TIME);
	}
	
	protected String getTaskEndDateFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.END_DATE);
	}
	
	protected String getTaskStartDateFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.START_DATE);
	}
	
	protected String getTaskStartTimeFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.START_TIME);
	}
	
	protected String getTaskDateFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.DATE);
	}
	
	protected String getTaskClearTypeFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.CLEARTYPE);
	}
	
	protected String getTaskViewTypeFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.VIEWTYPE);
	}
	
	protected String getPageCommandFromInfoTable() {
		return infoTable.get(KEYWORD_TYPE.PAGE);
	}
	
	//************************ ERROR HANDLER *******************************
	protected Result commandErrorHandler(COMMAND_ERROR commandError) {
		switch(commandError) {
		case CLASH:
			return callSearch();
		case TASK_DOES_NOT_EXIST:
			return createResult(MESSAGE_COMMAND_FAIL_NO_SUCH_TASK);
		case NO_TASK_NAME:
			return createResult(MESSAGE_COMMAND_FAIL_NO_TASK_NAME);
		case INVALID_DATE:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_DATE);
		default:
			return null;
		}
	}
	
	protected COMMAND_ERROR errorDetectionForInvalidTaskNameAndId() {
		COMMAND_ERROR commandError = null;
		if (isTaskIdValid()) {
			return commandError;
		} else {
			String taskName = getTaskNameFromInfoTable();
			if (isTaskNameNullOrEmpty(taskName)) {
				commandError = COMMAND_ERROR.NO_TASK_NAME;
			} else {
				commandError = taskExistenceOrClashDetection(taskName);
			} 
		}
		return commandError;
	}

	private boolean isTaskNameNullOrEmpty(String taskName) {
		return taskName == null || taskName.isEmpty();
	}

	private COMMAND_ERROR taskExistenceOrClashDetection(String taskName) {
		int taskCount = numOfTasksWithSimilarNames(taskName);
		if (taskCount > 1) {
			return COMMAND_ERROR.CLASH;
		}
		else if (taskCount < 1) {
			return COMMAND_ERROR.TASK_DOES_NOT_EXIST;
		}
		return null;
	}
	
	protected boolean isTaskIdValid() {
		TaskInfo task = getTaskWithTaskId();
		if(task == null) {
			return false;
		}
		return true;
	}

	protected TaskInfo getTaskWithTaskId() {
		String taskId = getTaskIdFromInfoTable();
		if (taskId != null) {
			int taskIdInteger = Integer.parseInt(taskId);
			return taskView.getTaskFromViewByID(taskIdInteger-1);
		}
		return null;
	}
	
	protected TaskInfo getTaskWithTaskName() {
		String taskName = getTaskNameFromInfoTable();
		if (taskName != null && !taskName.isEmpty()) {
			return taskView.getTaskFromViewByName(taskName);
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
	
//	protected Result invalidTaskNameAndClashErrorDetection() {
//	String taskName = getTaskNameFromInfoTable();
//	String feedback = "";
//	Result errorFeedback = null;
//	
//	if(!existsATaskWithGivenTaskId()) {
//		if (taskName != null && !taskName.isEmpty()){
//			int taskCount = numOfTasksWithSimilarNames(taskName);
//			
//			if (taskCount > 1) {
//				errorFeedback = callSearch();
//			}
//			else if (taskCount < 1) {
//				feedback = MESSAGE_COMMAND_FAIL_NO_SUCH_TASK;
//				errorFeedback = createResult(feedback);
//			}
//		} else {
//			feedback = MESSAGE_COMMAND_FAIL_NO_TASK_NAME;
//			errorFeedback = createResult(feedback);
//		}
//	}
//	return errorFeedback;
//}
}
