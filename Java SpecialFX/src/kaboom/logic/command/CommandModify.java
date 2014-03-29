package kaboom.logic.command;

import java.util.Hashtable;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;


public class CommandModify extends Command {
	
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	private static final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to cast a spell on <%1$s>";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_NAME_CHANGE = "<%1$s> has evolved into <%2$s>";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_TIME_CHANGE = "<%1$s> has manipulated time";
	private static final String MESSAGE_COMMAND_MODIFY_SUCCESS_PRIORITY_CHANGE = "<%1$s> is seeing stars";
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		String taskName = "";
		
		try {
			//get name of TaskInfo that user wants to modify
			taskName = preModifiedTaskInfo.getTaskName();
			//get TaskInfo that user wants to modify;
			preModifiedTaskInfo = taskListShop.getTaskByName(taskName);
			
			//store TaskInfo to modify into temp taskinfo
			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
			//transfer all the new information over
			if (taskInfo.getTaskName() != null) {
				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
				temp.setTaskName (taskInfo.getTaskName());
				
			}
			if (taskInfo.getImportanceLevel() != preModifiedTaskInfo.getImportanceLevel()) {
				temp.setImportanceLevel (taskInfo.getImportanceLevel());
			}
			if (taskInfo.getStartDate() != null) {
				temp.setStartDate (taskInfo.getStartDate());
			}
			if (taskInfo.getEndDate() != null) {
				temp.setEndDate (taskInfo.getEndDate());
			}
			setEndDateAndTimeToHourBlock (temp);
			determineAndSetTaskType(temp);
			
			//store and update in memory
			taskInfo = temp;
			taskListShop.updateTask(taskInfo, preModifiedTaskInfo);
		} catch (Exception e) {
			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL, taskName);
			return createResult(taskListShop.getAllTaskInList(), feedback);
		}
		feedback = String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, preModifiedTaskInfo.getTaskName());
		return createResult(taskListShop.getAllTaskInList(), feedback);
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
}
