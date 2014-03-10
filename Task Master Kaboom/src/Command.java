/* 
** Purpose: 
*/

public class Command {
	
	protected static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	protected static final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add %1$s";
	protected static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	protected static final String MESSAGE_COMMAND_DELETE_FAIL = "%1$s fail to delete.";
	protected static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	protected static final String MESSAGE_COMMAND_MODIFY_FAIL = "Fail to modify %1$s";
	protected static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done. %1$d item(s) found.";
	protected static final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	protected static final String MESSAGE_COMMAND_UNDO_SUCCESS = "Command undone!";
	protected static final String MESSAGE_COMMAND_UNDO_FAIL = "Fail to undo.";
	
	protected COMMAND_TYPE commandType;
	protected TaskInfo taskInfoToBeModified;
	protected TaskInfo taskInfo;
	protected TaskListShop taskListShop;
	protected DisplayData displayData;
	
	Command () {
		commandType = COMMAND_TYPE.INVALID;
		taskInfo = null;
		taskInfoToBeModified = null;
		taskListShop = TaskListShop.getInstance();
		displayData = DisplayData.getInstance();
	}
	
	public void setCommandType (COMMAND_TYPE type) {
		commandType = type;
	}
	
	public void setTaskInfo (TaskInfo info) {
		taskInfo = info;
	}
	
	public void setTaskInfoToBeModified (TaskInfo info) {
		taskInfoToBeModified = info;
	}
	
	public COMMAND_TYPE getCommandType () {
		return commandType;
	}
	
	public TaskInfo getTaskInfo () {
		return taskInfo;
	}
	
	public TaskInfo getTaskInfoToBeModified () {
		return taskInfoToBeModified;
	}
	
	public String execute() {
		return MESSAGE_COMMAND_INVALID;
	}
	
	public String undo () {
		return MESSAGE_COMMAND_UNDO_FAIL;
	}
}
