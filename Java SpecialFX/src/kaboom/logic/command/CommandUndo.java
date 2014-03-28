package kaboom.logic.command;

import kaboom.logic.Result;
import kaboom.storage.History;

public class CommandUndo extends Command{
	
	public CommandUndo() {
		commandType = COMMAND_TYPE.UNDO;
	}
	
	public Result execute() {
		Command commandToUndo = History.getInstance().getMostRecentCommand();
		String feedback = "";
		if (commandToUndo == null) {
			feedback = MESSAGE_COMMAND_UNDO_FAIL;
			return createResult(taskListShop.getAllTaskInList(),feedback);
		}
		
		feedback = commandToUndo.undo();
		return createResult(taskListShop.getAllTaskInList(),feedback);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
}
