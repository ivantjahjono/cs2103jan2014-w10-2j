//@author A0073731J

package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;


public class CommandClear extends Command {
	
	private final String CLEAR_TYPE_ALL = "all";
	private final String CLEAR_TYPE_CURRENT = "current";
	private final String CLEAR_TYPE_EMPTY = "";
	private final String CLEAR_TYPE_ARCHIVE = "archive";
	
	private final String MESSAGE_COMMAND_CLEAR_SUCCESS = "1.. 2.. 3.. Pooof! Your schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS = "3.. 2.. 1.. Pooof! Your archive has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE = "You trying to be funny?";
//	private final String MESSAGE_COMMAND_CLEAR_FAIL_NO_TYPE = "please enter <clear all> to remove all tasks or <clear current> to remove current view";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_NOT_IMPLEMENTED = "LOL";
	
	Vector<TaskInfo> tasksCleared;
	Vector<TaskInfo> archiveTasksCleared;
	
	String clearType;
		
	public CommandClear () {
		commandType = COMMAND_TYPE.CLEAR;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.CLEARTYPE
		};
		clearType = null;
	}

	public Result execute() {
		assert taskListShop != null;
		
		clearType = infoTable.get(KEYWORD_TYPE.CLEARTYPE);
		
		if(clearType == null || clearType.isEmpty()) {
			clearType = CLEAR_TYPE_EMPTY;
		} else {
			clearType = clearType.toLowerCase().trim();
		}
		
		String commandFeedback = "";
		
		switch (clearType) {
		case CLEAR_TYPE_ALL:
			tasksCleared = taskView.getAllCurrentTasks();
			archiveTasksCleared = taskView.getAllArchivedTasks();
			commandFeedback = MESSAGE_COMMAND_CLEAR_SUCCESS;
			taskView.clearCurrentTasks();
			taskView.clearArchivedTasks();
			addCommandToHistory ();
			break;
		case CLEAR_TYPE_CURRENT:
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_NOT_IMPLEMENTED;
			break;
		case CLEAR_TYPE_EMPTY:
			//take as all
			commandFeedback = MESSAGE_COMMAND_CLEAR_SUCCESS;
			tasksCleared = taskView.getAllCurrentTasks();
//			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_NO_TYPE;
			taskView.clearCurrentTasks();
			addCommandToHistory ();
			break;
		case CLEAR_TYPE_ARCHIVE:
			commandFeedback = MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS;
			archiveTasksCleared = taskView.getAllArchivedTasks();
			taskView.clearArchivedTasks ();
			addCommandToHistory ();
			break;
		default: 
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE;
		}
		taskView.clearSearchView();
		
		return createResult(commandFeedback);
	}
	
	public boolean undo () {
		boolean isUndoSuccessful = false;

		if(tasksCleared != null) {
			for (int i = 0; i < tasksCleared.size(); i++) {
				taskView.addTask(tasksCleared.get(i));
			}
			if (tasksCleared.size() == taskListShop.shopSize()) {
				isUndoSuccessful = true;
			} else {
				isUndoSuccessful = false;
			}
		}
		
		if(archiveTasksCleared != null) {
			for (int i = 0; i < archiveTasksCleared.size(); i++) {
				taskView.addArchivedTask(archiveTasksCleared.get(i));
			}
			if (archiveTasksCleared.size() == taskListShop.archiveShopSize()) {
				isUndoSuccessful = true;
			} else {
				isUndoSuccessful = false;
			}
		}
		
		return isUndoSuccessful;
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