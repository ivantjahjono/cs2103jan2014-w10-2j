//@author A0073731J

package kaboom.logic.command;

import kaboom.shared.Result;

public class CommandUpdate extends Command {
	public Result execute() {
		taskManager.refreshTasksAndResetRecent();
		
		return createResult(null);
	}
	
	public boolean parseInfo(String info) {
		return true;
	}
}
