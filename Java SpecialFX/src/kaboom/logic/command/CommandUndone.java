package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.TextParser;

public class CommandUndone extends Command{
	private static final String MESSAGE_COMMAND_UNDONE_SUCCESS = "Set %1$s to incomplete";
	private static final String MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE = "%1$s was incomplete";
	private static final String MESSAGE_COMMAND_UNDONE_FAIL = "%1$s does not exist";
	
	TaskInfo taskToBeModified;
	
	public CommandUndone() {
		commandType = COMMAND_TYPE.UNDONE;
		initialiseKeywordList();
	}
	
	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		String taskName = taskInfo.getTaskName();
		taskToBeModified = taskListShop.getTaskByName(taskName);
		
		if(taskToBeModified == null) {
			//can throw exception (task does not exist)
			feedback = String.format(MESSAGE_COMMAND_UNDONE_FAIL, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}
		
		if(!taskToBeModified.getDone()) {
			//can throw exception (command incomplete)
			feedback = String.format(MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}
		
		taskInfo = new TaskInfo(taskToBeModified);
		taskInfo.setDone(false);
		taskListShop.updateTask(taskInfo, taskToBeModified);
		feedback = String.format(MESSAGE_COMMAND_UNDONE_SUCCESS, taskName);
		return createResult(taskListShop.getAllCurrentTasks(), feedback);
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
	
	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}
		
		return true;
	}
}
