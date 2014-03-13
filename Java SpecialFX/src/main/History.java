package main;

import java.util.Vector;


public class History {
	
	private final int MAX_COMMAND_TO_STORE = 10;
	
	private Vector<Command> previousCommandList;
	
	public History () {
		previousCommandList = new Vector<Command>();
	}
	
	public Command getMostRecentCommand () {
		if (isCommandListEmpty()) {
			return null;
		}
		
		Command recentCommand = previousCommandList.lastElement();
		return recentCommand;
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
