package kaboom.logic.command;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.DateAndTimeFormat;
import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.Result;
import kaboom.logic.TASK_TYPE;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;


public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	boolean toChangeStartTimeAndDate;
	boolean toChangeEndTimeAndDate;
	boolean toChangeTaskName;
	boolean toChangePriority;
	
	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
		initialiseKeywordList();
		toChangePriority = false;
		toChangeTaskName = false;
		toChangeStartTimeAndDate = false;
		toChangeEndTimeAndDate= false;
	}

	public Result execute() {
		assert taskInfo != null;
		assert taskInfoToBeModified != null;
		assert TaskListShop.getInstance() != null;
		
		
		String feedback = "";
		String taskName = "";
		
		//get name of TaskInfo that user wants to modify
		taskName = taskInfoToBeModified.getTaskName();
		//get TaskInfo that user wants to modify;
		preModifiedTaskInfo = TaskListShop.getInstance().getTaskByName(taskName);
		
		try {
			//store TaskInfo to modify into temp taskinfo
			TaskInfo temp = new TaskInfo(preModifiedTaskInfo);
			//transfer all the new information over
			if (toChangeTaskName) {
				//bug at textparser get modified name where if time and date commands are keyed in will be saved as taskname
				temp.setTaskName (taskInfo.getTaskName());
			}
			if (toChangeStartTimeAndDate) {
				temp.setStartDate (taskInfo.getStartDate());
			}
			if (toChangeEndTimeAndDate) {
				temp.setEndDate (taskInfo.getEndDate());
			}
			if (toChangePriority) {
				temp.setImportanceLevel (taskInfo.getImportanceLevel());
			}
			//set task type (buggy due to calendar not null)
			if(preModifiedTaskInfo.getTaskType() == TASK_TYPE.FLOATING){
				if(toChangeEndTimeAndDate) {
					temp.setTaskType (TASK_TYPE.DEADLINE);
				} 
				if(toChangeStartTimeAndDate) {
					temp.setTaskType (TASK_TYPE.TIMED);
				}
			}
			if(preModifiedTaskInfo.getTaskType() == TASK_TYPE.DEADLINE){
				if(toChangeStartTimeAndDate) {
					temp.setTaskType (TASK_TYPE.TIMED);
				}
			}
			//store and update in memory
			taskInfo = temp;
			TaskListShop.getInstance().updateTask(taskInfo, preModifiedTaskInfo);
			//set useless variable to null
			taskInfoToBeModified = null;
		} catch (Exception e) {
			feedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL, taskName);
			return createResult(taskListShop.getAllTaskInList(), feedback);
		}
		feedback = String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, preModifiedTaskInfo.getTaskName());
		return createResult(taskListShop.getAllTaskInList(), feedback);
	}
	
	public String undo () {
		System.out.println(preModifiedTaskInfo.getTaskName()+" > "+taskInfo.getTaskName());
		TaskListShop.getInstance().updateTask(preModifiedTaskInfo, taskInfo);
		return String.format(MESSAGE_COMMAND_UNDO_SUCCESS);
	}
	
	private void initialiseKeywordList() {
		keywordList.clear();
		keywordList.add(KEYWORD_TYPE.PRIORITY);
		keywordList.add(KEYWORD_TYPE.END_TIME);
		keywordList.add(KEYWORD_TYPE.END_DATE);
		keywordList.add(KEYWORD_TYPE.START_TIME);
		keywordList.add(KEYWORD_TYPE.START_DATE);
		keywordList.add(KEYWORD_TYPE.MODIFIED_TASKNAME);
		keywordList.add(KEYWORD_TYPE.TASKNAME);
	}
	
	public void storeTaskInfo(Hashtable<KEYWORD_TYPE, String> infoHashes) {
		String taskName = infoHashes.get(KEYWORD_TYPE.TASKNAME);
		taskInfoToBeModified = new TaskInfo();
		
		if (taskName != null) {
			taskInfoToBeModified.setTaskName(taskName);
		}
		
		taskInfo = new TaskInfo();
		String modifiedTaskName = infoHashes.get(KEYWORD_TYPE.MODIFIED_TASKNAME);
		if(modifiedTaskName != null) {
			taskInfo.setTaskName(modifiedTaskName);
			toChangeTaskName = true;
		}
		
		String priority = infoHashes.get(KEYWORD_TYPE.PRIORITY);
		if (priority != null) {
			taskInfo.setImportanceLevel(Integer.parseInt(priority));
			toChangePriority = true;
		}
		//The below are taken from the old controller methods
		String startDate = infoHashes.get(KEYWORD_TYPE.START_DATE);
		String startTime = infoHashes.get(KEYWORD_TYPE.START_TIME);
		String endDate = infoHashes.get(KEYWORD_TYPE.END_DATE);
		String endTime = infoHashes.get(KEYWORD_TYPE.END_TIME);
		//extra check to add 1 hour to start time if end time and date is null
		if(startDate != null && startTime != null) {
			if(endDate == null && endTime == null) {
				endDate = startDate;
				int endtime = Integer.parseInt(startTime) + 100;
				System.out.println(endtime);
				if(endtime >= 2400) {
					endtime -= 2400;
				}
				endTime = String.format("%04d", endtime);
				
			}
		}
		Calendar startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
		Calendar endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
		taskInfo.setStartDate(startDateAndTime);
		taskInfo.setEndDate(endDateAndTime);
		
		if(startDate != null && startTime != null) {
			toChangeStartTimeAndDate = true;
		}
		if(endDate != null && endTime != null) {
			toChangeEndTimeAndDate = true;
		}
	}
}
