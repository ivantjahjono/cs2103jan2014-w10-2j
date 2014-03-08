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
		ADD, DELETE, MODIFY, SEARCH, INVALID;
}

enum KEYWORD_TYPE {
	INVALID, TASKID, TASKNAME, MODIFIED_TASKNAME, START_DATE, START_TIME, END_DATE, END_TIME, PRIORITY
}

public class TaskMasterKaboom {
	
	private static final String KEYWORD_COMMAND_ADD = "add";
	private static final String KEYWORD_COMMAND_DELETE = "delete";
	private static final String KEYWORD_COMMAND_MODIFY = "modify";
	private static final String KEYWORD_COMMAND_SEARCH = "search";
	
	private static final int CORRECT_24HOUR_FORMAT_MIN = 100;
	private static final int CORRECT_24HOUR_FORMAT_MAX = 2359;
	
	private static final int THE_24_HOUR_FORMAT_CODE = 1;
	private static final int THE_24_HOUR_FORMAT_WITH_COLON_CODE = 2;
	private static final int THE_AM_FORMAT_CODE = 3;
	private static final int THE_AM_FORMAT_WITH_COLON_CODE = 4;
	private static final int THE_PM_FORMAT_CODE = 5;
	private static final int THE_PM_FORMAT_WITH_COLON_CODE = 6;
	private static final int START_DATE_COUNT = 1;
	private static final int END_DATE_COUNT = 2;
	
	private static final String FILENAME = "KABOOM_FILE.dat";
	
	private static GraphicInterface taskUi;
	private static DisplayData guiDisplayData;
	private static Storage fileStorage;
	private static History historyofCommands = new History();
	
	// Temporary static
	private static int counter = 1;				// Use to create temporary task
	private static boolean isRunning = true;
	
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
		
		//
		while (isRunning) {
			 // Updates the task data inside here every minute
		}
	}
	
	private static boolean setupUi () {
		try {
			taskUi = new GraphicInterface();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void activateUi () {
		updateUi("Welcome back, Commander");
		taskUi.run(null);
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
	public static String processCommand(String userInputSentence) {		
		Command commandToExecute = null;
		String feedback = "";
		COMMAND_TYPE commandType = determineCommandType(userInputSentence);
		String commandParametersString = removeFirstWord(userInputSentence);
		
		commandToExecute = createCommandBasedOnCommandType(commandType);
		Error errorType = updateCommandInfoFromParameter(commandToExecute, commandParametersString);
		
		if (errorType == null) {
			feedback = commandToExecute.execute();
		} else {
			feedback = errorType.getErrorMessage();
		}
		
		// Later to be move to somewhere else
		updateUi(feedback);
		
		// Add recent command to History list
		addToCommandHistory(new Command());
		
		// Save data to file
		fileStorage.store();
		
		return feedback;
	}
	
	private static String removeFirstWord(String userInputSentence) {
		String wordRemoved = userInputSentence.replace(getFirstWord(userInputSentence), "").trim();
		return wordRemoved;
	}

	private static void updateUi(String feedback) {
		Vector<TaskInfo> taskToDisplay = TaskListShop.getInstance().getAllTaskInList();
		
		guiDisplayData.setFeedbackMessage(feedback);
		guiDisplayData.setTaskDataToDisplay(taskToDisplay);
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
				
			default:
				newlyCreatedCommand = new Command();
				break;
				
		}
		
		return newlyCreatedCommand;
	}

	private static Error updateCommandInfoFromParameter(Command commandToUpdate, String parameters) {
		TaskInfo taskInformation = new TaskInfo();
		
		Error errorType = createTaskInfoBasedOnCommand(taskInformation, parameters);
		commandToUpdate.setTaskInfo(taskInformation);

		return errorType;
	}
	
	private static COMMAND_TYPE determineCommandType(String userCommand) {
		String commandTypeString = getFirstWord(userCommand);
		
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
			default:
				return COMMAND_TYPE.INVALID;
		}
	}
	
	private static Error createTaskInfoBasedOnCommand(TaskInfo newTaskInfo, String userInputSentence) {
		// Currently it is randomly generated.
		//updateTaskInfoBasedOnParameter(newlyCreatedTaskInfo, userInputSentence);
		
		Error errorEncountered = updateTaskInfo(newTaskInfo, userInputSentence);
		
		return errorEncountered;
	}
	
	private static void updateTaskInfoBasedOnParameter(TaskInfo taskInfoToUpdate, String parameterString) {
		// Decoy information
		Random randomGenerator = new Random();
		
		String taskname = String.format("Task %d", counter);
		int startDay = randomGenerator.nextInt(32);
		int startMonth = randomGenerator.nextInt(12);
		int startYear = 2014;
		int startHour = randomGenerator.nextInt(24);
		int startMinute = randomGenerator.nextInt(60);
		Calendar startDate = new GregorianCalendar(startYear, startMonth, startDay, startHour, startMinute);
		
		int endDay = randomGenerator.nextInt(32);
		int endMonth = randomGenerator.nextInt(12);
		int endYear = 2014;
		int endHour = randomGenerator.nextInt(24);
		int endMinute = randomGenerator.nextInt(60);
		Calendar endDate = new GregorianCalendar(endYear, endMonth, endDay, endHour, endMinute);
		
		int priority = randomGenerator.nextInt(4);
		
		taskInfoToUpdate.setTaskName(taskname);
		taskInfoToUpdate.setStartDate(startDate);
		taskInfoToUpdate.setEndDate(endDate);
		taskInfoToUpdate.setImportanceLevel(priority);
		
		counter++;
	}

	private static Error updateTaskInfo(TaskInfo thisTaskInfo, String userInputSentence){
		String[] processedText = textProcess(userInputSentence);
		String taskname = "";
		//int startDate;
		//int endDate;
		int priority = 2;
		
		// Cut the command into their respective syntax. Will return hash table of data strings
		Hashtable<KEYWORD_TYPE, String> keywordHashTable = new Hashtable<KEYWORD_TYPE, String>();
		Error errorEncountered = createKeywordTableBasedOnParameter(userInputSentence, keywordHashTable);
		if (errorEncountered != null) {
			return errorEncountered;
		}
		
		System.out.println(keywordHashTable);
		
		
		// TODO
		// One possible way of determining the command syntax.
		// 1. Determine the command type. Each command has a different syntax and even same command
		//    might have different formats. The command type can also be passed in by parameter
		//    to avoid determining the command type again.
		//    E.g: add hello world at 1130pm on 120112 by 1230am on 130112 ***
		//         <command> <task name> at <start time> on <date> by <end time> on <date>
		
		// 2. Switch on all syntax checks for keywords for that particular command.
		//    E.g: hello world at 1130pm on 120112 by 1230am on 130112 ***
		//    <task name> at <start time> on <date> by <end time> on <date> <priority level>
		
		
		// 3. Find the next closest syntax using the keywords such as 'at', 'by' or '*' for priority.
		//    based on syntax checks that is on.
		//    E.g: done <task name>
		//    Checks for keyword 'at' or 'by' are switched off as they are not related to the command 'done'.  
		// 3a. Cut the command until the next closest syntax.
		//    E.g: 1. hello world 
		//         2. at <start time> on <date> by <end time> on <date> <priority level>
		// 3b. The first string will be the task name. Update the information to task info.
		
		
		// 4. Find the next closest syntax again using the keywords such as 'at', 'by' or '*' for priority.
		// 4a. If there is no such keywords found, end the parsing. If not cut the command again using the
		//     remaining string.
		//     E.g: 1. at <start time> on <date>
		//          2. by <end time> on <date> <priority level>
		// 4b. Determine the syntax by the keyword. If it contains 'at' it is a start time, 
		//     if it has 'by' it is an end time and so on.
		
		
		// 5. Repeating steps 3 and 4 until there is no string left or no keyword is found.
		
		
		// 6. In steps of updating to task info, validate each information if it matches the formats.
		//    E.g: by 1230am on 130112***
		//         It is not a valid format as the asterisks are combined. Might be due to user
		//         typo.
		
		taskname = functionFindTaskname(processedText);
		thisTaskInfo.setTaskName(taskname);
		
		setTypeAndDate(thisTaskInfo, processedText);
		
		
		//thisTaskInfo.setStartDate(startDate);
		//thisTaskInfo.setEndDate(endDate);
		thisTaskInfo.setImportanceLevel(priority);
		
		return null;
	}
	
	//TODO Clean up and refactor code
	private static Error createKeywordTableBasedOnParameter(String userInputSentence, Hashtable<KEYWORD_TYPE, String> keywordTable) {
		String currentString = userInputSentence;
		int nextKeywordIndex = 0;
		KEYWORD_TYPE type = KEYWORD_TYPE.INVALID;
		KEYWORD_TYPE prevType = type;
		String cutOutString = "";
		
		
		while (nextKeywordIndex != -1) {
			// Get index of next keyword and keyword type
			nextKeywordIndex = getNextKeywordIndex(currentString);
			
			if (currentString.length() > 0 && nextKeywordIndex < 1) {
				nextKeywordIndex =  currentString.length();
			}
			
			if (nextKeywordIndex != -1) {
				// Cut to next keyword
				cutOutString = currentString.substring(0, nextKeywordIndex);
				
				if (keywordTable.isEmpty()) {
					type = KEYWORD_TYPE.TASKNAME;
				} else {
					type = getKeywordType(cutOutString);
				}
				
				if (type == KEYWORD_TYPE.START_DATE && prevType == KEYWORD_TYPE.END_TIME) {
					type = KEYWORD_TYPE.END_DATE;
				}
				
				// Is there already data in table for that type ?
				if (keywordTable.get(type) != null) {
					return new Error(Error.ERROR_TYPE.MESSAGE_DUPLICTE_COMMAND_PARAMETERS);
				}
				
				// add to table
				keywordTable.put(type, cutOutString);
				
				// Remove the string from the original string
				currentString = currentString.replace(cutOutString, "");
				
				prevType = type;
			}
		}
		
		return null;
	}

	private static KEYWORD_TYPE getKeywordType(String cutOutString) {
		// TODO Auto-generated method stub
		if (cutOutString.contains("at")) {
			return KEYWORD_TYPE.START_TIME;
		} else if (cutOutString.contains("by")) {
			return KEYWORD_TYPE.END_TIME;
		} else if (cutOutString.contains("*")) {
			return KEYWORD_TYPE.PRIORITY;
		} else if (cutOutString.contains("on")) {
			return KEYWORD_TYPE.START_DATE;
		}
		
		return KEYWORD_TYPE.INVALID;
	}

	private static int getNextKeywordIndex(String stringToSearch) {
		int nearestIndex = -1;
		int currentIndex = 0;
		String currentKeyword = "";
		
		// Hard coded value
		for (int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					currentKeyword = "at";
					break;
					
				case 1:
					currentKeyword = "by";
					break;
					
				case 2:
					currentKeyword = "*";
					break;
					
				case 3:
					currentKeyword = "on";
					break;
			}
			
			currentIndex = stringToSearch.indexOf(currentKeyword);
			
			if (currentIndex > 0  && (nearestIndex == -1 || currentIndex < nearestIndex)) {
				nearestIndex = currentIndex;
			}
		}
		
		return nearestIndex;
	}

	private static void setTypeAndDate(TaskInfo thisTaskInfo, String[] processedText){
		boolean deadlineType = false;
		boolean timedType = false;
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		for(int i=0; i<processedText.length; i++){
			//check that this is the first encounter of the keyword "at"
			if(processedText[i].equals("at") && (timedType == false)){
				timedType = true;
				String allegedTime = processedText[i+1];
				TimeFormat currTimeFormat = new TimeFormat();
				if(verifyTimeValidity(allegedTime, currTimeFormat)){
					timeTranslator(startDate, Integer.parseInt(allegedTime), currTimeFormat);
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
			//check that this is the first encounter of the keyword "by"
			else if(processedText[i].equals("by") && (deadlineType == false)){
				deadlineType = true;
				String allegedTime = processedText[i+1];
				TimeFormat currTimeFormat = new TimeFormat();
				if(verifyTimeValidity(allegedTime, currTimeFormat)){
					timeTranslator(endDate, Integer.parseInt(allegedTime), currTimeFormat);
				}
				else{
					thisTaskInfo = null;
					return;
				}
			}
			
			//if this is not the first encounter of the keyword, it means, the user keyed in a wrong input format
			
		}
		
		//Calendar currStartDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE));
		//Calendar currEndDate = new GregorianCalendar(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE));
		detectAndSetTaskDate(processedText, startDate, endDate);
		thisTaskInfo.setStartDate(startDate);
		thisTaskInfo.setEndDate(endDate);
		setTaskType(thisTaskInfo, deadlineType, timedType);
		
	}
	
	private static void detectAndSetTaskDate(String[] processedText, Calendar startDate, Calendar endDate){
		int onWordCounter = 0; //this variable is to ensure that there are maximum of 2 "on" keyword for startDate and endDate
		
		for(int i=0; i<processedText.length; i++){
			if(processedText[i].equals("on")){
				onWordCounter++;
				if(onWordCounter == START_DATE_COUNT){
					dateTranslator(startDate, processedText[i+1]);
				}
				else if(onWordCounter == END_DATE_COUNT){
					dateTranslator(endDate, processedText[i+1]);
				}
				else
				{
					
					return;
				}
			}
		}
	}
	
	
	private static void dateTranslator(Calendar thisDate, String theDate){
		//this method should already take in the proper date format. verification should be separated in another method
		int date;
		int month;
		int year;
		String[] dateArray = new String[3];
		
		dateArray = theDate.split("/");
		
		if(dateArray[2] != null){
			year = Integer.parseInt(dateArray[2]);
			thisDate.set(Calendar.YEAR, year);
		}
		if(dateArray[1] != null){
			month = Integer.parseInt(dateArray[1]);
			thisDate.set(Calendar.MONTH, month-1);
		}
		if(dateArray[0] != null){
			date = Integer.parseInt(dateArray[0]);
			thisDate.set(Calendar.DAY_OF_MONTH, date);
		}
		return;
	}
	
	private static void timeTranslator(Calendar theTime, int correctTime, TimeFormat currTimeFormat){
		//this method translates ALL time formats
		if(currTimeFormat.getTimeFormatCode() == THE_24_HOUR_FORMAT_CODE){
			int hour = correctTime / CORRECT_24HOUR_FORMAT_MIN;
			int minute = correctTime % CORRECT_24HOUR_FORMAT_MIN;
			theTime.set(Calendar.HOUR_OF_DAY, hour);
			theTime.set(Calendar.MINUTE, minute);
		}
		else{
			return;
		}
		return;
		
	}

	private static boolean verifyTimeValidity(String allegedTime, TimeFormat currTimeFormat) {
		
		try{
			//check if it's the 24 hour format without separation. Eg: 1700, 1000
			int correctTimeFormat = Integer.parseInt(allegedTime);
			//check if the time is in logical number
			//!!!!!!!!!revise this again. Logic error
			if((correctTimeFormat >= CORRECT_24HOUR_FORMAT_MIN) && (correctTimeFormat <= CORRECT_24HOUR_FORMAT_MAX) ){
				currTimeFormat.setTimeFormatCode(THE_24_HOUR_FORMAT_CODE);
				return true;
			}
			else {
				return false;
			}
			
		}
		catch(IllegalArgumentException exception){
			//this means either invalid format or the other different formats
			//5am, 5pm, 17:00, 5:00am, etc
			return false;
		}
		
		//this is a stub
		//return false;
	}

	private static void setTaskType(TaskInfo thisTaskInfo,
			boolean deadlineType, boolean timedType) {
		if (timedType){
			thisTaskInfo.setTaskType(TASK_TYPE.TIMED);
		}
		else if(deadlineType){
			thisTaskInfo.setTaskType(TASK_TYPE.DEADLINE);
		}
		else{
			thisTaskInfo.setTaskType(TASK_TYPE.FLOATING);
		}
	}
	
	private static String functionFindTaskname(String[] processedText){
		String actualTaskName = "";
		for(int i=0; i<processedText.length; i++){
			if((!processedText[i].equals("by")) && (!processedText[i].equals("at")) && (!processedText[i].equals("on"))){
				actualTaskName += processedText[i] + " ";	
			}
			else{
				break;
			}
		}
		return actualTaskName;
	}
	
	private static String[] textProcess(String userInputSentence){
		String[] commandAndData = userInputSentence.trim().split("\\s+");
		return commandAndData;
	}
	
	private static String getFirstWord(String userInputSentence) {
		String[] elements = textProcess(userInputSentence);
		String firstWord = elements[0].toLowerCase();
		return firstWord;
	}
}
