package kaboom.logic.command;

import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;



public class CommandAdd extends Command {
	
	private static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	private static final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add %1$s";

	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String commandFeedback = "";
		
		if (taskInfo != null && !taskInfo.getTaskName().equals("")) {
			try {
				if (taskListShop.addTaskToList(taskInfo)) {
					commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
				} else {
					commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
				}
			} catch (Exception e) {
				commandFeedback = "Added a file with no Task Name";
			}
		}
		else {
			commandFeedback = "Added a file with no Task Name";
		}
		
		return createResult(taskListShop.getAllTaskInList(), commandFeedback);
	}
	
	public boolean undo () {
		String taskName = taskInfo.getTaskName();
		
		boolean isRemoveSuccess = taskListShop.removeTaskByName(taskName);
		
		return isRemoveSuccess;
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.PRIORITY);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}
	
	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		saveTaskName(infoHashes,taskInfo);
		saveTaskPriority(infoHashes,taskInfo);
		saveTaskDateAndTime(infoHashes,taskInfo);
	}
}
