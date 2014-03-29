package kaboom.logic.command;

import java.util.Hashtable;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;

public class CommandDone extends Command{
	private static final String MESSAGE_COMMAND_DONE_SUCCESS = "Set %1$s to complete";
	private static final String MESSAGE_COMMAND_DONE_AlEADY_COMPLETED = "%1$s was completed";
	private static final String MESSAGE_COMMAND_DONE_FAIL = "%1$s does not exist";
	
	TaskInfo taskToBeModified;
	
	public CommandDone() {
		commandType = COMMAND_TYPE.DONE;
		initialiseKeywordList();
	}
	
	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		String taskName = taskInfo.getTaskName();
		taskToBeModified = taskListShop.getTaskByName(taskName);
		
		if(taskToBeModified == null) {
			//can throw exception (task does not exist)
			feedback = String.format(MESSAGE_COMMAND_DONE_FAIL, taskName);
			return createResult(taskListShop.getAllTaskInList(), feedback);
		}
		
		if(taskToBeModified.isDone()) {
			//can throw exception (command incomplete)
			feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
			return createResult(taskListShop.getAllTaskInList(), feedback);
		}
		
		taskInfo = new TaskInfo(taskToBeModified);
		taskInfo.setDone(true);
		taskListShop.updateTask(taskInfo, taskToBeModified);
		feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
		return createResult(taskListShop.getAllTaskInList(), feedback);
	}
	
	public boolean undo() {
		taskListShop.updateTask(taskToBeModified, taskInfo);
		return true;
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}
	
	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		saveTaskName(infoHashes,taskInfo);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
}
