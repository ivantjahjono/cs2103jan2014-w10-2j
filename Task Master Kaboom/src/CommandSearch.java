	/*
	 * Current search is base on task name and result is unique. 
	 * Search result will be returned in the command's taskInfo variable
	 * 
	 */

import java.util.Vector;

public class CommandSearch extends Command {
	
	CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
	}


	public String execute() {
		String taskName = taskInfo.getTaskName();
		Vector<TaskInfo> allTasks = TaskListShop.getInstance().getAllTaskInList();
		int count = 0;  //Count the number of items found
		
		for (int i = 0; i < allTasks.size(); i++) {
			TaskInfo singleTask = allTasks.get(i);
			if (singleTask.getTaskName().contains(taskName)) {
				TaskListShop.getInstance().addTaskToSearch(singleTask);
				count++;
			}
		}
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, count);
	}
	
}
