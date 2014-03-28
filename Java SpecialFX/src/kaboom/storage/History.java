package kaboom.storage;

import java.util.Vector;

import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandFactory;


public class History {
	
	private final int MAX_COMMAND_TO_STORE = 10;
	
	private static History historyInstance = null;
	
	private Vector<Command> previousCommandList;
	private Command currentCommandView;
	
	public static History getInstance () {
		if (historyInstance == null) {
			historyInstance = new History();
		}
		return historyInstance;
	}
	
	public History () {
		previousCommandList = new Vector<Command>();
		currentCommandView = null; 
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
		return currentCommandView;
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
	
	public void addToRecentCommands(Command recentCommand) {
		previousCommandList.add(recentCommand);
		
		trimOutOldCommands();
	}
	
	public void setCurrentViewCommand(Command setView) {
		currentCommandView = setView;
	}
	
	private void trimOutOldCommands () {
		int firstObjectIndex = 0;
		
		while (previousCommandList.size() > MAX_COMMAND_TO_STORE) {
			previousCommandList.remove(firstObjectIndex);
		}
	}
}
