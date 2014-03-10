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


	public Result execute() {
		String commandFeedback = "";
		
		String taskName = taskInfo.getTaskName();
		TaskInfo searchTask = taskListShop. getTaskByName(taskName);
		Vector<TaskInfo> tasksFound = new Vector<TaskInfo>();
		
		for (int i = 0; i < allTasks.size(); i++) {
			TaskInfo singleTask = allTasks.get(i);
			if (singleTask.getTaskName().contains(taskName)) {
				tasksFound.add(singleTask);
			}
		}
		commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());
		
		return createResult(tasksFound, commandFeedback);
	}
	
}
