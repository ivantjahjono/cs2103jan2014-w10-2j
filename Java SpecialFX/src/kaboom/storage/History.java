package kaboom.storage;

import java.util.Stack;

import kaboom.logic.command.Command;

public class History {

	private final int MAX_COMMAND_TO_STORE = 10;

	private static History historyInstance = null;

	private Stack<Command> previousCommandList;
	private Command currentCommandView;

	public static History getInstance () {
		if (historyInstance == null) {
			historyInstance = new History();
		}
		return historyInstance;
	}

	public History () {
		previousCommandList = new Stack<Command>();
		currentCommandView = null;
	}

	public Command getMostRecentCommand () {
		if (previousCommandList.empty()) {
			return null;
		}

		return previousCommandList.pop();
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

	public void addToRecentCommands(Command recentCommand) {
		previousCommandList.push(recentCommand);

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
