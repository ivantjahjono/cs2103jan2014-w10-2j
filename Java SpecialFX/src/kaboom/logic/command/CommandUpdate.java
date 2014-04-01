package kaboom.logic.command;

import kaboom.logic.Result;

public class CommandUpdate extends Command {

	public Result execute() {
		taskListShop.refreshTasks();
		
		return createResult(taskListShop.getAllCurrentTasks(), null);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
}
