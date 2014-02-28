
public class CommandSearch extends Command {
	
	CommandSearch () {
		commandType = COMMAND_TYPE.SEARCH;
	}

	public String execute() {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "My Task");
	}
	
	public String undo () {
		//TODO Not done
		return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "My Task");
	}
}
