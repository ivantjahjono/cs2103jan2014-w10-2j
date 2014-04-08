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
	TaskInfo taskInfo = null;
	
	public CommandUndone() {
		commandType = COMMAND_TYPE.UNDONE;
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
		Result executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		String taskName = taskToBeModified.getTaskName();

		
		if (!taskToBeModified.getDone()) {
			feedback = String.format(MESSAGE_COMMAND_UNDONE_AlEADY_INCOMPLETE, taskName);
			executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		} else {
			taskListShop.setUndoneByName(taskName);
			taskView.deleteInView(taskToBeModified);
			feedback = String.format(MESSAGE_COMMAND_UNDONE_SUCCESS, taskName);
			executionResult = createResult(taskListShop.getAllCurrentTasks(), feedback);
		}
		

//		if (isNumeric(taskName)) {
//			int index = Integer.parseInt(taskName)-1;
//			taskToBeModified = taskListShop.getArchivedTaskByID(index);
//			taskListShop.setUndoneByID(index);
//			taskView.deleteInView(taskToBeModified);
//		}
//
//		else if (taskCount > 1) {
//			Command search = new CommandSearch();
//			search.storeTaskInfo(infoTable);
//			return search.execute();  //No support for archive search yet
//		} else {
//			taskToBeModified = taskListShop.getTaskByName(taskName);
//
//		}
//
//		if (taskToBeModified == null) {
//			feedback = String.format(MESSAGE_COMMAND_UNDONE_FAIL, taskName);
//			return createResult(taskListShop.getAllCurrentTasks(), feedback);
//		}


		taskToBeModified.setRecent(true);
		addCommandToHistory();
		return executionResult;
	}

	public boolean undo() {
		taskListShop.setLastToDone();
		taskView.addToView(taskToBeModified);
		taskView.doneInView(taskToBeModified);
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
