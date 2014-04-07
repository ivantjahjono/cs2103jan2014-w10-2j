package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.History;
import kaboom.ui.DISPLAY_STATE;
import kaboom.ui.TaskView;

public class CommandView extends Command{
	private final String KEYWORD_TODAY = "today";
	private final String KEYWORD_FUTURE = "future";
	private final String KEYWORD_TIMELESS = "timeless";
	private final String KEYWORD_EXPIRED = "expired";
	private final String KEYWORD_ARCHIVE = "archive";
	
	private final String MESSAGE_VIEW_TODAY 	= "Viewing all the tasks for today";
	private final String MESSAGE_VIEW_TIMELESS 	= "Viewing timeless tasks";
	private final String MESSAGE_VIEW_EXPIRED 	= "Viewing expired tasks";
	private final String MESSAGE_VIEW_FUTURE 	= "Viewing upcoming tasks";
	private final String MESSAGE_VIEW_ARCHIVE 	= "Viewing completed tasks";
	private final String MESSAGE_VIEW_INVALID 	= "Invalid View Mode";
	
	String 			viewType;
	DISPLAY_STATE 	stateToSet;
	
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
			case TODAY:
				taskList = taskListShop.getAllCurrentTasks();
				feedback = MESSAGE_VIEW_TODAY;
				break;
			case FUTURE:
				taskList = new Vector<TaskInfo>();
				feedback = MESSAGE_VIEW_FUTURE;
				break;
			case TIMELESS:
				taskList = taskListShop.getFloatingTasks();
				feedback = MESSAGE_VIEW_TIMELESS;
				break;
			case EXPIRED:
				taskList = taskListShop.getDeadlineTasks();
				feedback = MESSAGE_VIEW_EXPIRED;
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
		History.getInstance().setCurrentViewCommand(this);
		return commandResult;
	}

	private DISPLAY_STATE determineDisplayState(String viewType2) {
		switch(viewType) {
			case KEYWORD_TODAY:
				return DISPLAY_STATE.TODAY;
			case KEYWORD_FUTURE:
				return DISPLAY_STATE.FUTURE;
			case KEYWORD_TIMELESS:
				return DISPLAY_STATE.TIMELESS;
			case KEYWORD_EXPIRED:
				return DISPLAY_STATE.EXPIRED;
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
