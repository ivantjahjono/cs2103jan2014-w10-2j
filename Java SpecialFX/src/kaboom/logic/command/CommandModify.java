//@author A0073731J

package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.command.Command.COMMAND_ERROR;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;
import kaboom.storage.TaskManager;


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
		assert taskView != null;
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
		commandError = modifyDateAndTime(temp);
		if(commandError == COMMAND_ERROR.INVALID_DATE) {
			feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(feedback);
		}
		commandError = validateStartAndEndTime (temp);
		if(commandError == COMMAND_ERROR.INVALID_DATE) {
			feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
			return createResult(feedback);
		}
		determineAndSetTaskType(temp);
		//store and update in memory
		modifiedTaskInfo = temp;
		modifiedTaskInfo.setRecent(true);
		taskView.updateTask(modifiedTaskInfo, preModifiedTaskInfo);
		
		feedback = feedbackGenerator();
		addCommandToHistory ();
		return createResult(feedback);
	}

	public boolean undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+ modifiedTaskInfo.getTaskName());
		taskView.updateTask(preModifiedTaskInfo, modifiedTaskInfo);
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
	
	private COMMAND_ERROR modifyDateAndTime(TaskInfo temp) {
		//Get Start Date And Time		
		String startTime = getStartTime();
		String startDate = getStartDate();
		//Get End Date and Time		
		String endTime = getEndTime();
		String endDate = getEndDate();
		
		if(startDate == INVALID_DATE || endDate == INVALID_DATE) {
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
			Calendar startCal = dateAndTimeFormat.formatStringToCalendar(startDate, startTime);
			Calendar endCal = dateAndTimeFormat.formatStringToCalendar(endDate, endTime);
			temp.setStartDate(startCal);
			temp.setEndDate(endCal);			
			
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
		return null;
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
