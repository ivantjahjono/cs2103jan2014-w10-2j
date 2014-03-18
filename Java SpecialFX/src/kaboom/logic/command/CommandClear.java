package kaboom.logic.command;

import java.util.Vector;

import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;


public class CommandClear extends Command {

	Vector<TaskInfo> tasksCleared;
		
	public CommandClear () {
		commandType = COMMAND_TYPE.CLEAR;
	}

	public Result execute() {
		tasksCleared = TaskListShop.getInstance().getAllTaskInList();
		Vector<TaskInfo> display = TaskListShop.getInstance().clearAllTasks();
		return createResult(display, MESSAGE_COMMAND_CLEAR_SUCCESS);
	}
	
	public String undo () {

		boolean isUndoSuccess = false;

		for (int i = 0; i < tasksCleared.size(); i++) {
			TaskListShop.getInstance().addTaskToList(tasksCleared.get(i));
		}
		
		if (tasksCleared.size() == TaskListShop.getInstance().shopSize()) {
			isUndoSuccess = true;
		}
		
		if (isUndoSuccess) {
			return MESSAGE_COMMAND_UNDO_SUCCESS;
		} else {
			return MESSAGE_COMMAND_UNDO_FAIL;
		}
	}
}