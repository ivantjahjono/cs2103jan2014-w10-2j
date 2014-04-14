//@author A0073731J

package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

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
	
	private final String INVALID_DATE = "INVALID DATE";
	
	
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

		COMMAND_ERROR commandError = errorDetectionForInvalidTaskNameAndId();
		if(commandError != null) {
			return commandErrorHandler(commandError);
		} else {
			preModifiedTaskInfo = getTask();
		}
		
		String feedback = "";
		
		TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
		hasNameChanged = modifyTaskName(temp);
		hasPriorityChanged = modifyTaskPriority(temp);
//		commandError = modifyDateAndTime(temp);
//		if(commandError == COMMAND_ERROR.INVALID_DATE) {
//			feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
//			return createResult(feedback);
//		}
		determineAndSetDateAndTime(temp);
		commandError = validateStartAndEndTime (temp);
		if(commandError == COMMAND_ERROR.INVALID_DATE) {
			feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(feedback);
		}
		determineAndSetTaskType(temp);
		//store and update in memory
		modifiedTaskInfo = temp;
		modifiedTaskInfo.setRecent(true);
		taskManager.updateTask(modifiedTaskInfo, preModifiedTaskInfo);
		
		feedback = feedbackGenerator();
		addCommandToHistory ();
		return createResult(feedback);
	}

	public boolean undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+ modifiedTaskInfo.getTaskName());
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

	//for testing
	public void setPreModifiedTask(TaskInfo task) {
		preModifiedTaskInfo = task;
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
	
	private boolean modifyTaskName(TaskInfo temp) {
		if (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME) != null) {
			temp.setTaskName (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
			return true;
		}
		return false;
	}
	
	private boolean modifyTaskPriority(TaskInfo temp) {
		String taskPriority = infoTable.get(KEYWORD_TYPE.PRIORITY);
		int originalPriorityLevel = temp.getPriority();
		if(taskPriority != null) {
			int priorityLevelAfterChange = Integer.parseInt(taskPriority);
			if (originalPriorityLevel != priorityLevelAfterChange) {
				temp.setPriority (priorityLevelAfterChange);
				return true;
			}
		}
		return false;
	}
	
	private String getPrevStartDate() {
		String startDate = null;
		if(preModifiedTaskInfo.getStartDate() != null) {
			startDate = dateAndTimeFormat.dateFromCalendarToString(preModifiedTaskInfo.getStartDate());
		}
		return startDate;
	}
	
	private String getPrevStartTime() {
		String startTime = null;
		if(preModifiedTaskInfo.getStartDate() != null) {
			startTime = dateAndTimeFormat.timeFromCalendarToString(preModifiedTaskInfo.getStartDate());
		}
		return startTime;
	}
	
	private String getNewStartTime(String startTime) {
		String newStartTime =  dateAndTimeFormat.convertStringTimeTo24HourString(infoTable.get(KEYWORD_TYPE.START_TIME));
		if(newStartTime != null) {
			startTime = dateAndTimeFormat.convertStringTimeTo24HourString(newStartTime);
			hasTimeChanged = true;
		}
		return startTime;
	}
	
	private String getNewStartDate(String startDate) {
		String newStartDate = dateAndTimeFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.START_DATE));
		if(newStartDate != null) {
			if(dateAndTimeFormat.isDateValid(newStartDate)) {
				startDate = newStartDate;
				hasTimeChanged = true;
			} else {
				startDate = INVALID_DATE;
			}
		}
		return startDate;
	}
	
	private String getPrevEndDate() {
		String endDate = null;
		if(preModifiedTaskInfo.getEndDate() != null) {
			endDate = dateAndTimeFormat.dateFromCalendarToString(preModifiedTaskInfo.getEndDate());
		}
		return endDate;
	}
	
	private String getPrevEndTime() {
		String endTime = null;
		if(preModifiedTaskInfo.getEndDate() != null) {
			endTime = dateAndTimeFormat.timeFromCalendarToString(preModifiedTaskInfo.getEndDate());
		}
		return endTime;
	}
	
	private String getNewEndDate(String endDate) {
		String newEndDate = dateAndTimeFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.END_DATE));
		if(newEndDate != null) {
			if(dateAndTimeFormat.isDateValid(newEndDate)) {
				endDate = newEndDate;
				hasTimeChanged = true;
			} else {
				endDate = INVALID_DATE;
			}
		}
		return endDate;
	}
	
	private String getNewEndTime(String endTime) {
		String newEndTime = dateAndTimeFormat.convertStringTimeTo24HourString(infoTable.get(KEYWORD_TYPE.END_TIME));
		if(newEndTime != null) {
			endTime = dateAndTimeFormat.convertStringTimeTo24HourString(newEndTime);
			hasTimeChanged = true;
		}
		return endTime;
	}
	
	private String getStartDate() {
		String startDate = getPrevStartDate();
		startDate = getNewStartDate(startDate);
		return startDate;
	}
	
	private String getStartTime() {
		String startTime = getPrevStartTime();
		startTime = getNewStartTime(startTime);
		return startTime;
	}
	
	private String getEndTime() {
		String endTime = getPrevEndTime();
		endTime = getNewEndTime(endTime);
		return endTime;
	}
	
	private String getEndDate() {
		String endDate = getPrevEndDate();
		endDate = getNewEndDate(endDate);
		return endDate;
	}
	
	private COMMAND_ERROR validateStartAndEndTime (TaskInfo temp) {
		if(temp.getStartDate() != null && temp.getEndDate() != null) {
			if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
				return COMMAND_ERROR.INVALID_DATE;
			}
		}
		return null;
	}
	
}
