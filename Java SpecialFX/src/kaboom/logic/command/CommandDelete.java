package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.History;


public class CommandDelete extends Command {

	private static final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%1$s> deleted. 1 less work to do :D";
	private static final String MESSAGE_COMMAND_DELETE_FAIL = "Aww... fail to delete <%1$s>.";
	private static final String MESSAGE_COMMAND_DELETE_INVALID = "Enter a taskname or task id, please ?";

	String taskId;
	Hashtable<KEYWORD_TYPE,String> taskInfoTable;
	TaskInfo prevTask;

	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		initialiseKeywordList();
		taskId = null;
		taskInfoTable = null;
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskInfoTable != null;
		assert taskListShop != null;

		String taskName = taskInfo.getTaskName();
		String commandFeedback;

		if (taskName.equals("")) {
			commandFeedback = MESSAGE_COMMAND_DELETE_INVALID;
			return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
		}

		History history = History.getInstance();
		int taskCount = taskListShop.numOfTasksWithSimilarNames(taskName);

		if (taskCount > 1) {
			commandFeedback = "OH YEA! CLASH.. BOO000000000M!";

			Command search = new CommandSearch();
			search.storeTaskInfo(taskInfoTable);
			return search.execute();
		}
		else if (isNumeric(taskName)) {
			int index = history.taskID.get(Integer.parseInt(taskName)-1);
			prevTask = taskListShop.removeTaskByID(index);  //Set for undo
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		} else if (taskCount == 1){
			prevTask = taskListShop.removeTaskByName(taskName);
			assert prevTask != null;
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskName);
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_DELETE_FAIL, taskName);
		}

		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}

	private boolean isNumeric(String taskName) {
		return taskName.matches("\\d{1,4}");
	}

	public boolean undo () {
		if (taskListShop.addTaskToList(prevTask)) {
			return true;
		}
		return false;
	}

	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
		keywordList.add(KEYWORD_TYPE.TASKID);
	}

	public void storeTaskInfo (Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		taskInfoTable = infoHashes;
		saveTaskName(infoHashes, taskInfo);
		taskId = infoHashes.get(KEYWORD_TYPE.TASKID);

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
