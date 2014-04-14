//@author A0096670W

/**
 * History.java:
 * This class stores and retrieves commands that are saved for undo purposes.
 * The commands are stored in a stack and the limit set is 10 commands. 
 * This is to ensure that the program does not take up too much memory even if
 * the user runs Task Master Kaboom for extended periods. 
 * This is a singleton class as there can only be one instance of this class. 
 */
package kaboom.storage;

import java.util.Stack;

import kaboom.logic.command.Command;

public class History {

	private final int MAX_COMMAND_TO_STORE = 10;

	private static History historyInstance = null;

	private Stack<Command> previousCommandList;

	public static History getInstance () {
		if (historyInstance == null) {
			historyInstance = new History();
		}
		return historyInstance;
	}

	private History() {
		previousCommandList = new Stack<Command>();
	}

	public Command getMostRecentCommand() {
		if (previousCommandList.empty()) {
			return null;
		}

		return previousCommandList.pop();
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

	private void trimOutOldCommands() {
		int firstObjectIndex = 0;

		while (previousCommandList.size() > MAX_COMMAND_TO_STORE) {
			previousCommandList.remove(firstObjectIndex);
		}
	}
}
