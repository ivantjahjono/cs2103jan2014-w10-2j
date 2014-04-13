//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

public class CommandDelete extends Command {

	private final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%s> deleted. 1 less work to do :D";
	private final String MESSAGE_COMMAND_DELETE_FAIL = "Delete fail. Can't access memory...";

	TaskInfo taskToBeDeleted;

	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		}; 
	}

	public Result execute() {
		assert taskManager != null;
		
		String commandFeedback = "";

//		Result errorResult = invalidTaskNameAndClashErrorDetection();
//		if(errorResult != null) {
//			return errorResult;
//		} else {
//			taskToBeDeleted = getTask();
//		}
		
		COMMAND_ERROR commandError = errorDetectionForInvalidTaskNameAndId();
		if(commandError != null) {
			return commandErrorHandler(commandError);
		} else {
			taskToBeDeleted = getTask();
		}
		commandFeedback = removeTaskFromMemory();
		
		return createResult(commandFeedback);
	}

	public boolean undo () {
		if (taskManager.addPresentTask(taskToBeDeleted)) {
			return true;
		}
		return false;
	}

	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);

		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}

		return true;
	}
	
	private String removeTaskFromMemory() {
		boolean hasRemovedTask = taskManager.removeTask(taskToBeDeleted);
		String feedback = "";
		if (hasRemovedTask) {
			addCommandToHistory ();
			feedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskToBeDeleted.getTaskName());
		} else {
			feedback = MESSAGE_COMMAND_DELETE_FAIL;
		}
		return feedback;
	}
}
