//@author A0096670W

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

public class CommandDone extends Command {
	private final String MESSAGE_COMMAND_DONE_SUCCESS = "Set %1$s to complete";
	private final String MESSAGE_COMMAND_DONE_AlEADY_COMPLETED = "%1$s was already completed";
	private final String MESSAGE_COMMAND_INVALID = "Nope. No such BOOM command. Type <help complete> for help.";
	
	TaskInfo taskToBeModified;

	public CommandDone() {
		commandType = COMMAND_TYPE.DONE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
	}

	public Result execute() {
		assert taskManager != null;
		
		if (infoTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return createResult(MESSAGE_COMMAND_INVALID);
		}
		
		errorDetectionForInvalidTaskNameAndId();
		if(!commandErrorList.isEmpty()) {
			return commandErrorHandler(commandErrorList.get(0));
		} else {
			taskToBeModified = getTask();
		}
		
		String feedback = MESSAGE_COMMAND_INVALID;
		String taskName = taskToBeModified.getTaskName();
		
		if (taskToBeModified.isDone()) {
			feedback = String.format(MESSAGE_COMMAND_DONE_AlEADY_COMPLETED, taskName);
		} else {
			taskManager.doneTask(taskToBeModified);
			addCommandToHistory();
			feedback = String.format(MESSAGE_COMMAND_DONE_SUCCESS, taskName);
		}
		
		return createResult(feedback);
	}

	public boolean undo() {
		taskToBeModified.setDone(false);
		taskManager.refreshTasks();  //Refresh to shift task to current
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