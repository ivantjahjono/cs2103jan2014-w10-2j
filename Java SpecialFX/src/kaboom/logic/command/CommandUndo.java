package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.storage.History;

public class CommandUndo extends Command{
	

	private static final String MESSAGE_COMMAND_UNDO_SUCCESS = "Command undone!";
	private static final String MESSAGE_COMMAND_UNDO_FAIL = "Fail to undo.";
	private static final String MESSAGE_COMMAND_NOTHING_TO_UNDO = "Bo task to undo";
	
	
	public CommandUndo() {
		commandType = COMMAND_TYPE.UNDO;
	}
	
	public Result execute() {
		Command commandToUndo = History.getInstance().getMostRecentCommand();
		String feedback = "";
		
		if (commandToUndo == null) {
			feedback = MESSAGE_COMMAND_NOTHING_TO_UNDO;
			return createResult(taskListShop.getAllTaskInList(),feedback);
		}
		
		boolean isUndoSuccessful = commandToUndo.undo();
		
		if (isUndoSuccessful) {
			feedback = MESSAGE_COMMAND_UNDO_SUCCESS;
			return createResult(taskListShop.getAllTaskInList(),feedback);
		}
		
		feedback = MESSAGE_COMMAND_UNDO_FAIL;
		return createResult(taskListShop.getAllTaskInList(),feedback);
	}
}
