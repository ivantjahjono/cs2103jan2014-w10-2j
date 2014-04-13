//@author A0073731J

package kaboom.logic.command;

import kaboom.logic.TextParser;

public class CommandFactory {
	private final String KEYWORD_COMMAND_ADD = "add";
	private final String KEYWORD_COMMAND_DELETE = "delete";
	private final String KEYWORD_COMMAND_MODIFY = "modify";
	private final String KEYWORD_COMMAND_SEARCH = "search";
	private final String KEYWORD_COMMAND_CLEAR	 = "clear";
	private final String KEYWORD_COMMAND_VIEW = "view";
	private final String KEYWORD_COMMAND_UNDO = "undo";
	private final String KEYWORD_COMMAND_DONE = "boom";
	private final String KEYWORD_COMMAND_UNDONE = "unboom";
	private final String KEYWORD_COMMAND_HELP = "help";
	private final String KEYWORD_COMMAND_PAGE = "page";
	
	private static TextParser textParser = TextParser.getInstance();
	static CommandFactory commandFactoryInstance = null;
	
	private CommandFactory() {
	}
	
	public static CommandFactory getInstance() {
		if (commandFactoryInstance == null) {
			commandFactoryInstance = new CommandFactory();
		}
		return commandFactoryInstance;
	}
	
	
	public Command createCommand(String userInputSentence) {
		
		String commandKeyword = textParser.getCommandKeyWord(userInputSentence);
		COMMAND_TYPE commandType = determineCommandType(commandKeyword);
		Command commandToExecute = createCommandBasedOnCommandType(commandType);	
		String userInputSentenceWithCommandKeyWordRemoved = textParser.removeFirstWord(userInputSentence);
		commandToExecute.initialiseCommandInfoTable(userInputSentenceWithCommandKeyWordRemoved);
		
		return commandToExecute;
	}
	
	private COMMAND_TYPE determineCommandType(String commandWord) {
		commandWord = commandWord.toLowerCase();
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
			case KEYWORD_COMMAND_DONE:
				return COMMAND_TYPE.DONE;
			case KEYWORD_COMMAND_UNDONE:
				return COMMAND_TYPE.UNDONE;
			case KEYWORD_COMMAND_HELP:
				return COMMAND_TYPE.HELP;
			case KEYWORD_COMMAND_PAGE:
				return COMMAND_TYPE.PAGE;
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private Command createCommandBasedOnCommandType (COMMAND_TYPE commandType) {
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
				
			case DONE:
				newlyCreatedCommand = new CommandDone();
				break;
				
			case UNDONE:
				newlyCreatedCommand = new CommandUndone();
				break;
				
			case HELP:
				newlyCreatedCommand = new CommandHelp();
				break;
				
			case PAGE:
				newlyCreatedCommand = new CommandPage();
				break;
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}
}
