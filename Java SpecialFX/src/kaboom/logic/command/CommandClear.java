package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.TextParser;


public class CommandClear extends Command {
	
	private static final String MESSAGE_COMMAND_CLEAR_SUCCESS = "Cleared memory";
	
	Vector<TaskInfo> tasksCleared;
		
	public CommandClear () {
		commandType = COMMAND_TYPE.CLEAR;
	}

	public Result execute() {
		assert taskListShop != null;
		
		tasksCleared = taskListShop.getAllCurrentTasks();
		Vector<TaskInfo> display = taskListShop.clearAllTasks();
		return createResult(display, MESSAGE_COMMAND_CLEAR_SUCCESS);
	}
	
	public boolean undo () {
		boolean isUndoSuccessful = false;

		for (int i = 0; i < tasksCleared.size(); i++) {
			taskListShop.addTaskToList(tasksCleared.get(i));
		}
		
		if (tasksCleared.size() == taskListShop.shopSize()) {
			isUndoSuccessful = true;
		}
		
		return isUndoSuccessful;
	}
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}
		
		return true;
	}
}