package main;


public class CommandDelete extends Command {
	
	CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
	}

	public Result execute() {
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
