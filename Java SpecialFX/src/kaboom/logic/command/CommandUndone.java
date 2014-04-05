package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.ui.TaskView;

public class CommandUndone extends Command{
	private final String MESSAGE_COMMAND_UNDONE_SUCCESS = "Set %1$s to incomplete";
	private final String MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE = "%1$s was incomplete";
	private final String MESSAGE_COMMAND_UNDONE_FAIL = "%1$s does not exist";

	TaskInfo taskToBeModified;
	Hashtable<KEYWORD_TYPE,String> taskInfoTable;
	TaskView taskView;

	public CommandUndone() {
		commandType = COMMAND_TYPE.UNDONE;
		initialiseKeywordList();
		taskInfoTable = null;
		taskView = TaskView.getInstance();
	}

	public Result execute() {
		assert taskListShop != null;

		String feedback = "";
		String taskName = taskInfo.getTaskName();

		int taskCount = taskListShop.numOfArchivedTasksWithSimilarNames(taskName);

		if (isNumeric(taskName)) {
			int index = Integer.parseInt(taskName)-1;
			taskToBeModified = taskListShop.getArchivedTaskByID(index);
			taskListShop.setUndoneByID(index);
			taskView.deleteInView(taskToBeModified);
		}

		else if (taskCount > 1) {
			feedback = "OH YEA! CLASH.. BOO000000000M!";

			Command search = new CommandSearch();
			search.storeTaskInfo(taskInfoTable);
			return search.execute();  //No support for archive search yet
		} else {
			taskToBeModified = taskListShop.getTaskByName(taskName);
			taskListShop.setUndoneByName(taskName);
			taskView.deleteInView(taskToBeModified);
		}

		if (taskToBeModified == null) {
			//can throw exception (task does not exist)
			feedback = String.format(MESSAGE_COMMAND_UNDONE_FAIL, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		if (taskToBeModified.getDone()) {
			//can throw exception (command incomplete)
			feedback = String.format(MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE, taskName);
			return createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		taskInfo.setRecent(true);
		feedback = String.format(MESSAGE_COMMAND_UNDONE_SUCCESS, taskName);
		return createResult(taskListShop.getAllCurrentTasks(), feedback);
	}

	public boolean undo() {
		taskListShop.setLastToDone();
		taskView.addToView(taskToBeModified);
		taskView.doneInView(taskToBeModified);
		return true;
	}

	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}

	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		taskInfoTable = infoHashes;
		saveTaskName(infoHashes, taskInfo);
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
