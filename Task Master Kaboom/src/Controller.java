/* 
 * This is the main Controller for Task Master Kaboom
 * 
 */

public class Controller {
	
	enum COMMAND_TYPE {
		ADD, DELETE, MODIFY, SEARCH, INVALID;
	}
	
	
	public String getUserInput(String userInput) {
		String userCommand = "";; //=getCommand(userinput)
		COMMAND_TYPE commandType = determineCommandType(userCommand);
		String feedback = executeCommand(commandType);		
		return feedback;
	}
	
	
	private COMMAND_TYPE determineCommandType(String userCommand) {
		switch(userCommand) {
		case "add":
			return COMMAND_TYPE.ADD;
		case "delete":
			return COMMAND_TYPE.DELETE;
		case "modify":
			return COMMAND_TYPE.MODIFY;
		case "search":
			return COMMAND_TYPE.SEARCH;
		default:
			return COMMAND_TYPE.INVALID;
		}
	}
	
	
	private String executeCommand(COMMAND_TYPE command) {
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
	
}
