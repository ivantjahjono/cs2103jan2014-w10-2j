package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.ui.DISPLAY_STATE;

public class CommandSearch extends Command {

	private final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done. %d item(s) found.";

	TaskInfo taskInfo = null;

	public CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.DATE,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.TASKNAME
		};
	}

	public Result execute() {

		assert taskInfo != null;
		assert taskListShop != null;
		
		Vector<TaskInfo> listToSearch = taskView.getCurrentView();
		Vector<TaskInfo> tasksFound = new Vector<TaskInfo>();


		String searchName = taskInfo.getTaskName().toLowerCase();
		Calendar searchByDate = taskInfo.getEndDate();
		Calendar searchOnDate = taskInfo.getStartDate();

		if (!searchName.equals("")) {
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				if (singleTask.getTaskName().toLowerCase().contains(searchName)) {
					if (!singleTask.getExpiryFlag()) {
						tasksFound.add(singleTask);  //Expired tasks are not considered
					}
				}
			}
		} else if (searchOnDate != null){
			//Search only on a particular day
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				Calendar taskStartDate = singleTask.getStartDate();
				Calendar taskEndDate = singleTask.getEndDate();
				if (taskStartDate != null) {
					if (taskStartDate.get(Calendar.DAY_OF_YEAR) == searchOnDate.get(Calendar.DAY_OF_YEAR) &&
							taskStartDate.get(Calendar.YEAR) == searchOnDate.get(Calendar.YEAR)) {
						tasksFound.add(singleTask);
					}
				} else if(taskEndDate!=null) {
					if (taskEndDate.get(Calendar.DAY_OF_YEAR) == searchOnDate.get(Calendar.DAY_OF_YEAR) &&
						taskEndDate.get(Calendar.YEAR) == searchOnDate.get(Calendar.YEAR)) {
						tasksFound.add(singleTask);
					}
				}
			}
		} else if (searchByDate != null) {
			//Search only on a particular day
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				Calendar taskEndDate = singleTask.getEndDate();
				if (taskEndDate != null) {
					if (DateAndTimeFormat.getInstance().isFirstDateBeforeSecondDate(taskEndDate, searchByDate)) {
						tasksFound.add(singleTask);
					}
				}
			}
		}

		String commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());
		taskView.setSearchView(tasksFound);

		return createResult(commandFeedback, DISPLAY_STATE.SEARCH); 
	}

	public boolean parseInfo(String info, Vector<FormatIdentify> indexList) {
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = updateFormatList(info, indexList);
		updateFormatListBasedOnHashtable(indexList, taskInformationTable);

		if (taskInformationTable.containsKey(KEYWORD_TYPE.INVALID)) {
			return false;
		}

		return true;
	}

	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {	
		taskInfo = new TaskInfo();
		saveTaskPriority(infoHashes,taskInfo);
		saveTaskStartDateAndTime(infoHashes,taskInfo);
		saveTaskEndDateAndTime(infoHashes,taskInfo);
		saveTaskName(infoHashes,taskInfo);
	}
}
