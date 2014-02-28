
public class CommandAdd extends Command {

	CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
	}

	public String execute() {
		if (TaskListShop.getInstance().addTaskToList(taskInfo)) {
			return String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		}
		return String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
	}
	
	public String undo () {
		String taskName = taskInfo.getTaskName();
		
		boolean isRemoveSuccess = TaskListShop.getInstance().removeTaskByName(taskName);
		
		if (isRemoveSuccess) {
			return MESSAGE_COMMAND_UNDO_SUCCESS;
		} else {
			return MESSAGE_COMMAND_UNDO_FAIL;
		}
	}
}
