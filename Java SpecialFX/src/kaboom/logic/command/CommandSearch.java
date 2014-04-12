//@author A0096670W
package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

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

		String searchName = taskInfo.getTaskName().toLowerCase();
		Calendar searchByDate = taskInfo.getEndDate();
		Calendar searchOnDate = taskInfo.getStartDate();

		Vector<TaskInfo> tasksFound = new Vector<TaskInfo>();
		Vector<TaskInfo> listToSearch;

		if (searchByDate != null || searchOnDate != null) {
			listToSearch = taskListShop.getAllCurrentTasks();
		} else {
			listToSearch = taskView.getCurrentView();
		}

		if (!searchName.equals("")) {
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				if (singleTask.getTaskName().toLowerCase().contains(searchName)) {
					tasksFound.add(singleTask);
				}
			}
		} else if (searchOnDate != null){
			//Search only on a particular day
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				if (isTaskOnDate(singleTask, searchOnDate)) {
					tasksFound.add(singleTask);
				}
			}
		} else if (searchByDate != null) {
			//Cumulative search to a particular day
			for (int i = 0; i < listToSearch.size(); i++) {
				TaskInfo singleTask = listToSearch.get(i);
				Calendar taskEndDate = singleTask.getEndDate();
				if (taskEndDate != null) {
					if (isTaskByDate(singleTask, searchByDate)) {
						tasksFound.add(singleTask);
					}
				}
			}
		}

		String commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());
		taskView.setSearchView(tasksFound);

		return createResult(commandFeedback, DISPLAY_STATE.SEARCH, null); 
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

	//Checks if tasks starts or ends on a particular date
	//Checks the start time of timed tasks or in between
	//Checks the end time of deadline tasks
	private boolean isTaskOnDate(TaskInfo task, Calendar searchOnDate) {
		Calendar taskStartDate = task.getStartDate();
		Calendar taskEndDate = task.getEndDate();

		if (taskStartDate != null) {
			if (taskStartDate.get(Calendar.DAY_OF_YEAR) == searchOnDate.get(Calendar.DAY_OF_YEAR) &&
					taskStartDate.get(Calendar.YEAR) == searchOnDate.get(Calendar.YEAR)) {
				return true;
			} else if (taskStartDate.before(searchOnDate) && taskEndDate.after(searchOnDate)) {
				//For tasks that start before and ends after the search date
				//Since there is a start date, there must surely be an end date
				return true;
			}
		} else if (taskEndDate != null) {
			if (taskEndDate.get(Calendar.DAY_OF_YEAR) == searchOnDate.get(Calendar.DAY_OF_YEAR) &&
					taskEndDate.get(Calendar.YEAR) == searchOnDate.get(Calendar.YEAR)) {
				return true;
			}
		}
		return false;
	}

	//Searches tasks that ends by that particular date
	//Does not include tasks that start before but end after that date
	private boolean isTaskByDate(TaskInfo task, Calendar searchByDate) {
		Calendar taskEndDate = task.getEndDate();
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 00);
		today.set(Calendar.MINUTE, 00);
		today.set(Calendar.SECOND, 00);

		if (taskEndDate != null) {
			if (taskEndDate.get(Calendar.DAY_OF_YEAR) == searchByDate.get(Calendar.DAY_OF_YEAR) &&
					taskEndDate.get(Calendar.YEAR) == searchByDate.get(Calendar.YEAR)) {
				return true;
			} else if (taskEndDate.after(today) && taskEndDate.before(searchByDate)) {
				return true;
			}
		}
		return false;
	}
}
