package kaboom.logic.command;

import java.util.Hashtable;
import java.util.Vector;

import kaboom.logic.KEYWORD_TYPE;
import kaboom.logic.TextParser;

public class CommandFactory {
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final String KEYWORD_COMMAND_CLEAR = "clear";
	private static final String KEYWORD_COMMAND_VIEW = "view";
	private static final String KEYWORD_COMMAND_UNDO = "undo";
	private static final String KEYWORD_COMMAND_DONE = "boom";
	
	public static Command createCommand(String userInputSentence) {
		
		//1. Get Command 
		String commandKeyword = TextParser.getCommandKeyWord(userInputSentence);
		
		//2. Get Command keyword
		COMMAND_TYPE commandType = determineCommandType(commandKeyword);
		
		//2. Create Command
		Command commandToExecute = createCommandBasedOnCommandType(commandType);	
		
		//3. Remove Command Word From UserInput
		userInputSentence = TextParser.removeFirstWord(userInputSentence);
		
		//4. Get CommandKeywordList
		Vector<KEYWORD_TYPE> commandKeywordList = commandToExecute.getKeywordList();
		
		//5. Extract Task Info Base on Keywords
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = TextParser.testExtractList(userInputSentence, commandKeywordList);
		
		//6. Command stores TaskInfo
		commandToExecute.extractAndStoreTaskInfo(taskInformationTable);
		
		
		return commandToExecute;
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
			case KEYWORD_COMMAND_DONE:
				return COMMAND_TYPE.DONE;
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
				
			case DONE:
				newlyCreatedCommand = new CommandDone();
				break;
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}
}
