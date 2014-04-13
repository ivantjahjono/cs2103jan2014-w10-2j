//@author A0099863H
package kaboom.logic;

import java.util.Vector;

import kaboom.logic.command.COMMAND_TYPE;
import kaboom.logic.command.Command;
import kaboom.logic.command.CommandFactory;
import kaboom.logic.command.CommandUpdate;
import kaboom.shared.DISPLAY_STATE;
import kaboom.shared.FormatIdentify;
import kaboom.shared.Result;
import kaboom.shared.TaskInfo;
import kaboom.storage.Storage;
import kaboom.storage.TaskView;
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
	private CommandFactory commandFactory;
	
	static TaskMasterKaboom instance;
	
	private int refreshCounter;
	private int REFRESH_POINT = 3;
	
	private TaskMasterKaboom () {
		commandFactory = CommandFactory.getInstance();
		resetRefreshCounter();
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
		if (refreshCounter >= REFRESH_POINT) {
			Command updateCommand = new CommandUpdate();
			updateCommand.execute();
			
			//4. Save data to file
			fileStorage.store();
			
			guiDisplayData.updateDisplayWithResult();
			resetRefreshCounter();
		} else {
			refreshCounter++;
		}
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
	
		resetRefreshCounter();
		TaskView.getInstance().refreshAllTasksFlags();
		
		Command commandToExecute = null;
		Result commandResult = null;
		
		//1. Create Command
		commandToExecute = commandFactory.createCommand(userInputSentence);
	
		//2. Execute Command
		try {
			commandResult = commandToExecute.execute();
		} catch (Exception e) {
			commandResult = new Result();
			commandResult.setFeedback("Error executing command! Please inform your administrator!");
		}
		
		updateUi(commandResult);
		
		//3. Save data to file
		fileStorage.store();
		
		return commandResult.getFeedback();
	}

	private void resetRefreshCounter() {
		refreshCounter = 0;
	}
	
	public boolean processSyntax(String usercommand) {
		Command commandToExecute = null;
		boolean processResult = false;
		Vector<FormatIdentify> characterIndexList = new Vector<FormatIdentify>();
		
		commandToExecute = commandFactory.createCommand(usercommand);
		
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
	
	public Vector<Integer> updateTaskCount() {
		return TaskView.getInstance().getTasksCountList();
	}
	
	public Integer indexToGoTo(TaskInfo taskToFocus) {
		return TaskView.getInstance().getTaskPositionInView(taskToFocus);
	}
	
	public Vector<TaskInfo> setAndGetView(DISPLAY_STATE displayState) {
		return TaskView.getInstance().setAndGetView(displayState);
	}
}
