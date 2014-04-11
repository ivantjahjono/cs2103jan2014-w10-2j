//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;

public class CommandDone extends Command{
	private final String MESSAGE_COMMAND_DONE_SUCCESS = "Set %1$s to complete";
	private final String MESSAGE_COMMAND_DONE_AlEADY_COMPLETED = "%1$s was already completed";

	TaskInfo taskToBeModified;

	public CommandDone() {
		commandType = COMMAND_TYPE.DONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
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
		
		if (taskToBeModified.getDone()) {
			feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
		} else {
			taskToBeModified.setDone(true);
			taskToBeModified.setExpiryFlag(false);
			taskListShop.refreshTasks();  //Refresh to shift task to archive
			taskView.deleteInSearchView(taskToBeModified);
			feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
		}
		
		taskToBeModified.setRecent(true);
		addCommandToHistory ();
		return createResult(feedback);
	}

	public boolean undo() {
		taskToBeModified.setDone(false);
		taskListShop.refreshTasks();  //Refresh to shift task to current
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
}