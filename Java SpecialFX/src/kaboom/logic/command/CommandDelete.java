package kaboom.logic.command;

import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.History;


public class CommandDelete extends Command {
	
	private static final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%1$s> deleted. 1 less work to do :D";
	private static final String MESSAGE_COMMAND_DELETE_FAIL = "Aww... fail to delete <%1$s>.";
	
	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String taskName = taskInfo.getTaskName();
		String commandFeedback = "";
		
		History history = History.getInstance();
		
		if (isNumeric(taskName)) {
			history.taskID = taskListShop.getCorrespondingID(taskListShop.getAllCurrentTasks());
			int index = history.taskID.get(Integer.parseInt(taskName));
			taskListShop.removeTaskByID(index);
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		}
		
		else if (taskListShop.removeTaskByName(taskName)) {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
		}
		
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}
	
	private boolean isNumeric(String taskName) {
		return taskName.matches("\\d{1,4}");
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
