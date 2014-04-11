//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;


public class CommandDelete extends Command {

	private final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%s> deleted. 1 less work to do :D";

	TaskInfo taskToBeDeleted;

	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		}; 
	}

	public Result execute() {
		assert taskListShop != null;
		
		//set task id;
		String commandFeedback = "";

		Result errorResult = taskDetectionWithErrorFeedback();
		if(errorResult != null) {
			return errorResult;
		} else {
			taskToBeDeleted = getTask();
		}
		
		taskListShop.removeTask(taskToBeDeleted);
		taskView.deleteInSearchView(taskToBeDeleted);
		commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskToBeDeleted.getTaskName());
		addCommandToHistory ();
		return createResult(commandFeedback);
	}

	public boolean undo () {
		if (taskListShop.addTaskToList(taskToBeDeleted)) {
			taskView.addToSearchView(taskToBeDeleted);
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
}
