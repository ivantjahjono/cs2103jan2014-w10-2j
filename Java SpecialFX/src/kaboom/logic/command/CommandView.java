package kaboom.logic.command;

import java.util.Vector;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;

public class CommandView extends Command{
	private static final String KEYWORD_FLOAT = "floating";
	private static final String KEYWORD_RUNNING = "running";
	private static final String KEYWORD_DEADLINE = "deadline";
	private static final String KEYWORD_SEARCH = "search";
	
	private static final String MESSAGE_VIEW_FLOAT = "Floating Task Mode";
	private static final String MESSAGE_VIEW_RUNNING = "Running Task Mode";
	private static final String MESSAGE_VIEW_DEADLINE = "Deadline Task Mode";
	private static final String MESSAGE_VIEW_SEARCH = "Search Result Mode";
	private static final String MESSAGE_VIEW_INVALID = "Invalid View Mode";
	
	
	public CommandView () {
		commandType = COMMAND_TYPE.VIEW;
		initialiseKeywordList();
	}

	public Result execute() {
		assert TaskListShop.getInstance() != null;
		
		//temp
		assert taskInfo.getTaskName() != null;
		String viewToSwitch = taskInfo.getTaskName();
		String feedback = "";
		Vector<TaskInfo> taskList = null;
		
		switch(viewToSwitch) {
			case KEYWORD_FLOAT:
				taskList = TaskListShop.getInstance().getFloatingTasks();
				feedback = MESSAGE_VIEW_FLOAT;
				break;
			case KEYWORD_RUNNING:
				taskList = TaskListShop.getInstance().getNonExpiredTasks();
				feedback = MESSAGE_VIEW_RUNNING;
				break;
			case KEYWORD_DEADLINE:
				taskList = TaskListShop.getInstance().getDeadlineTasks();
				feedback = MESSAGE_VIEW_DEADLINE;
				break;
			case KEYWORD_SEARCH:
				//UNDER CONSTRUCTION LOL
			default:
				feedback = MESSAGE_VIEW_INVALID;
		}

		return createResult(taskList, feedback);
	}
	
	public String undo() {
		return null;
	}
	
	private void initialiseKeywordList() {
		keywordList.add(KEYWORD_TYPE.VIEWTYPE);
	}
}
