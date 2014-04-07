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
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
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
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		commandError = validateStartAndEndTime (taskInfo);
		if(commandError != COMMAND_ERROR.NIL) {
			commandFeedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}
		
		setTaskType(taskInfo);
		
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
	
//	private String saveStartDateAndTime() {
//		String feedback = "";
//		String startDate = infoTable.get(KEYWORD_TYPE.START_DATE);
//		String startTime = infoTable.get(KEYWORD_TYPE.START_TIME);
//		boolean hasStartDate = false;
//		boolean hasStartTime = false;
//		
//		if(startDate != null) {
//			hasStartDate = true;
//		}
//		if(startTime != null) {
//			hasStartTime = true;
//		}
//		if(!hasStartDate && !hasStartTime) {
//			taskInfo.setStartDate(null);
//		} else {
//			if(!hasStartTime) {
//				startTime = "0000";
//			}
//			startTime = datFormat.convertStringTimeTo24HourString(startTime);
//			
//			if(hasStartDate) {
//				//startDate = datFormat.convertStringDateToDayMonthYearFormat(startDate); <<<<<<<<<<<<<<<<<
//				if(!datFormat.isDateValid(startDate)) {
//					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
//				} else {
//					taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
//				}
//			} else {
//				startDate = datFormat.getTodayDate();
//				taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
//			}
//		}
//		return feedback;
//	}
//	
//	private String saveEndDateAndTime() {
//		String feedback = "";
//		String endDate = infoTable.get(KEYWORD_TYPE.END_DATE);
//		String endTime = infoTable.get(KEYWORD_TYPE.END_TIME);
//		boolean hasEndDate = false;
//		boolean hasEndTime = false;
//		
//		if(endDate != null) {
//			hasEndDate = true;
//		}
//		if(endTime != null) {
//			hasEndTime = true;
//		}
//		if(!hasEndDate && !hasEndTime) {
//			taskInfo.setEndDate(null);
//		} else {
//			if(!hasEndTime) {
//				endTime = "0000";
//			}
//			endTime = datFormat.convertStringTimeTo24HourString(endTime);
//			
//			if(hasEndDate) {
//				endDate = datFormat.convertStringDateToDayMonthYearFormat(endDate);
//				if(!datFormat.isDateValid(endDate)) {
//					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
//				} else {
//					//hasEndDate noEndTime hasStartDate -> Append same time(startTime)
//					if(taskInfo.getStartDate() != null && !hasEndTime) {
//						endTime = datFormat.timeFromCalendarToString(taskInfo.getStartDate());
//					}
//					
//					taskInfo.setEndDate(datFormat.formatStringToCalendar(endDate, endTime));
//				}
//			} else {
//				//hasEndTime noEndDate hasStartDate -> check if time is before start time set 1 day later else set same day				
//				if(taskInfo.getStartDate() != null) {
//					if(Integer.parseInt(endTime) < Integer.parseInt(datFormat.timeFromCalendarToString(taskInfo.getStartDate()))) {
//						Calendar endDateCal = datFormat.addDayToCalendar(taskInfo.getStartDate(), 1);
//						datFormat.convertStringTimeToCalendar(endDateCal, endTime);
//						taskInfo.setEndDate(endDateCal);
//					}
//				} else {
//					endDate = datFormat.getTodayDate();
//					taskInfo.setEndDate(datFormat.formatStringToCalendar(endDate, endTime));
//				}
//			}
//		}
//		return feedback;
//	}
//	
//	private String startAndEndTimeValidityAndSetTaskType () {
//		String feedback = "";
//		
//		boolean hasStartCal = false;
//		boolean hasEndCal = false;
//		if(taskInfo.getStartDate() != null) {
//			hasStartCal = true;
//		}
//		if(taskInfo.getEndDate() != null) {
//			hasEndCal = true;
//		}
//		
//		if(hasStartCal && hasEndCal) {
//			if(!datFormat.isFirstDateBeforeSecondDate(taskInfo.getStartDate(),taskInfo.getEndDate())) {
//				feedback = MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE;
//			} else {
//				taskInfo.setTaskType(TASK_TYPE.TIMED);
//			}
//		} else if(hasStartCal && !hasEndCal) {
//			taskInfo.setEndDate(datFormat.addTimeToCalendar(taskInfo.getStartDate(), 1, 0));
//			taskInfo.setTaskType(TASK_TYPE.TIMED);
//		} else if(!hasStartCal && hasEndCal) {
//			taskInfo.setTaskType(TASK_TYPE.DEADLINE);
//		} else {
//			taskInfo.setTaskType(TASK_TYPE.FLOATING);
//		}
//		return feedback;
//	}
	
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
				//time to 0000 if different date or start time to 0000 and end time to 2359 if same date
				Calendar startCal = datFormat.formatStringToCalendar(startDate, "0000");
				Calendar endCal = datFormat.formatStringToCalendar(endDate, "0000");
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
				//set time to 0000 and save start date and end date to 1 day later
				Calendar startCal = datFormat.formatStringToCalendar(startDate, "0000");
				Calendar endCal = datFormat.addDayToCalendar(startCal, 1);
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
				//set time to 0000 and save end date only
				Calendar endCal = datFormat.formatStringToCalendar(endDate, "0000");
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
