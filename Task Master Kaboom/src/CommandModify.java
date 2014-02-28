
public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
	}

	public String execute() {
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
	
	public String undo () {
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
}
