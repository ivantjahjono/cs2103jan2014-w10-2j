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
	TaskView taskView;
	TaskInfo taskInfo = null;
	
	public CommandUndone() {
		commandType = COMMAND_TYPE.UNDONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
		taskView = TaskView.getInstance();
	}

	public Result execute() {
		assert taskListShop != null;

		String feedback = "";
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);

		int taskCount = taskListShop.numOfArchivedTasksWithSimilarNames(taskName);

		if (isNumeric(taskName)) {
			int index = Integer.parseInt(taskName)-1;
			taskToBeModified = taskListShop.getArchivedTaskByID(index);
			taskListShop.setUndoneByID(index);
			taskView.deleteInSearchView(taskToBeModified);
		}

		else if (taskCount > 1) {
			Command search = new CommandSearch();
			search.storeTaskInfo(infoTable);
			return search.execute();  //No support for archive search yet
		} else {
			taskToBeModified = taskListShop.getTaskByName(taskName);
			taskListShop.setUndoneByName(taskName);
			taskView.deleteInSearchView(taskToBeModified);
		}

		if (taskToBeModified == null) {
			feedback = String.format(MESSAGE_COMMAND_UNDONE_FAIL, taskName);
			return createResult(feedback);
		}

		if (taskToBeModified.getDone()) {
			feedback = String.format(MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE, taskName);
			return createResult(feedback);
		}

		taskToBeModified.setRecent(true);
		feedback = String.format(MESSAGE_COMMAND_UNDONE_SUCCESS, taskName);
		addCommandToHistory();
		return createResult(feedback);
	}

	public boolean undo() {
		taskListShop.setLastToDone();
		taskView.addToSearchView(taskToBeModified);
		return true;
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
