package kaboom.logic.command;

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
	private final String MESSAGE_COMMAND_ADD_FAIL_NO_NAME = "Enter a task name please :'(";
	private final String MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE = "Wow! How did the task end before it even started? 0.0";
	private final String MESSAGE_COMMAND_ADD_FAIL_INVALID_DATE = "Invalid date... Noob";

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

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String commandFeedback = "";
		
		
		taskInfo = new TaskInfo();
		
		
		//End if no task name
		if (infoTable.get(KEYWORD_TYPE.TASKNAME) == null) {
			commandFeedback = MESSAGE_COMMAND_ADD_FAIL_NO_NAME;
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		} else {
			taskInfo.setTaskName(infoTable.get(KEYWORD_TYPE.TASKNAME));
		}
		
		//Default priority = 1
		if (infoTable.get(KEYWORD_TYPE.PRIORITY) == null) {
			taskInfo.setImportanceLevel(1);
		} else {
			taskInfo.setImportanceLevel(Integer.parseInt(infoTable.get(KEYWORD_TYPE.PRIORITY)));
		}
		
		
		/*
		 * If only date is specified: Set calendar to date and default time of 0000 (12am)
		 * If only time is specified: Set calendar to time and default date to current day
		 * If both are specified: Set calendar to respective date and time
		 * If both are null: return null;
		 * If any are invalid: cancel add and return invalid command
		 */
		//Start date and time
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
				if(!datFormat.isDateValid(startDate)) {
					commandFeedback = MESSAGE_COMMAND_ADD_FAIL_INVALID_DATE;
					return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
				} else {
					taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
				}
			} else {
				startDate = datFormat.getTodayDate();
				taskInfo.setStartDate(datFormat.formatStringToCalendar(startDate, startTime));
			}
		}
		//end date end time
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
			
			if(hasEndDate) {
				if(!datFormat.isDateValid(endDate)) {
					commandFeedback = MESSAGE_COMMAND_ADD_FAIL_INVALID_DATE;
					return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
				} else {
					endTime = datFormat.convertStringTimeTo24HourString(endTime);
					taskInfo.setStartDate(datFormat.formatStringToCalendar(endDate, endTime));
				}
			} else {
				endDate = datFormat.getTodayDate();
				taskInfo.setStartDate(datFormat.formatStringToCalendar(endDate, endTime));
			}
		}
		
		
		//compare start and end (TASKTYPE)
		boolean hasStartCal = false;
		boolean hasEndCal = false;
		if(taskInfo.getStartDate() != null) {
			hasStartCal = true;
		}
		if(taskInfo.getEndDate() != null) {
			hasEndCal = true;
		}
		
		if(hasStartCal && hasEndCal) {
			if(!datFormat.dateValidityForStartAndEndDate(taskInfo.getStartDate(),taskInfo.getEndDate())) {
				commandFeedback = MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE;
				return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
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
		
		taskInfo.setRecent(true);
		
		if (taskListShop.addTaskToList(taskInfo)) {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
		}
		
		
//		if (taskInfo!=null && !taskInfo.getTaskName().isEmpty()) {
//			if(DateAndTimeFormat.getInstance().dateValidityForStartAndEndDate(taskInfo.getStartDate(),taskInfo.getEndDate())) {
//				taskInfo.setRecent(true);
//				
//				if (taskListShop.addTaskToList(taskInfo)) {
//					commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
//				} else {
//					commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
//				}
//			} else {
//				commandFeedback = MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE;
//			}
//		}
//		else {
//			commandFeedback = MESSAGE_COMMAND_ADD_FAIL_NO_NAME;
//		}
		
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
	
//	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
//		taskInfo = new TaskInfo();
//		saveTaskName(infoHashes,taskInfo);
//		saveTaskPriority(infoHashes,taskInfo);
//		saveTaskStartDateAndTime(infoHashes,taskInfo);
//		saveTaskEndDateAndTime(infoHashes,taskInfo);
//		setEndDateAndTimeToHourBlock (taskInfo);
//		determineAndSetTaskType(taskInfo);
//	}
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}
		
		return true;
	}
	
	
}
