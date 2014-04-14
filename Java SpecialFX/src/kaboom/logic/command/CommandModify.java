//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;


public class CommandModify extends Command {

	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_CHANGE = "Nothing happened...";
	private final String MESSAGE_TASK_NAME = "<%1$s> has";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE = " evolved into <%1$s>";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE = " manipulated time";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE = " consulted the stars";
	private final String MESSAGE_COMMAND_MODIFY_CONNECTOR = ",";
	
	TaskInfo preModifiedTaskInfo;
	TaskInfo modifiedTaskInfo;
	boolean hasNameChanged;
	boolean hasTimeChanged;
	boolean hasPriorityChanged;

	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.PRIORITY,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.END_DATE,
				KEYWORD_TYPE.START_TIME,
				KEYWORD_TYPE.START_DATE,
				KEYWORD_TYPE.MODIFIED_TASKNAME,
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
		hasNameChanged = false;
		hasTimeChanged = false;
		hasPriorityChanged = false;
	}

	public Result execute() {
		assert infoTable != null;

		if(infoTable == null) {
			return createResult("No TaskInfoTable");
		}

		errorDetectionForInvalidTaskNameAndId();
		if(!commandErrorList.isEmpty()) {
			return commandErrorHandler(commandErrorList.get(0));
		} else {
			preModifiedTaskInfo = getTask();
		}
		
		String feedback = "";
		
		TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
		modifyTaskDetails(temp);

		Result errorResult = validateStartAndEndTime (temp);
		if(errorResult != null) {
			return errorResult;
		}
		
		determineAndSetTaskType(temp);
		updateAndStoreInTaskManager(temp);
		
		DISPLAY_STATE displayState = determineDisplayState(modifiedTaskInfo);
		feedback = feedbackGenerator();
		return createResult(feedback, displayState, modifiedTaskInfo);
	}



	public boolean undo () {
		taskManager.updateTask(preModifiedTaskInfo, modifiedTaskInfo);
		return true;
	}

	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);

		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}

		return true;
	}

	private void modifyTaskDetails(TaskInfo temp) {
		modifyTaskName(temp);
		modifyTaskPriority(temp);
		getAndSetDateAndTime(temp);
	}
	
	private void updateAndStoreInTaskManager(TaskInfo temp) {
		modifiedTaskInfo = temp;
		modifiedTaskInfo.setRecent(true);
		taskManager.updateTask(modifiedTaskInfo, preModifiedTaskInfo);
		addCommandToHistory ();
	}
	
	private String feedbackGenerator() {
		String feedback = String.format(MESSAGE_TASK_NAME, preModifiedTaskInfo.getTaskName());
		int countNumOfModifications = 0;
		if(hasNameChanged) {
			countNumOfModifications++;
			feedback += String.format(MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE, modifiedTaskInfo.getTaskName());
		}
		if(hasTimeChanged) {
			if (countNumOfModifications > 0) {
				feedback += MESSAGE_COMMAND_MODIFY_CONNECTOR;
			}
			countNumOfModifications++;
			feedback += MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE;
		}
		if(hasPriorityChanged) {
			if (countNumOfModifications > 0) {
				feedback += MESSAGE_COMMAND_MODIFY_CONNECTOR;
			}
			feedback += MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE;
		}

		if(!hasNameChanged && !hasTimeChanged && !hasPriorityChanged) {
			feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_CHANGE;
		}

		return feedback;
	}
	
	private void modifyTaskName(TaskInfo temp) {
		if (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME) != null) {
			temp.setTaskName (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
			hasNameChanged = true;
		} else {
			hasNameChanged = false;
		}
	}
	
	private void modifyTaskPriority(TaskInfo temp) {
		String taskPriority = infoTable.get(KEYWORD_TYPE.PRIORITY);
		int originalPriorityLevel = temp.getPriority();
		if(taskPriority != null) {
			int priorityLevelAfterChange = Integer.parseInt(taskPriority);
			if (originalPriorityLevel != priorityLevelAfterChange) {
				temp.setPriority (priorityLevelAfterChange);
				hasPriorityChanged = true;
			} else {
				hasPriorityChanged = false;
			}
		} 
	}
	
	private void getAndSetDateAndTime(TaskInfo temp) {
		String startTime = getStartTime();
		String startDate = getStartDate();	
		String endTime = getEndTime();
		String endDate = getEndDate();
		determineAndSetDateAndTime(temp, startDate, startTime, endDate, endTime);
	}

	private String getStartDate() {
		String startDate = null;
		if(preModifiedTaskInfo.getStartDate() != null) {
			startDate = dateAndTimeFormat.dateFromCalendarToString(preModifiedTaskInfo.getStartDate());
		}
		startDate = getNewDateOrTimeFromInfoTable(startDate,KEYWORD_TYPE.START_DATE);
		return startDate;
	}
	private String getStartTime() {
		String startTime = null;
		if(preModifiedTaskInfo.getStartDate() != null) {
			startTime = dateAndTimeFormat.timeFromCalendarToString(preModifiedTaskInfo.getStartDate());
		}
		startTime = getNewDateOrTimeFromInfoTable(startTime,KEYWORD_TYPE.START_TIME);
		return startTime;
	}
	
	private String getEndTime() {
		String endTime = null;
		if(preModifiedTaskInfo.getEndDate() != null) {
			endTime = dateAndTimeFormat.timeFromCalendarToString(preModifiedTaskInfo.getEndDate());
		}
		endTime = getNewDateOrTimeFromInfoTable(endTime,KEYWORD_TYPE.END_TIME);
		return endTime;
	}
	
	private String getEndDate() {
		String endDate = null;
		if(preModifiedTaskInfo.getEndDate() != null) {
			endDate = dateAndTimeFormat.dateFromCalendarToString(preModifiedTaskInfo.getEndDate());
		}
		endDate = getNewDateOrTimeFromInfoTable(endDate,KEYWORD_TYPE.END_DATE);
		return endDate;
	}
	
	private String getNewDateOrTimeFromInfoTable(String dateOrTime, KEYWORD_TYPE keyword) {
		String newDateOrTime = infoTable.get(keyword);
		if(newDateOrTime != null) {
			dateOrTime = newDateOrTime;
			hasTimeChanged = true;
		}
		return dateOrTime;
	}
	
	//for testing
	public void setPreModifiedTask(TaskInfo task) {
		preModifiedTaskInfo = task;
	}
	
}
