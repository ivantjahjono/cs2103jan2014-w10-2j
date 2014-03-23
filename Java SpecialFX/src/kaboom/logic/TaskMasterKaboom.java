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
import kaboom.logic.command.CommandFactory;
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
	
	private static final String MESSAGE_WELCOME = "Welcome back, Commander";
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static GraphicInterface taskUi;
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private static History historyofCommands = new History();

	
	static TaskMasterKaboom instance;
	
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
		Command commandToExecute = null;
		Result commandResult = null;
		
		String commandKeyword = TextParser.getCommandKeyWord(userInputSentence);

		commandToExecute = CommandFactory.createCommand(commandKeyword);
		
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
	
	
}
