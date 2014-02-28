/* 
** Purpose: 
*/

public class Command {
	
	public static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	public static final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add %1$s";
	public static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	public static final String MESSAGE_COMMAND_DELETE_FAIL = "%1$s fail to delete.";
	public static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	public static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done";
	public static final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	
	COMMAND_TYPE commandType;
	TaskInfo taskInfo;
	
	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}
	
	public void setTaskInfo (TaskInfo info) {
		taskInfo = info;
	}
	
	public COMMAND_TYPE getCommandType () {
		return commandType;
	}
	
	public TaskInfo getTaskInfo () {
		return taskInfo;
	}
	
	public String execute() {
		switch(commandType) {
		case ADD:
			return add();
		case DELETE:
			return delete();
		case MODIFY: 
			return modify();
		case SEARCH:
			return search();
		default: 
			return MESSAGE_COMMAND_INVALID;
		
		}
	}
	

	private String add() {
		if (TaskListShop.getInstance().addTaskToList(taskInfo)) {
			return String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
		}
		return String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
	}
	
	private String delete() {
		String taskName = taskInfo.getTaskName();

		if (TaskListShop.getInstance().removeTaskByName(taskName)) {
			return String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);		
		}
		
		return String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
	}
	
	//Not done
	private String modify() {
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
	
	//Not done
	private static String search() {
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "My Task");
	}
	
}
