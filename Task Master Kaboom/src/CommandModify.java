
public class CommandModify extends Command {
	
	TaskInfo preModifiedTaskInfo;		// Use to store premodified data so that can undo later
	
	CommandModify () {
		commandType = COMMAND_TYPE.MODIFY;
	}

	public String execute() {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
	
	public String undo () {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "My Task");
	}
}
