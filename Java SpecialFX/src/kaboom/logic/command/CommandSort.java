package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;

public class CommandSort extends Command {

	private final String MESSAGE_COMMAND_SORT_SUCCESS = "Sort done. %d item(s) sorted.";

	TaskInfo taskInfo = null;
	
	public CommandSort() {
		commandType = COMMAND_TYPE.SORT;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.END_DATE,
				KEYWORD_TYPE.START_TIME,
				KEYWORD_TYPE.START_DATE,
				KEYWORD_TYPE.PRIORITY,
				KEYWORD_TYPE.TASKNAME
		};
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;

		if (!taskInfo.getTaskName().equals("")) {
			taskListShop.sort(KEYWORD_TYPE.TASKNAME);
		}
		else if (taskInfo.getStartDate() != null) {
			taskListShop.sort(KEYWORD_TYPE.START_DATE);

		}
		else if (taskInfo.getEndDate() != null) {
			taskListShop.sort(KEYWORD_TYPE.END_DATE);
		}
		else {
			taskListShop.sort(KEYWORD_TYPE.PRIORITY);
		}

		String commandFeedback = String.format(MESSAGE_COMMAND_SORT_SUCCESS, taskListShop.shopSize());
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
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
