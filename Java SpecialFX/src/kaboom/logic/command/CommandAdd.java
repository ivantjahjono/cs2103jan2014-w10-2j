package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.storage.TaskListShop;



public class CommandAdd extends Command {

	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
	}

	public Result execute() {
		assert taskInfo != null;
		assert TaskListShop.getInstance() != null;
		
		String commandFeedback = "";
		
		
		if (taskListShop.addTaskToList(taskInfo)) {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
		}
		
		return createResult(taskListShop.getAllTaskInList(), commandFeedback);
	}
	
	public String undo () {
		String taskName = taskInfo.getTaskName();
		
		boolean isRemoveSuccess = taskListShop.removeTaskByName(taskName);
		
		if (isRemoveSuccess) {
			return MESSAGE_COMMAND_UNDO_SUCCESS;
		} else {
			return MESSAGE_COMMAND_UNDO_FAIL;
		}
	}
}
