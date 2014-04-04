package kaboom.logic;

import java.util.Vector;

import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandFactory;
import kaboom.logic.command.CommandUpdate;
import kaboom.storage.History;
import kaboom.storage.Storage;
import kaboom.storage.TaskListShop;
import kaboom.ui.DisplayData;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/

public class TaskMasterKaboom {
	private final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private String FILENAME = "KABOOM_FILE.dat";
	
	private DisplayData 	guiDisplayData;
	private Storage 		fileStorage;
	private History 		commandHistory;
	
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
		// Setup Storage
		initialiseStorage();
	
		// Setup UI
		guiDisplayData = DisplayData.getInstance();
		updateUiWithFirstLoadedMemory();
	}
	
	public void setFilename (String newFilename) {
		FILENAME = newFilename;
	}

	private void updateUiWithFirstLoadedMemory() {
		Result introResult = new Result();
		
		introResult.setFeedback(MESSAGE_WELCOME);
		updateUi(introResult);
	}
	
	private boolean initialiseStorage () {
		fileStorage = new Storage(FILENAME);
		fileStorage.load();
		
		return true;
	}
	
	public void updateTaskList () {
		Command updateCommand = new CommandUpdate();
		updateCommand.execute();
		
		//4. Save data to file
		fileStorage.store();
		
		guiDisplayData.updateDisplayWithResult();
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
		Vector<FormatIdentify> characterIndexList = new Vector<FormatIdentify>();
		
		//System.out.println("Processing:" + usercommand);
		
		//1. Get Command 
		String commandKeyword = TextParser.getInstance().getCommandKeyWord(usercommand);
		
		//2. Create Command
		commandToExecute = CommandFactory.createCommand(commandKeyword);
		
		try {
			processResult = commandToExecute.parseInfo(usercommand, characterIndexList);
		} catch (Exception e) {
			
		}
	
		guiDisplayData.setFormatDisplay(characterIndexList);
		return processResult;
	}
	
	
	public void activateCommand (COMMAND_TYPE commandToActivate) {
		
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
			case DONE:
			case UNDONE:
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
