package kaboom.storage;

import java.util.Vector;

import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;


public class History {
	
	private final int MAX_COMMAND_TO_STORE = 10;
	
	private static History historyInstance = null;
	private Vector<Command> previousCommandList;
	
	public static History getInstance () {
		if (historyInstance == null) {
			historyInstance = new History();
		}
		return historyInstance;
	}
	
	public History () {
		previousCommandList = new Vector<Command>();
	}

	public Command getMostRecentCommand () {
		if (isCommandListEmpty()) {
			return null;
		}
		
		Command recentCommand = previousCommandList.lastElement();
		previousCommandList.remove(previousCommandList.size()-1);
		return recentCommand;
	}
	
	public Command getMostRecentCommandView () {
		if (isCommandListEmpty()) {
			return null;
		}
		
		for (int i = previousCommandList.size()-1; i >= 0; i--) {
			Command currentCommand = previousCommandList.get(i);
			if (currentCommand.getCommandType() == COMMAND_TYPE.VIEW) {
				return currentCommand;
			}
		}
		
		return null;
	}
	
	public int size() {
		return previousCommandList.size();
	}
	
	public void clear() {
		previousCommandList.clear();
	}

	private boolean isCommandListEmpty() {
		return previousCommandList.size() == 0;
	}
	
	public void addToRecentCommands (Command recentCommand) {
		previousCommandList.add(recentCommand);
		
		trimOutOldCommands();
	}
	
	private void trimOutOldCommands () {
		int firstObjectIndex = 0;
		
		while (previousCommandList.size() > MAX_COMMAND_TO_STORE) {
			previousCommandList.remove(firstObjectIndex);
		}
	}
}
