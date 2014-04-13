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
	private final String CLEAR_TYPE_PRESENT = "";
	private final String CLEAR_TYPE_ARCHIVE = "archive";
	
	private final String MESSAGE_COMMAND_CLEAR_SUCCESS = "1.. 2.. 3.. Pooof! Your schedule has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS = "3.. 2.. 1.. Pooof! Your archive has gone with the wind";
	private final String MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE = "please enter <clear all> to remove all tasks or <clear current> to remove current view";
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
		assert taskDepo != null;	
		retrieveClearType();
		String commandFeedback = determineAndProcessClearType();
		
		return createResult(commandFeedback);
	}
	
	public boolean undo () {
		boolean isUndoSuccessful = false;

		if(tasksCleared != null) {
			for (int i = 0; i < tasksCleared.size(); i++) {
				taskView.addTask(tasksCleared.get(i));
			}
			if (tasksCleared.size() == taskView.presentTaskCount()) {
				isUndoSuccessful = true;
			} else {
				isUndoSuccessful = false;
			}
		}
		
		if(archiveTasksCleared != null) {
			for (int i = 0; i < archiveTasksCleared.size(); i++) {
				taskView.addArchivedTask(archiveTasksCleared.get(i));
			}
			if (archiveTasksCleared.size() == taskView.archiveTaskCount()) {
				isUndoSuccessful = true;
			} else {
				isUndoSuccessful = false;
			}
		}
		
		return isUndoSuccessful;
	}

	private void retrieveClearType() {
		clearType = getTaskClearTypeFromInfoTable();
			
		if(clearType == null || clearType.isEmpty()) {
			clearType = CLEAR_TYPE_PRESENT;
		} else {
			clearType = clearType.toLowerCase().trim();
		}	
	}
	
	private String determineAndProcessClearType() {
		String commandFeedback = "";
		switch (clearType) {
		case CLEAR_TYPE_ALL:
			commandFeedback = clearAll();
			break;
		case CLEAR_TYPE_CURRENT:
			//TODO
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_NOT_IMPLEMENTED;
			break;
		case CLEAR_TYPE_PRESENT:
			commandFeedback = clearPresent();
			break;
		case CLEAR_TYPE_ARCHIVE:
			commandFeedback = clearArchive();
			break;
		default: 
			commandFeedback = MESSAGE_COMMAND_CLEAR_FAIL_INVALID_TYPE;
		}
		return commandFeedback;
	}
	
	private String clearAll() {
		tasksCleared = taskView.getAllPresentTasks();
		archiveTasksCleared = taskView.getAllArchivedTasks();
		taskView.clearPresentTasks();
		taskView.clearArchivedTasks();
		taskView.clearSearchView();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_SUCCESS;
	}
	
	private String clearPresent() {
		tasksCleared = taskView.getAllPresentTasks();
		taskView.clearSearchView();
		taskView.clearPresentTasks();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_SUCCESS;
	}
	
	private String clearArchive() {
		archiveTasksCleared = taskView.getAllArchivedTasks();
		taskView.clearSearchView();
		taskView.clearArchivedTasks ();
		addCommandToHistory ();
		return MESSAGE_COMMAND_CLEAR_ARCHIVE_SUCCESS;
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