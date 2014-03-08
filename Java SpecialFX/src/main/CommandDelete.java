package main;


public class CommandDelete extends Command {
	
	CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
	}

	public String execute() {
		String taskName = taskInfo.getTaskName();

		if (taskListShop.removeTaskByName(taskName)) {
			return String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);	
			
		}
		
		return String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
	}
	
	public String undo () {
		if (taskListShop.addTaskToList(taskInfo)) {
			return String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		}
		return String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
	}
}
