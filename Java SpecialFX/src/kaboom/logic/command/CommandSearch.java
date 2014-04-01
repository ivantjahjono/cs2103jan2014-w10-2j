package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.FormatIdentify;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TASK_TYPE;
import kaboom.storage.History;
import kaboom.ui.DISPLAY_STATE;

public class CommandSearch extends Command {

	private static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done. %d item(s) found.";

	public CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
		initialiseKeywordList();
	}

	public Result execute() {

		assert taskInfo != null;
		assert taskListShop != null;

		Calendar endDate = taskInfo.getEndDate();
		if (endDate != null) {
			//Set the end time to 2359 for searching		
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE, 59);
			taskInfo.setEndDate(endDate);
		}

		String commandFeedback;
		Vector<TaskInfo> tasksFound = new Vector<TaskInfo>();
		Vector<TaskInfo> allTasks = taskListShop.getAllCurrentTasks();

		String taskName = taskInfo.getTaskName();
		if (!taskName.equals("")) {
			//If taskName is not empty, search by task name			
			for (int i = 0; i < allTasks.size(); i++) {
				TaskInfo singleTask = allTasks.get(i);
				if (singleTask.getTaskName().contains(taskName)) {
					tasksFound.add(singleTask);  //Expired tasks are considered too
				}
			}
			
			//task id search (have to convert to get from task id)
			if(taskName.matches("\\d+")) {
				int id = Integer.parseInt(taskName);
				if(id < allTasks.size() && id >= 0) {
					tasksFound.add(allTasks.get(id-1));
				}
			}
		}
		else if (endDate != null){
			//search by date
			for (int i = 0; i < allTasks.size(); i++) {
				TaskInfo singleTask = allTasks.get(i);
				if (!singleTask.getTaskType().equals(TASK_TYPE.FLOATING)) {
					Calendar targetDate = taskInfo.getEndDate();
					if (singleTask.getEndDate().before(targetDate)) {
						//For deadline tasks
						tasksFound.add(singleTask);
					}
					if ((singleTask.getStartDate() != null && singleTask.getStartDate().before(targetDate)) || 
							singleTask.getEndDate().after(targetDate)) {
						//For timed tasks
						if (!tasksFound.contains(singleTask)) {
							tasksFound.add(singleTask);  //Expired tasks are considered too
						}
					}
				}
			}
		}

		History.getInstance().setViewingTasks(tasksFound);
		commandFeedback = String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, tasksFound.size());

		Result commandResult = createResult(tasksFound, commandFeedback);
		commandResult.setDisplayState(DISPLAY_STATE.SEARCH);
		
		return commandResult; 
	}

	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
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
		saveTaskName(infoHashes,taskInfo);
		saveTaskPriority(infoHashes,taskInfo);
		saveTaskStartDateAndTime(infoHashes,taskInfo);
		saveTaskEndDateAndTime(infoHashes,taskInfo);
	}
}
