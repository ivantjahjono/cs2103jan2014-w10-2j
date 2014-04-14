//@author A0096670W
package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

public class CommandUndone extends Command {
	private final String MESSAGE_COMMAND_UNDONE_SUCCESS = "Set %1$s to incomplete";
	private final String MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE = "%1$s was incomplete";
	private final String MESSAGE_COMMAND_INVALID = "No such unboom command. Type <help complete> for help.";
	
	TaskInfo taskToBeModified;
	TaskInfo taskInfo = null;

	public CommandUndone() {
		commandType = COMMAND_TYPE.UNDONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
	}

	public Result execute() {
		assert taskManager != null;

//		Result errorResult = invalidTaskNameAndClashErrorDetection();
//		if(errorResult != null) {
//			return errorResult;
//		} else {
//			taskToBeModified = getTask();
//		}
		
		if (infoTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return createResult(MESSAGE_COMMAND_INVALID);
		}
		
		COMMAND_ERROR commandError = errorDetectionForInvalidTaskNameAndId();
		if(commandError != null) {
			return commandErrorHandler(commandError);
		} else {
			taskToBeModified = getTask();
		}

		String feedback = MESSAGE_COMMAND_INVALID;
		String taskName = taskToBeModified.getTaskName();

		if (!taskToBeModified.getDone()) {
			feedback = String.format(MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE, taskName);
		} else {
			taskManager.undoneTask(taskToBeModified);
			addCommandToHistory ();
			feedback = String.format(MESSAGE_COMMAND_UNDONE_SUCCESS, taskName);
		}

		return createResult(feedback);
	}

	public boolean undo() {
		taskToBeModified.setDone(true);
		taskManager.refreshTasks();  //Refresh to shift task to archive
		taskManager.addToSearchView(taskToBeModified);
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
