package kaboom.logic.command;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.storage.TaskListShop;



public class CommandAdd extends Command {

	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskInfo != null;
		assert TaskListShop.getInstance() != null;
		
		String commandFeedback = "";
		
		try {
			if (taskListShop.addTaskToList(taskInfo)) {
				commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
			} else {
				commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
			}
		} catch (Exception e) {
			commandFeedback = "Added a file with no Task Name";
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
	
	private void initialiseKeywordList() {
		keywordList.add(KEYWORD_TYPE.PRIORITY);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}
}
