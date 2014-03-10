
public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
	}

	public String execute() {
		if (taskInfoToBeModified != null) {
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
			return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, preModifiedTaskInfo.getTaskName());
		}
		
		else {
			return String.format(MESSAGE_COMMAND_MODIFY_FAIL, preModifiedTaskInfo.getTaskName());	
		}
	}
	
	public String undo () {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
}
