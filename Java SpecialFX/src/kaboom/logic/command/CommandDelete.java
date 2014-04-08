package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.ui.TaskView;


public class CommandDelete extends Command {

	private final String MESSAGE_COMMAND_DELETE_SUCCESS = "<%1$s> deleted. 1 less work to do :D";
	private final String MESSAGE_COMMAND_DELETE_FAIL = "Aww... fail to delete <%1$s>.";
	private final String MESSAGE_COMMAND_DELETE_INVALID = "Enter a taskname or task id, please ?";
	private final String MESSAGE_COMMAND_DELETE_NO_SUCH_TASK = "<%1$s> does not exist...";

	TaskInfo taskToBeDeleted;
	TaskView taskView;

	public CommandDelete () {
		commandType = COMMAND_TYPE.DELETE;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.TASKID,
				KEYWORD_TYPE.TASKNAME
		};
		taskView = TaskView.getInstance();  
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
		
		taskListShop.removeTaskByName(taskToBeDeleted.getTaskName());
		taskView.deleteInView(taskToBeDeleted);
		commandFeedback = String.format(MESSAGE_COMMAND_DELETE_SUCCESS, taskToBeDeleted.getTaskName());
		addCommandToHistory ();
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}

	public boolean undo () {
		if (taskListShop.addTaskToList(taskToBeDeleted)) {
			taskView.addToView(taskToBeDeleted);
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
