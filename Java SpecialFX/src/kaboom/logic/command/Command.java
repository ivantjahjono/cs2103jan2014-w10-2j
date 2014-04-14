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
import kaboom.storage.TaskManager;


public class Command {
	protected final String MESSAGE_COMMAND_FAIL_INVALID_DATE = "Oops! Did you check the calendar? The date you've entered is invalid";
	protected final String MESSAGE_COMMAND_FAIL_NO_SUCH_TASK = "Oops! No such task exist";
	protected final String MESSAGE_COMMAND_FAIL_NO_TASK_NAME = "Enter a taskname or task id, please ?";
	protected final String MESSAGE_COMMAND_FAIL_INVALID_TASKNAME = "Oops! Invalid taskname??";
	protected final String MESSAGE_COMMAND_FAIL_INVALID_TASKID = "Oops! Invalid ID??";
	protected final String MESSAGE_COMMAND_FAIL_INVALID_STARTDATE_AFTER_ENDDATE = "Oops! Please schedule to another time";
	protected final String MESSAGE_COMMAND_INVALID = "Please enter a valid command. Type <help> for info.";
	
	protected COMMAND_TYPE commandType;
	protected TextParser textParser;
	protected KEYWORD_TYPE[] keywordList;
	Hashtable<KEYWORD_TYPE, String> infoTable;
	protected TaskManager taskManager;
	protected Vector <COMMAND_ERROR> commandErrorList;
	protected DateAndTimeFormat dateAndTimeFormat;
	
	protected enum COMMAND_ERROR{
		CLASH, TASK_DOES_NOT_EXIST, NO_TASK_NAME, INVALID_DATE, INVALID_TASKNAME , INVALID_TASKID, INVALID_STARTENDDATE
	}
	
	public Command () {
		commandType = COMMAND_TYPE.INVALID;
		textParser = TextParser.getInstance();
		taskManager = TaskManager.getInstance();
		infoTable = new Hashtable<KEYWORD_TYPE, String>();
		keywordList = new KEYWORD_TYPE[0];
		dateAndTimeFormat = DateAndTimeFormat.getInstance();
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
		commandErrorList = new Vector <COMMAND_ERROR>();
		infoTable = textParser.extractList(userInputSentence, keywordList);
		convertInformationToStandardFormat ();
	}
	
	public void initialiseCommandInfoTable(Hashtable<KEYWORD_TYPE, String> infoTable) {
		commandErrorList = new Vector <COMMAND_ERROR>();
		this.infoTable = infoTable;
		convertInformationToStandardFormat ();
	}
	
	protected void convertInformationToStandardFormat () {
		Enumeration<KEYWORD_TYPE> elementItr =  infoTable.keys();
		
		while(elementItr.hasMoreElements()) {
			KEYWORD_TYPE currentKeyword = elementItr.nextElement();
			switch(currentKeyword) {
			case START_TIME:
			case END_TIME:
				String time = infoTable.get(currentKeyword);
				time = dateAndTimeFormat.convertStringTimeTo24HourString(time);
				infoTable.put(currentKeyword, time);
				break;
			case START_DATE:
			case END_DATE:
			case DATE:
				String date = infoTable.get(currentKeyword);
				date = dateAndTimeFormat.convertStringDateToDayMonthYearFormat(date);
				infoTable.put(currentKeyword, date);
				break;
			default:
				break;
			}
		}
	}
	
	protected void addCommandErrorToList (COMMAND_ERROR commandError) {
		commandErrorList.add(commandError);
	}
	
	protected void addCommandToHistory () {
		taskManager.addToHistory(this);;
	}
	
	protected int numOfTasksWithSimilarNames(String name) {
		int count = 0;
		Vector<TaskInfo> currentViewList = taskManager.getCurrentView();
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

	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		if (info.equals("")) {
			return true;
		}
		addThisStringToFormatList(info, indexList, KEYWORD_TYPE.INVALID);
		return false;
	}
	
	protected Hashtable<KEYWORD_TYPE, String> updateFormatList (String info, Vector<FormatIdentify> indexList) {
		getCommandString(info, indexList);
		info = textParser.removeFirstWord(info);
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = textParser.extractList(info, keywordList);	
		return taskInformationTable;
	}
	
	protected void updateFormatListBasedOnHashtable(Vector<FormatIdentify> indexList, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		String stringDate;
		
		Enumeration<KEYWORD_TYPE> elementItr =  taskInformationTable.keys();
		
		while (elementItr.hasMoreElements()) {
			KEYWORD_TYPE currentKeyword = elementItr.nextElement();
			KEYWORD_TYPE resultKeyword = currentKeyword;
			
			switch (currentKeyword) {					
				case START_DATE:
				case END_DATE:
					stringDate = taskInformationTable.get(currentKeyword);
					if (!DateAndTimeFormat.getInstance().isDateValid(stringDate)) {
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
		case INVALID_TASKNAME:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_TASKNAME);
		case INVALID_TASKID:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_TASKID);
		case INVALID_STARTENDDATE:
			return createResult(MESSAGE_COMMAND_FAIL_INVALID_STARTDATE_AFTER_ENDDATE);
		default:
			return null;
		}
	}
	
	protected COMMAND_ERROR errorDetectionForInvalidTaskNameAndId() {	
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		if(taskId != null && !isStringNullOrEmpty(taskName)) {
			return COMMAND_ERROR.INVALID_TASKNAME;
		} else if(taskId != null) {
			if (!isTaskIdValid()) {
				return COMMAND_ERROR.INVALID_TASKID;
			} else {
				return null;
			}
		} else {
			if (isStringNullOrEmpty(taskName)) {
				return COMMAND_ERROR.NO_TASK_NAME;
			} else {
				return taskExistenceOrClashDetection(taskName);
			}
		}
	}

	protected boolean isStringNullOrEmpty(String string) {
		return string == null || string.isEmpty();
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
		String taskId = infoTable.get(KEYWORD_TYPE.TASKID);
		if (taskId != null) {
			int taskIdInteger = Integer.parseInt(taskId);
			return taskManager.getTaskFromViewByID(taskIdInteger-1);
		}
		return null;
	}
	
	protected TaskInfo getTaskWithTaskName() {
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		if (taskName != null && !taskName.isEmpty()) {
			return taskManager.getTaskFromViewByName(taskName);
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
	
	protected void determineAndSetDateAndTime(TaskInfo task, String startDate, String startTime, String endDate, String endTime) {
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
			saveStartAndEndCalendars (task, startCal, endCal);		

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
			
			saveStartAndEndCalendars (task, startCal, endCal);	
			
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
			saveStartAndEndCalendars (task, startCal, endCal);	
			
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
			saveStartAndEndCalendars (task, startCal, endCal);	
		}
	}
	
	protected void saveStartAndEndCalendars (TaskInfo task, Calendar start, Calendar end) {
		task.setStartDate(start);
		task.setEndDate(end);
	}
	
	protected DISPLAY_STATE determineDisplayState(TaskInfo task) {
		DISPLAY_STATE stateToSet;
		if (task.getTaskType() == TASK_TYPE.FLOATING) {
			stateToSet = DISPLAY_STATE.TIMELESS;
		} else if (TaskInfo.isTaskToday(task)) {
			stateToSet =  DISPLAY_STATE.TODAY;
		} else if (TaskInfo.isFutureTask(task)){
			stateToSet =  DISPLAY_STATE.FUTURE;
		} else {
			stateToSet =  DISPLAY_STATE.EXPIRED;
		}
		return stateToSet;
	}
	
	protected Result validateStartAndEndTime (TaskInfo temp) {
		if(temp.getStartDate() != null && temp.getEndDate() != null) {
			if(!dateAndTimeFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
				return commandErrorHandler(COMMAND_ERROR.INVALID_STARTENDDATE);
			}
		}
		return null;
	}
}
