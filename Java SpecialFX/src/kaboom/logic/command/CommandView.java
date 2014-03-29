package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;
import kaboom.ui.DISPLAY_STATE;

public class CommandView extends Command{
	private static final String KEYWORD_RUNNING = "running";
	private static final String KEYWORD_DEADLINE = "deadline";
	private static final String KEYWORD_TIMED = "timed";
	private static final String KEYWORD_SEARCH = "search";
	private static final String KEYWORD_ALL = "all";
	
	private static final String MESSAGE_VIEW_RUNNING = "Running Task Mode";
	private static final String MESSAGE_VIEW_DEADLINE = "Deadline Task Mode";
	private static final String MESSAGE_VIEW_TIMED = "Timed Task Mode";
	private static final String MESSAGE_VIEW_ALL = "All Task Mode";
	private static final String MESSAGE_VIEW_SEARCH = "Search Result Mode";
	private static final String MESSAGE_VIEW_INVALID = "Invalid View Mode";
	
	String viewType;
	
	public CommandView () {
		commandType = COMMAND_TYPE.VIEW;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskListShop != null;
		
		String feedback = "";
		Vector<TaskInfo> taskList = null;
		Result commandResult = createResult(taskList, feedback);
		
		if(viewType == null) {
			viewType = "Nothing to view";
		}
		switch(viewType) {
			case KEYWORD_RUNNING:
				taskList = taskListShop.getFloatingTasks();
				feedback = MESSAGE_VIEW_RUNNING;
				commandResult.setDisplayState(DISPLAY_STATE.RUNNING);
				break;
			case KEYWORD_DEADLINE:
				taskList = taskListShop.getDeadlineTasks();
				feedback = MESSAGE_VIEW_DEADLINE;
				commandResult.setDisplayState(DISPLAY_STATE.DEADLINE);
				break;
			case KEYWORD_TIMED:
				taskList = taskListShop.getTimedTasks();
				feedback = MESSAGE_VIEW_TIMED;
				commandResult.setDisplayState(DISPLAY_STATE.TIMED);
				break;
			case KEYWORD_ALL:
				taskList = taskListShop.getAllTaskInList();
				feedback = MESSAGE_VIEW_ALL;
				commandResult.setDisplayState(DISPLAY_STATE.ALL);
			case KEYWORD_SEARCH:
				//UNDER CONSTRUCTION LOL
				break;
			default:
				feedback = MESSAGE_VIEW_INVALID;
		}
		
		commandResult.setTasksToDisplay(taskList);
		commandResult.setFeedback(feedback);
		return commandResult;
	}

	private void initialiseKeywordList() {
		keywordList.add(KEYWORD_TYPE.VIEWTYPE);
	}
	
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		viewType = infoHashes.get(KEYWORD_TYPE.VIEWTYPE);
	}
	
	public void setViewType (String newViewType) {
		viewType = newViewType;
	}
	
	public String getViewType () {
		return viewType;
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
	
}
