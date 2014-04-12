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
	private final String MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE = "Oops! Task cannot end before it even started";

	DateAndTimeFormat datFormat;
	TaskInfo taskInfo;

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
			return createResult(commandFeedback);
		}

		saveTaskPriority();

		//		commandFeedback = saveStartDateAndTime();
		//		if(!commandFeedback.isEmpty()) {
		//			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		//		}
		//		
		//		commandFeedback = saveEndDateAndTime();
		//		if(!commandFeedback.isEmpty()) {
		//			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		//		}
		//		
		//		commandFeedback = startAndEndTimeValidityAndSetTaskType ();
		//		if(!commandFeedback.isEmpty()) {
		//			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		//		}

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

		setTaskType(taskInfo);
		taskInfo.setRecent(true);

		DISPLAY_STATE stateToSet = DISPLAY_STATE.INVALID;
		if (taskView.addTask(taskInfo)) {
			addCommandToHistory ();
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());

			if (taskInfo.getTaskType() == TASK_TYPE.FLOATING) {
				stateToSet = DISPLAY_STATE.TIMELESS;
			} else if (taskListShop.isTaskToday(taskInfo)) {
				stateToSet =  DISPLAY_STATE.TODAY;
			} else if (taskListShop.isFutureTask(taskInfo)){
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
			taskInfo.setPriority(1);
		} else {
			taskInfo.setPriority(Integer.parseInt(infoTable.get(KEYWORD_TYPE.PRIORITY)));
		}
	}

	private COMMAND_ERROR modifyDateAndTime(TaskInfo temp) {
		String startDate = datFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.START_DATE));
		String startTime = datFormat.convertStringTimeTo24HourString(infoTable.get(KEYWORD_TYPE.START_TIME));
		String endDate = datFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.END_DATE));
		String endTime = datFormat.convertStringTimeTo24HourString(infoTable.get(KEYWORD_TYPE.END_TIME));

		if((startDate != null && !datFormat.isDateValid(startDate)) || (endDate != null && !datFormat.isDateValid(endDate))) {
			return COMMAND_ERROR.INVALID_DATE;
		}

		//Boolean Variables for condition checking
		boolean hasStartDate = (startDate != null);
		boolean hasStartTime = (startTime != null);
		boolean hasEndDate = (endDate != null);
		boolean hasEndTime = (endTime != null);
		boolean hasStartCal = (hasStartTime && hasStartDate);
		boolean hasEndCal = (hasEndTime && hasEndDate);


		if(hasStartCal && hasEndCal) {
			//save both start and end date 
			Calendar startCal = datFormat.formatStringToCalendar(startDate, startTime);
			Calendar endCal = datFormat.formatStringToCalendar(endDate, endTime);
			temp.setStartDate(startCal);
			temp.setEndDate(endCal);			

		} else if (hasStartCal && !hasEndCal) {
			Calendar startCal = datFormat.formatStringToCalendar(startDate, startTime);

			if(hasEndTime) {
				//set end date to start date (end time > start time) or 1 day after start date (end time <= start time)
				Calendar endCal = datFormat.formatStringToCalendar(startDate, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = datFormat.addDayToCalendar(endCal, 1);
				} 
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);

			} else if(hasEndDate) {
				//set end time to same start time (if not the same date) or 1hr after start time(same date)
				Calendar endCal = datFormat.formatStringToCalendar(endDate, startTime);
				if(!datFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					endCal = datFormat.addTimeToCalendar(endCal, 1, 0);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else {
				//set end date to 1 hour after start date
				Calendar endCal = datFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			}
		} else if (!hasStartCal && hasEndCal) {

			Calendar endCal = datFormat.formatStringToCalendar(endDate, endTime);

			if(hasStartTime) {
				//set start date to same end date (start time before end time) or before end date (start time >= end time)
				Calendar startCal = datFormat.formatStringToCalendar(endDate, startTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					startCal = datFormat.addDayToCalendar(startCal, -1);
				} 
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if(hasStartDate) {
				//set start time to same end time (if not the same date) or 1hr before end time(same date)
				Calendar startCal = datFormat.formatStringToCalendar(startDate, endTime);
				if(!datFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = datFormat.addTimeToCalendar(startCal, -1, 0);
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
				Calendar startCal = datFormat.formatStringToCalendar(startDate, "0000");
				Calendar endCal = datFormat.formatStringToCalendar(endDate, "2359");
				if(!datFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
					startCal = datFormat.formatStringToCalendar(startDate, "0000");
					endCal = datFormat.formatStringToCalendar(endDate, "2359");
					endCal = datFormat.addTimeToCalendar(endCal, 1, 0);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime && hasEndTime) {
				//set to today if start < end time or set start to today and end to next day
				String today = datFormat.getDateToday2();
				Calendar startCal = datFormat.formatStringToCalendar(today, startTime);
				Calendar endCal = datFormat.formatStringToCalendar(today, endTime);
				if(Integer.parseInt(endTime) <= Integer.parseInt(startTime)) {
					endCal = datFormat.addDayToCalendar(endCal, 1);
				}
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime && hasEndDate) {
				//set time to 1hr block with end date
				Calendar startCal = datFormat.formatStringToCalendar(endDate, startTime);
				Calendar endCal = datFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartDate && hasEndTime) {
				//set time to 1hr block before end time and date to start date
				Calendar endCal = datFormat.formatStringToCalendar(startDate, endTime);
				Calendar startCal = datFormat.addTimeToCalendar(endCal, -1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartDate) {
				//set time from 0000 to 2359 and save start date
				Calendar startCal = datFormat.formatStringToCalendar(startDate, "0000");
				Calendar endCal = datFormat.formatStringToCalendar(startDate, "2359");
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasStartTime) {
				//set date to today and save start date and end date to 1 hour later
				String today = datFormat.getDateToday2();
				Calendar startCal = datFormat.formatStringToCalendar(today, startTime);
				Calendar endCal = datFormat.addTimeToCalendar(startCal, 1, 0);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);
			} else if (hasEndDate) {
				//set time to 2359 and save end date only
				Calendar endCal = datFormat.formatStringToCalendar(endDate, "2359");
				temp.setEndDate(endCal);
			} else if (hasEndTime) {
				//set date to today and save end date only
				String today = datFormat.getDateToday2();
				Calendar endCal = datFormat.formatStringToCalendar(today, endTime);
				temp.setEndDate(endCal);
			}
		}
		return COMMAND_ERROR.NIL;
	}

	private COMMAND_ERROR validateStartAndEndTime (TaskInfo temp) {
		if(temp.getStartDate() != null && temp.getEndDate() != null) {
			if(!datFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
				return COMMAND_ERROR.INVALID_DATE;
			}
		}
		return COMMAND_ERROR.NIL;
	}

	private void setTaskType (TaskInfo temp) {
		if (temp.getStartDate() != null && temp.getEndDate() !=null) {
			temp.setTaskType(TASK_TYPE.TIMED);
		} else if(temp.getStartDate() == null && temp.getEndDate() == null) {
			temp.setTaskType(TASK_TYPE.FLOATING);
		} else {
			temp.setTaskType(TASK_TYPE.DEADLINE);
		}
	}

}
