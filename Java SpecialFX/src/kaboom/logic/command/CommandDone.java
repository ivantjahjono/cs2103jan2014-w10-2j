package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.TextParser;

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
		if (isNumeric(taskName)) {
			taskToBeModified = taskListShop.getTaskByID(Integer.parseInt(taskName)-1);
		} else {
			taskToBeModified = taskListShop.getTaskByName(taskName);
		}

		if(taskToBeModified == null) {
			//can throw exception (task does not exist)
			feedback = String.format(MESSAGE_COMMAND_DONE_FAIL, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		if(taskToBeModified.getDone()) {
			//can throw exception (command incomplete)
			feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		taskInfo = new TaskInfo(taskToBeModified);
		taskInfo.setDone(true);
		taskListShop.updateTask(taskInfo, taskToBeModified);
		feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
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

	private boolean isNumeric(String taskName) {
		return taskName.matches("\\d{1,4}");
	}
}
