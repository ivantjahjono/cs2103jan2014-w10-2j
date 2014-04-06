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
import kaboom.storage.TaskListShop;
import kaboom.ui.TaskView;


public class CommandModify extends Command {

	private final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	private final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to cast a spell on <%1$s>";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME = "Master Wugui says: 'My time has come to find the task name'";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_SUCH_TASK = "Trying to manipulate air";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_TO_MODIFY = "<%1$s> does not exist...";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_CHANGE = "Nothing happened...";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_SET_ENDDATEBOFORESTARDATE = "Trying to let <%1$s> end before it even started...";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_SET_STARTDATEAFTERENDDATE = "Trying to let <%1$s> start after it ended...";

	private final String MESSAGE_TASK_NAME = "<%1$s> has";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE = " evolved into <%1$s>";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE = " manipulated time";
	private final String MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE = " consulted the stars";
	private final String MESSAGE_COMMAND_MODIFY_CONNECTOR = ",";

	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	TaskInfo taskInfoToModify;
//	Hashtable<KEYWORD_TYPE, String> taskInfoTable;
	boolean hasNameChanged;
	boolean hasTimeChanged;
	boolean hasPriorityChanged;
	TaskView taskView;

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
		taskView = TaskView.getInstance();
	}

	/*
	 * Bug: Will overwrite prev dates if input date is invalid
	 * 
	 */
	public Result execute() {
		assert taskListShop != null;
		assert infoTable != null;

		if(infoTable == null) {
			return createResult(taskListShop.getAllCurrentTasks(), "No TaskInfoTable");
		}

		String feedback = "";
		String taskName = "";

		if (infoTable.get(KEYWORD_TYPE.TASKNAME) != null) {
			taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
			
			 if (infoTable.get(KEYWORD_TYPE.TASKNAME).isEmpty()) {
				feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			}
			 
			if (isNumeric(taskName)) {
				int index = taskView.getIndexFromView(Integer.parseInt(taskName)-1);
				preModifiedTaskInfo = taskListShop.getTaskByID(index);
			} else {
				int taskCount = taskListShop.numOfTasksWithSimilarNames(taskName);

				if (taskCount > 1) {
					Command search = new CommandSearch();
					search.storeTaskInfo(infoTable);
					return search.execute();
				}
				else if (taskCount == 1) {
					preModifiedTaskInfo = taskListShop.getTaskByName(taskName);
				}
				else {
					feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_SUCH_TASK;
					return createResult(taskListShop.getAllCurrentTasks(), feedback);
				}
			}
		}  else {
			feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}


		if (preModifiedTaskInfo != null) {
			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);

			//Modify task name
			if (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME) != null) {
				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
				temp.setTaskName (infoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
				hasNameChanged = true;
			}

			//Modify priority
			String taskPriority = infoTable.get(KEYWORD_TYPE.PRIORITY);
			int originalPriorityLevel = temp.getImportanceLevel();
			if(taskPriority != null) {
				int priorityLevelAfterChange = Integer.parseInt(taskPriority);
				if (originalPriorityLevel != priorityLevelAfterChange) {
					temp.setImportanceLevel (priorityLevelAfterChange);
					hasPriorityChanged = true;
				}
			}

			
				
			//Modify Start Date And Time			
			DateAndTimeFormat datFormat = DateAndTimeFormat.getInstance();
			String startDate = null;
			String startTime = null;
			String newStartDate = infoTable.get(KEYWORD_TYPE.START_DATE);
			String newStartTime = infoTable.get(KEYWORD_TYPE.START_TIME);
			boolean hasStartDate = false;
			boolean hasStartTime = false;
			
			//Previous start date and time information
			if(preModifiedTaskInfo.getStartDate() != null) {
				startDate = datFormat.dateFromCalendarToString(preModifiedTaskInfo.getStartDate());
				startTime = datFormat.timeFromCalendarToString(preModifiedTaskInfo.getStartDate());
				hasStartDate = true;
				hasStartTime = true;
			}
			
			//Overwrite old information
			if(newStartDate != null) {
				if(datFormat.isDateValid(newStartDate)) {
					startDate = newStartDate;
					hasStartDate = true;
				} else {
					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
					return createResult(taskListShop.getAllCurrentTasks(), feedback);
				}
			}
			if(newStartTime != null) {
				startTime = datFormat.convertStringTimeTo24HourString(newStartTime);
				hasStartTime = true;
			}

			
			
			
			//Modify EndDate and Time
			String endDate = null;
			String endTime = null;
			String newEndDate = infoTable.get(KEYWORD_TYPE.END_DATE);
			String newEndTime = infoTable.get(KEYWORD_TYPE.END_TIME);
			boolean hasEndDate = false;
			boolean hasEndTime = false;

			if(preModifiedTaskInfo.getEndDate() != null) {
				endDate = datFormat.dateFromCalendarToString(preModifiedTaskInfo.getEndDate());
				endTime = datFormat.timeFromCalendarToString(preModifiedTaskInfo.getEndDate());
				hasEndDate = true;
				hasEndTime = true;
			}
			
			//transfer part
			if(newEndDate != null) {
				if(datFormat.isDateValid(newEndDate)) {
					endDate = newEndDate;
					hasEndDate = true;
				} else {
					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
					return createResult(taskListShop.getAllCurrentTasks(), feedback);
				}
			}
			if(newEndTime != null) {
				endTime = datFormat.convertStringTimeTo24HourString(newEndTime);
				hasEndTime = true;
			}

			
			boolean hasStartCal = false;
			boolean hasEndCal = false;
			if(hasStartTime && hasStartDate) {
				hasStartCal = true;
			}	
			if(hasEndTime && hasEndDate) {
				hasEndCal = true;
			}
			
			if(hasStartCal && hasEndCal) {
				
				//save both start and end date 
				Calendar startCal = datFormat.formatStringToCalendar(startDate, startTime);
				Calendar endCal = datFormat.formatStringToCalendar(endDate, endTime);
				temp.setStartDate(startCal);
				temp.setEndDate(endCal);			
				
			} else if (hasStartCal && !hasEndCal) {
				
				Calendar startCal = datFormat.formatStringToCalendar(startDate, startTime);
				
				if(hasEndTime) {
					//set end date to start date (end time > start time) or after start date (end time <= start time)
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
					//time to 0000 if different date or start time to current time and end time to 1hr later if same date
					Calendar startCal = datFormat.formatStringToCalendar(startDate, "0000");
					Calendar endCal = datFormat.formatStringToCalendar(endDate, "0000");
					if(!datFormat.isFirstDateBeforeSecondDate(startCal, endCal)) {
						String currentTime = datFormat.getCurrentTime();
						startCal = datFormat.formatStringToCalendar(startDate, currentTime);
						endCal = datFormat.formatStringToCalendar(endDate, currentTime);
						endCal = datFormat.addTimeToCalendar(endCal, 1, 0);
					}
					temp.setStartDate(startCal);
					temp.setEndDate(endCal);
				} else if (hasStartTime && hasEndTime) {
					//set to today if start < end time or set start to today and end to next day
					String today = datFormat.getDateToday();
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
 					String today = datFormat.getDateToday();
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
					String today = datFormat.getDateToday();
					Calendar endCal = datFormat.formatStringToCalendar(today, endTime);
					temp.setEndDate(endCal);
				}
			}
			
			
			//validate start end time if both are not null
			if(temp.getStartDate() != null && temp.getEndDate() != null) {
				if(!datFormat.isFirstDateBeforeSecondDate(temp.getStartDate(), temp.getEndDate())) {
					feedback = MESSAGE_COMMAND_FAIL_INVALID_DATE;
					return createResult(taskListShop.getAllCurrentTasks(), feedback);
				}
			}
			
			//set task type
			if (temp.getStartDate() != null && temp.getEndDate() !=null) {
				temp.setTaskType(TASK_TYPE.TIMED);
			} else if(temp.getStartDate() == null && temp.getEndDate() == null) {
				temp.setTaskType(TASK_TYPE.FLOATING);
			} else {
				temp.setTaskType(TASK_TYPE.DEADLINE);
			}
			
			//store and update in memory
			taskInfo = temp;
			taskInfo.setRecent(true);
			taskListShop.updateTask(taskInfo, preModifiedTaskInfo);
			taskView.swapView(taskInfo, preModifiedTaskInfo);
		} else {
			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_TO_MODIFY,taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

//		feedback = feedbackGenerator();
		return createResult(taskListShop.getAllCurrentTasks(), feedback);
	}

	public boolean undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+taskInfo.getTaskName());
		TaskListShop.getInstance().updateTask(preModifiedTaskInfo, taskInfo);
		taskView.swapView(preModifiedTaskInfo, taskInfo);
		return true;
	}

	@SuppressWarnings("unchecked")
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		//		taskInfo = new TaskInfo();
		//		preModifiedTaskInfo = new TaskInfo();
		//		
		//		saveTaskName(infoHashes, preModifiedTaskInfo);
		//		saveModifiedTaskName(infoHashes, taskInfo);
		//		saveTaskPriority(infoHashes, taskInfo);
		//		saveTaskStartDateAndTime(infoHashes, taskInfo);
		//		saveTaskEndDateAndTime(infoHashes, taskInfo);
//		taskInfoTable = (Hashtable<KEYWORD_TYPE, String>) infoHashes.clone();
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

//	private String feedbackGenerator() {
//		String feedback = String.format(MESSAGE_TASK_NAME, preModifiedTaskInfo.getTaskName());
//		int countNumOfModifications = 0;
//		if(hasNameChanged) {
//			countNumOfModifications++;
//			feedback += String.format(MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE, taskInfo.getTaskName());
//		}
//		if(hasTimeChanged) {
//			if (countNumOfModifications > 0) {
//				feedback += MESSAGE_COMMAND_MODIFY_CONNECTOR;
//			}
//			countNumOfModifications++;
//			feedback += MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE;
//		}
//		if(hasPriorityChanged) {
//			if (countNumOfModifications > 0) {
//				feedback += MESSAGE_COMMAND_MODIFY_CONNECTOR;
//			}
//			feedback += MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE;
//		}
//
//		if(!hasNameChanged && !hasTimeChanged && !hasPriorityChanged) {
//			feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_CHANGE;
//		}
//
//		return feedback;
//	}

	private boolean isNumeric(String taskName) {
		return taskName.matches("\\d{1,4}");
	}
}
