/*
** This is main class that will run Task Master Kaboom
** 
** 
**/


public class TaskMasterKaboom {
	
	enum COMMAND_TYPE {
		ADD, DELETE, MODIFY, SEARCH, INVALID;
	}
	
	public static final String KEYWORD_COMMAND_ADD = "add";
	public static final String KEYWORD_COMMAND_DELETE = "delete";
	public static final String KEYWORD_COMMAND_MODIFY = "modify";
	public static final String KEYWORD_COMMAND_SEARCH = "search";
	
	
	public static void main(String[] args) {
		// Setup application
			// Setup UI
			// Setup Memory
			// Setup Logic
		
		// Start processing user commands

		
		// Get command from UI
		String command = "add";
		
		// Process command line
		String commandFeedback = ProcessCommand(command);
	
		// Return feedback to
		System.out.println(commandFeedback);
	}
	
	/*
	 * Purpose: ProcessCommand will read the userCommand and break down into
	 * respective information for the task information. Currently, it returns
	 * the feedback for command that is executed.
	 * 
	 * Note: Public access allow execution from test driven development
	 * to run straight into command.
	 * 
	 * Future improvement: Return task class instead.
	 */	
	public static String ProcessCommand(String userCommand) {
		COMMAND_TYPE commandType = determineCommandType(userCommand);
		String feedback = executeCommand(commandType);		
		return feedback;
	}
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		String commandTypeString = getFirstWord(userCommand);
		
		// Determine what command to execute
		switch(commandTypeString) {
			case KEYWORD_COMMAND_ADD:
				return COMMAND_TYPE.ADD;
			case KEYWORD_COMMAND_DELETE:
				return COMMAND_TYPE.DELETE;
			case KEYWORD_COMMAND_MODIFY:
				return COMMAND_TYPE.MODIFY;
			case KEYWORD_COMMAND_SEARCH:
				return COMMAND_TYPE.SEARCH;
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String executeCommand(COMMAND_TYPE command) {
		switch(command) {
		case ADD:
			return "";
		case DELETE:
			return "";
		case MODIFY:
			return "";
		case SEARCH:
			return "";
		case INVALID:
			return "";
		default:
			return "";
		}
	}
	
	private static String getFirstWord(String userCommand) {
		return userCommand.trim().split(" ")[0];
	}
}
