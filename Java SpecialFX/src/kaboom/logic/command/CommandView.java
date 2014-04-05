package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.ui.DISPLAY_STATE;
import kaboom.ui.TaskView;

public class CommandView extends Command{
	private final String KEYWORD_RUNNING = "running";
	private final String KEYWORD_DEADLINE = "deadline";
	private final String KEYWORD_TIMED = "timed";
	private final String KEYWORD_SEARCH = "search";
	private final String KEYWORD_ARCHIVE = "archive";
	private final String KEYWORD_ALL = "all";
	
	private final String MESSAGE_VIEW_RUNNING = "Running Task Mode";
	private final String MESSAGE_VIEW_DEADLINE = "Deadline Task Mode";
	private final String MESSAGE_VIEW_TIMED = "Timed Task Mode";
	private final String MESSAGE_VIEW_ALL = "All Task Mode";
	private final String MESSAGE_VIEW_SEARCH = "Search Result Mode";
	private final String MESSAGE_VIEW_ARCHIVE = "Archive View Mode";
	private final String MESSAGE_VIEW_INVALID = "Invalid View Mode";
	
	String viewType;
	DISPLAY_STATE stateToSet;
	
	public CommandView () {
		commandType = COMMAND_TYPE.VIEW;
		stateToSet = DISPLAY_STATE.INVALID;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.VIEWTYPE	
		};
	}

	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		Vector<TaskInfo> taskList = null;
		Result commandResult = createResult(taskList, feedback);
		
		if(viewType == null) {
			viewType = "Nothing to view";
		}
		
		switch(stateToSet) {
			case RUNNING:
				taskList = taskListShop.getFloatingTasks();
				feedback = MESSAGE_VIEW_RUNNING;
				break;
			case DEADLINE:
				taskList = taskListShop.getDeadlineTasks();
				feedback = MESSAGE_VIEW_DEADLINE;
				break;
			case TIMED:
				taskList = taskListShop.getTimedTasks();
				feedback = MESSAGE_VIEW_TIMED;
				break;
			case ALL:
				taskList = taskListShop.getAllCurrentTasks();
				feedback = MESSAGE_VIEW_ALL;
				break;
			case SEARCH:
				taskList = new Vector<TaskInfo>();
				feedback = MESSAGE_VIEW_SEARCH;
				break;
			case ARCHIVE:
				taskList = taskListShop.getAllArchivedTasks();
				feedback = MESSAGE_VIEW_ARCHIVE;
				break;
			default:
				taskList = new Vector<TaskInfo>();
				feedback = MESSAGE_VIEW_INVALID;
		}
		
		TaskView.getInstance().setCurrentView(taskList);
		commandResult.setDisplayState(stateToSet);
		commandResult.setTasksToDisplay(taskList);
		commandResult.setFeedback(feedback);
		return commandResult;
	}

	private DISPLAY_STATE determineDisplayState(String viewType2) {
		switch(viewType) {
			case KEYWORD_RUNNING:
				return DISPLAY_STATE.RUNNING;
			case KEYWORD_DEADLINE:
				return DISPLAY_STATE.DEADLINE;
			case KEYWORD_TIMED:
				return DISPLAY_STATE.TIMED;
			case KEYWORD_ALL:
				return DISPLAY_STATE.ALL;
			case KEYWORD_SEARCH:
				return DISPLAY_STATE.SEARCH;
			case KEYWORD_ARCHIVE:
				return DISPLAY_STATE.ARCHIVE;
			default:
				return DISPLAY_STATE.INVALID;
		}
	}
	
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		viewType = infoHashes.get(KEYWORD_TYPE.VIEWTYPE);
		
		if (infoHashes.containsKey(KEYWORD_TYPE.INVALID)) {
			viewType += infoHashes.get(KEYWORD_TYPE.VIEWTYPE);
		}

		stateToSet = determineDisplayState(viewType);
	}
	
	public void setDisplayState (DISPLAY_STATE state) {
		stateToSet = state;
	}
	
	public DISPLAY_STATE getDisplayState () {
		return stateToSet;
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
