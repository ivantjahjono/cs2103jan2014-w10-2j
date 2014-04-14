//@author A0096670W

/**
 * CommandSearch.java:
 * This class performs the searching for tasks using either the name
 * or the date.
 */
package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.DateAndTimeFormat;
import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;

public class CommandSearch extends Command {

	private final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done. %d item(s) found.";
	private final String MESSAGE_COMMAND_SEARCH_ERROR = "Please enter something to search";

	TaskInfo taskInfo = null;
	Vector<TaskInfo> tasksFound;
	Vector<TaskInfo> listToSearch;

	public CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.DATE,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.TASKNAME
		};
		tasksFound = new Vector<TaskInfo>();
	}

	public Result execute() {

		assert taskManager != null;
		String commandFeedback;

		DateAndTimeFormat dateAndTimeFormat = DateAndTimeFormat.getInstance();

		String searchName = infoTable.get(KEYWORD_TYPE.TASKNAME).toLowerCase();
		String searchOnDateInString = dateAndTimeFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.DATE));
		String searchByDateInString = dateAndTimeFormat.convertStringDateToDayMonthYearFormat(infoTable.get(KEYWORD_TYPE.END_DATE));

		Calendar searchOnDate = dateAndTimeFormat.formatStringToCalendar(searchOnDateInString, dateAndTimeFormat.getEndTimeOfTheDay());
		Calendar searchByDate = dateAndTimeFormat.formatStringToCalendar(searchByDateInString, dateAndTimeFormat.getStartTimeOfTheDay());

		listToSearch = taskManager.getAllPresentTasks();

		if (!searchName.equals("")) {
			searchUsingName(searchName);
		} else if (searchOnDate != null){
			searchOnlyOnDate(searchOnDate);
		} else if (searchByDate != null) {
			searchCumulativeByDate(searchByDate);
		} else {
			commandFeedback = MESSAGE_COMMAND_SEARCH_ERROR;
			return createResult(commandFeedback);
		}

		commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());
		taskManager.setSearchView(tasksFound);

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

	private void searchCumulativeByDate(Calendar searchByDate) {
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

	private void searchOnlyOnDate(Calendar searchOnDate) {
		for (int i = 0; i < listToSearch.size(); i++) {
			TaskInfo singleTask = listToSearch.get(i);
			if (isTaskOnDate(singleTask, searchOnDate)) {
				tasksFound.add(singleTask);
			}
		}
	}

	private void searchUsingName(String searchName) {
		for (int i = 0; i < listToSearch.size(); i++) {
			TaskInfo singleTask = listToSearch.get(i);
			if (singleTask.getTaskName().toLowerCase().contains(searchName)) {
				tasksFound.add(singleTask);
			}
		}
	}

	/**
	 * This function checks if a task starts or ends on the date.
	 * Checks the start/end time or in between for timed tasks
	 * Checks the end time for deadline tasks
	 * @param task
	 * @param searchOnDate
	 */
	private boolean isTaskOnDate(TaskInfo task, Calendar searchOnDate) {
		Calendar taskStartDate = task.getStartDate();
		Calendar taskEndDate = task.getEndDate();

		if (taskStartDate != null) {
			if (isTaskStartOrEndDate(taskStartDate, searchOnDate)) {
				return true;
			} else if (isSearchDateBetweenTaskDates(taskStartDate, taskEndDate, searchOnDate)) {
				return true;
			}
		} else if (taskEndDate != null) {
			if (isTaskStartOrEndDate(taskEndDate, searchOnDate)) {
				return true;
			}
		}
		return false;
	}

	private boolean isSearchDateBetweenTaskDates(Calendar taskStartDate,
			Calendar taskEndDate, Calendar searchOnDate) {
		//For tasks that start before and ends after the search date
		//Since there is a start date, there must surely be an end date
		assert taskEndDate != null;
		return taskStartDate.before(searchOnDate) && taskEndDate.after(searchOnDate);
	}

	private boolean isTaskStartOrEndDate(Calendar taskDate, Calendar searchDate) {
		return taskDate.get(Calendar.DAY_OF_YEAR) == searchDate.get(Calendar.DAY_OF_YEAR) &&
				taskDate.get(Calendar.YEAR) == searchDate.get(Calendar.YEAR);
	}

	/**
	 * This class searches for tasks that ends by that particular date.
	 * Does not include tasks that start before but ends after that date.
	 * @param task
	 * @param searchByDate
	 */
	private boolean isTaskByDate(TaskInfo task, Calendar searchByDate) {
		Calendar taskEndDate = task.getEndDate();

		if (taskEndDate != null) {
			if (isTaskStartOrEndDate(taskEndDate, searchByDate)) {
				return true;
			} else if (isTaskBetweenNowAndSearchDate(taskEndDate, searchByDate)) {
				return true;
			}
		}
		return false;
	}

	private boolean isTaskBetweenNowAndSearchDate(Calendar taskEndDate, Calendar searchByDate) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 00);
		today.set(Calendar.MINUTE, 00);
		today.set(Calendar.SECOND, 00);
		return taskEndDate.after(today) && taskEndDate.before(searchByDate);
	}
}
