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

	TaskInfo taskToBeModified;
	TaskView taskView;

	public CommandDone() {
		commandType = COMMAND_TYPE.DONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
		taskView = TaskView.getInstance();
	}

	public Result execute() {
		assert taskListShop != null;
		
		Result errorResult = taskDetectionWithErrorFeedback();
		if(errorResult != null) {
			return errorResult;
		} else {
			taskToBeModified = getTask();
		}
		
		String feedback = MESSAGE_COMMAND_INVALID;
		String taskName = taskToBeModified.getTaskName();
		Result executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		
		if (taskToBeModified.getDone()) {
			feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
			executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		} else {
			taskListShop.setDoneByName(taskName);
			taskView.deleteInView(taskToBeModified);
			feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
			executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		}

		taskToBeModified.setRecent(true);
		addCommandToHistory ();
		return executionResult;
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
}