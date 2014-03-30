package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;


public class CommandModify extends Command {
	
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	private static final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to cast a spell on <%1$s>";
	private static final String MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME = "Trying to manipulate air";
	private static final String MESSAGE_COMMAND_MODIFY_FAIL_NO_CHANGE = "Nothing happened...";
	private static final String MESSAGE_COMMAND_MODIFY_FAIL_INVALID_STARTANDEND_TIME = "Trying to set <%1$s> to end before it even started...";
	
	private static final String MESSAGE_TASK_NAME = "<%1$s>";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE = " has evolved into <%1$s>";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE = " has manipulated time";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE = " is seeing stars";
	private static final String MESSAGE_COMMAND_MODIFY_CONNECTOR = ",";
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	boolean hasNameChanged;
	boolean hasTimeChanged;
	boolean hasPriorityChanged;
	
	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
		initialiseKeywordList();
		hasNameChanged = false;
		hasTimeChanged = false;
		hasPriorityChanged = false;
	}

	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		String taskName = "";
		
		try {
			//get name of TaskInfo that user wants to modify
			taskName = preModifiedTaskInfo.getTaskName();
			
			if(taskName.isEmpty()) {
				feedback = MESSAGE_COMMAND_MODIFY_FAIL_NO_TASK_NAME;
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			}
			
			//get TaskInfo that user wants to modify;
			preModifiedTaskInfo = taskListShop.getTaskByName(taskName);
			
			//store TaskInfo to modify into temp taskinfo
			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
			//transfer all the new information over
			if (taskInfo.getTaskName() != null) {
				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
				temp.setTaskName (taskInfo.getTaskName());
				hasNameChanged = true;
			}
			if (taskInfo.getImportanceLevel() != preModifiedTaskInfo.getImportanceLevel()) {
				temp.setImportanceLevel (taskInfo.getImportanceLevel());
				hasPriorityChanged = true;
			}

			Calendar startDate = preModifiedTaskInfo.getStartDate();
			Calendar endDate = preModifiedTaskInfo.getEndDate();	
			if(taskInfo.getStartDate() != null) {
				startDate = taskInfo.getStartDate();
			}
			if(taskInfo.getEndDate() != null) {
				endDate = taskInfo.getEndDate();
			}
			
			if(DateAndTimeFormat.getInstance().dateValidityForStartAndEndDate(startDate, endDate)){
				hasTimeChanged = true;
				temp.setStartDate (startDate);
				temp.setEndDate (endDate);
			} else {
				feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL_INVALID_STARTANDEND_TIME, taskName);
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			}
			
			//1hr block error
			setEndDateAndTimeToHourBlock (temp);
			determineAndSetTaskType(temp);
			
			//store and update in memory
			taskInfo = temp;
			taskListShop.updateTask(taskInfo, preModifiedTaskInfo);
		} catch (Exception e) {
			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}
		feedback = feedbackGenerator();
		return createResult(taskListShop.getAllCurrentTasks(), feedback);
	}
	
	public boolean undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+taskInfo.getTaskName());
		TaskListShop.getInstance().updateTask(preModifiedTaskInfo, taskInfo);
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
	
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		preModifiedTaskInfo = new TaskInfo();
		
		saveTaskName(infoHashes, preModifiedTaskInfo);
		saveModifiedTaskName(infoHashes, taskInfo);
		saveTaskPriority(infoHashes, taskInfo);
		saveTaskStartDateAndTime(infoHashes, taskInfo);
		saveTaskEndDateAndTime(infoHashes, taskInfo);
	}

	
	public boolean parseInfo(String info) {
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
}
