
public class CommandAdd extends Command {

	CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
	}

	public Result execute() {
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
