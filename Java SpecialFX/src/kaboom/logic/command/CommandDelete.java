package kaboom.logic.command;

import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;



public class CommandDelete extends Command {
	
	private static final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%1$s> deleted. 1 less work to do :D";
	private static final String MESSAGE_COMMAND_DELETE_FAIL = "Aww... fail to delete <%1$s>.";
	private static final String MESSAGE_COMMAND_DELETE_FAIL_NO_NAME_OR_ID = "Trying to remove air...";
	private static final String MESSAGE_COMMAND_DELETE_FAIL_INVALID_ID = "That's some weird numbers you have there...";
	
	String taskId;
	
	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		initialiseKeywordList();
		taskId = null;
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String taskName = taskInfo.getTaskName();
		String commandFeedback = MESSAGE_COMMAND_DELETE_FAIL_NO_NAME_OR_ID;
		
		if (taskName != null) {
			if (taskListShop.removeTaskByName(taskName)) {
				commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
			} else {
				commandFeedback = String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
			}
		} 
		if (taskId != null) {
			if(isNumeric(taskId)) {
				/*
				 * TODO
				 */
			} else {
				commandFeedback = MESSAGE_COMMAND_DELETE_FAIL_INVALID_ID;
			}
		
		}
		
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}
	
	public boolean undo () {
		if (taskListShop.addTaskToList(taskInfo)) {
			return true;
		}
		return false;
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
		keywordList.add(KEYWORD_TYPE.TASKID);
	}
	
	public void storeTaskInfo (Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		saveTaskName(infoHashes, taskInfo);
		taskId = infoHashes.get(KEYWORD_TYPE.TASKID);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
	
	private boolean isNumeric(String someString) {
		return someString.matches("\\d{1,4}");
	}
}
