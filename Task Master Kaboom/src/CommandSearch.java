	/*
	 * Current search is base on task name and result is unique. 
	 * Search result will be returned in the command's taskInfo variable
	 * 
	 */

public class CommandSearch extends Command {
	
	CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
	}


	public String execute() {
		String taskName = taskInfo.getTaskName();
		TaskInfo searchTask = taskListShop. getTaskByName(taskName);
		taskInfo = searchTask;
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS);
	}
	
}
