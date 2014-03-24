package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.storage.History;

public class CommandUndo extends Command{
	
	public CommandUndo() {
		commandType = COMMAND_TYPE.UNDO;
	}
	
	public Result execute() {
		Command commandToUndo = History.getInstance().getMostRecentCommand();
		
		if (commandToUndo == null) {
			return createResult(taskListShop.getAllTaskInList(),MESSAGE_COMMAND_UNDO_FAIL);
		}
		
		commandToUndo.undo();
		return createResult(taskListShop.getAllTaskInList(),MESSAGE_COMMAND_UNDO_SUCCESS);
	}
}
