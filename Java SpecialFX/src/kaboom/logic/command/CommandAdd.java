package kaboom.logic.command;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.FormatIdentify;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TaskInfo;



public class CommandAdd extends Command {
	private final String MESSAGE_COMMAND_ADD_SUCCESS = "WOOT! <%1$s> ADDED. MORE STUFF TO DO!";
	private final String MESSAGE_COMMAND_ADD_FAIL = "Fail to add <%1$s>... Error somewhere...";
	private final String MESSAGE_COMMAND_ADD_FAIL_NO_NAME = "Enter a task name please :'(";
	private final String MESSAGE_COMMAND_ADD_FAIL_STARTDATE_OVER_ENDDATE = "Wow! How did the task end before it even started? 0.0";

	
	public CommandAdd () {
		commandType = COMMAND_TYPE.ADD;
		keywordList = new KEYWORD_TYPE[] {
				KEYWORD_TYPE.PRIORITY,
				KEYWORD_TYPE.END_TIME,
				KEYWORD_TYPE.END_DATE,
				KEYWORD_TYPE.START_TIME,
				KEYWORD_TYPE.START_DATE,
				KEYWORD_TYPE.TASKNAME
		};
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
