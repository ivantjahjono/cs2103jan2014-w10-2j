package main;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import kaboomUserInterface.GraphicInterface;


/*
** This is main class that will run Task Master KABOOM
** 
** 
**/
enum COMMAND_TYPE {
		ADD, DELETE, MODIFY, SEARCH, INVALID, CLEAR;
}

enum KEYWORD_TYPE {
	INVALID, TASKID, TASKNAME, MODIFIED_TASKNAME, START_DATE, START_TIME, END_DATE, END_TIME, PRIORITY, DATE
}

public class TaskMasterKaboom {
	
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	private static final String KEYWORD_COMMAND_CLEAR = "clear";
	
	private static final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static GraphicInterface taskUi;
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private static History historyofCommands = new History();
	
	private static boolean isRunning;
	
	public static void main(String[] args) {
		// Setup application
			// Setup Memory
			//addTemporaryTaskForTesting();
		
			// Setup Storage
			initialiseStorage();
		
			// Setup UI
			setupUi();
			guiDisplayData = DisplayData.getInstance();
			
			// Setup Logic
		
		// Run the UI
		activateUi();
		
		// Start processing user commands
		
//		// Get command from UI
//		String command = "add";
//		
//		// Process command line
//		String commandFeedback = processCommand(command);
//	
//		// Return feedback to
//		System.out.println("Feedback: " + commandFeedback);
		
//		isRunning = true;
//		while (isRunning) {
//			
//		}
	}
	
	private static void setupUi () {
		taskUi = new GraphicInterface();
	}
	
	private static void activateUi () {
		updateUiWithFirstLoadedMemory();
		taskUi.run(null);
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
	
	public static void exitProgram () {
		isRunning = false;
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
		Command commandToExecute = null;
		Result commandResult = null;
		
		String commandKeyword = TextParser.getCommandKeyWord(userInputSentence);
		
		COMMAND_TYPE commandType = determineCommandType(commandKeyword);

		commandToExecute = createCommandBasedOnCommandType(commandType);
		
		Hashtable<KEYWORD_TYPE, String> taskInformationTable = TextParser.extractTaskInformation(userInputSentence);
		
		Error errorType = updateCommandInfoBasedOnTaskInformationTable(commandToExecute, taskInformationTable);
		
		if (errorType == null) {
			commandResult = commandToExecute.execute();
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

	
	//********************************
	private static Error updateCommandInfoBasedOnTaskInformationTable(Command commandToUpdate, Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		
		TaskInfo newTaskInfo = getNewTaskInfoFromTaskInformationTable(taskInformationTable);
		TaskInfo taskInfoToModify = getTaskInfoToModifyTaskInformationTable(taskInformationTable);
		
		
		/* previous
		TaskInfo taskInformation = new TaskInfo();
		//to store existing taskinfo to be modified 
		TaskInfo taskInformationToBeModified = new TaskInfo();
		
		Error errorType = createTaskInfoBasedOnCommand(taskInformation, taskInformationToBeModified, parameters);
		*/
		
		commandToUpdate.setTaskInfo(newTaskInfo);
		
		//modify
		if (!taskInfoToModify.isEmpty()) {
			commandToUpdate.setTaskInfoToBeModified(taskInfoToModify);
		}
		 
		
		return null;
	}
	
	private static TaskInfo getNewTaskInfoFromTaskInformationTable(Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		TaskInfo taskInfo = new TaskInfo();
		
		taskInfo.setTaskName(taskInformationTable.get(KEYWORD_TYPE.TASKNAME));
		
		String startDate = taskInformationTable.get(KEYWORD_TYPE.START_DATE);
		String startTime = taskInformationTable.get(KEYWORD_TYPE.START_TIME);
		Calendar startDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(startDate, startTime);
		
		String endDate = taskInformationTable.get(KEYWORD_TYPE.END_DATE);
		String endTime = taskInformationTable.get(KEYWORD_TYPE.END_TIME);
		Calendar endDateAndTime = DateAndTimeFormat.getInstance().formatStringToCalendar(endDate, endTime);
				
		taskInfo.setStartDate(startDateAndTime);
		taskInfo.setEndDate(endDateAndTime);
		taskInfo.setTaskType(TASK_TYPE.FLOATING);	// HARDCODED TO DEFAULT
		
		if (taskInformationTable.containsKey(KEYWORD_TYPE.PRIORITY)) {
			String priority = taskInformationTable.get(KEYWORD_TYPE.PRIORITY);
			taskInfo.setImportanceLevel(Integer.parseInt(priority));
		}
		
		return taskInfo;
	}
	
	private static TaskInfo getTaskInfoToModifyTaskInformationTable(Hashtable<KEYWORD_TYPE, String> taskInformationTable) {
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskName(taskInformationTable.get(KEYWORD_TYPE.MODIFIED_TASKNAME));
		return taskInfo;
	}
	
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		
		// Determine what command to execute
		switch(userCommand) {
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
	
	
}
