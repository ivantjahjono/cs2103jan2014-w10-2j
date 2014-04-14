//@author A0073731J

package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.command.Command.COMMAND_ERROR;
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

	DateAndTimeFormat dateAndTimeFormat;
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
		dateAndTimeFormat = DateAndTimeFormat.getInstance();
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
		assert taskView != null;

		String commandFeedback = "";
		taskInfo = new TaskInfo();
		
		if(!commandErrorList.isEmpty()) {
			return commandErrorHandler(commandErrorList.get(0));
		} 
		
		taskInfo.setTaskName(getTaskNameFromInfoTable());
		saveTaskPriority();

		COMMAND_ERROR commandError = modifyDateAndTime(taskInfo);
		if(commandError != COMMAND_ERROR.NIL) {
			commandFeedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(commandFeedback);
		}

		commandError = validateStartAndEndTime (taskInfo);
		if(commandError != COMMAND_ERROR.NIL) {
			commandFeedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(commandFeedback);
		}

		//check wad type of task

		determineAndSetTaskType(taskInfo);
		taskInfo.setRecent(true);

		DISPLAY_STATE stateToSet = DISPLAY_STATE.INVALID;
		if (taskView.addTask(taskInfo)) {
			addCommandToHistory ();
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());

			if (taskInfo.getTaskType() == TASK_TYPE.FLOATING) {
				stateToSet = DISPLAY_STATE.TIMELESS;
			} else if (TaskInfo.isTaskToday(taskInfo)) {
				stateToSet =  DISPLAY_STATE.TODAY;
			} else if (TaskInfo.isFutureTask(taskInfo)){
				stateToSet =  DISPLAY_STATE.FUTURE;
			} else {
				stateToSet =  DISPLAY_STATE.EXPIRED;
			}
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
			taskInfo = null;
		}

		return createResult(commandFeedback, stateToSet, taskInfo);
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

	//********************************* STORING METHODS **********************************************
	private COMMAND_ERROR taskNameValidity() {
		String taskName = getTaskNameFromInfoTable();
		String taskId = getTaskIdFromInfoTable();
		if (hasTaskIdAndTaskName(taskName, taskId)) {
			return COMMAND_ERROR.INVALID_TASKNAME;
		} else if (isStringNullOrEmpty(taskName)) {
			return COMMAND_ERROR.NO_TASK_NAME;
		} else {
			return null;
		}
	}

	private boolean hasTaskIdAndTaskName(String taskName, String taskId) {
		return taskId != null && !isStringNullOrEmpty(taskName);
	}

	private void saveTaskPriority() {
		String priorityString = getTaskPriorityFromInfoTable();
		if (priorityString == null) {
			taskInfo.setPriority(DEFAULT_PRIORITY);
		} else {
			int priorityInteger = Integer.parseInt(priorityString);
			taskInfo.setPriority(priorityInteger);
		}
	}
	

	
	private COMMAND_ERROR modifyDateAndTime(TaskInfo temp) {
		String startDate = getTaskStartDateFromInfoTable();
		String startTime = getTaskStartTimeFromInfoTable();
		String endTime = getTaskEndTimeFromInfoTable();
		String endDate = getTaskEndDateFromInfoTable();

		//Boolean Variables for condition checking
		boolean hasStartDate = (startDate != null);
		boolean hasStartTime = (startTime != null);
		boolean hasEndDate = (endDate != null);
		boolean hasEndTime = (endTime != null);
		boolean hasStartCal = (hasStartTime && hasStartDate);
		boolean hasEndCal = (hasEndTime && hasEndDate);


		if(hasStartCal && hasEndCal) {
			//save both start and end date 
			setToSpecifiedStartAndEndDateAndTime(temp, startDate, startTime,
					endTime, endDate);			

		} else if (hasStartCal && !hasEndCal) {
			Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, startTime);

			if(hasEndTime) {
				//set end date to start date (end time > start time) or 1 day after start date (end time <= start time)
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = dateAndTimeFormat.addDayToCalendar(endCal, 1);
				} 
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);

			} else if(hasEndDate) {
				//set end time to same start time (if not the same date) or 1hr after start time(same date)
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					endCal = dateAndTimeFormat.addTimeToCalendar(endCal, 1, 0);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else {
				//set end date to 1 hour after start date
				Calendar endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			}
		} else if (!hasStartCal && hasEndCal) {

			Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, endTime);

			if(hasStartTime) {
				//set start date to same end date (start time before end time) or before end date (start time >= end time)
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					startCal = dateAndTimeFormat.addDayToCalendar(startCal, -1);
				} 
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if(hasStartDate) {
				//set start time to same end time (if not the same date) or 1hr before end time(same date)
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = dateAndTimeFormat.addTimeToCalendar(startCal, -1, 0);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);	
			} else {
				//overwrite end cal
				temp.setEndDate(endCal);	
			}
		} else {
			if (hasStartDate && hasEndDate) {
				//time to 0000 if different date or start time to 2359 and end time to 2359 if same date
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
				if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
					endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
					endCal = dateAndTimeFormat.addTimeToCalendar(endCal, 1, 0);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime && hasEndTime) {
				//set to today if start < end time or set start to today and end to next day
				String today = dateAndTimeFormat.getDateToday2();
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(today, startTime);
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(today, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = dateAndTimeFormat.addDayToCalendar(endCal, 1);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime && hasEndDate) {
				//set time to 1hr block with end date
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(endDate, startTime);
				Calendar endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartDate && hasEndTime) {
				//set time to 1hr block before end time and date to start date
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(startDate, endTime);
				Calendar startCal = dateAndTimeFormat.addTimeToCalendar(endCal, -1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartDate) {
				//set time from 0000 to 2359 and save start date
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getStartTimeOfTheDay());
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(startDate, dateAndTimeFormat.getEndTimeOfTheDay());
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime) {
				//set date to today and save start date and end date to 1 hour later
				String today = dateAndTimeFormat.getDateToday2();
				Calendar startCal = dateAndTimeFormat.formatStringToCalendar(today, startTime);
				Calendar endCal = dateAndTimeFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasEndDate) {
				//set time to 2359 and save end date only
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, dateAndTimeFormat.getEndTimeOfTheDay());
				temp.setEndDate(endCal);
			} else if (hasEndTime) {
				//set date to today and save end date only
				String today = dateAndTimeFormat.getDateToday2();
				Calendar endCal = dateAndTimeFormat.formatStringToCalendar(today, endTime);
				temp.setEndDate(endCal);
			}
		}
		return COMMAND_ERROR.NIL;
	}

	private void setToSpecifiedStartAndEndDateAndTime(TaskInfo temp,
			String startDate, String startTime, String endTime, String endDate) {
		Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, startTime);
		Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, endTime);
		temp.setStartDate(startCal);
		temp.setEndDate(endCal);
	}

	private COMMAND_ERROR validateStartAndEndTime (TaskInfo temp) {
		if(temp.getStartDate() != null && temp.getEndDate() != null) {
			if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
				return COMMAND_ERROR.INVALID_DATE;
			}
		}
		return COMMAND_ERROR.NIL;
	}
	
	
	protected void convertAndValidateInformation () {
		COMMAND_ERROR taskNameError = taskNameValidity();
		if (taskNameError != null) {
			addCommandErrorToList(taskNameError);
		}
		
		String startDate = convertDateToStandardFormat(getTaskStartDateFromInfoTable());
		String endDate = convertDateToStandardFormat(getTaskEndDateFromInfoTable());
		
		if((startDate != null && !dateAndTimeFormat.isDateValid(startDate)) || (endDate != null && !dateAndTimeFormat.isDateValid(endDate))) {
			addCommandErrorToList(COMMAND_ERROR.INVALID_DATE);
		} 
		if(startDate != null){
			infoTable.put(KEYWORD_TYPE.START_DATE, startDate);
		}
		if(endDate != null){
			infoTable.put(KEYWORD_TYPE.END_DATE, endDate);
		}
		
		
		String startTime = convertTimeToStandardFormat(getTaskStartTimeFromInfoTable());
		String endTime = convertTimeToStandardFormat(getTaskEndTimeFromInfoTable());
		if(startTime != null) {
			infoTable.put(KEYWORD_TYPE.START_TIME, startTime);
		}
		if(endTime != null) {
			infoTable.put(KEYWORD_TYPE.END_TIME, endTime);
		}
	}
	
	private String convertDateToStandardFormat (String date) {
		return dateAndTimeFormat.convertStringDateToDayMonthYearFormat(date);
	}
	
	private String convertTimeToStandardFormat (String time) {
		return dateAndTimeFormat.convertStringTimeTo24HourString(time);
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
