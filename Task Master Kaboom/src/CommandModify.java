
public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
	}

	public String execute() {
		String taskName = "";
		if (taskInfoToBeModified != null) {
			taskName = taskInfoToBeModified.getTaskName();
		}
		if (!taskName.isEmpty()) {
			preModifiedTaskInfo = TaskListShop.getInstance().getTaskByName(taskName);
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
			return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, taskName);
		}
		
		
		return String.format(MESSAGE_COMMAND_MODIFY_FAIL, taskName);	
	}
	
	public String undo () {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
}
