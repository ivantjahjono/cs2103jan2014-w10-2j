package kaboom.logic;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
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

	
	static TaskMasterKaboom instance;
	
	private TaskMasterKaboom () {
		
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
//	public boolean processCommand(String userInputSentence) {
//		assert userInputSentence != null;
//		
//		Command commandToExecute = null;
//		Result commandResult = null;
////		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
////		String commandParametersString = TextParser.removeFirstWord(userInputSentence);
//		
//		String commandKeyword = TextParser.getCommandKeyWord(userInputSentence);
//		commandToExecute = CommandFactory.createCommand(commandKeyword);		
//		Hashtable<KEYWORD_TYPE, String> taskInformationTable = TextParser.extractTaskInformation(userInputSentence);
//		
//		updateCommandInfoBasedOnTaskInformationTable(commandToExecute, taskInformationTable);
//		
//		try {
//			commandResult = commandToExecute.execute();
//		} catch (Exception e) {
//			commandResult = new Result();
//			commandResult.setFeedback("Error executing command! Please inform your administrator!");
//		}
//		
//		updateUi(commandResult);
//		
//		// Add recent command to History list
//		addToCommandHistory(new Command());
//		
//		// Save data to file
//		fileStorage.store();
//		
//		return true;
//	}
	
	//***********************************************
	//			THE NEW CONTROLLER IS HERE	(LIVE)	*
	//***********************************************
	public boolean processCommand(String userInputSentence) {
		assert userInputSentence != null;
	
		Command commandToExecute = null;
		Result commandResult = null;
		
		//1. Get Command 
		String commandKeyword = TextParser.getCommandKeyWord(userInputSentence);
		
		//2. Create Command
		commandToExecute = CommandFactory.createCommand(commandKeyword);		
		
		//3. Remove Command Word From UserInput
		userInputSentence = TextParser.removeFirstWord(userInputSentence);
		
		//4. Get CommandKeywordList
		Vector<KEYWORD_TYPE> commandKeywordList = commandToExecute.getKeywordList();
		
		//5. Extract Task Info Base on Keywords
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = extractTaskInfo(userInputSentence, commandKeywordList);
		
		//6. Command stores TaskInfo
		commandToExecute.storeTaskInfo(taskInformationTable);
		
		try {
			commandResult = commandToExecute.execute();
		} catch (Exception e) {
			commandResult = new Result();
			commandResult.setFeedback("Error executing command! Please inform your administrator!");
		}
		
		updateUi(commandResult);
		
		// Add recent command to History list
		addToCommandHistory(commandToExecute);
		
		// Save data to file
		fileStorage.store();
		
		return true;
	}
	

	private void updateUi(Result commandResult) {
		guiDisplayData.updateDisplayWithResult(commandResult);
	}
	
	private void addToCommandHistory(Command command) {
		if (command.getCommandType() != COMMAND_TYPE.INVALID && command.getCommandType() != COMMAND_TYPE.UNDO && 
				command.getCommandType() != COMMAND_TYPE.SEARCH) {
			History.getInstance().addToRecentCommands(command);
		}
	}

	
	//********************************
	private void updateCommandInfoBasedOnTaskInformationTable(Command commandToUpdate, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		
		TaskInfo newTaskInfo = getNewTaskInfoFromTaskInformationTable(taskInformationTable);
		TaskInfo taskInfoToModify = getTaskInfoToModifyTaskInformationTable(taskInformationTable);
		
		commandToUpdate.setTaskInfo(newTaskInfo);
		
		//modify
		if (!taskInfoToModify.isEmpty()) {
			commandToUpdate.setTaskInfoToBeModified(taskInfoToModify);
		}

	}

	private TaskInfo getNewTaskInfoFromTaskInformationTable(Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		TaskInfo taskInfo = new TaskInfo();
		
		taskInfo.setTaskName(taskInformationTable.get(KEYWORD_TYPE.TASKNAME));
		
		//if (taskInformationTable.contains(KEYWORD_TYPE.START_TIME)) {
			String startDate = taskInformationTable.get(KEYWORD_TYPE.START_DATE);
			String startTime = taskInformationTable.get(KEYWORD_TYPE.START_TIME);
			Calendar startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
			taskInfo.setStartDate(startDateAndTime);
		//}
		
		//if (taskInformationTable.contains(KEYWORD_TYPE.END_TIME)) {
			String endDate = taskInformationTable.get(KEYWORD_TYPE.END_DATE);
			String endTime = taskInformationTable.get(KEYWORD_TYPE.END_TIME);
			Calendar endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
			taskInfo.setEndDate(endDateAndTime);
		//}
				
		taskInfo.setTaskType(TASK_TYPE.TIMED);	// HARDCODED TO DEFAULT
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.PRIORITY)) {
			String priority = taskInformationTable.get(KEYWORD_TYPE.PRIORITY);
			taskInfo.setImportanceLevel(Integer.parseInt(priority));
		}
		
		return taskInfo;
	}

	//This function calls the text parser to get the information that is expected in the keyword list
	//and returns it to the caller
	private static Hashtable<KEYWORD_TYPE, String> extractTaskInfo(String userInputSentence, Vector<KEYWORD_TYPE> expectedKeywordList) {
		//TODO
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = TextParser.testExtractList(userInputSentence,expectedKeywordList);
		return taskInformationTable;
	}
	
	private TaskInfo getTaskInfoToModifyTaskInformationTable(Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskName(taskInformationTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
		return taskInfo;
	}
}
