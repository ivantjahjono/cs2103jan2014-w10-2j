package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.shared.FormatIdentify;
import kaboom.shared.KEYWORD_TYPE;
import kaboom.shared.Result;
import kaboom.storage.History;

public class CommandUndo extends Command{
	private final String MESSAGE_COMMAND_UNDO_SUCCESS = "Command undone!";
	private final String MESSAGE_COMMAND_UNDO_FAIL = "Fail to undo.";
	private final String MESSAGE_COMMAND_NOTHING_TO_UNDO = "No more action to undo";
	
	public CommandUndo() {
		commandType = COMMAND_TYPE.UNDO;
	}
	
	public Result execute() {
		Command commandToUndo = History.getInstance().getMostRecentCommand();
		String feedback = "";
		
		if (commandToUndo == null) {
			feedback = MESSAGE_COMMAND_NOTHING_TO_UNDO;
			return createResult(feedback);
		}
		
		boolean isUndoSuccessful = commandToUndo.undo();
		
		if (isUndoSuccessful) {
			feedback = MESSAGE_COMMAND_UNDO_SUCCESS;
			return createResult(feedback);
		}
		
		taskView.refreshTasks();
		feedback = MESSAGE_COMMAND_UNDO_FAIL;
		return createResult(feedback);
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
