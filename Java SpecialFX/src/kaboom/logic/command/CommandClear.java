package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;


public class CommandClear extends Command {
	
	private final String CLEAR_TYPE_ALL = "all";
	private final String CLEAR_TYPE_CURRENT = "current";
	private final String CLEAR_TYPE_EMPTY = "";
	
	private final String MESSAGE_COMMAND_CLEAR_SUCCESS = "1.. 2.. 3.. Pooof! Your schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE = "1.. 2.. 3.. Pooof! Your schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_NO_TYPE = "please enter <clear all> to remove all tasks or <clear current> to remove current view";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_NOT_IMPLEMENTED = "LOL";
	
	Vector<TaskInfo> tasksCleared;
	String clearType;
		
	public CommandClear () {
		commandType = COMMAND_TYPE.CLEAR;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.CLEARTYPE
		};
		clearType = null;
	}

	public Result execute() {
		assert taskListShop != null;
		
		clearType = infoTable.get(KEYWORD_TYPE.CLEARTYPE);
		
		if(clearType == null) {
			clearType = CLEAR_TYPE_ALL;
		}
		
		String commandFeedback = "";
		Vector<TaskInfo> display = taskListShop.clearAllTasks();
		
		switch (clearType) {
		case CLEAR_TYPE_ALL:
			tasksCleared = taskListShop.getAllCurrentTasks();
			commandFeedback = MESSAGE_COMMAND_CLEAR_SUCCESS;
			display = taskListShop.clearAllTasks();
			break;
		case CLEAR_TYPE_CURRENT:
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_NOT_IMPLEMENTED;
			display = taskListShop.getAllCurrentTasks();
			break;
		case CLEAR_TYPE_EMPTY:
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_NO_TYPE;
			display = taskListShop.getAllCurrentTasks();
			break;
		default: 
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE;
			display = taskListShop.getAllCurrentTasks();
		}
		
		display = taskListShop.getAllCurrentTasks();
		return createResult(display, commandFeedback);
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