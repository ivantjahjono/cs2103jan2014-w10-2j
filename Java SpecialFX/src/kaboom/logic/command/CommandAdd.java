//@author A0073731J

package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TASK_TYPE;
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
		assert taskView != null;

		String commandFeedback = "";
			
		validateInfoTable ();
		if(!commandErrorList.isEmpty()) {
			return commandErrorHandler(commandErrorList.get(0));
		} 
		
		taskInfo.setTaskName(infoTable.get(KEYWORD_TYPE.TASKNAME));
		saveTaskPriority();

		modifyDateAndTime(taskInfo);

		Result errorResult = validateStartAndEndTime (taskInfo);
		if(errorResult != null) {
			return errorResult;
		}

		determineAndSetTaskType(taskInfo);
		taskInfo.setRecent(true);

		DISPLAY_STATE stateToSet = DISPLAY_STATE.INVALID;
		if (taskView.addTask(taskInfo)) {
			addCommandToHistory ();
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
			stateToSet = determineDisplayState();
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
			taskInfo = null;
		}

		return createResult(commandFeedback, stateToSet, taskInfo);
	}

	private DISPLAY_STATE determineDisplayState() {
		DISPLAY_STATE stateToSet;
		if (taskInfo.getTaskType() == TASK_TYPE.FLOATING) {
			stateToSet = DISPLAY_STATE.TIMELESS;
		} else if (TaskInfo.isTaskToday(taskInfo)) {
			stateToSet =  DISPLAY_STATE.TODAY;
		} else if (TaskInfo.isFutureTask(taskInfo)){
			stateToSet =  DISPLAY_STATE.FUTURE;
		} else {
			stateToSet =  DISPLAY_STATE.EXPIRED;
		}
		return stateToSet;
	}

	public boolean undo () {
		return taskView.removeTask(taskInfo);
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
	
	private void modifyDateAndTime(TaskInfo temp) {
		String startDate = infoTable.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoTable.get(KEYWORD_TYPE.START_TIME);
		String endTime = infoTable.get(KEYWORD_TYPE.END_TIME);
		String endDate = infoTable.get(KEYWORD_TYPE.END_DATE);

		//Boolean Variables for condition checking
		boolean hasStartDate = (startDate != null);
		boolean hasStartTime = (startTime != null);
		boolean hasEndDate = (endDate != null);
		boolean hasEndTime = (endTime != null);
		boolean hasStartDateAndTime = (hasStartTime && hasStartDate);
		boolean hasEndDateAndTime = (hasEndTime && hasEndDate);


		if(hasStartDateAndTime && hasEndDateAndTime) {
			//save both start and end date 
			Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, startTime);
			Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, endTime);
			saveStartAndEndCalendars (temp, startCal, endCal);		

		} else if (hasStartDateAndTime) {
			Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, startTime);
			Calendar endCal = null;
			if(hasEndTime) {
				//set end date to start date (end time > start time) or 1 day after start date (end time <= start time)
				endCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = dateAndTimeFormat.addDayToCalendar(endCal, 1);
				} 
			} else if(hasEndDate) {
				//set end time to same start time (if not the same date) or 1hr after start time(same date)
				endCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					endCal = dateAndTimeFormat.addTimeToCalendar(endCal, 1, 0);
				}
			} else {
				//set end date to 1 hour after start date
				endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);
			}
			
			saveStartAndEndCalendars (temp, startCal, endCal);	
			
		} else if (hasEndDateAndTime) {

			Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, endTime);
			Calendar startCal = null;

			if(hasStartTime) {
				//set start date to same end date (start time before end time) or before end date (start time >= end time)
				startCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					startCal = dateAndTimeFormat.addDayToCalendar(startCal, -1);
				} 
			} else if(hasStartDate) {
				//set start time to same end time (if not the same date) or 1hr before end time(same date)
				startCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = dateAndTimeFormat.addTimeToCalendar(startCal, -1, 0);
				}	
			} 
			saveStartAndEndCalendars (temp, startCal, endCal);	
			
		} else {
			Calendar startCal = null;
			Calendar endCal = null;
			if (hasStartDate && hasEndDate) {
				//time to 0000 if different date or start time to 2359 and end time to 2359 if same date
				startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
				endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
					endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
					endCal = dateAndTimeFormat.addTimeToCalendar(endCal, 1, 0);
				}
				
			} else if (hasStartTime && hasEndTime) {
				//set to today if start < end time or set start to today and end to next day
				String today = dateAndTimeFormat.getDateToday2();
				startCal = dateAndTimeFormat.formatStringToCalendar(today, startTime);
				endCal = dateAndTimeFormat.formatStringToCalendar(today, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = dateAndTimeFormat.addDayToCalendar(endCal, 1);
				}
				
			} else if (hasStartTime && hasEndDate) {
				//set time to 1hr block with end date
				startCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);	
				
			} else if (hasStartDate && hasEndTime) {
				//set time to 1hr block before end time and date to start date
				endCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				startCal = dateAndTimeFormat.addTimeToCalendar(endCal, -1, 0);	
				
			} else if (hasStartDate) {
				//set time from 0000 to 2359 and save start date
				startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
				endCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getEndTimeOfTheDay());

			} else if (hasStartTime) {
				//set date to today and save start date and end date to 1 hour later
				String today = dateAndTimeFormat.getDateToday2();
				startCal = dateAndTimeFormat.formatStringToCalendar(today, startTime);
				endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);

			} else if (hasEndDate) {
				//set time to 2359 and save end date only
				endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
				
			} else if (hasEndTime) {
				//set date to today and save end date only
				String today = dateAndTimeFormat.getDateToday2();
				endCal = dateAndTimeFormat.formatStringToCalendar(today, endTime);
			}
			saveStartAndEndCalendars (temp, startCal, endCal);	
		}
	}
	
	private void saveStartAndEndCalendars (TaskInfo task, Calendar start, Calendar end) {
		task.setStartDate(start);
		task.setEndDate(end);
	}
	
	
	private Result validateStartAndEndTime (TaskInfo temp) {
		if(temp.getStartDate() != null && temp.getEndDate() != null) {
			if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
				return commandErrorHandler(COMMAND_ERROR.INVALID_DATE);
			}
		}
		return null;
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
		if (hasTaskIdAndTaskName(taskName, taskId)) {
			addCommandErrorToList(COMMAND_ERROR.INVALID_TASKNAME);
		} else if (isStringNullOrEmpty(taskName)) {
			addCommandErrorToList(COMMAND_ERROR.NO_TASK_NAME);
		} 
	}

	private boolean hasTaskIdAndTaskName(String taskName, String taskId) {
		return taskId != null && !isStringNullOrEmpty(taskName);
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
