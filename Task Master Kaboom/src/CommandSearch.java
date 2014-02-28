
public class CommandSearch extends Command {
	
	CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
	}

	public String execute() {
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "My Task");
	}
	
	public String undo () {
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "My Task");
	}
}
