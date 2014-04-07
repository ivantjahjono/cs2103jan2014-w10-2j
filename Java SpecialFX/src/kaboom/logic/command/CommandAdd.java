package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;



public class CommandAdd extends Command {
	private final String MESSAGE_COMMAND_ADD_SUCCESS = "WOOT! <%1$s> ADDED. MORE STUFF TO DO!";
	private final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add <%1$s>... Error somewhere...";
	private final String MESSAGE_COMMAND_ADD_FAIL_NO_NAME = "Error! Task cannot be entered without a name Y_Y";
	private final String MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE = "Wow! How did the task end before it even started? 0.0";
	
	DateAndTimeFormat datFormat;
	
	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.PRIORITY,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.END_DATE,
				KEYWORD_TYPE.START_TIME,
				KEYWORD_TYPE.START_DATE,
				KEYWORD_TYPE.TASKNAME
		};
		datFormat = DateAndTimeFormat.getInstance();
	}
	
	/* 
	 * Error handling (Prevent add):
	 * -When there is no task name
	 * -Invalid dates
	 * -Memory inaccessible
	 * Date Formats:
	 * If only date is specified: Set calendar to date and default time of 0000 (12am)
	 * If only time is specified: Set calendar to time and default date to current day
	 * If both are specified: Set calendar to respective date and time
	 * If both are null: return null;
	 * If any are invalid: cancel add and return invalid command
	 */
	public Result execute() {
		assert taskListShop != null;
		
		String commandFeedback = "";
		
		taskInfo = new TaskInfo();
		
		commandFeedback = saveTaskNameAndGetErrorMessage();
		if(!commandFeedback.isEmpty()) {
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}

		saveTaskPriority();

		commandFeedback = saveStartDateAndTime();
		if(!commandFeedback.isEmpty()) {
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		commandFeedback = saveEndDateAndTime();
		if(!commandFeedback.isEmpty()) {
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		commandFeedback = startAndEndTimeValidityAndSetTaskType ();
		if(!commandFeedback.isEmpty()) {
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		taskInfo.setRecent(true);
		
		if (taskListShop.addTaskToList(taskInfo)) {
			addCommandToHistory ();
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
		}
		
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}

	public boolean undo () {
		String taskName = taskInfo.getTaskName();
		
		TaskInfo task = taskListShop.removeTaskByName(taskName);
		
		if (task == null)
			return false;
		else
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
	
	//********************************* STORING METHODS **********************************************
	private String saveTaskNameAndGetErrorMessage() {
		String feedback = "";
		//End if no task name
		if (infoTable.get(KEYWORD_TYPE.TASKNAME) == null || infoTable.get(KEYWORD_TYPE.TASKNAME).isEmpty()) {
			feedback = MESSAGE_COMMAND_ADD_FAIL_NO_NAME;
		} else {
			taskInfo.setTaskName(infoTable.get(KEYWORD_TYPE.TASKNAME));
		}
		return feedback;
	}
	
	private void saveTaskPriority() {
		//Default priority = 1
		if (infoTable.get(KEYWORD_TYPE.PRIORITY) == null) {
			taskInfo.setImportanceLevel(1);
		} else {
			taskInfo.setImportanceLevel(Integer.parseInt(infoTable.get(KEYWORD_TYPE.PRIORITY)));
		}
	}
	
	private String saveStartDateAndTime() {
		String feedback = "";
		String startDate = infoTable.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoTable.get(KEYWORD_TYPE.START_TIME);
		boolean hasStartDate = false;
		boolean hasStartTime = false;
		
		if(startDate != null) {
			hasStartDate = true;
		}
		if(startTime != null) {
			hasStartTime = true;
		}
		if(!hasStartDate && !hasStartTime) {
			taskInfo.setStartDate(null);
		} else {
			if(!hasStartTime) {
				startTime = "0000";
			}
			startTime = datFormat.convertStringTimeTo24HourString(startTime);
			
			if(hasStartDate) {
				//startDate = datFormat.convertStringDateToDayMonthYearFormat(startDate); <<<<<<<<<<<<<<<<<
				if(!datFormat.isDateValid(startDate)) {
					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
				} else {
					taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
				}
			} else {
				startDate = datFormat.getTodayDate();
				taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
			}
		}
		return feedback;
	}
	
	private String saveEndDateAndTime() {
		String feedback = "";
		String endDate = infoTable.get(KEYWORD_TYPE.END_DATE);
		String endTime = infoTable.get(KEYWORD_TYPE.END_TIME);
		boolean hasEndDate = false;
		boolean hasEndTime = false;
		
		if(endDate != null) {
			hasEndDate = true;
		}
		if(endTime != null) {
			hasEndTime = true;
		}
		if(!hasEndDate && !hasEndTime) {
			taskInfo.setEndDate(null);
		} else {
			if(!hasEndTime) {
				endTime = "0000";
			}
			endTime = datFormat.convertStringTimeTo24HourString(endTime);
			
			if(hasEndDate) {
				endDate = datFormat.convertStringDateToDayMonthYearFormat(endDate);
				if(!datFormat.isDateValid(endDate)) {
					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
				} else {
					//hasEndDate noEndTime hasStartDate -> Append same time(startTime)
					if(taskInfo.getStartDate() != null && !hasEndTime) {
						endTime = datFormat.timeFromCalendarToString(taskInfo.getStartDate());
					}
					
					taskInfo.setEndDate(datFormat.formatStringToCalendar(endDate, endTime));
				}
			} else {
				//hasEndTime noEndDate hasStartDate -> check if time is before start time set 1 day later else set same day				
				if(taskInfo.getStartDate() != null) {
					if(Integer.parseInt(endTime) < Integer.parseInt(datFormat.timeFromCalendarToString(taskInfo.getStartDate()))) {
						Calendar endDateCal = datFormat.addDayToCalendar(taskInfo.getStartDate(), 1);
						datFormat.convertStringTimeToCalendar(endDateCal, endTime);
						taskInfo.setEndDate(endDateCal);
					}
				} else {
					endDate = datFormat.getTodayDate();
					taskInfo.setEndDate(datFormat.formatStringToCalendar(endDate, endTime));
				}
			}
		}
		return feedback;
	}
	
	private String startAndEndTimeValidityAndSetTaskType () {
		String feedback = "";
		
		boolean hasStartCal = false;
		boolean hasEndCal = false;
		if(taskInfo.getStartDate() != null) {
			hasStartCal = true;
		}
		if(taskInfo.getEndDate() != null) {
			hasEndCal = true;
		}
		
		if(hasStartCal && hasEndCal) {
			if(!datFormat.isFirstDateBeforeSecondDate(taskInfo.getStartDate(),taskInfo.getEndDate())) {
				feedback = MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE;
			} else {
				taskInfo.setTaskType(TASK_TYPE.TIMED);
			}
		} else if(hasStartCal && !hasEndCal) {
			taskInfo.setEndDate(datFormat.addTimeToCalendar(taskInfo.getStartDate(), 1, 0));
			taskInfo.setTaskType(TASK_TYPE.TIMED);
		} else if(!hasStartCal && hasEndCal) {
			taskInfo.setTaskType(TASK_TYPE.DEADLINE);
		} else {
			taskInfo.setTaskType(TASK_TYPE.FLOATING);
		}
		return feedback;
	}
	
}
