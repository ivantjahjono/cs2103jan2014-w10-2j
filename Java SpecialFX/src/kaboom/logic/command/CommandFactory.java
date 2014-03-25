package kaboom.logic.command;

public class CommandFactory {
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final String KEYWORD_COMMAND_CLEAR = "clear";
	private static final String KEYWORD_COMMAND_VIEW = "view";
	private static final String KEYWORD_COMMAND_UNDO = "undo";
	
	public static Command createCommand(String commandWord) {
		COMMAND_TYPE commandType = determineCommandType(commandWord);
		return createCommandBasedOnCommandType(commandType);
	}
	
	
	private static COMMAND_TYPE determineCommandType(String commandWord) {		
		switch(commandWord) {
			case KEYWORD_COMMAND_ADD:
				return COMMAND_TYPE.ADD;
			case KEYWORD_COMMAND_DELETE:
				return COMMAND_TYPE.DELETE;
			case KEYWORD_COMMAND_MODIFY:
				return COMMAND_TYPE.MODIFY;
			case KEYWORD_COMMAND_SEARCH:
				return COMMAND_TYPE.SEARCH;
			case KEYWORD_COMMAND_CLEAR:
				return COMMAND_TYPE.CLEAR;
			case KEYWORD_COMMAND_VIEW:
				return COMMAND_TYPE.VIEW;
			case KEYWORD_COMMAND_UNDO:
				return COMMAND_TYPE.UNDO;
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static Command createCommandBasedOnCommandType (COMMAND_TYPE commandType) {
		Command newlyCreatedCommand = new Command();
		
		switch (commandType) {
			case ADD:
				newlyCreatedCommand = new CommandAdd();
				break;
				
			case DELETE:
				newlyCreatedCommand = new CommandDelete();
				break;
				
			case MODIFY:
				newlyCreatedCommand = new CommandModify();
				break;
				
			case SEARCH:
				newlyCreatedCommand = new CommandSearch();
				break;
				
			case CLEAR:
				newlyCreatedCommand = new CommandClear();
				break;
				
			case VIEW:
				newlyCreatedCommand = new CommandView();
				break;
				
			case UNDO:
				newlyCreatedCommand = new CommandUndo();
				break;
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}
}
