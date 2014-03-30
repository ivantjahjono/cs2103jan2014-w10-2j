package kaboom.storage;

import java.util.Vector;

import kaboom.logic.TaskInfo;
import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandFactory;

public class History {

	private final int MAX_COMMAND_TO_STORE = 10;

	private static History historyInstance = null;

	private Vector<Command> previousCommandList;
	public Vector<TaskInfo> tasksToView;  //Tasks that are being viewed by the UI
	private Command currentCommandView;
	public Vector<Integer> taskID;  //The corresponding position in the vector

	public static History getInstance () {
		if (historyInstance == null) {
			historyInstance = new History();
		}
		return historyInstance;
	}

	public History () {
		previousCommandList = new Vector<Command>();
		currentCommandView = null;
		tasksToView = new Vector<TaskInfo>();
		taskID = new Vector<Integer>();
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

	public void setViewingTasks(Vector<TaskInfo> viewingTasks) {
		tasksToView = viewingTasks;
		taskID = TaskListShop.getInstance().getCorrespondingID(viewingTasks);
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
