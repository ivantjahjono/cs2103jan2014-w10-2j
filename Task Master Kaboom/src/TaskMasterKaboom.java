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
	
	public static final String MESSAGE_COMMAND_ADD_SUCCESS = "Successfully added %1$s";
	public static final String MESSAGE_COMMAND_DELETE_SUCCESS = "%1$s deleted.";
	public static final String MESSAGE_COMMAND_MODIFY_SUCCESS = "Modify %1$s successful";
	public static final String MESSAGE_COMMAND_SEARCH_SUCCESS = "Search done";
	public static final String MESSAGE_COMMAND_INVALID = "Invalid command!";
	
	
	public static void main(String[] args) {
		// Setup application
			// Setup UI
			// Setup Memory
			// Setup Logic
		
		// Start processing user commands

		
		// Get command from UI
		String command = "add";
		
		// Process command line
		String commandFeedback = processCommand(command);
	
		// Return feedback to
		System.out.println("Feedback: " + commandFeedback);
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
	public static String processCommand(String userInputSentence) {
		
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		if (commandType != COMMAND_TYPE.INVALID){
			TaskInfo currentTaskInfo = createTaskInfoBasedOnCommand(userInputSentence);
		}
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
			return String.format(MESSAGE_COMMAND_ADD_SUCCESS, "MYTASK");
		case DELETE:
			return String.format(MESSAGE_COMMAND_DELETE_SUCCESS, "MYTASK");
		case MODIFY:
			return String.format(MESSAGE_COMMAND_MODIFY_SUCCESS, "MYTASK");
		case SEARCH:
			return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "MYTASK");
		case INVALID:
			return String.format(MESSAGE_COMMAND_SEARCH_SUCCESS, "MYTASK");
		default:
			return MESSAGE_COMMAND_INVALID;
		}
	}
	
	private static TaskInfo createTaskInfoBasedOnCommand(String userInputSentence) {
		return null;
	}
	
	private static String[] textProcess(String userCommand){
		String[] commandWord = userCommand.trim().split("\\s+");
		return commandWord;
	}
	
	private static String getFirstWord(String userCommand) {
		String[] elements = textProcess(userCommand);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
}
