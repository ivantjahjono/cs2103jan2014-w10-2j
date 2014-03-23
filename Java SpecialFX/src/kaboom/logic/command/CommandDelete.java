package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.storage.TaskListShop;



public class CommandDelete extends Command {
	
	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
	}

	public Result execute() {
		assert taskInfo != null;
		assert TaskListShop.getInstance() != null;
		
		String taskName = taskInfo.getTaskName();
		String commandFeedback = "";
		
		if (taskListShop.removeTaskByName(taskName)) {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
		}
		
		return createResult(taskListShop.getAllTaskInList(), commandFeedback);
	}
	
	public String undo () {
		if (taskListShop.addTaskToList(taskInfo)) {
			return String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		}
		return String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
	}
}