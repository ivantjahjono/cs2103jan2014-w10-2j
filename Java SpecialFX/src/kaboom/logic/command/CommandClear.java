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
	private final String CLEAR_TYPE_PRESENT = "present";
	private final String CLEAR_TYPE_ARCHIVE = "archive";
	
	private final String MESSAGE_COMMAND_CLEAR_ALL_SUCCESS = "1.. 2.. 3.. Pooof! Your schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_PRESENT_SUCCESS = "1.. 2.. 3.. Pooof! Your present schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS = "3.. 2.. 1.. Pooof! Your archive has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE = "enter <clear all> to remove all tasks or <clear current> to remove current view";
	
	private enum CLEAR_TYPE {
		ALL, PRESENT, ARCHIVE, INVALID
	}
	
	Vector<TaskInfo> tasksCleared;
	Vector<TaskInfo> archiveTasksCleared;
	
	String clearTypeInString;
		
	public CommandClear () {
		commandType = COMMAND_TYPE.CLEAR;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.CLEARTYPE
		};
		clearTypeInString = null;
	}

	public Result execute() {
		assert taskDepo != null;	
		
		CLEAR_TYPE clearType = retrieveClearTypeAndDetermineClearType();
		String commandFeedback = processClearType(clearType);
		
		return createResult(commandFeedback);
	}
	
	public boolean undo () {
		boolean isUndoSuccessful = false;
		isUndoSuccessful = undoClearedPresentTaskAndClearedArchiveTask(isUndoSuccessful);
		
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

	private CLEAR_TYPE retrieveClearTypeAndDetermineClearType() {
		clearTypeInString = getTaskClearTypeFromInfoTable();
		if (clearTypeInString == null) {
			return CLEAR_TYPE.INVALID;
		} else {
			clearTypeInString = clearTypeInString.toLowerCase().trim();
			return determineClearType(clearTypeInString);
		}
	}
	
	private CLEAR_TYPE determineClearType(String clearTypeInString) {
		switch (clearTypeInString) {
		case CLEAR_TYPE_ALL:
			return CLEAR_TYPE.ALL;
		case CLEAR_TYPE_PRESENT:
			return CLEAR_TYPE.PRESENT;
		case CLEAR_TYPE_ARCHIVE:
			return CLEAR_TYPE.ARCHIVE;
		default: 
			return CLEAR_TYPE.INVALID;
		}
	}
	
	private String processClearType(CLEAR_TYPE clearType) {
		String commandFeedback = "";
		switch (clearType) {
		case ALL:
			commandFeedback = clearAll();
			break;
		case PRESENT:
			commandFeedback = clearPresent();
			break;
		case ARCHIVE:
			commandFeedback = clearArchive();
			break;
		default: 
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE;
		}
		return commandFeedback;
	}
	
	private String clearAll() {
		saveAndClearPresentTaskFromTaskView();
		saveAndClearArhiveTaskFromTaskView();
		taskView.clearSearchView();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_ALL_SUCCESS;
	}
	
	private String clearPresent() {
		saveAndClearPresentTaskFromTaskView();
		taskView.clearSearchView();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_PRESENT_SUCCESS;
	}
	
	private String clearArchive() {
		saveAndClearArhiveTaskFromTaskView();
		taskView.clearSearchView();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS;
	}

	private void saveAndClearPresentTaskFromTaskView() {
		tasksCleared = taskView.getAllPresentTasks();
		taskView.clearPresentTasks();
	}
	
	private void saveAndClearArhiveTaskFromTaskView() {
		archiveTasksCleared = taskView.getAllArchivedTasks();
		taskView.clearArchivedTasks ();
	}
	
	private boolean addClearedPresentTaskToMemory() {
		for (int i = 0; i < tasksCleared.size(); i++) {
			taskView.addTask(tasksCleared.get(i));
		}
		if (tasksCleared.size() == taskView.presentTaskCount()) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean addClearedArchiveTaskToMemory() {
		for (int i = 0; i < archiveTasksCleared.size(); i++) {
			taskView.addArchivedTask(archiveTasksCleared.get(i));
		}
		if (archiveTasksCleared.size() == taskView.archiveTaskCount()) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean undoClearedArchiveTask() {
		boolean isUndoSuccessful;
		isUndoSuccessful = addClearedArchiveTaskToMemory();
		if(isUndoSuccessful == false) {
			taskView.clearArchivedTasks ();
		}
		return isUndoSuccessful;
	}

	private boolean undoClearedPresentTask() {
		boolean isUndoSuccessful;
		isUndoSuccessful = addClearedPresentTaskToMemory();
		if(isUndoSuccessful == false) {
			taskView.clearPresentTasks();
		}
		return isUndoSuccessful;
	}
	
	private boolean undoClearedPresentTaskAndClearedArchiveTask(
			boolean isUndoSuccessful) {
		if(tasksCleared != null) {
			isUndoSuccessful = undoClearedPresentTask();
		}
		if(archiveTasksCleared != null) {
			isUndoSuccessful = undoClearedArchiveTask();
		}
		return isUndoSuccessful;
	}
	

}