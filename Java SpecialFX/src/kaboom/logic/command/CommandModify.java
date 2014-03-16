package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.logic.TaskInfo;
import kaboom.storage.TaskListShop;


public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	public CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
	}

	public Result execute() {
		String commandFeedback = "";
		String taskName = "";
		
		if (taskInfoToBeModified != null) {
			taskName = taskInfoToBeModified.getTaskName();
		}
		if (!taskName.isEmpty()) {
			preModifiedTaskInfo = TaskListShop.getInstance().getTaskByName(taskName);
			preModifiedTaskInfo = TaskListShop.getInstance().getTaskByName(taskInfoToBeModified.getTaskName());
		}
		if (preModifiedTaskInfo != null) {
			taskInfoToBeModified = preModifiedTaskInfo;
			//transfer all the new information over to taskinfotobemodified
			if (taskInfo.getTaskName() != "") {
				taskInfoToBeModified.setTaskName (taskInfo.getTaskName());
			}
			if (taskInfo.getTaskType() != null) {
				taskInfoToBeModified.setTaskType (taskInfo.getTaskType());
			}
			if (taskInfo.getStartDate() != null) {
				taskInfoToBeModified.setStartDate (taskInfo.getStartDate());
			}
			if (taskInfo.getEndDate() != null) {
				taskInfoToBeModified.setEndDate (taskInfo.getEndDate());
			}
			if (taskInfo.getImportanceLevel() != 0) {
				taskInfoToBeModified.setImportanceLevel (taskInfo.getImportanceLevel());
			}
			
			//need error check
			TaskListShop.getInstance().updateTask (preModifiedTaskInfo, taskInfoToBeModified);
			
			//need to decide which taskName to return (previous or updated).
			commandFeedback = String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, preModifiedTaskInfo.getTaskName());
		} else {
			commandFeedback = String.format(MESSAGE_COMMAND_MODIFY_FAIL, preModifiedTaskInfo.getTaskName());
		}
		
		return createResult(taskListShop.getAllTaskInList(), commandFeedback);
	}
		
		

	
	public String undo () {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
}
