package kaboom.logic.command;

import java.util.Vector;

import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TASK_TYPE;
import kaboom.storage.TaskListShop;

public class CommandSearch extends Command {
	
	public CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
		initialiseKeywordList();
	}

	public Result execute() {
		
		assert taskInfo != null;
		assert TaskListShop.getInstance() != null;
		String commandFeedback;
		Vector<TaskInfo> tasksFound = new Vector<TaskInfo>();
		Vector<TaskInfo> allTasks = TaskListShop.getInstance().getAllTaskInList();
		
		String taskName = taskInfo.getTaskName();
		if (taskName != null) {
			//If taskName is not null, search by task name			
			for (int i = 0; i < allTasks.size(); i++) {
				TaskInfo singleTask = allTasks.get(i);
				if (singleTask.getTaskName().contains(taskName)) {
					tasksFound.add(singleTask);
				}
			}
		}
		else {
			//search by end date
			for (int i = 0; i < allTasks.size(); i++) {
				TaskInfo singleTask = allTasks.get(i);
				if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING) 
						&& singleTask.getEndDate().before(taskInfo.getEndDate())) {
					tasksFound.add(singleTask);
				}
			}
		}
		//Search by end time? 
		
		commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());

	 	return createResult(tasksFound, commandFeedback);
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.END_TIME);  //Does this work?
	}
}
