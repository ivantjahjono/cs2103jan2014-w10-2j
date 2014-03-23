package kaboom.logic;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import kaboom.logic.Error.ERROR_TYPE;
import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandAdd;
import kaboom.logic.command.CommandClear;
import kaboom.logic.command.CommandDelete;
import kaboom.logic.command.CommandModify;
import kaboom.logic.command.CommandSearch;
import kaboom.storage.History;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;
import kaboom.ui.GraphicInterface;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/

public class TaskMasterKaboom {
	
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final String KEYWORD_COMMAND_CLEAR = "clear";
	
	private static final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private static History historyofCommands = new History();
	
	// Temporary static
	private static int counter = 1;				// Use to create temporary task
	
	static TaskMasterKaboom instance;
	
	public static TaskMasterKaboom getInstance () {
		if (instance == null) {
			instance = new TaskMasterKaboom();
		}
		return instance;
	}
	
	private TaskMasterKaboom () {
	}
	
	public void initialiseKaboom() {
		// Setup application
			// Setup Memory
			//addTemporaryTaskForTesting();
		
			// Setup Storage
			initialiseStorage();
		
			// Setup UI
			guiDisplayData = DisplayData.getInstance();
			updateUiWithFirstLoadedMemory();
			//setupUi();
			
			
			// Setup Logic
		
		// Run the UI
		//activateUi();
		
		// Start processing user commands
	}

	private static void updateUiWithFirstLoadedMemory() {
		Result introResult = new Result();
		introResult.setTasksToDisplay(TaskListShop.getInstance().getAllTaskInList());
		introResult.setFeedback(MESSAGE_WELCOME);
		updateUi(introResult);
	}
	
	private static boolean initialiseStorage () {
		fileStorage = new Storage(FILENAME);
		fileStorage.load();
		
		return true;
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
	public static boolean processCommand(String userInputSentence) {
		assert userInputSentence != null;
		
		Command commandToExecute = null;
		Result commandResult = null;
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		String commandParametersString = TextParser.removeFirstWord(userInputSentence);
		
		commandToExecute = createCommandBasedOnCommandType(commandType);
		Error errorType = updateCommandInfoFromParameter(commandToExecute, commandParametersString);
		
		if (errorType == null) {
			try {
				commandResult = commandToExecute.execute();
			} catch (Exception e) {
				commandResult = new Result();
				commandResult.setFeedback("Error executing command! Please inform your administrator!");
			}
		} else {
			commandResult = new Result();
			commandResult.setFeedback(errorType.getErrorMessage());
		}
		
		updateUi(commandResult);
		
		// Add recent command to History list
		addToCommandHistory(new Command());
		
		// Save data to file
		fileStorage.store();
		
		return true;
	}
	

	private static void updateUi(Result commandResult) {
		guiDisplayData.updateDisplayWithResult(commandResult);
	}
	
	private static void addToCommandHistory(Command command) {
		if (command.getCommandType() != COMMAND_TYPE.INVALID) {
			historyofCommands.addToRecentCommands(command);
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
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}

	private static Error updateCommandInfoFromParameter(Command commandToUpdate, String parameters) {
		TaskInfo taskInformation = new TaskInfo();
		//to store existing taskinfo to be modified 
		TaskInfo taskInformationToBeModified = new TaskInfo();
		
		Error errorType = createTaskInfoBasedOnCommand(taskInformation, taskInformationToBeModified, parameters);
		commandToUpdate.setTaskInfo(taskInformation);
		
		//modify
		if (!taskInformationToBeModified.isEmpty()) {
			commandToUpdate.setTaskInfoToBeModified(taskInformationToBeModified);
		}

		return errorType;
	}
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		String commandTypeString = TextParser.getFirstWord(userCommand);
		
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
			case KEYWORD_COMMAND_CLEAR:
				return COMMAND_TYPE.CLEAR;
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static Error createTaskInfoBasedOnCommand(TaskInfo newTaskInfo, TaskInfo oldTaskInfo, String userInputSentence) {
		// Currently it is randomly generated.
		//updateTaskInfoBasedOnParameter(newlyCreatedTaskInfo, userInputSentence);
		
		Error errorEncountered = TextParser.updateTaskInfo(newTaskInfo, oldTaskInfo, userInputSentence);
		
		return errorEncountered;
	}


}
