package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.ui.TaskView;

public class CommandDone extends Command{
	private final String MESSAGE_COMMAND_DONE_SUCCESS = "Set %1$s to complete";
	private final String MESSAGE_COMMAND_DONE_AlEADY_COMPLETED = "%1$s was completed";
	private final String MESSAGE_COMMAND_DONE_FAIL = "%1$s does not exist";

	TaskInfo taskToBeModified;
	TaskView taskView;

	public CommandDone() {
		commandType = COMMAND_TYPE.DONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKNAME
		};
		taskView = TaskView.getInstance();
	}

	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		String taskName = infoTable.get(KEYWORD_TYPE.TASKNAME);
		//get id

		int taskCount = taskListShop.numOfTasksWithSimilarNames(taskName);

		if (isNumeric(taskName)) {
			int index = taskView.getIndexFromView(Integer.parseInt(taskName)-1);
			taskToBeModified = taskListShop.getTaskByID(index);
			if (taskToBeModified.getDone()) {
				feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			} else if (taskToBeModified == null) {
				feedback = String.format(MESSAGE_COMMAND_DONE_FAIL, taskName);
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			} else {
				taskListShop.setDoneByID(index);
				taskView.deleteInView(taskToBeModified);
			}
		}
		else if (taskCount > 1) {
			//CLASH
			Command search = new CommandSearch();
			search.storeTaskInfo(infoTable);
			return search.execute();
		} else {
			taskToBeModified = taskListShop.getTaskByName(taskName);
			if (taskToBeModified.getDone()) {
				feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			} else if (taskToBeModified == null) {
				feedback = String.format(MESSAGE_COMMAND_DONE_FAIL, taskName);
				return createResult(taskListShop.getAllCurrentTasks(), feedback);
			} else {
				taskListShop.setDoneByName(taskName);
				taskView.deleteInView(taskToBeModified);
			}
		}

		taskToBeModified.setRecent(true);
		feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
		addCommandToHistory ();
		return createResult(taskListShop.getAllCurrentTasks(), feedback);
	}

	public boolean undo() {
		taskListShop.setLastToUndone();
		taskView.addToView(taskToBeModified);
		taskView.undoneInView(taskToBeModified);
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