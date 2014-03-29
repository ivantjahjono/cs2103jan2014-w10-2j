package kaboom.logic;

import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandFactory;
import kaboom.logic.command.CommandUpdate;
import kaboom.storage.History;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/

public class TaskMasterKaboom {
	
	private static final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private History commandHistory;
	
	static TaskMasterKaboom instance;
	
	private TaskMasterKaboom () {
		commandHistory = History.getInstance();
	}
	
	public static TaskMasterKaboom getInstance () {
		if (instance == null) {
			instance = new TaskMasterKaboom();
		}
		return instance;
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

	private void updateUiWithFirstLoadedMemory() {
		Result introResult = new Result();
		introResult.setTasksToDisplay(TaskListShop.getInstance().getAllTaskInList());
		introResult.setFeedback(MESSAGE_WELCOME);
		updateUi(introResult);
	}
	
	private boolean initialiseStorage () {
		fileStorage = new Storage(FILENAME);
		fileStorage.load();
		
		return true;
	}
	
	public void updateTaskList () {
		// TODO a hack around currently to update to current view mode
		
		Command updateCommand = new CommandUpdate();
		Result updateResult = updateCommand.execute();
		
		// Get the latest view command and execute it
		Command recentViewCommand = History.getInstance().getMostRecentCommandView();
		
		if (recentViewCommand != null) {
			updateResult = recentViewCommand.execute();
		}
		
		updateUi(updateResult);
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
	//***********************************************
	//			THE NEW CONTROLLER IS HERE	(LIVE)	*
	//***********************************************
	public String processCommand(String userInputSentence) {
		assert userInputSentence != null;
	
		Command commandToExecute = null;
		Result commandResult = null;
		
		//1. Create Command
		commandToExecute = CommandFactory.createCommand(userInputSentence);
	
		//2. Execute Command
		try {
			commandResult = commandToExecute.execute();
		} catch (Exception e) {
			commandResult = new Result();
			commandResult.setFeedback("Error executing command! Please inform your administrator!");
		}
		
		updateUi(commandResult);
		
		//3. Add recent command to History list
		addToCommandHistory(commandToExecute);
		
		//4. Save data to file
		fileStorage.store();
		
		return commandResult.getFeedback();
	}
	
	public boolean processSyntax(String usercommand) {
		Command commandToExecute = null;
		boolean processResult = false;
		
		if (usercommand.trim().equals("")) {
			return true;
		}
		
		//System.out.println("Processing:" + usercommand);
		
		//1. Get Command 
		String commandKeyword = TextParser.getCommandKeyWord(usercommand);
		
		//2. Create Command
		commandToExecute = CommandFactory.createCommand(commandKeyword);
		
		//3. Remove Command Word From UserInput
		usercommand = TextParser.removeFirstWord(usercommand);
		
		try {
			processResult = commandToExecute.parseInfo(usercommand);
		} catch (Exception e) {
			
		}

		return processResult;
	}
	

	private void updateUi(Result commandResult) {
		guiDisplayData.updateDisplayWithResult(commandResult);
	}
	
	private void addToCommandHistory(Command command) {
		COMMAND_TYPE currentCommandType = command.getCommandType();
		
		switch (currentCommandType) {
			case VIEW:
				commandHistory.setCurrentViewCommand(command);
				break;
				
			case ADD:
			case DELETE:
			case MODIFY:
				commandHistory.addToRecentCommands(command);
				break;
				
			case INVALID:
			case UNDO:
			case SEARCH:
				break;
				
			default:
				break;
		}
	}
	
	//********************************


}
