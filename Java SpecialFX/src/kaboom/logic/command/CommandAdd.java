package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;



public class CommandAdd extends Command {
	
	private static final String MESSAGE_COMMAND_ADD_SUCCESS = "WOOT! <%1$s> ADDED. MORE STUFF TO DO!";
	private static final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add <%1$s>... Error somewhere...";
	private static final String MESSAGE_COMMAND_ADD_FAIL_NO_NAME = "Enter a task name please :'(";
	private static final String MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE = "Wow! How did the task end before it even started? 0.0";

	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		initialiseKeywordList();
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskListShop != null;
		
		String commandFeedback = "";
		
		if (taskInfo!=null && !taskInfo.getTaskName().isEmpty()) {
			if(DateAndTimeFormat.getInstance().dateValidityForStartAndEndDate(taskInfo.getStartDate(),taskInfo.getEndDate())) {
				taskInfo.setRecent(true);
				
				if (taskListShop.addTaskToList(taskInfo)) {
					commandFeedback = String.format(MESSAGE_COMMAND_ADD_SUCCESS, taskInfo.getTaskName());
				} else {
					commandFeedback = String.format(MESSAGE_COMMAND_ADD_FAIL, taskInfo.getTaskName());
				}
			} else {
				commandFeedback = MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE;
			}
			
		}
		else {
			commandFeedback = MESSAGE_COMMAND_ADD_FAIL_NO_NAME;
		}
		
		return createResult(taskListShop.getAllCurrentTasks(), commandFeedback);
	}
	
	public boolean undo () {
		String taskName = taskInfo.getTaskName();
		
		TaskInfo task = taskListShop.removeTaskByName(taskName);
		
		if (task == null)
			return false;
		else
			return true;
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.TASKID);
		keywordList.add(KEYWORD_TYPE.PRIORITY);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}
	
	protected void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		taskInfo = new TaskInfo();
		saveTaskName(infoHashes,taskInfo);
		saveTaskPriority(infoHashes,taskInfo);
		saveTaskStartDateAndTime(infoHashes,taskInfo);
		saveTaskEndDateAndTime(infoHashes,taskInfo);
		setEndDateAndTimeToHourBlock (taskInfo);
		determineAndSetTaskType(taskInfo);
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
