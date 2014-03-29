package kaboom.logic.command;

import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;



public class CommandDelete extends Command {
	
	private static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	private static final String MESSAGE_COMMAND_DELETE_FAIL = "%1$s fail to delete.";
	
	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String taskName = taskInfo.getTaskName();
		String commandFeedback = "";
		
		if (taskListShop.removeTaskByName(taskName)) {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
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
	}
	
	public void storeTaskInfo (Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		saveTaskName(infoHashes, taskInfo);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
}
