package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;
import kaboom.ui.TaskView;


public class CommandModify extends Command {

	private final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	private final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to cast a spell on <%1$s>";
	private final String MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME = "Trying to manipulate air";
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
	Hashtable<KEYWORD_TYPE, String> taskInfoTable;
	boolean hasNameChanged;
	boolean hasTimeChanged;
	boolean hasPriorityChanged;
	TaskView taskView;

	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
		initialiseKeywordList();
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
		assert taskInfoTable != null;

		if(taskInfoTable == null) {
			return createResult(taskListShop.getAllCurrentTasks(), "No TaskInfoTable");
		}

		String feedback = "";
		String taskName = "";

		if (taskInfoTable.get(KEYWORD_TYPE.TASKNAME) != null) {
			taskName = taskInfoTable.get(KEYWORD_TYPE.TASKNAME);

			if (isNumeric(taskName)) {
				int index = taskView.getIndexFromView(Integer.parseInt(taskName)-1);
				preModifiedTaskInfo = taskListShop.getTaskByID(index);
			} else {
				int taskCount = taskListShop.numOfTasksWithSimilarNames(taskName);

				if (taskCount > 1) {
					Command search = new CommandSearch();
					search.storeTaskInfo(taskInfoTable);
					return search.execute();
				}
				else if (taskCount == 1) {
					preModifiedTaskInfo = taskListShop.getTaskByName(taskName);
				}
				else {
					feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
					return createResult(taskListShop.getAllCurrentTasks(), feedback);
				}
			}
		} else if (taskInfoTable.get(KEYWORD_TYPE.TASKNAME).isEmpty()) {
			feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		} else {
			feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}


		if (preModifiedTaskInfo != null) {
			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);

			//Modify task name
			if (taskInfoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME) != null) {
				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
				temp.setTaskName (taskInfoTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
				hasNameChanged = true;
			}

			//Modify priority
			String taskPriority = taskInfoTable.get(KEYWORD_TYPE.PRIORITY);
			int originalPriorityLevel = temp.getImportanceLevel();
			if(taskPriority != null) {
				int priorityLevelAfterChange = Integer.parseInt(taskPriority);
				if (originalPriorityLevel != priorityLevelAfterChange) {
					temp.setImportanceLevel (priorityLevelAfterChange);
					hasPriorityChanged = true;
				}
			}

			//Modify Date And Time			
			String startDate = null;
			String startTime = null;
			String endDate = null;
			String endTime = null;
			if(preModifiedTaskInfo.getStartDate() != null) {
				startDate = DateAndTimeFormat.getInstance().dateFromCalendarToString(preModifiedTaskInfo.getStartDate());
				startTime = DateAndTimeFormat.getInstance().timeFromCalendarToString(preModifiedTaskInfo.getStartDate());
			}
			if(preModifiedTaskInfo.getEndDate() != null) {
				endDate = DateAndTimeFormat.getInstance().dateFromCalendarToString(preModifiedTaskInfo.getEndDate());
				endTime = DateAndTimeFormat.getInstance().timeFromCalendarToString(preModifiedTaskInfo.getEndDate());
			}
			//transfer part
			if(taskInfoTable.get(KEYWORD_TYPE.START_DATE) != null) {
				startDate = taskInfoTable.get(KEYWORD_TYPE.START_DATE);
			}
			if(taskInfoTable.get(KEYWORD_TYPE.START_TIME) != null) {
				startTime = taskInfoTable.get(KEYWORD_TYPE.START_TIME);
			}
			if(taskInfoTable.get(KEYWORD_TYPE.END_DATE) != null) {
				endDate = taskInfoTable.get(KEYWORD_TYPE.END_DATE);
			}
			if(taskInfoTable.get(KEYWORD_TYPE.END_TIME) != null) {
				endTime = taskInfoTable.get(KEYWORD_TYPE.END_TIME);
			}

			try {
				Calendar startDateCal = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
				Calendar endDateCal = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);

				if(taskInfoTable.get(KEYWORD_TYPE.START_DATE) != null || taskInfoTable.get(KEYWORD_TYPE.START_TIME) != null || 
						taskInfoTable.get(KEYWORD_TYPE.END_DATE) != null || taskInfoTable.get(KEYWORD_TYPE.END_TIME) != null) {
					if(DateAndTimeFormat.getInstance().dateValidityForStartAndEndDate(startDateCal, endDateCal)){
						hasTimeChanged = true;
						temp.setStartDate (startDateCal);
						temp.setEndDate (endDateCal);
					} else {
						if (taskInfoTable.get(KEYWORD_TYPE.START_DATE) != null || taskInfoTable.get(KEYWORD_TYPE.START_TIME) != null) {
							feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_SET_STARTDATEAFTERENDDATE, taskName);
						} else {
							feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_SET_ENDDATEBOFORESTARDATE, taskName);
						}
						return createResult(taskListShop.getAllCurrentTasks(), feedback);
					}
				}
				setEndDateAndTimeToHourBlock (temp);
			} catch (Exception e) {
				feedback = e.toString();
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			}

			determineAndSetTaskType(temp);
			//store and update in memory
			taskInfo = temp;
			taskInfo.setRecent(true);
			taskListShop.updateTask(taskInfo, preModifiedTaskInfo);
			taskView.swapView(taskInfo, preModifiedTaskInfo);
		} else {
			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_TO_MODIFY,taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		feedback = feedbackGenerator();
		return createResult(taskListShop.getAllCurrentTasks(), feedback);

		//		try {
		//	
		//			//get name of TaskInfo that user wants to modify
		//			taskName = preModifiedTaskInfo.getTaskName();
		//			
		//			if(taskName.isEmpty()) {
		//				feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
		//				return createResult(taskListShop.getAllCurrentTasks(), feedback);
		//			}
		//			
		//			//get TaskInfo that user wants to modify;
		//			preModifiedTaskInfo = taskListShop.getTaskByName(taskName);
		//			
		//			//store TaskInfo to modify into temp taskinfo
		//			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
		//			//transfer all the new information over
		//			if (taskInfo.getTaskName() != null) {
		//				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
		//				temp.setTaskName (taskInfo.getTaskName());
		//				hasNameChanged = true;
		//			}
		//			if (taskInfo.getImportanceLevel() != preModifiedTaskInfo.getImportanceLevel()) {
		//				temp.setImportanceLevel (taskInfo.getImportanceLevel());
		//				hasPriorityChanged = true;
		//			}
		//
		//			Calendar startDate = preModifiedTaskInfo.getStartDate();
		//			Calendar endDate = preModifiedTaskInfo.getEndDate();	
		//			if(taskInfo.getStartDate() != null) {
		//				startDate = taskInfo.getStartDate();
		//			}
		//			if(taskInfo.getEndDate() != null) {
		//				endDate = taskInfo.getEndDate();
		//			}
		//			
		//			if(DateAndTimeFormat.getInstance().dateValidityForStartAndEndDate(startDate, endDate)){
		//				hasTimeChanged = true;
		//				temp.setStartDate (startDate);
		//				temp.setEndDate (endDate);
		//			} else {
		//				feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_INVALID_STARTANDEND_TIME, taskName);
		//				return createResult(taskListShop.getAllCurrentTasks(), feedback);
		//			}
		//			
		//			//1hr block error
		//			setEndDateAndTimeToHourBlock (temp);
		//			determineAndSetTaskType(temp);
		//			
		//			//store and update in memory
		//			taskInfo = temp;
		//			taskListShop.updateTask(taskInfo, preModifiedTaskInfo);
		//		} catch (Exception e) {
		//			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL, taskName);
		//			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		//		}
		//		feedback = feedbackGenerator();
		//		return createResult(taskListShop.getAllCurrentTasks(), feedback);
	}

	public boolean undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+taskInfo.getTaskName());
		TaskListShop.getInstance().updateTask(preModifiedTaskInfo, taskInfo);
		taskView.swapView(preModifiedTaskInfo, taskInfo);
		return true;
	}

	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.PRIORITY);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.MODIFIED_TASKNAME);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
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
		taskInfoTable = (Hashtable<KEYWORD_TYPE, String>) infoHashes.clone();
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
			feedback += String.format(MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE, taskInfo.getTaskName());
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

	private boolean isNumeric(String taskName) {
		return taskName.matches("\\d{1,4}");
	}
}
