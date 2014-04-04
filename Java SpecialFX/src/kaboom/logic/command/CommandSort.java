package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;

public class CommandSort extends Command {

	private final String MESSAGE_COMMAND_SORT_SUCCESS = "Sort done. %d item(s) sorted.";

	public CommandSort() {
		commandType = COMMAND_TYPE.SORT;
		initializeKeywordList();
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

	public void initializeKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKNAME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.PRIORITY);
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
