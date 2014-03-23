package kaboom.logic.command;

import kaboom.logic.Result;

public class CommandUpdate extends Command {

	public Result execute() {
		taskListShop.refreshTasks();
		
		return createResult(taskListShop.getAllTaskInList(), null);
	}
}
