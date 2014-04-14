//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;



public class CommandAdd extends Command {
	private final String MESSAGE_COMMAND_ADD_SUCCESS = "WOOT! <%1$s> ADDED. MORE STUFF TO DO!";
	private final String MESSAGE_COMMAND_ADD_FAIL = "Oops! Fail to add <%1$s>... Error somewhere...";
	private final String MESSAGE_COMMAND_ADD_FAIL_NO_NAME = "Oops! Task cannot be entered without a name Y_Y";
	
	private final int DEFAULT_PRIORITY = 1;

	TaskInfo taskInfo;

	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.PRIORITY,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.END_DATE,
				KEYWORD_TYPE.START_TIME,
				KEYWORD_TYPE.START_DATE,
				KEYWORD_TYPE.TASKNAME
		};
		taskInfo = new TaskInfo();
	}

	public Result execute() {
		assert taskManager != null;

		String commandFeedback = "";
			
		validateInfoTable ();
		if(!commandErrorList.isEmpty()) {
			return commandErrorHandler(commandErrorList.get(0));
		} 
		
		taskInfo.setTaskName(infoTable.get(KEYWORD_TYPE.TASKNAME));
		saveTaskPriority();

		getAndSetDateAndTime();

		Result errorResult = validateStartAndEndTime (taskInfo);
		if(errorResult != null) {
			return errorResult;
		}

		determineAndSetTaskType(taskInfo);
		taskInfo.setRecent(true);

		DISPLAY_STATE stateToSet = DISPLAY_STATE.INVALID;
		if (taskManager.addPresentTask(taskInfo)) {
			addCommandToHistory ();
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
			stateToSet = determineDisplayState(taskInfo);
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
			taskInfo = null;
		}

		return createResult(commandFeedback, stateToSet, taskInfo);
	}

	public boolean undo () {
		return taskManager.removeTask(taskInfo);
	}

	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);

		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}

		return true;
	}

	private void saveTaskPriority() {
		String priorityString = infoTable.get(KEYWORD_TYPE.PRIORITY);
		if (priorityString == null) {
			taskInfo.setPriority(DEFAULT_PRIORITY);
		} else {
			int priorityInteger = Integer.parseInt(priorityString);
			taskInfo.setPriority(priorityInteger);
		}
	}
	
	private void getAndSetDateAndTime() {
		String startDate = infoTable.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoTable.get(KEYWORD_TYPE.START_TIME);
		String endTime = infoTable.get(KEYWORD_TYPE.END_TIME);
		String endDate = infoTable.get(KEYWORD_TYPE.END_DATE);
		determineAndSetDateAndTime(taskInfo, startDate, startTime, endDate, endTime);
	}
	
	private void validateInfoTable () {
		validateTaskName();
		String startDate = infoTable.get(KEYWORD_TYPE.START_DATE);
		validateDate(startDate);
		String endDate = infoTable.get(KEYWORD_TYPE.END_DATE);
		validateDate(endDate);
	}
	
	private void validateDate (String date) {
		if (date != null && !dateAndTimeFormat.isDateValid(date)) {
			addCommandErrorToList(COMMAND_ERROR.INVALID_DATE);
		}
	}
	
	private void validateTaskName() {
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		if (taskId != null) {
			addCommandErrorToList(COMMAND_ERROR.INVALID_TASKNAME);
		} else if (isStringNullOrEmpty(taskName)) {
			addCommandErrorToList(COMMAND_ERROR.NO_TASK_NAME);
		} 
	}
	
	protected Result commandErrorHandler(COMMAND_ERROR commandError) {
		switch(commandError) {
		case NO_TASK_NAME:
			return createResult(MESSAGE_COMMAND_ADD_FAIL_NO_NAME);
		case INVALID_DATE:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_DATE);
		case INVALID_TASKNAME:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_TASKNAME);
		default:
			return null;
		}
	}
}
